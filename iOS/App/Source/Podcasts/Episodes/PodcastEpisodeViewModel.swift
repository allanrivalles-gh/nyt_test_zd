//
//  PodcastEpisodeViewModel.swift
//  theathletic-ios
//
//  Created by Jan Remes on 18/02/2019.
//  Copyright © 2019 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticNavigation
import Foundation

struct PodcastEpisodePlaybackState: Codable {
    let episodeId: String
    let timeElapsed: TimeInterval
    let markedAsPlayed: Bool
}

extension PodcastEpisodePlaybackState: StorageObject {
    var storageIdentifier: String {
        episodeId
    }
}

final class PodcastEpisodeViewModel: ObservableObject {

    enum AnalyticsSource: Equatable, Codable {
        case boxScoreLatestNews
        case podcastFollowingFeed
    }

    let title: String
    let duration: TimeInterval
    let episodeId: String
    let episodeDescription: String
    let date: Date
    let imageUrl: URL?
    let mp3Url: URL?
    let isTeaser: Bool
    let disableComments: Bool
    let commentCount: Int
    let permalinkUrl: URL?
    let clips: [PodcastClip]
    let podcastId: String
    let podcastTitle: String
    let followingEntity: FollowingEntity?
    let analyticsSource: AnalyticsSource?
    let analyticsSourceGameId: String?
    let analyticsSourceView: AnalyticsEvent.View?
    let feedAnalytics: FeedSectionAnalyticsConfiguration?
    private let sampleClipsLength: Int = 2

    @Published private var isFinished: Bool
    @Published private var serverTimeElapsed: TimeInterval
    @Published var markedAsPlayed: Bool = false
    @Published var isPlaying: Bool = false

    var previewClips: [PodcastClip] {
        clips.prefixArray(sampleClipsLength)
    }

    var hiddenClips: [PodcastClip] {
        guard clips.count > sampleClipsLength else { return [] }

        return clips.suffixArray(clips.count - sampleClipsLength)
    }

    var timeElapsed: TimeInterval {
        max(serverTimeElapsed, storageTimeElapsed)
    }

    var timeRemaining: TimeInterval {
        max(duration - timeElapsed, 0)
    }

    var listenedProgress: Float {
        Float(timeElapsed / duration)
    }

    var hasFinished: Bool {
        PodcastTime.isNearEnd(remaining: timeRemaining) && !isPlaying
    }

    var progressDescription: String {
        if hasFinished || markedAsPlayed {
            return Strings.played.localized
        } else if timeElapsed > 0 {
            return String(
                format: Strings.timeLeft.localized,
                Date.secondsToHoursAndMinutesLong(
                    timeRemaining.toInt()
                )
            )
        } else {
            return Date.secondsToHoursAndMinutesLong(
                duration.toInt()
            )
        }
    }

    var commentDescription: String {
        let comments = Strings.comments.localized.uppercased()

        guard commentCount > 0 else {
            return comments
        }
        if commentCount > 1 {
            return "\(commentCount) \(comments)"
        } else {
            return "\(commentCount) \(Strings.comment.localized.uppercased())"
        }
    }

    var dateDisplayString: String {
        date.podcastDateDisplayString
    }

    var teamHubDisplayDateString: String {
        Date.podcastEpisodeDateFormatter.string(from: date)
    }

    var hasPlayed: Bool {
        hasFinished || markedAsPlayed
    }

    var navigationDestination: AthleticScreen {
        return .listen(.podcastEpisode(podcastEpisodeDto, focusedCommentId: nil))
    }

