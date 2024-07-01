//
//  PagingTabView.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 12/14/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import Combine
import SwiftUI

public protocol PagingTab: Identifiable, Hashable, ObservableObject {
    associatedtype Badge: View

    var title: String { get }
    var badge: Badge { get }
    var shouldShowBadge: Bool { get set }
}

extension PagingTab {
    public var badge: some View {
        EmptyView()
    }

    public var shouldShowBadge: Bool {
        get { false }
        set {}
    }
}

public enum PagingTabSizing {
    case equalWidth
    case equalSpacing
}

public enum PagingTabUnderscoreStyle {
    case hugText
    case fillWidth
    case hidden
}

public enum PagingTabBarVisibility {
    case always
    case ifMultipleTabs(hiddenHeight: CGFloat = 0)
}

public struct PagingTabView<Tab: PagingTab, Content: View>: View {
    public enum BarType {
        case fixed
        case scrolling
    }

    let tabs: [Tab]
    @Binding var selectedTab: Tab
    let type: BarType?
    let viewForTab: (Tab) -> Content

    @State private var pagedTab: Tab
    private let actions: PagingTabActions<Tab>

    @State private var tabViewXOffset: CGFloat = 0
    @State private var tabContentXOffsets: [Tab: CGFloat] = [:]
    @State private var fixedBarTypeMinWidth: CGFloat = 0
    @State private var width: CGFloat?
    @State private var widthChanged = PassthroughSubject<Void, Never>()

    @Environment(\.pagingTabBarBackgroundColor) private var tabBarBackgroundColor
    @Environment(\.pagingTabBarVisibility) private var tabBarVisibility

    public init(
        tabs: [Tab],
        selectedTab: Binding<Tab>,
        type: BarType? = nil,
        onTapFilter: VoidClosure? = nil,
        onSelectTab: ((_ prevTab: Tab, _ newTab: Tab) -> Void)? = nil,
        onSwipeToTab: ((Tab) -> Void)? = nil,
        secondaryAction: ((Tab) -> VoidClosure?)? = nil,
        @ViewBuilder viewForTab: @escaping (Tab) -> Content
    ) {
        self.tabs = tabs
        self._selectedTab = selectedTab
        self._pagedTab = State(initialValue: selectedTab.wrappedValue)
        self.type = type
        self.actions = PagingTabActions(
            onTapFilter: onTapFilter,
            onSelectTab: onSelectTab,
            onSwipeToTab: onSwipeToTab,
            secondaryAction: secondaryAction
        )
        self.viewForTab = viewForTab
    }

    public var body: some View {
        GeometryReader { geometry in
            VStack(spacing: 0) {
                switch tabBarVisibility {
                case .always, nil:
                    tabBar(containerGeometry: geometry)

                case .ifMultipleTabs(let hiddenHeight):
                    if tabs.count > 1
                        || actions.onTapFilter != nil
                        || actions.secondaryAction != nil
                    {
                        tabBar(containerGeometry: geometry)
                    } else if hiddenHeight > 0 {
                        Rectangle()
                            .fill(tabBarBackgroundColor ?? .clear)
                            .frame(height: hiddenHeight)
                    }
                }

                tabView
            }
            .overlay { automaticBarTypeCalculator }
            .onChange(of: selectedTab) { newValue in
                guard pagedTab != newValue else { return }
                pagedTab = newValue
            }
            .onChange(of: pagedTab) { newValue in
                guard selectedTab != newValue else { return }

                /// If the selected tab does not equal the paged tab we can infer we swiped to get here.
                actions.onSwipeToTab?(newValue)
                selectedTab = newValue
            }
            .onReceive(widthChanged) {
                /// When the width changes, SwiftUI.TabView is about to mess with the paging offset and selection, so we need to
                /// put the currently selected tab back again on the next run loop.
                fixImminentBrokenTabLayout()
            }
        }
    }

    @ViewBuilder
    private func tabBar(containerGeometry: GeometryProxy) -> some View {
        switch barType(availableWidth: containerGeometry.size.width) {
        case .fixed:
            FixedTabBar(
                tabs: tabs,
                selectedTab: $selectedTab,
                actions: actions
            )
        case .scrolling:
            ScrollingTabBar(
                tabs: tabs,
                selectedTab: $selectedTab,
                actions: actions
            )
        }
    }

