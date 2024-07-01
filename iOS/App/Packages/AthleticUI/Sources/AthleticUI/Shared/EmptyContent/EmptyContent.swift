//
//  EmptyContent.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 16/12/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation
import SwiftUI

public struct EmptyContent: View {
    public let state: LoadingState
    public var backgroundColor: Color
    public var errorMessage: String
    public var showProgressViewOnLoading: Bool
    public var reloadButtonActionOnFailed: (() async -> Void)?

    public init(
        state: LoadingState,
        backgroundColor: Color = .chalk.dark100,
        errorMessage: String,
        showProgressViewOnLoading: Bool = true,
        reloadButtonActionOnFailed: (() async -> Void)? = nil
    ) {
        self.state = state
        self.backgroundColor = backgroundColor
        self.errorMessage = errorMessage
        self.showProgressViewOnLoading = showProgressViewOnLoading
        self.reloadButtonActionOnFailed = reloadButtonActionOnFailed
    }

    public var body: some View {
        switch state {
        case .loading:
            if showProgressViewOnLoading {
                ProgressView()
                    .progressViewStyle(.athletic)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(backgroundColor)
            } else {
                EmptyView()
            }
        case .failed:
            VStack(spacing: 24) {
                Text(errorMessage)
                    .multilineTextAlignment(.center)
                    .fontStyle(.calibreUtility.xl.medium)
                    .foregroundColor(.chalk.dark800)
                if let reloadButtonActionOnFailed {
                    Button(
                        action: {
                            Task {
                                await reloadButtonActionOnFailed()
                            }
                        },
                        label: {
                            Text(Strings.reload.localized)
                        }
                    )
                    .buttonStyle(.core(size: .fitted, level: .primary))
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(backgroundColor)

        case .initial, .loaded:
            EmptyView()
        }
    }
}

struct EmptyContent_Previews: PreviewProvider {
    @ViewBuilder
    static var previews: some View {
        EmptyContent(
            state: .loading(),
            backgroundColor: .chalk.dark100,
            errorMessage: "Generic Error"
        )
        EmptyContent(
            state: .failed,
            backgroundColor: .chalk.dark100,
            errorMessage: "Failed"
        ) {}
    }
}
