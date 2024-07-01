//
//  SwiftUIView.swift
//
//
//  Created by Mark Corbyn on 20/7/2022.
//

import Combine
import SwiftUI

public struct LoadingDots: View {

    private let dotSize: CGFloat
    private let dotColor: Color
    private let duration: TimeInterval
    private let timer: AnyPublisher<Date, Never>

    public init(
        dotSize: CGFloat = 5,
        dotColor: Color = .chalk.dark800,
        duration: TimeInterval = 1.6
    ) {
        self.dotSize = dotSize
        self.dotColor = dotColor
        self.duration = duration
        self.timer = Timer.publish(every: duration, on: .main, in: .common)
            .autoconnect()
            .eraseToAnyPublisher()
    }

    public var body: some View {
        ZStack {
            GeometryReader { geometry in
                let singleDotAnimationDuration = duration * 5 / 8
                LoadingDot(
                    size: dotSize,
                    color: dotColor,
                    duration: singleDotAnimationDuration,
                    delay: 0,
                    containerWidth: geometry.size.width,
                    timer: timer
                )
                LoadingDot(
                    size: dotSize,
                    color: dotColor,
                    duration: singleDotAnimationDuration,
                    delay: duration * 1 / 8,
                    containerWidth: geometry.size.width,
                    timer: timer
                )
                LoadingDot(
                    size: dotSize,
                    color: dotColor,
                    duration: singleDotAnimationDuration,
                    delay: duration * 2 / 8,
                    containerWidth: geometry.size.width,
                    timer: timer
                )
            }
        }
        .frame(height: dotSize)
    }

}

private struct LoadingDot: View {
    let size: CGFloat
    let color: Color
    let duration: TimeInterval
    let delay: Double
    let containerWidth: CGFloat
    let timer: AnyPublisher<Date, Never>

    init(
        size: CGFloat,
        color: Color,
        duration: TimeInterval,
        delay: Double,
        containerWidth: CGFloat,
        timer: AnyPublisher<Date, Never>
    ) {
        self.size = size
        self.color = color
        self.duration = duration
        self.delay = delay
        self.containerWidth = containerWidth
        self.timer = timer

        _destinationOffset = State(wrappedValue: 0)
        _sourceOffset = State(wrappedValue: max(0, containerWidth - size))
    }

    @State private var sourceOffset: CGFloat
    @State private var destinationOffset: CGFloat

    var body: some View {
        Circle()
            .fill(color)
            .frame(width: size, height: size)
            .offset(x: destinationOffset)
            .opacity(0.7)
            .animation(
                .easeInOut(duration: duration).delay(delay),
                value: destinationOffset
            )
            .task {
                swap(&destinationOffset, &sourceOffset)
            }
            .onReceive(timer) { _ in
                swap(&destinationOffset, &sourceOffset)
            }
    }
}

struct LoadingDots_Previews: PreviewProvider {

    static var previews: some View {
        VStack(spacing: 16) {
            example(title: "Default") {
                LoadingDots()
            }
            example(title: "Size 5, red, 60pt width") {
                LoadingDots(dotSize: 5, dotColor: .chalk.red)
                    .frame(width: 60)
            }
            example(title: "Size 16, brightGreen, 150pt width") {
                LoadingDots(dotSize: 16, dotColor: .chalk.green)
                    .frame(width: 150)
            }
            example(title: "Fast") {
                LoadingDots(duration: 0.4)
                    .frame(width: 150)
            }
            example(title: "Slow") {
                LoadingDots(duration: 4)
                    .frame(width: 150)
            }

            Spacer()
        }
        .padding()
        .previewLayout(.fixed(width: 320, height: 320))
        .loadCustomFonts()
    }

    @ViewBuilder
    private static func example<Content: View>(
        title: String,
        @ViewBuilder _ loading: () -> Content
    ) -> some View {
        VStack(spacing: 2) {
            Text(title)
                .fontStyle(.calibreUtility.l.medium)
                .frame(maxWidth: .infinity, alignment: .leading)
            loading()
        }
    }
}