    var podcastEpisodeDto: PodcastEpisode {
        let mappedClips = clips.map { dtoClip in
            AthleticNavigation.PodcastClip(
                id: dtoClip.id,
                title: dtoClip.title,
                startPosition: dtoClip.startPosition,
                endPosition: dtoClip.endPosition
            )
        }

        return PodcastEpisode(
            title: title,
            duration: duration,
            episodeId: episodeId,
            episodeDescription: episodeDescription,
            date: date,
            imageUrl: imageUrl,
            mp3Url: mp3Url,
            isTeaser: isTeaser,
            disableComments: disableComments,
            commentCount: commentCount,
            permalinkUrl: permalinkUrl,
            clips: mappedClips,
            isFinished: isFinished,
            serverTimeElapsed: serverTimeElapsed,
            markedAsPlayed: markedAsPlayed,
            series: PodcastEpisode.Series(
                id: podcastId,
                title: podcastTitle
            )
        )
    }

    private var storageTimeElapsed: TimeInterval {
        PodcastEpisodePlaybackState.retrieveFromStorage(with: episodeId)?.timeElapsed ?? 0
    }

    private var storageMarkedAsPlayed: Bool {
        PodcastEpisodePlaybackState.retrieveFromStorage(
            with: episodeId
        )?.markedAsPlayed ?? false
    }

    private var isPlayedState: Bool {
        markedAsPlayed || hasFinished
    }

    func playingDescription(progress: TimeInterval) -> String {
        return progress > 0 && !isPlayedState
            ? String(
                format: Strings.timeLeft.localized,
                Date.secondsToHoursAndMinutesLong(
                    max(duration - progress, 0).toInt()
                )
            )
            : progressDescription
    }

    func shortPlayingDescription(progress: TimeInterval) -> String {
        if isPlayedState {
            return Strings.played.localized
        }

        var timeRemaining: Int?

        if progress > 0 {
            timeRemaining = max(duration - progress, 0).toInt()
        } else if timeElapsed > 0 {
            timeRemaining = self.timeRemaining.toInt()
        }

        if let timeRemaining {
            return
                "-\(Date.secondsToHoursMinutesAndSecondsShort(timeRemaining, alwaysShowHours: false))"
        }

        return Date.secondsToHoursAndMinutesLong(duration.toInt())
    }

    func playingProgress(progress: TimeInterval) -> Float {
        return progress > 0
            ? Float((progress / duration))
            : listenedProgress
    }

    func invalidatePlayedState() {
        markedAsPlayed = storageMarkedAsPlayed
    }

    func showExpandClipsButton(clip: PodcastClip) -> Bool {
        previewClips.count - 1 == clips.firstIndex(of: clip)
            && !hiddenClips.isEmpty
    }

    init(
        episode: GQL.BoxScorePodcastEpisodeBlock,
        analyticsSourceGameId: String,
        analyticsSourceView: AnalyticsEvent.View
    ) {
        episodeId = episode.episodeId
        episodeDescription = episode.description ?? ""
        title = episode.title
        duration = Double(episode.duration ?? 0)
        date = episode.publishedAt
        imageUrl = URL(string: episode.imageUrl)
        mp3Url = URL(string: episode.mp3Url)
        isTeaser = false
        disableComments = episode.disableComments
        commentCount = episode.commentCount
        permalinkUrl = URL(string: episode.permalink)
        isFinished = episode.userData?.isFinished ?? false
        serverTimeElapsed = TimeInterval(episode.userData?.timeElapsed ?? 0)
        clips =
            episode.clips?.compactMap {
                PodcastClip(clip: $0.fragments.boxScorePodcastEpisodeClip)
            } ?? []
        podcastId = episode.podcastId
        podcastTitle = episode.podcastTitle ?? ""
        followingEntity = nil
        analyticsSource = .boxScoreLatestNews
        self.analyticsSourceGameId = analyticsSourceGameId
        self.analyticsSourceView = analyticsSourceView
        self.feedAnalytics = nil
        markedAsPlayed = storageMarkedAsPlayed
    }

