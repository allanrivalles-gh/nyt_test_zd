//
//  PodcastPlayView.swift
//  theathletic-ios
//
//  Created by Charles Huang on 1/27/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticNavigation
import SwiftUI

struct PodcastPlayView: View {
    enum PodcastPlayViewStyle {
        case large
        case regular
    }

    @EnvironmentObject private var listenModel: ListenModel
    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var navigationRestoration: NavigationRestorationController

    let model: PodcastEpisodeViewModel
    let style: PodcastPlayViewStyle
    @Binding private(set) var isPaywallPresented: Bool

    var body: some View {
        Button(
            action: {
                if model.isTeaser || entitlement.hasAccessToContent {
                    Task {
                        await listenModel.handlePlayAction(
                            model: model,
                            analyticsView: .podcastEpisode,
                            navigationRestorationController: navigationRestoration
                        )
                    }
                } else {
                    isPaywallPresented.toggle()
                }
            },
            label: {
                ZStack {
                    switch style {
                    case .large:
                        Circle()
                            .strokeBorder(
                                Color.chalk.dark800,
                                lineWidth: 1
                            )
                            .frame(width: 50, height: 50)
                    case .regular:
                        Circle()
                            .fill(Color.chalk.dark300)
                            .frame(width: 28, height: 28)
                    }

                    PlayImage(model: model, style: style)
                        .environmentObject(listenModel.audioPlayerManager)
                }
            }
        )
        .buttonStyle(.borderless)
    }
}

private struct PlayImage: View {
    @EnvironmentObject private var audioPlayerManager: AudioPlayerManager
    let model: PodcastEpisodeViewModel
    let style: PodcastPlayView.PodcastPlayViewStyle

    var body: some View {
        if audioPlayerManager.playerState == .playing,
            audioPlayerManager.currentItem?.itemId == model.episodeId
        {
            Image(systemName: "pause.fill")
                .resizable()
                .foregroundColor(.chalk.dark700)
                .frame(
                    width: style == .large ? 14 : 7,
                    height: style == .large ? 14 : 7
                )
        } else {
            Image(systemName: "play.fill")
                .resizable()
                .foregroundColor(.chalk.dark700)
                .frame(
                    width: style == .large ? 16 : 8,
                    height: style == .large ? 16 : 8
                )
                .padding(.leading, style == .large ? 4 : 2)
        }
    }
}
