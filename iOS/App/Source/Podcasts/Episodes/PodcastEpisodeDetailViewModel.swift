//
//  PodcastEpisodeDetailViewModel.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 4/6/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticUI
import Foundation
import UIKit

final class PodcastEpisodeDetailViewModel: ObservableObject, Hashable, Codable {
    typealias LinkTapAction = (DeepLinkType?) -> Void

    let episode: PodcastEpisodeViewModel
    let fromElement: AnalyticsEvent.Element?
    private(set) var onTap: LinkTapAction? = nil

    init(
        episode: PodcastEpisodeViewModel,
        fromElement: AnalyticsEvent.Element? = nil,
        onTap: LinkTapAction? = nil
    ) {
        self.episode = episode
        self.fromElement = fromElement
        self.onTap = onTap
    }

    lazy var descriptionStyles: AttributedLabelView.HtmlContentStyles = {
        let style = AthleticFont.Style.calibreUtility.l.regular
        let color = UIColor.chalk.dark800
        let linkColor = UIColor.systemBlue

        return (
            [
                AttributedLabelView.paragraphStyle(
                    font: .font(for: style),
                    paragraphStyleColor: color,
                    lineHeightMultiple: 1.25
                ),
                AttributedLabelView.linkStyle(color: linkColor),
            ],
            AttributedLabelView.linkStyle(color: linkColor),
            AttributedLabelView.allStyle(
                font: .font(for: style),
                color: color,
                textAlignment: .left
            )
        )
    }()

    enum CodingKeys: CodingKey {
        case episode
        case fromElement
    }

    public func hash(into hasher: inout Hasher) {
        hasher.combine(episode.episodeId)
        hasher.combine(episode.podcastId)
    }

    static func == (lhs: PodcastEpisodeDetailViewModel, rhs: PodcastEpisodeDetailViewModel) -> Bool
    {
        lhs.episode.episodeId == rhs.episode.episodeId
            && lhs.episode.podcastId == rhs.episode.podcastId
    }

}

extension PodcastEpisodeDetailViewModel: AttributedLabelViewDelegate {
    func handleLinkTap(
        url: URL,
        analyticsIdentifier: AttributedLabelView.AnalyticsIdentifier? = nil
    ) {
        let deepLinkType: DeepLinkType? = DeeplinkModel.parse(url: url)
        onTap?(deepLinkType)
    }
}
