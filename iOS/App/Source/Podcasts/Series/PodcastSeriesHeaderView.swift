//
//  PodcastSeriesHeaderView.swift
//  theathletic-ios
//
//  Created by Charles Huang on 1/31/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

struct PodcastSeriesHeaderView: View {
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject var viewModel: PodcastSeriesListViewModel

    var body: some View {
        SwiftUI.TabView {
            PodcastSeriesHeaderMainView(
                viewModel: viewModel
            )
            .colorScheme(colorScheme == .dark ? .dark : .light)
            PodcastSeriesHeaderDetailView(
                viewModel: viewModel
            )
            .colorScheme(colorScheme == .dark ? .dark : .light)
        }
        .tabViewStyle(PageTabViewStyle())
        .indexViewStyle(.page(backgroundDisplayMode: .always))
        .darkScheme()
    }
}
