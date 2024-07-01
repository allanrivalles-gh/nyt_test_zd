//
//  NavigationRestorationController+SwiftUI.swift
//
//
//  Created by Leonardo da Silva on 19/09/23.
//

import AthleticFoundation
import AthleticStorage
import SwiftUI

extension View {
    public func provideNavigationRestorationController(
        navigationModel: NavigationModel,
        strategy: NavigationRestorationStrategy,
        podcastPlayerHookup: @escaping (NavigationRestorationController) -> Void
    ) -> some View {
        modifier(
            ProvideNavigationRestorationControllerModifier(
                navigationModel: navigationModel,
                strategy: strategy,
                podcastPlayerHookup: podcastPlayerHookup
            )
        )
    }
}

private struct ProvideNavigationRestorationControllerModifier: ViewModifier {
    @StateObject private var controller: NavigationRestorationController

    let navigationModel: NavigationModel
    let podcastPlayerHookup: (NavigationRestorationController) -> Void

    init(
        navigationModel: NavigationModel,
        strategy: NavigationRestorationStrategy,
        podcastPlayerHookup: @escaping (NavigationRestorationController) -> Void
    ) {
        self._controller = StateObject(
            wrappedValue: NavigationRestorationController(
                navigationModel: navigationModel,
                storage: NavigationRestorationControllerStorableStorage(),
                strategy: strategy
            )
        )
        self.navigationModel = navigationModel
        self.podcastPlayerHookup = podcastPlayerHookup
    }

    func body(content: Content) -> some View {
        content
            .onAppear {
                podcastPlayerHookup(controller)
            }
            .environmentObject(controller)
    }
}

private struct NavigationRestorationControllerStorableStorage:
    NavigationRestorationControllerStorage
{
    @Storable(keyName: "navigationRestoration") static private var storage: Data?

    var persisted: Data? {
        get { Self.storage }
        nonmutating set { Self.storage = newValue }
    }
}
