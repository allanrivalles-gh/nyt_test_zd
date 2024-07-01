//
//  NavigationRestorationController.swift
//
//
//  Created by Leonardo da Silva on 18/09/23.
//

import AthleticFoundation
import Combine
import SwiftUI

@MainActor
public protocol NavigationRestorationStrategy {
    func invalidateRestoration(storage: NavigationRestorationControllerStorage)

    func restore(
        navigationModel: NavigationModel,
        storage: NavigationRestorationControllerStorage,
        following: [FollowingEntity]
    ) async

    func onRestorablePodcastAction(
        forPodcastEpisode: PodcastEpisode?,
        navigationModel: NavigationModel,
        storage: NavigationRestorationControllerStorage
    )

    func onRestorablePointReached(
        navigationModel: NavigationModel,
        storage: NavigationRestorationControllerStorage
    )
}

public protocol NavigationRestorationControllerStorage {
    var persisted: Data? { get nonmutating set }
}

@MainActor
public class NavigationRestorationController: ObservableObject {
    private var shouldRestore = true
    public let navigationModel: NavigationModel
    public let storage: NavigationRestorationControllerStorage
    public let strategy: NavigationRestorationStrategy
    public let timeSetting: TimeSettings
    private var navigationModelSubscription: AnyCancellable?

    public init(
        navigationModel: NavigationModel,
        storage: NavigationRestorationControllerStorage,
        strategy: NavigationRestorationStrategy,
        timeSetting: TimeSettings = SystemTimeSettings()
    ) {
        self.navigationModel = navigationModel
        self.storage = storage
        self.strategy = strategy
        self.timeSetting = timeSetting

        navigationModelSubscription = navigationModel.navigationStateDidChange
            .sink { [weak self] in
                self?.onRestorablePointReached()
            }
    }

    public func onLaunchThroughDeeplink() {
        shouldRestore = false
        strategy.invalidateRestoration(storage: storage)
    }

    public func onReadyToRestore(
        following: [FollowingEntity]
    ) async {
        guard shouldRestore else { return }
        shouldRestore = false
        await strategy.restore(
            navigationModel: navigationModel,
            storage: storage,
            following: following
        )
    }

    public func onRestorablePodcastAction(
        forPodcastEpisode: PodcastEpisode?
    ) {
        strategy.onRestorablePodcastAction(
            forPodcastEpisode: forPodcastEpisode,
            navigationModel: navigationModel,
            storage: storage
        )
    }

    public func onRestorablePointReached() {
        strategy.onRestorablePointReached(
            navigationModel: navigationModel,
            storage: storage
        )
    }
}
