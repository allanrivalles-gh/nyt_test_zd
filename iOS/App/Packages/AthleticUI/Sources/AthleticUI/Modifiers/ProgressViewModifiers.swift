//
//  AthleticProgressStyleVIew.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/20/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI

public struct AthleticProgressViewStyle: ProgressViewStyle {
    public func makeBody(configuration: Configuration) -> some View {
        ZStack {
            RoundedRectangle(cornerRadius: 8)
                .foregroundColor(.chalk.dark300)
            VStack(spacing: 8) {
                ProgressView()
                    .progressViewStyle(.circular)
            }
            .padding()
            .foregroundColor(.chalk.dark800)
        }
        .fixedSize()
    }
}

public struct AthleticProgressViewStyleFailed: ProgressViewStyle {
    public func makeBody(configuration: Configuration) -> some View {
        VStack(spacing: 8) {
            Image(systemName: "exclamationmark.circle.fill")
                .resizable()
                .foregroundStyle(.white, Color.chalk.red)
                .frame(width: 45, height: 45)
            configuration.label
                .fontStyle(.calibreUtility.l.regular)
                .multilineTextAlignment(.center)
                .frame(maxWidth: 200)
                .fixedSize(horizontal: false, vertical: true)
        }
        .padding()
        .foregroundColor(.chalk.dark800)
        .background(
            RoundedRectangle(cornerRadius: 8)
                .foregroundColor(.chalk.dark300)
        )
    }
}

public struct ButtonProgressViewStyle: ProgressViewStyle {
    let isLoading: Bool

    public func makeBody(configuration: Configuration) -> some View {
        ProgressView()
            .opacity(isLoading ? 1 : 0)
            .progressViewStyle(
                CircularProgressViewStyle(
                    tint: .chalk.dark800
                )
            )
    }
}

public struct ArticleProgressViewStyle: ProgressViewStyle {
    public func makeBody(configuration: Configuration) -> some View {
        ProgressView(configuration)
            .accentColor(.chalk.blue)
    }
}

extension ProgressViewStyle where Self == AthleticProgressViewStyle {
    public static var athletic: AthleticProgressViewStyle { .init() }
    public static var athleticFailed: AthleticProgressViewStyleFailed { .init() }
    public static var article: ArticleProgressViewStyle { .init() }

    public static func inButton(isLoading: Bool) -> ButtonProgressViewStyle {
        ButtonProgressViewStyle(isLoading: isLoading)
    }
}
