//
//  LiveBlogContainerView.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 5/3/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticNavigation
import AthleticUI
import SwiftUI

struct LiveBlogContainerView: View {

    @EnvironmentObject private var compass: Compass
    @EnvironmentObject private var navigationModel: NavigationModel
    @Environment(\.colorScheme) private var colorScheme
    @StateObject private var navigationCoordinator = ContentWebViewNavigationCoordinator()
    @StateObject private var viewModel: LiveBlogContainerViewModel

    init(
        liveBlogId: String,
        postId: String?,
        permalink: String? = nil,
        permalinkForEmbed: String? = nil,
        gameId: String? = nil
    ) {
        self._viewModel = StateObject(
            wrappedValue: LiveBlogContainerViewModel(
                liveBlogId: liveBlogId,
                postId: postId,
                permalink: permalink,
                permalinkForEmbed: permalinkForEmbed,
                gameId: gameId
            )
        )
    }

    var body: some View {
        Group {
            if let gameId = viewModel.gameId {
                GameDetail(
                    viewModel: GameScreenViewModelFactory.viewModel(
                        fromId: gameId,
                        initialTabSelectionOverride: .liveBlog
                    )
                )
            } else {
                if let permalinkForEmbed = viewModel.permalinkForEmbed {
                    InAppWebView(
                        viewModel: WebviewViewModel(
                            type: .webViewTest(permalinkForEmbed),
                            navigationModel: navigationModel,
                            navigatedExternalUrl: $navigationCoordinator.externalUrl,
                            isRefreshable: true,
                            isThemeAware: true,
                            adType: .liveBlog
                        )
                    )
                    .edgesIgnoringSafeArea(.bottom)
                    .safariView(url: $navigationCoordinator.externalUrl)
                    .toolbar(
                        content: {
                            ToolbarItemGroup(placement: .navigationBarTrailing) {
                                if let shareItem = viewModel.shareItem {
                                    ShareLink(item: shareItem.url, message: shareItem.titleText) {
                                        ShareIcon()
                                    }
                                }
                            }
                        }
                    )
                    .tabBarHidden()
                } else {
                    ProgressView().progressViewStyle(.athletic)
                }
            }
        }
        .task {
            await viewModel.onAppear()
        }
    }
}

struct LiveBlogContainerView_Previews: PreviewProvider {
    static var previews: some View {
        LiveBlogContainerView(liveBlogId: "1", postId: nil)
    }
}
