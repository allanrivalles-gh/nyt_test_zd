//
//  AnimatedLikeButton.swift
//  theathletic-ios
//
//  Created by Jason Xu on 9/14/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

public struct AnimatedLikeButton: View {

    @State private var likeImageScale: CGFloat = 1
    @State private var likeImageOpacity = 1.0
    @State private var likeImageDegrees = 0.0
    @State private var isCircleAnimating = false
    @State private var isCircle2Animating = false
    @State private var isLikeIconAnimating = false

    private let isLiked: Bool
    private let likesCount: Int
    private let textColor: Color?
    private let action: () -> Void

    private let secondCirclePulseDelay = 0.09
    private let circlePulseDuration = 0.35

    public init(
        isLiked: Bool,
        likesCount: Int,
        textColor: Color? = nil,
        action: @escaping () -> Void
    ) {
        self.isLiked = isLiked
        self.likesCount = likesCount
        self.textColor = textColor
        self.action = action
    }

    public var body: some View {
        Button(action: {
            action()
            if !isLiked {
                UIImpactFeedbackGenerator(style: .rigid).impactOccurred()

                // reset animations
                withAnimation(nil) {
                    isCircleAnimating = false
                    isCircle2Animating = false
                    likeImageScale = 0.001
                    likeImageOpacity = 0
                    likeImageDegrees = 25
                }
                withAnimation(
                    .easeOut(duration: 0.15)
                ) {
                    likeImageOpacity = 1
                    likeImageScale = 1
                }
                withAnimation(
                    .linear(duration: circlePulseDuration)
                ) {
                    isCircleAnimating = true
                }
                withAnimation(
                    .linear(duration: circlePulseDuration)
                        .delay(secondCirclePulseDelay)
                ) {
                    isCircle2Animating = true
                }
                withAnimation(
                    .easeOut(duration: 0.25)
                ) {
                    isLikeIconAnimating = true
                    likeImageDegrees = -20
                }
                withAnimation(
                    .linear(duration: 0.1)
                        .delay(0.25)
                ) {
                    isLikeIconAnimating = false
                    likeImageDegrees = 0
                }
            }
        }) {
            HStack(spacing: 4) {
                ZStack {
                    pulsingCircles
                    Image(
                        isLiked ? "icn_like_active" : "icn_like"
                    )
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 25, height: 25)
                    .rotationEffect(
                        .degrees(likeImageDegrees)
                    )
                    .scaleEffect(likeImageScale)
                    .opacity(likeImageOpacity)
                    .padding(.bottom, isLikeIconAnimating ? 10 : 0)
                }
                .frame(width: 25, height: 25)
                if likesCount > 0 {
                    Text(likesCount.string)
                        .fontStyle(.calibreUtility.s.regular)
                        .foregroundColor(textColor)
                }
            }
        }
    }

    private var pulsingCircles: some View {
        ZStack {
            Circle()
                .stroke(Color.chalk.dark800, style: StrokeStyle(lineWidth: 2))
                .opacity(isCircleAnimating ? 0 : 0.85)
                .frame(
                    width: isCircleAnimating ? 40 : 0,
                    height: isCircleAnimating ? 40 : 0
                )
            Circle()
                .stroke(Color.chalk.dark800, style: StrokeStyle(lineWidth: 2))
                .opacity(isCircle2Animating ? 0 : 0.85)
                .frame(
                    width: isCircle2Animating ? 40 : 0,
                    height: isCircle2Animating ? 40 : 0
                )
        }
        .padding(.bottom, 5)
    }
}
