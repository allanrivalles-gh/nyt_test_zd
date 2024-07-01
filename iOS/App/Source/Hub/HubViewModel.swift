//
//  HubViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 28/7/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloNetworking
import AthleticApolloTypes
import AthleticBrackets
import AthleticFoundation
import AthleticNavigation
import AthleticScoresFoundation
import AthleticUI
import Combine
import Foundation
import SwiftUI

@MainActor
final class HubViewModel: ObservableObject {

    @MainActor
    final class Header: ObservableObject {
        let title: String
        @Published var subtitle: String?
        let logos: [ATHImageResource]
        let entity: FollowingEntity
        let followButton: FollowMenuButtonViewModel

        init(entity: FollowingEntity, following: FollowingModel) {
            self.title = entity.longDisplayName ?? entity.shortDisplayName ?? ""
            self.subtitle = nil
            self.logos = [entity.imageUrl.map({ ATHImageResource(url: $0) })].compactMap { $0 }
            self.entity = entity
            self.followButton = FollowMenuButtonViewModel(
                entity: entity,
                followingModel: following
            )
        }
    }

    let header: Header
    let colors: HubColors
    let tabs: [HubTab]
    @Published var selectedTab: HubTab

    @Published private(set) var dataFetchState: LoadingState = .initial

    var leagueSeasonId: String? {
        entity.leagueCode.flatMap { compass.config.flags.hardcodedSeasonId(for: $0) }
    }

    var teamId: String? {
        guard entity.entityType == .team else {
            return nil
        }

        return entity.gqlId
    }

    var teamLegacyId: Int? {
        guard entity.entityType == .team else {
            return nil
        }

        return Int(entity.legacyId)
    }

    var isTeamOrLeagueEntity: Bool {
        [.team, .league].contains(entity.entityType)
    }

    let entity: FollowingEntity
    private let network: HubNetworking
    private let compass: Compass
    private let eventManager: AnalyticEventManager

    init(
        entity: FollowingEntity,
        network: HubNetworking,
        compass: Compass,
        following: FollowingModel,
        selectedTabType: HubTabType? = nil,
        eventManager: AnalyticEventManager = AnalyticsManagers.events
    ) {
        self.entity = entity
        self.network = network
        self.compass = compass
        self.eventManager = eventManager

        header = Header(entity: entity, following: following)
        colors = HubColors(entityColorHex: entity.iconColor)

        let tabs: [HubTab]
        switch entity.entityType {
        case .team:
            tabs = Self.teamTabs(for: entity, featureFlags: compass.config.flags)
        case .league:
            tabs = Self.leagueTabs(for: entity, featureFlags: compass.config.flags)
        case .author, nil:
            tabs = Self.generalTabs(for: entity)
        }

        self.tabs = tabs
        if let selectedTabType = selectedTabType,
            let tab = tabs.first(where: { $0.tabType == selectedTabType })
        {
            selectedTab = tab
        } else {
            selectedTab = tabs[0]
        }
    }

    private static func teamTabs(
        for team: FollowingEntity,
        featureFlags: FeatureFlags
    ) -> [HubTab] {
        var tabs: [HubTab] = [HubTab(tabType: .feed, sport: team.sport)]

        if featureFlags.isTeamHubPodcastsEnabled && team.hasPodcastTab {
            tabs.append(
                HubTab(tabType: .podcasts, sport: team.sport)
            )
        }

        if UserDefaults.adminEnableTeamThreads {
            tabs.append(HubTab(tabType: .threads, sport: team.sport))
        }

        if team.hasSupportedScores {
            if let leagueCode = team.associatedLeagueGqlId.flatMap({ GQL.LeagueCode(rawValue: $0) }
            ), featureFlags.isBracketEnabled(for: leagueCode) {
                let seasonId = featureFlags.hardcodedSeasonId(for: leagueCode)
                if team.hasActiveBracket || seasonId != nil {
                    tabs.append(HubTab(tabType: .bracket, sport: team.sport))
                }
            }

            tabs.append(
                contentsOf: [
                    HubTab(tabType: .schedule, sport: team.sport),
                    HubTab(tabType: .standings, sport: team.sport),
                ]
            )
        }

        if team.gqlId != nil && (team.isAssociatedWithPrimaryLeague || team.teamType != .club) {
            tabs.append(
                contentsOf: [
                    HubTab(
                        tabType: .stats,
                        sport: team.sport
                    ),
                    HubTab(
                        tabType: .roster,
                        sport: team.sport
                    ),
                ]
            )
        }

        return tabs
    }

    private static func leagueTabs(
        for league: FollowingEntity,
        featureFlags: FeatureFlags
    ) -> [HubTab] {
        var tabs: [HubTab] = [HubTab(tabType: .feed, sport: league.sport)]

        if league.hasSupportedScores {
            tabs.append(
                contentsOf: [
                    HubTab(tabType: .schedule, sport: league.sport),
                    HubTab(tabType: .standings, sport: league.sport),
                ]
            )
            if let leagueCode = league.leagueCode, featureFlags.isBracketEnabled(for: leagueCode) {
                let seasonId = featureFlags.hardcodedSeasonId(for: leagueCode)
                if league.hasActiveBracket || seasonId != nil {
                    tabs.append(HubTab(tabType: .bracket, sport: league.sport))
                }
            }
        }

        return tabs
    }

