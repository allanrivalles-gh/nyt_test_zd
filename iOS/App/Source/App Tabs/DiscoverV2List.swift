//
//  DiscoverV2List.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 4/25/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticNavigation
import AthleticUI
import SwiftUI

struct DiscoverV2List: View {
    @EnvironmentObject private var navigationModel: NavigationModel

    var body: some View {
        DiscoverV2ListContent(navigationModel: navigationModel)
    }
}

private struct DiscoverV2ListContent: View {
    @StateObject private var feedViewModel: FeedV2ViewModel
    @State private var isSearchActive = false

    init(navigationModel: NavigationModel) {
        _feedViewModel = StateObject(
            wrappedValue: FeedV2ViewModel(
                id: "discover",
                network: AppEnvironment.shared.network,
                requestConfiguration: .makeDiscover(),
                navigationModel: navigationModel
            )
        )
    }

    var body: some View {
        FeedV2ListView(viewModel: feedViewModel)
            .navigationTitle(Strings.discover.localized)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text(Strings.theAthletic.localized)
                        .fontStyle(.slab.m.bold)
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    NavigationLink(screen: .feed(.search)) {
                        Image("icon_search")
                            .resizable()
                            .renderingMode(.template)
                            .foregroundColor(.chalk.dark800)
                            .padding(6)
                            .frame(width: 32, height: 32)
                            .background(Color.chalk.dark300)
                            .clipShape(Circle())
                    }
                }
            }
            .navigationBarDefaultBackgroundColor()
            .onReceive(
                NotificationCenter.default.publisher(for: .UserContentEditionDidChange)
                    .receive(on: RunLoop.main)
            ) { _ in
                Task {
                    await feedViewModel.reloadContent(isInteractive: true)
                }
            }
    }
}

struct DiscoverV2List_Previews: PreviewProvider {
    static var previews: some View {
        DiscoverV2List()
    }
}
