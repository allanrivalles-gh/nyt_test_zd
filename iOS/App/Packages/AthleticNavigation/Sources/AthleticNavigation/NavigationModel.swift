//
//  NavigationModel.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 4/6/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import Combine
import Foundation

public final class NavigationModel: ObservableObject {
    public class Path: ObservableObject {
        @Published public var nodes: [AthleticScreen]

        public init(nodes: [AthleticScreen] = []) {
            self.nodes = nodes
        }

        public func push(_ screen: AthleticScreen) {
            nodes.append(screen)
        }

        public func clear() {
            nodes.removeAll()
        }

        public var last: AthleticScreen? {
            nodes.last
        }

        public var isEmpty: Bool {
            nodes.isEmpty
        }
    }

    public class TabSelection: ObservableObject {
        public let reselectedTab = PassthroughSubject<MainTab, Never>()

        fileprivate init(onTabReselected: @escaping () -> Void) {
            self.onTabReselected = onTabReselected
        }

        let onTabReselected: () -> Void

        @Published public var value: MainTab = .home {
            willSet {
                if value == newValue {
                    onTabReselected()
                    reselectedTab.send(newValue)
                }
            }
        }
    }

    public private(set) lazy var selectedTab = TabSelection(onTabReselected: onTabReselected)

    public let homePath = Path()
    public let scoresPath = Path()
    public let discoverPath = Path()
    public let listenPath = Path()
    public let accountPath = Path()
    public let webViewTestPath = Path()
    public var entityPath = Path()
    public let diagnosticsPath = Path()

    // MARK: Context independent sheets
    @Published public var isAttributionSurveyActive = false

    public var entityId: String?

    // MARK: Sub Tab Properties
    @Published public var listenSelectedTab: ListenTab?

    /// Provided so that outside of this package you can init
    public init() {}

    public func addScreenToSelectedTab(_ screen: AthleticScreen?) {
        guard let screen else {
            assertionFailure("screen was nil")
            return
        }
        switch selectedTab.value {
        case .account:
            accountPath.push(screen)
        case .home:
            homePath.push(screen)
        case .scores:
            scoresPath.push(screen)
        case .discover:
            discoverPath.push(screen)
        case .listen:
            listenPath.push(screen)
        case .entity:
            entityPath.push(screen)
        }
    }

    public func selectTab(_ tab: MainTab) {
        /// Set the tab
        switch tab {
        case .entity(let entity, _):
            selectedTab.value = tab
            if entityId != entity.id {
                entityPath = Path()
            }
            entityId = entity.id
        default:
            selectedTab.value = tab
        }
    }

    public func selectListenTab(withSubTab tabType: ListenTab = .following) {
        selectedTab.value = .listen
        listenSelectedTab = tabType
    }

    public func reset() {
        homePath.clear()
        scoresPath.clear()
        discoverPath.clear()
        accountPath.clear()
        listenPath.clear()
        selectedTab.value = .home
        listenSelectedTab = nil
    }

    private func onTabReselected() {
        switch selectedTab.value {
        case .account:
            accountPath.clear()
        case .home:
            homePath.clear()
        case .scores:
            scoresPath.clear()
        case .discover:
            discoverPath.clear()
        case .listen:
            listenPath.clear()
        case .entity:
            if entityId != nil {
                entityPath.clear()
            }
        }
    }
}

extension NavigationModel {

    /// This publisher is responsible for emitting events every time there's a change in the navigation state.
    /// It's different from the standard `objectWillChange` provided by `ObservableObject`.
    /// Instead of emitting before a change occurs (like `objectWillChange`), it emits after the change has already happened.
    /// This property can be used for tracking navigation events like screen transitions (push/pop) or tab selection changes.
    public var navigationStateDidChange: AnyPublisher<Void, Never> {
        homePath.objectWillChange
            .merge(with: scoresPath.objectWillChange)
            .merge(with: discoverPath.objectWillChange)
            .merge(with: listenPath.objectWillChange)
            .merge(with: accountPath.objectWillChange)
            .merge(with: webViewTestPath.objectWillChange)
            .merge(with: entityPath.objectWillChange)
            .merge(
                with: selectedTab.$value.dropFirst().map { _ in Void() }
                    .eraseToAnyPublisher()
            )
            /// The use of `receive(on: RunLoop.main)` is crucial.
            /// It ensures the publisher emits after the main run loop completes its current cycle.
            /// This behavior effectively makes the publisher emit after the state changes have occurred.
            .receive(on: RunLoop.main)
            .eraseToAnyPublisher()
    }
}