    init(episode: GQL.PodcastEpisodeDetail) {
        episodeId = episode.id
        episodeDescription = episode.description
        title = episode.title
        duration = Double(episode.duration)
        date = episode.publishedAt
        imageUrl = URL(string: episode.imageUri)
        mp3Url = URL(string: episode.mp3Uri)
        isTeaser = episode.isTeaser
        disableComments = episode.disableComments
        commentCount = episode.commentCount
        permalinkUrl = URL(string: episode.permalink)
        isFinished = episode.finished ?? false
        serverTimeElapsed = TimeInterval(episode.timeElapsed ?? 0)
        clips = []
        podcastId = episode.parentPodcast?.fragments.podcastDetail.id ?? ""
        podcastTitle = episode.parentPodcast?.fragments.podcastDetail.title ?? ""
        followingEntity = nil
        analyticsSource = nil
        analyticsSourceGameId = nil
        analyticsSourceView = nil
        self.feedAnalytics = nil
        markedAsPlayed = storageMarkedAsPlayed
    }

    init(
        episode: GQL.PodcastEpisodeConsumable,
        followingEntity: FollowingEntity? = nil,
        analyticsSource: AnalyticsSource? = nil,
        analyticsSourceView: AnalyticsEvent.View? = nil,
        feedAnalytics: FeedSectionAnalyticsConfiguration? = nil
    ) {
        episodeId = episode.podcastEpisodeId
        episodeDescription = episode.description ?? ""
        title = episode.title
        duration = Double(episode.duration ?? 0)
        disableComments = episode.disableComments
        commentCount = episode.podcastCommentCount
        date = episode.publishedAt
        imageUrl = URL(string: episode.imageUrl)
        mp3Url = URL(string: episode.mp3Url)
        isTeaser = false
        permalinkUrl = URL(string: episode.permalink)
        serverTimeElapsed = 0
        isFinished = episode.finished ?? false
        clips = episode.clips?.compactMap { PodcastClip(clip: $0) } ?? []
        podcastId = episode.parentPodcast?.fragments.podcastDetail.id ?? ""
        podcastTitle = episode.parentPodcast?.fragments.podcastDetail.title ?? ""
        self.followingEntity = followingEntity
        self.analyticsSource = analyticsSource
        analyticsSourceGameId = nil
        self.analyticsSourceView = analyticsSourceView
        self.feedAnalytics = feedAnalytics
        markedAsPlayed = storageMarkedAsPlayed
    }

    convenience init(
        episode: GQL.PodcastEpisodeConsumable,
        followingEntity: FollowingEntity? = nil,
        feedAnalytics: FeedSectionAnalyticsConfiguration
    ) {
        self.init(
            episode: episode,
            followingEntity: followingEntity,
            analyticsSourceView: feedAnalytics.sourceView,
            feedAnalytics: feedAnalytics
        )
    }

    /// DTO initializer
    init(episode: PodcastEpisode) {
        episodeId = episode.episodeId
        episodeDescription = episode.episodeDescription
        title = episode.title
        duration = episode.duration
        date = episode.date
        imageUrl = episode.imageUrl
        mp3Url = episode.mp3Url
        isTeaser = episode.isTeaser
        disableComments = episode.disableComments
        commentCount = episode.commentCount
        permalinkUrl = episode.permalinkUrl
        isFinished = episode.isFinished
        serverTimeElapsed = episode.serverTimeElapsed
        markedAsPlayed = episode.markedAsPlayed
        clips = episode.clips.map { dtoClip in
            PodcastClip(
                id: dtoClip.id,
                title: dtoClip.title,
                startPosition: dtoClip.startPosition,
                endPosition: dtoClip.endPosition
            )
        }
        podcastId = episode.series.id
        podcastTitle = episode.series.title
        followingEntity = nil
        analyticsSource = nil
        analyticsSourceGameId = nil
        analyticsSourceView = nil
        feedAnalytics = nil
    }

