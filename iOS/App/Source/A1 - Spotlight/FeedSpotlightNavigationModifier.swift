//
//  FeedSpotlightNavigationModifier.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 4/20/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

struct FeedSpotlightNavigationModifier: ViewModifier {
    @Binding var item: SpotlightViewModel?
    @State private var isActive: Bool = false

    func body(content: Content) -> some View {
        content
            .background(
                ConditionalNavigationLink(object: $item, isActive: $isActive) { item in
                    ArticleDetail(viewModel: ArticleDetailViewModel(articleId: item.articleId))
                        .globalEnvironment()
                }
            )
    }
}