    private var tabView: some View {
        SwiftUI.TabView(selection: $pagedTab) {
            let _ = DuplicateIDLogger.logDuplicates(in: tabs)
            ForEach(tabs) { tab in
                viewForTab(tab)
                    .getFrame(in: .global) { frame in
                        tabContentXOffsets[tab] = frame.minX
                    }
                    .tag(tab)
            }
        }
        .getFrame(in: .global) { frame in
            tabViewXOffset = frame.minX
        }
        .getSize { size in
            let oldWidth = width
            let newWidth = size.width

            guard oldWidth != newWidth else {
                return
            }

            width = newWidth

            if oldWidth != nil {
                widthChanged.send()
            }
        }
        .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
    }

    @ViewBuilder
    private var automaticBarTypeCalculator: some View {
        if type == nil {
            FixedTabBar(
                tabs: tabs,
                selectedTab: $selectedTab,
                actions: actions
            )
            .fixedSize()
            .getSize { minSize in
                if fixedBarTypeMinWidth != minSize.width {
                    fixedBarTypeMinWidth = minSize.width
                }
            }
            .hidden()
        }
    }

    private func fixImminentBrokenTabLayout() {
        let originallySelectedTab = selectedTab
        Task {
            await MainActor.run {
                if originallySelectedTab != selectedTab {
                    /// If the selected tab is different on the next run loop it means the layout catastropically broke.
                    /// Simply re-apply the correct selected tab and everything will return to normal.
                    selectedTab = originallySelectedTab

                } else if let tabOffset = tabContentXOffsets[selectedTab],
                    tabOffset != tabViewXOffset,
                    let adjacentTab = tabs.element(adjacentTo: selectedTab)
                {
                    /// If the correct tab is selected but the tab content isn't in the correct position we need to force the selection
                    /// to a different tab and then set it back to the correct tab. This forces SwiftUI to lay out the tab view correctly.
                    selectedTab = adjacentTab

                    Task {
                        let oneThousandthOfASecond: UInt64 = 1_000_000_000 / 1000
                        try await Task.sleep(nanoseconds: oneThousandthOfASecond)
                        await MainActor.run {
                            /// Belts and braces - make sure something else didn't intentially change tab while we
                            /// were sleeping e.g. a manual tap, swipe or an API's triggered reload.
                            guard selectedTab == adjacentTab else {
                                return
                            }

                            /// Finally put the correct tab back and SwiftUI should fix the layout
                            selectedTab = originallySelectedTab
                        }
                    }
                }
            }
        }
    }

    private func barType(availableWidth: CGFloat) -> BarType {
        if let type = type {
            return type
        } else if fixedBarTypeMinWidth < availableWidth {
            return .fixed
        } else {
            return .scrolling
        }
    }
}

private struct FixedTabBar<Tab: PagingTab>: View {
    let tabs: [Tab]
    @Binding var selectedTab: Tab
    let actions: PagingTabActions<Tab>
    @Environment(\.pagingTabBarBackgroundColor) private var backgroundColor

    var body: some View {
        HStack(spacing: 20) {
            if let onTapFilter = actions.onTapFilter {
                FilterButton(action: onTapFilter)
            }
            TabsStackView(
                tabs: tabs,
                tabPadding: 0,
                textPadding: 8,
                selectedTab: $selectedTab,
                actions: actions
            )
        }
        .frame(height: 36)
        .background(backgroundColor)
    }
}

private struct ScrollingTabBar<Tab: PagingTab>: View {
    let tabs: [Tab]
    @Binding var selectedTab: Tab
    let actions: PagingTabActions<Tab>
    @Environment(\.pagingTabBarBackgroundColor) private var backgroundColor

    @State private var scrollViewContentSize: CGSize = .zero
    @State private var scrollViewSize: CGSize = .zero
    @State private var scrollViewOffset: CGPoint = .zero

    private var shouldShowLeadingGradient: Bool {
        scrollViewOffset.x > 0
    }

    private var shouldShowTrailingGradient: Bool {
        scrollViewOffset.x < (scrollViewContentSize.width - scrollViewSize.width)
    }