    private static func generalTabs(for entity: FollowingEntity) -> [HubTab] {
        [HubTab(tabType: .feed, sport: entity.sport)]
    }

    func fetchHeaderInfoIfNeeded() async {
        guard !dataFetchState.isLoading else {
            return
        }

        guard
            entity.entityType == .team,
            let teamId = entity.gqlId
        else {
            /// In future this entity may be leagues or authors
            return
        }

        do {
            dataFetchState = .loading(showPlaceholders: true)
            let headerInfo = try await network.fetchTeamHubHeader(teamId: teamId)
            dataFetchState = .loaded
            header.subtitle = headerInfo.currentStanding ?? " "
        } catch {
            dataFetchState = .failed
            header.subtitle = " "
        }
    }

    func trackTabSelection(for tab: HubTab, oldTab: HubTab) {
        Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: oldTab.analyticsView,
                element: .feedNavigation,
                objectType: tab.analyticsObjectType,
                metaBlob: tab.metaBlob(entity: entity)
            ),
            manager: eventManager
        )
    }

    func trackTournamentGameTap(game: TournamentTile.Game) {
        let phase = GamePhase(
            statusCode: game.status,
            startedAt: game.startedAt
        )

        var phaseElement: AnalyticsEvent.Element? {
            switch phase {
            case .postGame:
                return .postGameBoxScore
            case .inGame:
                return .inGameBoxScore
            case .preGame, .nonStarter:
                return .preGameBoxScore
            default:
                return nil
            }
        }

        Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: .brackets,
                element: phaseElement,
                objectType: .gameId,
                objectIdentifier: game.id,
                metaBlob: .init(
                    leagueId: entity.entityType == .league ? entity.legacyId : nil
                )
            ),
            manager: eventManager
        )
    }

    func trackPodcastTabView() {
        Analytics.track(
            event: AnalyticsEventRecord(
                verb: .view,
                view: .podcasts,
                objectType: .teamId,
                objectIdentifier: teamId,
                metaBlob: .init(
                    leagueId: entity.metaBlobLeagueId
                )
            ),
            manager: eventManager
        )
    }
}

extension HubTab {
    fileprivate func metaBlob(entity: FollowingEntity) -> AnalyticsEvent.MetaBlob? {
        switch entity.entityType {
        case .league:
            let leagueId = entity.gqlId ?? entity.legacyId
            return AnalyticsEvent.MetaBlob(leagueId: leagueId)
        case .team:
            let teamId = entity.gqlId ?? entity.legacyId
            let leagueId = entity.associatedLeagueGqlId ?? entity.associatedLeagueLegacyId
            return AnalyticsEvent.MetaBlob(
                leagueId: leagueId,
                teamId: teamId,
                parentObjectType: .teamId
            )
        default: return nil
        }
    }

    fileprivate var analyticsObjectType: AnalyticsEvent.ObjectType {
        switch tabType {
        case .feed: return .home
        case .schedule: return .schedule
        case .standings: return .standings
        case .stats: return .stats
        case .roster: return .roster
        case .bracket: return .brackets
        /// TODO: update `threads` object type once feature is done
        case .threads: return .commentId
        case .podcasts: return .podcasts
        }
    }

    fileprivate var analyticsView: AnalyticsEvent.View {
        switch tabType {
        case .feed: return .home
        case .schedule: return .schedule
        case .standings: return .standings
        case .stats: return .stats
        case .roster: return .roster
        case .bracket: return .brackets
        /// TODO: update `threads` view once feature is done
        case .threads: return .comment
        case .podcasts: return .podcasts
        }
    }
}

extension FeatureFlags {
    fileprivate func hardcodedSeasonId(for league: GQL.LeagueCode) -> String? {
        switch league {
        case .nba where hardcodedNBASeason:
            return "SH3D1L749wt64cxn"
        case .mlb where hardcodedMLBSeason:
            return "tOef6WVsJd6iwbOv"
        case .nhl where hardcodedNHLSeason:
            return "U1QvCRbPsOZofiRE"
        default: break
        }
        return nil
    }

    fileprivate func isBracketEnabled(for league: GQL.LeagueCode) -> Bool {
        /// the server will be able to decide whether a league has Bracket or not available at the moment
        /// for now, the client side should make sure the bracket format is supported before making it available
        switch league {
        case .woc, .wwc: return true
        case .nba: return isNBABracketEnabled
        case .mlb: return isMLBBracketEnabled
        case .nhl: return isNHLBracketEnabled
        case .ncaamb, .ncaawb: return isMarchMadnessBracketEnabled
        default: return false
        }
    }
}
