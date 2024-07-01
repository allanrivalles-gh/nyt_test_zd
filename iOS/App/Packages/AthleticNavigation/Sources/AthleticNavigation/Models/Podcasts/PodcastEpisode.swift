//
//  PodcastEpisode.swift
//
//
//  Created by Jason Leyrer on 6/6/23.
//

import Foundation

public struct PodcastEpisode: Codable, Hashable {
    public struct Series: Codable, Hashable {
        public let id: String
        public let title: String

        public init(id: String, title: String) {
            self.id = id
            self.title = title
        }
    }
    public let title: String
    public let duration: TimeInterval
    public let episodeId: String
    public let episodeDescription: String
    public let date: Date
    public let imageUrl: URL?
    public let mp3Url: URL?
    public let isTeaser: Bool
    public let disableComments: Bool
    public let commentCount: Int
    public let permalinkUrl: URL?
    public let clips: [PodcastClip]
    public let isFinished: Bool
    public let serverTimeElapsed: TimeInterval
    public let markedAsPlayed: Bool
    public let series: Series

    public init(
        title: String,
        duration: TimeInterval,
        episodeId: String,
        episodeDescription: String,
        date: Date,
        imageUrl: URL?,
        mp3Url: URL?,
        isTeaser: Bool,
        disableComments: Bool,
        commentCount: Int,
        permalinkUrl: URL?,
        clips: [PodcastClip],
        isFinished: Bool,
        serverTimeElapsed: TimeInterval,
        markedAsPlayed: Bool,
        series: Series
    ) {
        self.title = title
        self.duration = duration
        self.episodeId = episodeId
        self.episodeDescription = episodeDescription
        self.date = date
        self.imageUrl = imageUrl
        self.mp3Url = mp3Url
        self.isTeaser = isTeaser
        self.disableComments = disableComments
        self.commentCount = commentCount
        self.permalinkUrl = permalinkUrl
        self.clips = clips
        self.isFinished = isFinished
        self.serverTimeElapsed = serverTimeElapsed
        self.markedAsPlayed = markedAsPlayed
        self.series = series
    }
}