    var body: some View {
        HStack(spacing: 0) {
            if let onTapFilter = actions.onTapFilter {
                FilterButton(action: onTapFilter)
            }
            ScrollViewReader { scrollViewProxy in
                TrackingScrollView(
                    .horizontal,
                    showsIndicators: false,
                    trackOffset: { offset in
                        withAnimation(.linear(duration: 0.1)) {
                            scrollViewOffset = offset
                        }
                    }
                ) {
                    TabsStackView(
                        tabs: tabs,
                        tabPadding: 12,
                        textPadding: 0,
                        selectedTab: $selectedTab,
                        actions: actions
                    )
                    .pagingTabSizing(.equalSpacing)
                    .pagingTabUnderscoreStyle(.hugText)
                    .frame(height: 36)
                    .getSize {
                        scrollViewContentSize = $0
                    }
                }
                .onTapGesture {
                    /// No-op.
                    /// This is truly bizarre but solves an issue where the bottom part of the tabs inside the scrollview isn't tappable.
                    /// https://stackoverflow.com/questions/69367905/swiftui-buttons-in-a-scrollview-is-not-tappable-sometimes
                }
                .getSize { size in
                    scrollViewSize = size

                    guard size.width > 0 else {
                        return
                    }

                    if let tab = tabs.first, selectedTab != tab {
                        scrollTo(tab: selectedTab, scrollViewProxy: scrollViewProxy)
                    }
                }
                .onChange(of: selectedTab) { value in
                    withAnimation {
                        scrollTo(tab: value, scrollViewProxy: scrollViewProxy)
                    }
                }
                .overlay(
                    HStack(spacing: 0) {
                        if shouldShowLeadingGradient {
                            EdgeGradient(position: .leading, color: backgroundColor)
                        }
                        Spacer()
                        if shouldShowTrailingGradient {
                            EdgeGradient(position: .trailing, color: backgroundColor)
                        }
                    }
                    .allowsHitTesting(false)
                )
            }
        }
        .background(backgroundColor)
    }

    private func scrollTo(tab: Tab, scrollViewProxy: ScrollViewProxy) {
        guard let index = adjustedScrollIndex(for: tab) else {
            scrollViewProxy.scrollTo(tab, anchor: anchorPoint(for: tab))
            return
        }

        scrollViewProxy.scrollTo(tabs[index], anchor: anchorPoint(for: tab))
    }

    private func anchorPoint(for tab: Tab) -> UnitPoint? {
        guard let index = tabs.firstIndex(where: { $0 == tab }) else {
            return nil
        }

        let anchor: UnitPoint?

        switch index {
        case 0:
            anchor = .center
        case tabs.count - 1, tabs.count - 2:
            anchor = .trailing
        default:
            anchor = .center
        }

        return anchor
    }

    /// If we've made it to the n-1th tab, scroll to the last tab instead in order to
    /// avoid the last tab getting "pulled" left from the end of the stack when `scrollTo`
    /// is trying to center the n-1th element.
    private func adjustedScrollIndex(for tab: Tab) -> Int? {
        guard let index = tabs.firstIndex(where: { $0 == tab }) else {
            return nil
        }

        return index == tabs.count - 2 ? index + 1 : index
    }
}

private struct TabsStackView<Tab: PagingTab>: View {
    let tabs: [Tab]
    let tabPadding: CGFloat
    let textPadding: CGFloat
    @Binding var selectedTab: Tab
    let actions: PagingTabActions<Tab>

    @Environment(\.pagingTabSizing) private var tabSizing

    @Namespace private var namespace

    @State private var tabSpacing: CGFloat = 0

    var body: some View {
        switch tabSizing {
        case .equalWidth:
            GeometryReader { geometry in
                stack(tabWidth: geometry.size.width / CGFloat(max(tabs.count, 1)))
            }
        case .equalSpacing:
            stack(tabWidth: nil)
        }
    }

    private func stack(tabWidth: CGFloat?) -> some View {
        HStack(spacing: 0) {
            let _ = DuplicateIDLogger.logDuplicates(in: tabs)
            ForEach(tabs) { tab in
                Spacer(minLength: 0)
                    .getSize { spacerSize in
                        tabSpacing = spacerSize.width
                    }

                let secondaryAction = actions.secondaryAction?(tab)
                let isSelected = selectedTab == tab

                SplitTabItem(
                    tab: tab,
                    isSelected: isSelected,
                    tabWidth: tabWidth,
                    horizontalPadding: tabPadding,
                    textHorizontalPadding: textPadding,
                    interTabSpacing: tabSpacing,
                    namespace: namespace,
                    hasSecondaryAction: secondaryAction != nil,
                    tapAction: {
                        if isSelected, let secondaryAction = secondaryAction {
                            secondaryAction()
                        } else {
                            actions.onSelectTab?(selectedTab, tab)
                            selectedTab = tab
                        }
                    }
                )
                .id(tab)

                Spacer(minLength: 0)
            }
        }
        .animation(.easeOut(duration: 0.2), value: selectedTab)
    }
}

private struct FilterButton: View {
    let action: VoidClosure