    enum CodingKeys: String, CodingKey {
        case episodeId, episodeDescription, podcast, title, duration,
            date, imageUrl, mp3Url, isTeaser, disableComments, commentCount, permalinkUrl,
            isFinished,
            serverTimeElapsed, clips, podcastId, podcastTitle, followingEntity, markedAsPlayed,
            analyticsSource, analyticsSourceGameId, analyticsSourceView
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        episodeId = try container.decode(String.self, forKey: .episodeId)
        episodeDescription = try container.decode(String.self, forKey: .episodeDescription)
        title = try container.decode(String.self, forKey: .title)
        duration = try container.decode(TimeInterval.self, forKey: .duration)
        date = try container.decode(Date.self, forKey: .date)
        imageUrl = try container.decodeIfPresent(URL.self, forKey: .imageUrl)
        mp3Url = try container.decodeIfPresent(URL.self, forKey: .mp3Url)
        isTeaser = try container.decode(Bool.self, forKey: .isTeaser)
        disableComments =
            try container.decodeIfPresent(Bool.self, forKey: .disableComments) ?? false
        commentCount = try container.decode(Int.self, forKey: .commentCount)
        permalinkUrl = try container.decodeIfPresent(URL.self, forKey: .permalinkUrl)
        isFinished = try container.decode(Bool.self, forKey: .isFinished)
        serverTimeElapsed = try container.decode(TimeInterval.self, forKey: .serverTimeElapsed)
        clips = try container.decode([PodcastClip].self, forKey: .clips)
        feedAnalytics = nil

        /// Note: In older client versions, we encode/decode PodcastSeries instead of PodcastId and PodcastTitle. We need to
        /// cater for these older versions as viewModels was saved/loaded using StorageObject.
        if let podcastSeries = try container.decodeIfPresent(
            PodcastSeries.self,
            forKey: .podcast
        ) {
            podcastId = podcastSeries.id
            podcastTitle = podcastSeries.title
        } else {
            podcastId = try container.decode(String.self, forKey: .podcastId)
            podcastTitle = try container.decode(String.self, forKey: .podcastTitle)
        }

        followingEntity = try container.decodeIfPresent(
            FollowingEntity.self,
            forKey: .followingEntity
        )

        analyticsSource = try container.decodeIfPresent(
            AnalyticsSource.self,
            forKey: .analyticsSource
        )

        analyticsSourceGameId = try container.decodeIfPresent(
            String.self,
            forKey: .analyticsSourceGameId
        )

        analyticsSourceView = try container.decodeIfPresent(
            AnalyticsEvent.View.self,
            forKey: .analyticsSourceView
        )

        markedAsPlayed = try container.decode(Bool.self, forKey: .markedAsPlayed)
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(episodeId, forKey: .episodeId)
        try container.encode(episodeDescription, forKey: .episodeDescription)
        try container.encode(title, forKey: .title)
        try container.encode(duration, forKey: .duration)
        try container.encode(date, forKey: .date)
        try container.encodeIfPresent(imageUrl, forKey: .imageUrl)
        try container.encodeIfPresent(mp3Url, forKey: .mp3Url)
        try container.encode(isTeaser, forKey: .isTeaser)
        try container.encode(disableComments, forKey: .disableComments)
        try container.encode(commentCount, forKey: .commentCount)
        try container.encodeIfPresent(permalinkUrl, forKey: .permalinkUrl)
        try container.encode(isFinished, forKey: .isFinished)
        try container.encode(serverTimeElapsed, forKey: .serverTimeElapsed)
        try container.encode(clips, forKey: .clips)
        try container.encode(podcastId, forKey: .podcastId)
        try container.encode(podcastTitle, forKey: .podcastTitle)
        try container.encodeIfPresent(followingEntity, forKey: .followingEntity)
        try container.encodeIfPresent(analyticsSource, forKey: .analyticsSource)
        try container.encodeIfPresent(analyticsSourceGameId, forKey: .analyticsSourceGameId)
        try container.encodeIfPresent(analyticsSourceView, forKey: .analyticsSourceView)
        try container.encode(markedAsPlayed, forKey: .markedAsPlayed)
    }
}

// MARK: - Analytics

