//
//  BaseballBasesDiamond.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 10/6/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticUI
import Foundation
import SwiftUI

public struct BaseballBasesDiamond: View {
    public struct Highlighting: OptionSet, Hashable {
        public static let right = Highlighting(rawValue: 1 << 1)
        public static let middle = Highlighting(rawValue: 1 << 2)
        public static let left = Highlighting(rawValue: 1 << 3)

        public static let none: Highlighting = []

        public let rawValue: Int8

        public init(rawValue: Int8) {
            self.rawValue = rawValue
        }
    }

    let highlighting: Highlighting
    let baseSize: CGFloat
    var animationProperties = (isAnimated: false, duration: 0.6)

    public init(
        highlighting: Highlighting,
        baseSize: CGFloat,
        animationProperties: (isAnimated: Bool, duration: Double) = (
            isAnimated: false, duration: 0.6
        )
    ) {
        self.highlighting = highlighting
        self.baseSize = baseSize
        self.animationProperties = animationProperties
    }

    public var body: some View {
        VStack(spacing: 2) {
            HStack(spacing: 2) {
                BaseballBaseSquare(
                    size: baseSize,
                    isHighlighted: highlighting.contains(.middle),
                    rotationAngle: .degrees(-90),
                    animationProperties: animationProperties
                )
                BaseballBaseSquare(
                    size: baseSize,
                    isHighlighted: highlighting.contains(.right),
                    rotationAngle: .degrees(0),
                    animationProperties: animationProperties
                )
            }
            HStack(spacing: 2) {
                BaseballBaseSquare(
                    size: baseSize,
                    isHighlighted: highlighting.contains(.left),
                    rotationAngle: .degrees(180),
                    animationProperties: animationProperties
                )
                BaseballBaseSquare(
                    size: baseSize,
                    isHighlighted: false,
                    animationProperties: animationProperties
                )
                .opacity(0)
            }
        }
        .rotationEffect(.degrees(45))
        .offset(y: rotatedLengthDifference / 2)
        .padding(.horizontal, rotatedLengthDifference / 2)
    }

    private var sideLength: CGFloat {
        baseSize * 2 + 2
    }

    private var diagonalLength: CGFloat {
        sqrt(2) * sideLength
    }

    private var rotatedLengthDifference: CGFloat {
        diagonalLength - sideLength
    }
}

extension BaseballBasesDiamond.Highlighting {
    public init(endingBases: [Int]) {
        self = endingBases.reduce(into: .none) { optionSet, base in
            optionSet.insert(BaseballBasesDiamond.Highlighting(rawValue: 1 << base))
        }
    }
}

private struct BaseballBaseSquare: View {
    let size: CGFloat
    let isHighlighted: Bool
    var rotationAngle: Angle = .degrees(0)
    let animationProperties: (isAnimated: Bool, duration: Double)

    var body: some View {
        HStack {
            Rectangle()
                .fill(Color.chalk.dark400)
                .frame(width: size, height: size)
                .overlay(alignment: .bottom) {
                    Rectangle()
                        .fill(isHighlighted ? Color.chalk.red : Color.chalk.dark400)
                        .frame(width: size, height: isHighlighted ? size : 0)
                        .if(animationProperties.isAnimated) {
                            $0.animation(
                                .linear(duration: animationProperties.duration),
                                value: isHighlighted
                            )
                        }

                }
                .rotationEffect(isHighlighted ? rotationAngle : rotationAngle + .degrees(90))
        }
        /// Parent View may have .animation modifier on the entire BaseballBasesDiamond View.
        /// However, We do not want animation on the rotationEffect modifier
        .animation(.none, value: isHighlighted)
    }
}