    var body: some View {
        Button(action: action) {
            Image("icn_filter")
                .foregroundColor(.chalk.dark800)
                .frame(width: 30, height: 30)
        }
        .padding(.leading, 16)
    }
}

private struct PagingTabActions<Tab: PagingTab> {
    let onTapFilter: VoidClosure?
    let onSelectTab: ((_ prevTab: Tab, _ newTab: Tab) -> Void)?
    let onSwipeToTab: ((Tab) -> Void)?
    let secondaryAction: ((Tab) -> VoidClosure?)?
}

extension Array where Element: Equatable {
    fileprivate func element(adjacentTo sourceElement: Element) -> Element? {
        guard let sourceElementIndex = firstIndex(of: sourceElement) else {
            return nil
        }

        let adjacentIndex: Int
        if sourceElementIndex > startIndex {
            /// If there's an element before, take the one to the left of it
            adjacentIndex = index(before: sourceElementIndex)

        } else if sourceElementIndex < endIndex - 1 {
            /// If there's an element after, take the one after it
            adjacentIndex = index(after: sourceElementIndex)

        } else {
            return nil
        }

        return self[adjacentIndex]
    }
}

// MARK: - Environment Configuration

private struct PagingTabSelectedForegroundColorKey: EnvironmentKey {
    static let defaultValue: Color = .chalk.dark800
}

private struct PagingTabNormalForegroundColorKey: EnvironmentKey {
    static let defaultValue: Color = .chalk.dark800.opacity(0.7)
}

private struct PagingTabSizingKey: EnvironmentKey {
    static let defaultValue: PagingTabSizing = .equalWidth
}

private struct PagingTabUnderscoreStyleKey: EnvironmentKey {
    static let defaultValue: PagingTabUnderscoreStyle = .fillWidth
}

private struct PagingTabBarBackgroundColorKey: EnvironmentKey {
    static let defaultValue: Color? = nil
}

private struct PagingTabBarVisibilityKey: EnvironmentKey {
    static let defaultValue: PagingTabBarVisibility? = .always
}

extension EnvironmentValues {
    var pagingTabSelectedForegroundColor: Color {
        get { self[PagingTabSelectedForegroundColorKey.self] }
        set { self[PagingTabSelectedForegroundColorKey.self] = newValue }
    }

    var pagingTabNormalForegroundColor: Color {
        get { self[PagingTabNormalForegroundColorKey.self] }
        set { self[PagingTabNormalForegroundColorKey.self] = newValue }
    }

    var pagingTabSizing: PagingTabSizing {
        get { self[PagingTabSizingKey.self] }
        set { self[PagingTabSizingKey.self] = newValue }
    }

    var pagingTabUnderscoreStyle: PagingTabUnderscoreStyle {
        get { self[PagingTabUnderscoreStyleKey.self] }
        set { self[PagingTabUnderscoreStyleKey.self] = newValue }
    }

    var pagingTabBarBackgroundColor: Color? {
        get { self[PagingTabBarBackgroundColorKey.self] }
        set { self[PagingTabBarBackgroundColorKey.self] = newValue }
    }

    var pagingTabBarVisibility: PagingTabBarVisibility? {
        get { self[PagingTabBarVisibilityKey.self] }
        set { self[PagingTabBarVisibilityKey.self] = newValue }
    }
}

extension View {
    public func pagingTabSelectedForegroundColor(_ value: Color?) -> some View {
        environment(
            \.pagingTabSelectedForegroundColor,
            value ?? PagingTabSelectedForegroundColorKey.defaultValue
        )
    }

    public func pagingTabNormalForegroundColor(_ value: Color?) -> some View {
        environment(
            \.pagingTabNormalForegroundColor,
            value ?? PagingTabNormalForegroundColorKey.defaultValue
        )
    }

    public func pagingTabSizing(_ value: PagingTabSizing?) -> some View {
        environment(
            \.pagingTabSizing,
            value ?? PagingTabSizingKey.defaultValue
        )
    }

    public func pagingTabUnderscoreStyle(_ value: PagingTabUnderscoreStyle?) -> some View {
        environment(
            \.pagingTabUnderscoreStyle,
            value ?? PagingTabUnderscoreStyleKey.defaultValue
        )
    }

    public func pagingTabBarBackgroundColor(_ value: Color?) -> some View {
        environment(\.pagingTabBarBackgroundColor, value)
    }

    public func pagingTabBarVisibility(_ value: PagingTabBarVisibility?) -> some View {
        environment(
            \.pagingTabBarVisibility,
            value ?? PagingTabBarVisibilityKey.defaultValue
        )
    }
}
