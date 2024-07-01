//
//  KeyPointsNavigationRestorationStrategy.swift
//
//
//  Created by Leonardo da Silva on 19/09/23.
//

import AthleticFoundation
import SwiftUI

extension NavigationRestorationStrategy where Self == KeyPointsNavigationRestorationStrategy {
    public static func keyPoints() -> Self { KeyPointsNavigationRestorationStrategy() }
}

public class KeyPointsNavigationRestorationStrategy: NavigationRestorationStrategy {

    private let decoder = JSONDecoder()
    private let encoder = JSONEncoder()
    private var navigationRestoration: RestorationPoint = .init()

    public struct RestorationPoint {
        var screen: NavigationRestorationModel?
        var podcast: NavigationRestorationModel?

        public var latestRestoration: NavigationRestorationModel? {
            [screen, podcast].compactMap { $0 }.sorted(by: { $0.time > $1.time }).first
        }
    }

    public func invalidateRestoration(storage: NavigationRestorationControllerStorage) {
        navigationRestoration = .init()
        storage.persisted = nil
    }

    public func restore(
        navigationModel: NavigationModel,
        storage: NavigationRestorationControllerStorage,
        following: [FollowingEntity]
    ) async {
        guard
            let data = storage.persisted,
            let restoration = try? decoder.decode(NavigationRestorationModel?.self, from: data)
        else { return }

        invalidateRestoration(storage: storage)
        restoration.restore(navigation: navigationModel, following: following)
    }

    public func onRestorablePodcastAction(
        forPodcastEpisode: PodcastEpisode?,
        navigationModel: NavigationModel,
        storage: NavigationRestorationControllerStorage
    ) {
        if let forPodcastEpisode {
            navigationRestoration.podcast = NavigationRestorationModel(
                type: .podcast(episode: forPodcastEpisode)
            )
        } else {
            navigationRestoration.podcast = nil
        }

        storage.persisted = try? encoder.encode(navigationRestoration.latestRestoration)
    }

    public func onRestorablePointReached(
        navigationModel: NavigationModel,
        storage: NavigationRestorationControllerStorage
    ) {
        updateNavigationRestoration(
            navigationModel: navigationModel
        )
        storage.persisted = try? encoder.encode(navigationRestoration.latestRestoration)
    }

    private func updateNavigationRestoration(
        navigationModel: NavigationModel
    ) {
        if case .feed(.article(.detail(let id, _))) = navigationModel.currentScreen {
            navigationRestoration.screen = NavigationRestorationModel(
                type: .article(id: id)
            )
        } else if case .scores(.boxScore(let destination)) = navigationModel.currentScreen {
            navigationRestoration.screen = NavigationRestorationModel(
                type: .game(destination: destination)
            )
        } else if case .liveBlog(let liveBlogId, _) = navigationModel.currentScreen {
            navigationRestoration.screen = NavigationRestorationModel(
                type: .liveBlog(liveblogId: liveBlogId)
            )
        } else if case .hubDetails(let entity, _) = navigationModel.currentScreen {
            navigationRestoration.screen = NavigationRestorationModel(
                type: .entity(entity: entity)
            )
        } else if case .entity(let entity, _) = navigationModel.selectedTab.value {
            /// Check the selected tab last as this is for ipad only
            /// If no other cases are meant we check to see if we are on home page of entity on ipad
            ///If we are update screen to entity
            navigationRestoration.screen = NavigationRestorationModel(
                type: .entity(entity: entity)
            )
        } else {
            navigationRestoration.screen = nil
        }
    }
}

extension NavigationRestorationModel {

    fileprivate func restore(navigation: NavigationModel, following: [FollowingEntity]) {
        guard let screen else {
            assertionFailure("Restoration screen not implemented for \(self).")
            return
        }

        switch type {
        case .entity(let entity) where isIpad() && following.contains(entity):
            navigation.selectedTab.value = .entity(entity: entity, hubType: nil)
        default:
            navigation.addScreenToSelectedTab(screen)
        }
    }
}

extension NavigationModel {
    fileprivate var currentScreen: AthleticScreen? {
        switch selectedTab.value {
        case .account:
            return accountPath.last
        case .home:
            return homePath.last
        case .scores:
            return scoresPath.last
        case .discover:
            return discoverPath.last
        case .listen:
            return listenPath.last
        case .entity:
            return entityPath.last
        }
    }
}

private func isIpad() -> Bool {
    return UIDevice.current.userInterfaceIdiom == .pad
        || UIDevice.current.userInterfaceIdiom == .mac
}
