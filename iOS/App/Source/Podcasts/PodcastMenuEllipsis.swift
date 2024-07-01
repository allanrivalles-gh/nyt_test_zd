//
//  Ellipsis.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 2/10/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticNavigation
import AthleticUI
import SwiftUI

struct PodcastMenuEllipsis: View {

    let episodeModel: PodcastEpisodeViewModel
    let context: PodcastEpisodeActionsProvider.Context
    var foregroundColor: Color = .chalk.dark800
    let analyticsSource: PodcastEpisodeActionsProvider.AnalyticsSource?
    @Binding private(set) var isPaywallPresented: Bool

    init(
        episodeModel: PodcastEpisodeViewModel,
        context: PodcastEpisodeActionsProvider.Context,
        foregroundColor: Color = .chalk.dark800,
        isPaywallPresented: Binding<Bool>,
        analyticsSource: PodcastEpisodeActionsProvider.AnalyticsSource? = nil
    ) {
        self.episodeModel = episodeModel
        self.context = context
        self.foregroundColor = foregroundColor
        self._isPaywallPresented = isPaywallPresented
        self.analyticsSource = analyticsSource
    }

    var body: some View {
        Ellipsis()
            .foregroundColor(foregroundColor)
            .padding(6)
            .podcastMenu(
                context: context,
                model: episodeModel,
                isPaywallPresented: $isPaywallPresented,
                analyticsSource: analyticsSource
            )
    }
}