extension PodcastEpisodeViewModel: Analytical {
    var analyticData: AnalyticData {
        if let feedAnalytics {
            return AnalyticData(
                config: feedAnalytics,
                objectIdentifier: episodeId,
                eventTypes: [.impress]
            )
        }
        guard let analyticsSource, let analyticsSourceView else {
            return AnalyticData()
        }

        if analyticsSource == .boxScoreLatestNews {
            return AnalyticData(
                click: AnalyticsEventRecord(
                    verb: .click,
                    view: analyticsSourceView,
                    element: .latestNews,
                    objectType: .podcastEpisodeId,
                    objectIdentifier: episodeId,
                    metaBlob: analyticsMetaBlob
                )
            )
        } else {
            return AnalyticData()
        }
    }

    var impressionManager: AnalyticImpressionManager? {
        feedAnalytics?.impressionManager
    }

    var analyticsMetaBlob: AnalyticsEvent.MetaBlob? {
        switch analyticsSource {
        case .boxScoreLatestNews:
            return AnalyticsEvent.MetaBlob(
                gameId: analyticsSourceGameId
            )
        case .podcastFollowingFeed:
            guard let followingEntity else { return nil }
            return AnalyticsEvent.MetaBlob(
                leagueId: followingEntity.metaBlobLeagueId,
                teamId: followingEntity.teamId,
                podcastId: podcastId,
                parentObjectType: .teamId
            )
        case .none:
            return nil
        }
    }

    var podcastPlayAnalyticsMetaBlob: AnalyticsEvent.MetaBlob? {
        switch analyticsSource {
        case .boxScoreLatestNews:
            return AnalyticsEvent.MetaBlob(
                gameId: analyticsSourceGameId,
                podcastEpisodeId: episodeId
            )
        case .podcastFollowingFeed:
            guard let followingEntity else { return nil }
            return AnalyticsEvent.MetaBlob(
                leagueId: followingEntity.metaBlobLeagueId,
                teamId: followingEntity.teamId,
                podcastId: podcastId,
                parentObjectType: .teamId
            )
        case .none:
            return nil
        }
    }

    func trackActionProviderClick(
        eventManager: AnalyticEventManager = AnalyticsManagers.events
    ) async {
        guard let analyticsSource, let analyticsSourceView else {
            return
        }

        let element: AnalyticsEvent.Element
        switch analyticsSource {
        case .boxScoreLatestNews:
            element = .latestNews
        case .podcastFollowingFeed:
            element = .podcastEpisode
        }

        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: analyticsSourceView,
                element: element,
                objectType: .podcastEpisodeId,
                objectIdentifier: episodeId,
                metaBlob: analyticsMetaBlob
            ),
            manager: eventManager
        )
    }

    func trackProgressBarClick(
        eventManager: AnalyticEventManager = AnalyticsManagers.events
    ) async {
        guard let analyticsSource, let analyticsSourceView else {
            return
        }

        let element: AnalyticsEvent.Element
        let objectType: AnalyticsEvent.ObjectType
        let objectIdentifier: String?
        let metaBlob: AnalyticsEvent.MetaBlob?

        switch analyticsSource {
        case .boxScoreLatestNews:
            element = .latestNews
            objectType = .progress
            objectIdentifier = nil
            metaBlob = AnalyticsEvent.MetaBlob(
                gameId: analyticsSourceGameId,
                podcastEpisodeId: episodeId
            )
        case .podcastFollowingFeed:
            element = .progress
            objectType = .podcastEpisodeId
            objectIdentifier = episodeId
            metaBlob = analyticsMetaBlob
        }

        await Analytics.track(
            event: AnalyticsEventRecord(
                verb: .click,
                view: analyticsSourceView,
                element: element,
                objectType: objectType,
                objectIdentifier: objectIdentifier,
                metaBlob: metaBlob
            ),
            manager: eventManager
        )
    }
}

