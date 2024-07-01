//
//  AthleticSlider.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 1/27/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

/// This is based off of the custom slider implementation at
/// https://betterprogramming.pub/reusable-components-in-swiftui-custom-sliders-8c115914b856, but
/// adapted and simplified to fit our use case. This exists because SwiftUI currently does not
/// allow us to fully customize the `Slider` control to match the appearance of the slider on our
/// podcast player.

struct PodcastPlayerSlider: View {
    typealias SliderRange = (min: Double, max: Double)

    @Binding var value: Double
    @Binding var dragValue: Double
    @Binding var isDragging: Bool
    var range: SliderRange
    var onChange: (Double) -> Void

    private let knobWidth: CGFloat = 10

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                let frame = geometry.frame(in: .global)
                let offsetX = getOffsetX(frame: frame)
                let knobSize = CGSize(width: knobWidth, height: frame.height)

                let barLeftSize =
                    CGSize(width: CGFloat(offsetX + knobSize.width * 0.5), height: frame.height)

                let barRightSize =
                    CGSize(width: frame.width - barLeftSize.width, height: frame.height)

                let modifiers =
                    PodcastPlayerSliderComponents(
                        barLeft:
                            PodcastPlayerSliderModifier(
                                name: .barLeft,
                                size: barLeftSize,
                                offset: 0
                            ),
                        barRight:
                            PodcastPlayerSliderModifier(
                                name: .barRight,
                                size: barRightSize,
                                offset: barLeftSize.width
                            ),
                        knob: PodcastPlayerSliderModifier(
                            name: .knob,
                            size: knobSize,
                            offset: offsetX
                        )
                    )

                ZStack {
                    Color.chalk.dark800
                        .cornerRadius(2.5)
                        .frame(height: 5)
                        .modifier(modifiers.barLeft)

                    Color.chalk.dark300
                        .cornerRadius(2.5)
                        .frame(height: 5)
                        .modifier(modifiers.barRight)

                    Circle()
                        .fill(Color.chalk.dark800)
                        .modifier(modifiers.knob)
                }
                .onDragGesture(
                    minimumDistance: 0,
                    onChanged: {
                        isDragging = true
                        onDragChange($0, frame)
                    },
                    onEnded: { _ in
                        value = dragValue
                        onChange(dragValue)
                        isDragging = false
                    }
                )
            }
        }
    }

    private func onDragChange(_ drag: DragGesture.Value, _ frame: CGRect) {
        let width = (knob: Double(knobWidth), view: Double(frame.size.width))
        let xRange: SliderRange = (min: Double(0), max: Double(width.view - width.knob))

        /// knob center x
        var knobCenterX = Double(drag.startLocation.x + drag.translation.width)

        /// offset from center to leading edge of knob
        knobCenterX -= 0.5 * width.knob

        /// limit to leading edge
        knobCenterX = knobCenterX > xRange.max ? xRange.max : knobCenterX

        /// limit to trailing edge
        knobCenterX = knobCenterX < xRange.min ? xRange.min : knobCenterX

        knobCenterX = knobCenterX.convert(fromRange: (xRange.min, xRange.max), toRange: range)

        dragValue = knobCenterX
    }

    private func getOffsetX(frame: CGRect) -> CGFloat {
        let effectiveValue = isDragging ? dragValue : value
        let width = (knob: knobWidth, view: frame.size.width)
        let xRange: SliderRange = (min: 0, max: Double(width.view - width.knob))
        let result = effectiveValue.convert(fromRange: range, toRange: xRange)

        return CGFloat(result)
    }
}

private struct PodcastPlayerSliderComponents {
    let barLeft: PodcastPlayerSliderModifier
    let barRight: PodcastPlayerSliderModifier
    let knob: PodcastPlayerSliderModifier
}

private struct PodcastPlayerSliderModifier: ViewModifier {
    enum ComponentType {
        case barLeft
        case barRight
        case knob
    }

    let name: ComponentType
    let size: CGSize
    let offset: CGFloat

    func body(content: Content) -> some View {
        content
            .frame(width: size.width)
            .position(x: size.width * 0.5, y: size.height * 0.5)
            .offset(x: offset)
    }
}

extension Double {
    func convert(fromRange: (Double, Double), toRange: (Double, Double)) -> Double {
        var value = self
        value -= fromRange.0
        value /= Double(fromRange.1 - fromRange.0)
        value *= toRange.1 - toRange.0
        value += toRange.0

        return value
    }
}

struct PodcastPlayerSlider_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            PodcastPlayerSlider(
                value: .constant(40),
                dragValue: .constant(40),
                isDragging: .constant(false),
                range: (1, 100),
                onChange: { _ in }
            )
            .padding()
            .preferredColorScheme(.light)

            PodcastPlayerSlider(
                value: .constant(40),
                dragValue: .constant(40),
                isDragging: .constant(false),
                range: (1, 100),
                onChange: { _ in }
            )
            .padding()
            .preferredColorScheme(.dark)
        }
    }
}
