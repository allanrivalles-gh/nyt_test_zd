//
//  DiscussionHeaderViewModel.swift
//
//
//  Created by Jason Leyrer on 8/30/22.
//

import AthleticApolloTypes
import AthleticUI
import SwiftUI

public struct DiscussionHeaderViewModel {
    enum DiscussionType {
        case discussion
        case qanda(isLive: Bool)
    }

    let logoUrl: URL?
    let typeName: String
    let title: String
    let excerpt: String
    let authorName: String?
    let date: Date
    let isLive: Bool
    let highlightColor: Color?

    private let type: DiscussionType
    private let followingEntityProvider: CommentsFollowingEntityProvider?

    init?(
        discussionDetails: GQL.ArticleContentLite,
        type: DiscussionType,
        followingEntityProvider: CommentsFollowingEntityProvider? = nil
    ) {
        self.type = type

        switch type {
        case .qanda(let isLive):
            typeName = Strings.qandaTitle.localized
            self.isLive = isLive
        case .discussion:
            typeName = Strings.discussionTitle.localized
            isLive = false
        }

        title = discussionDetails.title
        excerpt = discussionDetails.excerptPlaintext
        authorName =
            discussionDetails.author.fragments.userDetailWrapper.asStaff?.fragments.userDetail.name
        date = discussionDetails.publishedAt

        self.followingEntityProvider = followingEntityProvider

        let teamIds = discussionDetails.teamIds?.compactMap { $0 } ?? []
        let leagueIds = discussionDetails.leagueIds?.compactMap { $0 } ?? []

        let teams = teamIds.compactMap { id in
            followingEntityProvider?.entity(forLegacyId: id, commentsEntityType: .team)
        }

        let leagues = leagueIds.compactMap { id in
            followingEntityProvider?.entity(forLegacyId: id, commentsEntityType: .league)
        }

        guard !teams.isEmpty else {
            /// If there are no tagged teams, use a league logo if possible
            if leagues.count == 1, let leagueLogoUrl = leagues.first?.imageUrl {
                logoUrl = leagueLogoUrl
                highlightColor = nil
                return
            }

            logoUrl = nil
            highlightColor = nil
            return
        }

        // if there is only one team, show the team's color and logo
        if teams.count == 1, let team = teams.first {
            if let color = team.color {
                highlightColor = Color(hex: color)
            } else {
                highlightColor = nil
            }

            logoUrl = team.imageUrl
        } else {
            highlightColor = nil

            let isSameLeague = teams.allSatisfy {
                $0.associatedLeagueLegacyId == teams.first?.associatedLeagueLegacyId
            }

            // if there is more than 1 team, and their league is the same, show the league logo
            if isSameLeague, let leagueId = teams.first?.associatedLeagueLegacyId {
                logoUrl =
                    leagues.first(
                        where: { $0.associatedLeagueLegacyId == leagueId }
                    )?.imageUrl
            } else {
                // Otherwise, show a placeholder (TA logo)
                logoUrl = nil
            }
        }
    }
}
