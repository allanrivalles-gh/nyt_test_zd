//
//  EmptyContentWithTask.swift
//
//
//  Created by Jason Leyrer on 6/23/23.
//

import AthleticFoundation
import SwiftUI

public struct EmptyContentWithTask: View {
    public let backgroundColor: Color
    public let errorMessage: String
    public let showProgressViewOnLoading: Bool
    public let reloadButtonActionOnFailed: (() async -> Void)?
    public let task: () async throws -> Void

    @State private var loadingState: LoadingState = .loading()

    public init(
        backgroundColor: Color = .chalk.dark100,
        errorMessage: String,
        showProgressViewOnLoading: Bool = true,
        reloadButtonActionOnFailed: (() async -> Void)? = nil,
        task: @escaping () async throws -> Void
    ) {
        self.backgroundColor = backgroundColor
        self.errorMessage = errorMessage
        self.showProgressViewOnLoading = showProgressViewOnLoading
        self.reloadButtonActionOnFailed = reloadButtonActionOnFailed
        self.task = task
    }

    public var body: some View {
        EmptyContent(
            state: loadingState,
            backgroundColor: backgroundColor,
            errorMessage: errorMessage,
            showProgressViewOnLoading: showProgressViewOnLoading,
            reloadButtonActionOnFailed: reloadButtonActionOnFailed
        )
        .task {
            do {
                try await task()
                loadingState = .loaded
            } catch {
                loadingState = .failed
            }
        }
    }
}
