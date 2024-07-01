//
//  RefreshableScrollView.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 21/1/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import Combine
import SwiftUI

/// A wrapper for a scrollview supporting an immitation of iOS Pull to Refresh.
/// In iOS 15 we should prefer to use native `refreshable` modifier if appropriate (on Lists).
public struct RefreshableScrollView<Content: View>: View {

    let axes: Axis.Set

    private let makeContent: () -> Content
    private var isRefreshEnabled: CurrentValueSubject<Bool, Never>?
    private let showsIndicators: Bool
    private let trackOffset: ((CGPoint) -> Void)?
    private let refreshAction: @Sendable () async -> Void

    private let topId = "top-\(UUID().uuidString)"
    private let refreshingHeight: CGFloat = 64.0
    private let refreshOffsetThreshold: CGFloat = 130

    @State private var isRefreshShowing: Bool = false
    @State private var fractionPulled: Double? = nil
    @State private var globalFrame: CGRect = .zero

    /// Create a refreshable scrollview wrapper
    /// - Parameters:
    ///   - isRefreshEnabled: A publisher to enable and disable pull-to-refresh functionality, for example to throttle requests.
    ///   - refreshAction: Action to perform when the user requests a refresh. You must call the completion closure once the refresh process has finished, whether it fails or succeeds.
    ///   - content: ScrollView content
    public init(
        _ axes: Axis.Set = .vertical,
        isRefreshEnabled: CurrentValueSubject<Bool, Never>? = nil,
        showsIndicators: Bool = true,
        trackOffset: ((CGPoint) -> Void)? = nil,
        refreshAction: @escaping @Sendable () async -> Void,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.axes = axes
        self.isRefreshEnabled = isRefreshEnabled
        self.refreshAction = refreshAction
        self.showsIndicators = showsIndicators
        self.trackOffset = trackOffset
        makeContent = content
    }

    public var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .top) {
                PullToRefreshIndicator(
                    fractionPulled: $fractionPulled,
                    refreshingHeight: refreshingHeight,
                    isRefreshShowing: isRefreshShowing
                )

                ScrollViewReader { proxy in
                    ScrollView(axes, showsIndicators: showsIndicators) {
                        EmptyView().id(topId)

                        makeContent()
                            .padding(
                                .top,
                                isRefreshShowing ? refreshingHeight : 0
                            )
                            .anchorPreference(key: OffsetPreferenceKey.self, value: .top) {
                                geometry[$0].y
                            }
                            .trackOffsetIfNeeded(trackOffset: trackOffset, globalFrame: globalFrame)
                    }
                    .onPreferenceChange(OffsetPreferenceKey.self) { offset in
                        guard isRefreshEnabled?.value != false && !isRefreshShowing else {
                            fractionPulled = nil
                            return
                        }

                        fractionPulled = (offset / refreshOffsetThreshold).clamped(to: 0...1)

                        if let fractionCompleted = fractionPulled, fractionCompleted >= 1 {
                            self.fractionPulled = nil
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            isRefreshShowing = true

                            Task { @MainActor in
                                await refreshAction()
                                withAnimation {
                                    isRefreshShowing = false
                                }
                            }
                        }
                    }
                    .getFrame(in: .global) {
                        globalFrame = $0
                    }
                }
            }
        }
    }
}

private struct PullToRefreshIndicator: View {

    /// Although this view doesn't mutate this value, we pass it as a binding to fix an iOS 17 issue caausing the scrollview to reset whenever this value changes.
    @Binding var fractionPulled: Double?

    let refreshingHeight: CGFloat
    let isRefreshShowing: Bool

    var body: some View {
        ProgressView(value: fractionPulled)
            .progressViewStyle(.pullArrow)
            .frame(height: refreshingHeight, alignment: .center)
            .opacity(fractionPulled != nil || isRefreshShowing ? 1 : 0)
    }

}

private struct PullArrowProgressViewStyle: ProgressViewStyle {
    private struct Constants {
        /// This prevents a a flood of "ignoring singular matrix" console warnings when scale is 0.
        static let minimumScale: Double = 0.01
    }

    @Environment(\.refreshableScrollViewTintColor) private var tintColor

    func makeBody(configuration: Configuration) -> some View {
        var scale: Double = Constants.minimumScale

        if let fractionCompleted = configuration.fractionCompleted {
            scale = fractionCompleted * 1.75
        }

        let progressViewStyle: CircularProgressViewStyle
        if let tintColor = tintColor {
            progressViewStyle = CircularProgressViewStyle(tint: tintColor)
        } else {
            progressViewStyle = .circular
        }

        return ZStack {
            Image("action_button_arrow_down_dark")
                .renderingMode(.template)
                .foregroundColor(tintColor ?? .chalk.dark500)
                .scaleEffect(max(scale, Constants.minimumScale), anchor: .center)
                .opacity(configuration.fractionCompleted ?? 0)

            ProgressView(configuration)
                .progressViewStyle(progressViewStyle)
                .opacity(configuration.fractionCompleted == nil ? 1 : 0)
        }
    }
}

private struct RefreshableScrollViewTintColorKey: EnvironmentKey {
    static let defaultValue: Color? = nil
}

extension EnvironmentValues {
    var refreshableScrollViewTintColor: Color? {
        get { self[RefreshableScrollViewTintColorKey.self] }
        set { self[RefreshableScrollViewTintColorKey.self] = newValue }
    }
}

extension View {

    @ViewBuilder
    fileprivate func trackOffsetIfNeeded(
        trackOffset: ((CGPoint) -> Void)?,
        globalFrame: CGRect
    ) -> some View {
        if let trackOffset = trackOffset {
            getScrollViewOffset { contentPosition in
                let offset = CGPoint(
                    x: -(contentPosition.x - globalFrame.minX),
                    y: -(contentPosition.y - globalFrame.minY)
                )
                trackOffset(offset)
            }
        } else {
            self
        }
    }

    public func refreshableScrollViewTintColor(_ value: Color?) -> some View {
        environment(
            \.refreshableScrollViewTintColor,
            value ?? RefreshableScrollViewTintColorKey.defaultValue
        )
    }
}

extension ProgressViewStyle where Self == PullArrowProgressViewStyle {
    static var pullArrow: PullArrowProgressViewStyle { .init() }
}

struct OffsetPreferenceKey: PreferenceKey {
    static var defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}

struct RefreshableScrollView_Previews: PreviewProvider {

    static var numberFormatter: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.numberStyle = .spellOut
        return formatter
    }()

    static var previews: some View {

        let isRefreshEnabled = CurrentValueSubject<Bool, Never>(true)

        RefreshableScrollView(
            isRefreshEnabled: isRefreshEnabled,
            refreshAction: {
                try? await Task.sleep(nanoseconds: 2_000_000_000)
            }
        ) {
            VStack(alignment: .leading) {
                let _ = DuplicateIDLogger.logDuplicates(
                    in: Array(1...100),
                    id: \.self
                )
                ForEach((1...100), id: \.self) {
                    Text(numberFormatter.string(from: NSNumber(value: $0))!)
                        .padding(.vertical, 4)
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
            .background(Color.blue)
        }
        .background(Color.pink)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
