//
//  ContentStatusIndicators.swift
//
//
//  Created by Jason Leyrer on 7/10/23.
//

import SwiftUI

public struct LiveIndicator: View {
    public let showsBylineLiveIndicator: Bool
    public let textColor: Color

    public init(showsBylineLiveIndicator: Bool = false, textColor: Color = .chalk.red) {
        self.showsBylineLiveIndicator = showsBylineLiveIndicator
        self.textColor = textColor
    }

    public var body: some View {
        HStack(spacing: 6) {
            Image("live_indicator")
                .resizable()
                .frame(width: 6, height: 6)
            Text(Strings.live.localized)
                .fontStyle(
                    showsBylineLiveIndicator
                        ? .calibreUtility.xs.medium : .calibreUtility.s.medium
                )
                .foregroundColor(textColor)
        }
    }
}

public struct ReadCircleIndicator: View {
    public let dimension: CGFloat

    public init(dimension: CGFloat = 14) {
        self.dimension = dimension
    }

    public var body: some View {
        VStack {
            Image("article_check")
                .resizable()
                .frame(width: 6, height: 6)
                .scaledToFill()
        }
        .frame(width: dimension, height: dimension)
        .background(Color.chalk.constant.gray800)
        .shadow(color: .chalk.constant.gray800.opacity(0.15), radius: 4)
        .clipShape(Circle())
    }
}

public struct ReadPillIndicator: View {
    public init() {}

    public var body: some View {
        HStack(spacing: 5) {
            Image("article_check")
                .resizable()
                .frame(width: 7, height: 9)
            Text(Strings.read.localized)
                .fontStyle(.calibreUtility.xs.regular)
                .foregroundColor(.chalk.constant.gray400)
        }
        .padding(.horizontal, 8)
        .frame(height: 18)
        .shadow(color: .chalk.constant.gray800.opacity(0.15), radius: 4)
        .background(Color.chalk.constant.gray800)
        .clipShape(Capsule())
    }
}
