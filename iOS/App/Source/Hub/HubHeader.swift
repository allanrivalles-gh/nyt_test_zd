//
//  HubHeader.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 4/8/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

struct HubHeader: View {

    @ObservedObject var viewModel: HubViewModel.Header
    @ObservedObject var collapsibleState: HubCollapsibleHeaderState

    let loadingState: LoadingState
    let foregroundColor: Color
    let backgroundColor: Color

    @EnvironmentObject private var followingModel: FollowingModel

    var body: some View {
        VStack(spacing: 6) {
            Text(viewModel.title)
                .fontStyle(.slab.s.bold)
                .lineLimit(1)

            if let subtitle = viewModel.subtitle {
                SubtitleText(subtitle)
            } else if loadingState == .loading(showPlaceholders: true) {
                SubtitleText(" ")
                    .hidden()
                    .overlay(
                        /// Overlay the loading indicator on placeholder text so that the header layout is consistent
                        /// once the text is populated
                        LoadingDots(dotColor: foregroundColor)
                            .frame(width: 40)
                    )
                    .transition(.identity)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.bottom, 4)
        .foregroundColor(foregroundColor.opacity(collapsibleState.expandedLabelsOpacity))
        .background(backgroundColor.ignoresSafeArea(.all, edges: .top))
        .toolbar {
            ToolbarItem(placement: .principal) {
                ZStack {
                    TeamLogoLazyImage(size: 32, resources: viewModel.logos)
                        .opacity(collapsibleState.logoOpacity)

                    NavigationBarTitleText(viewModel.title)
                        .foregroundColor(foregroundColor)
                        .opacity(collapsibleState.navigationTitleOpacity)
                }
            }
        }
        .getSize { size in
            collapsibleState.naturalHeight = size.height
        }
        .frame(height: collapsibleState.expansionState == .collapsed ? 0 : nil)
    }
}

private struct SubtitleText: View {
    private let text: String

    init(_ text: String) {
        self.text = text
    }

    var body: some View {
        Text(text)
            .fontStyle(.calibreUtility.l.regular)
            .fixedSize()
    }
}

struct HubHeader_Previews: PreviewProvider {

    @State static var startingOffset: CGFloat = 0
    @State static var scrollOffset: CGFloat = 0

    static var previews: some View {
        NavigationStack {
            VStack(spacing: 0) {
                HubHeader(
                    viewModel: .init(
                        entity: .init(
                            legacyId: "110",
                            name: "Mets",
                            shortName: "Mets",
                            shortDisplayName: "Mets",
                            longDisplayName: "New York Mets",
                            color: "012f6c",
                            iconColor: "053d88",
                            type: .team,
                            imageUrl:
                                "https://cdn-team-logos.theathletic.com/team-logo-110-96x96.png".url
                        ),
                        following: AppEnvironment.shared.following
                    ),
                    collapsibleState: HubCollapsibleHeaderState(),
                    loadingState: .loading(showPlaceholders: true),
                    foregroundColor: .chalk.constant.gray800,
                    backgroundColor: Color(hex: "DD0000")
                )

                ScrollView {
                    Text("Below Header")
                }
            }
        }
    }
}
