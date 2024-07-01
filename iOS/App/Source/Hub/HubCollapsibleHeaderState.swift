//
//  HubCollapsibleHeaderState.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 3/10/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

@MainActor
final class HubCollapsibleHeaderState: ObservableObject {

    enum Expansion {
        case expanded
        case collapsed
    }

    enum ScrollingTowardsEdge {
        case top
        case bottom
        case none
    }

    /// Keep track of the scroll offset for each tab.
    var tabScrollOffsets: [HubTab: CGFloat] = [:]

    /// The expanded height of the header.
    var naturalHeight: CGFloat?

    /// A timer to update the expansion state, attached to the main run loop so it will fire after the user has finished scrolling.
    weak var stateChangeTimer: Timer?

    /// Whether the header is expanded or collapsed.
    @Published var expansionState: Expansion = .expanded

    // MARK: - Opacity Calculations

    /// Opacity of the navigation bar title text (this only shows in collapsed state; expanded state has a logo in this location).
    var navigationTitleOpacity: CGFloat {
        switch expansionState {
        case .expanded:
            return 0
        case .collapsed:
            return 1
        }
    }

    /// Opacity of the team/league logo (only shows in expanded state)
    var logoOpacity: CGFloat {
        switch expansionState {
        case .expanded:
            return 1
        case .collapsed:
            return 0
        }
    }

    /// Opacity of the team name and standing labels (only show in expanded state)
    var expandedLabelsOpacity: CGFloat {
        switch expansionState {
        case .expanded:
            return 1
        case .collapsed:
            return 0
        }
    }

    /// Content views within the hub tabs should call this function whenever their scrollview offset changes
    /// - Parameters:
    ///   - offset: Scroll offset
    ///   - tab: Tab that was scrolled
    ///   - isSelectedTab: Whether this is the currently selected hub tab
    func handleOffsetUpdate(offset: CGFloat, forTab tab: HubTab, isSelectedTab: Bool) {
        guard tabScrollOffsets[tab] != offset else {
            /// Ignore the update if the value is the same
            return
        }

        let previousOffset = tabScrollOffsets[tab]
        tabScrollOffsets[tab] = offset

        guard isSelectedTab, let previousOffset else {
            return
        }

        updateHeaderHeight(
            previousOffset: previousOffset,
            currentOffset: offset
        )
    }

    private func updateHeaderHeight(
        previousOffset: CGFloat,
        currentOffset: CGFloat
    ) {
        guard let naturalHeight = naturalHeight, stateChangeTimer == nil else {
            return
        }

        let scrollingTowards: ScrollingTowardsEdge = {
            if previousOffset < currentOffset {
                return .top
            } else if currentOffset < previousOffset {
                return .bottom
            } else {
                return .none
            }
        }()

        let halfHeaderHeight = naturalHeight / 2

        switch (expansionState, scrollingTowards) {
        case (.collapsed, .top) where currentOffset > -halfHeaderHeight:
            /// Header is currently collapsed, we're scrolling upwards / towards the top, and we're past half the height of the header (near the very top), so we should expand
            scheduleHeaderExpansion(to: .expanded)

        case (.expanded, .bottom) where currentOffset < -halfHeaderHeight:
            /// Header is currently expanded, we're scrolling downwards / towards the bottom, and we've scrolled lower than half the header height, so we should collpase the header
            scheduleHeaderExpansion(to: .collapsed)
        default:
            break
        }
    }

    /// Schedules the expansion state update with a `Timer` running on the main run loop in `default` mode which means it will
    /// only fire after the user has finished scrolling, hence avoiding performance issues interrupting scroll.
    /// - Parameter updatedState: New state to animate
    private func scheduleHeaderExpansion(to updatedState: HubCollapsibleHeaderState.Expansion) {
        stateChangeTimer?.invalidate()
        stateChangeTimer = Timer.scheduledTimer(
            withTimeInterval: 0.1,
            repeats: false,
            block: { [weak self] _ in
                guard let self else {
                    return
                }

                Task { @MainActor in
                    withAnimation(.linear(duration: 0.2)) {
                        self.expansionState = updatedState
                    }

                    self.stateChangeTimer = nil
                }
            }
        )
    }
}