// MARK: - StorageObject
extension PodcastEpisodeViewModel: StorageObject {
    var storageIdentifier: String {
        episodeId
    }
}

// MARK: - Identifiable

extension PodcastEpisodeViewModel: Identifiable {
    var id: String {
        episodeId
    }
}

extension PodcastEpisodeViewModel: Equatable {
    static func == (lhs: PodcastEpisodeViewModel, rhs: PodcastEpisodeViewModel) -> Bool {
        return lhs.episodeId == rhs.episodeId
            && lhs.episodeDescription == rhs.episodeDescription
            && lhs.title == rhs.title
            && lhs.date == rhs.date
            && lhs.imageUrl == rhs.imageUrl
            && lhs.mp3Url == rhs.mp3Url
            && lhs.isTeaser == rhs.isTeaser
            && lhs.disableComments == rhs.disableComments
            && lhs.commentCount == rhs.commentCount
            && lhs.permalinkUrl == rhs.permalinkUrl
            && lhs.isFinished == rhs.isFinished
            && lhs.serverTimeElapsed == rhs.serverTimeElapsed
            && lhs.clips == rhs.clips
            && lhs.podcastId == rhs.podcastId
            && lhs.podcastTitle == rhs.podcastTitle
            && lhs.markedAsPlayed == rhs.markedAsPlayed
            && lhs.analyticsSource == rhs.analyticsSource
            && lhs.analyticsSourceGameId == rhs.analyticsSourceGameId
            && lhs.analyticsSourceView == rhs.analyticsSourceView
    }
}

extension PodcastEpisodeViewModel: Hashable {
    func hash(into hasher: inout Hasher) {
        hasher.combine(episodeId)
        hasher.combine(serverTimeElapsed)
        hasher.combine(markedAsPlayed)
    }
}

// MARK: - Shareable

extension PodcastEpisodeViewModel: Shareable {
    var shareTitle: String? {
        title
    }
}

extension Date {
    fileprivate var podcastDateDisplayString: String {
        guard !isDistantPast else { return "" }

        let timeSettings = SystemTimeSettings()

        if isSame(timeSettings.now(), granularity: .day) {
            return Strings.feedToday.localized
        } else if timeIntervalSince(Date()).magnitude < 7.days {
            return Date.longDayFormatter.string(from: self)
        } else if isSame(timeSettings.now(), granularity: .year) {
            return Date.monthDayNoCommaFormatter.string(from: self)
        } else {
            return Date.monthDayYearFormatter.string(from: self)
        }
    }
}

struct PodcastClip: Codable, Equatable, Identifiable, Hashable {

    init(
        id: Int,
        title: String? = nil,
        startPosition: Int? = nil,
        endPosition: Int? = nil
    ) {
        self.id = id
        self.title = title
        self.startPosition = startPosition
        self.endPosition = endPosition
    }

    let id: Int
    let title: String?
    let startPosition: Int?
    let endPosition: Int?

    var duration: String? {
        guard let startPosition, let endPosition else { return nil }

        return Date.secondsToHoursAndMinutesLong(
            endPosition - startPosition
        )
    }

    var startPositionFormatted: String? {
        guard let startPosition else { return nil }

        return Date.secondsToHoursMinutesAndSecondsShort(startPosition)
    }

    init(clip: GQL.PodcastEpisodeConsumable.Clip) {
        id = clip.id
        title = clip.title
        startPosition = clip.startPosition
        endPosition = clip.endPosition
    }

    init(clip: GQL.BoxScorePodcastEpisodeClip) {
        id = clip.id
        title = clip.title
        startPosition = clip.startPosition
        endPosition = clip.endPosition
    }

    func isPlayingClip(position: Int) -> Bool {
        guard let startPosition, let endPosition else { return false }

        return position >= startPosition && position <= endPosition
    }
}

enum PodcastTime {
    static func isNearEnd(remaining: TimeInterval) -> Bool {
        remaining < 45.seconds
    }
}
