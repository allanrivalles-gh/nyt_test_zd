//
//  PodcastSettings.swift
//  theathletic-ios
//
//  Created by Jan Remes on 20/10/2019.
//  Copyright © 2019 The Athletic. All rights reserved.
//

import Foundation

protocol SingleTitleItem {
    var title: String { get }
}

enum PodcastEpisodeDownloadType: Int, CaseIterable, SingleTitleItem {
    case streamOnly = 1
    case downloadOnWifi = 2
    case downloadOnWifiOrCelluar = 3

    var title: String {
        switch self {
        case .streamOnly:
            return Strings.podcastDownloadOff.localized
        case .downloadOnWifi:
            return Strings.podcastDownloadWifi.localized
        case .downloadOnWifiOrCelluar:
            return Strings.podcastDownloadWifiCellular.localized
        }
    }
}

enum PodcastEpisodeDeleteType: Int, CaseIterable, SingleTitleItem {
    case manually = 0
    case afterCompletion = 1

    var title: String {
        switch self {
        case .manually:
            return Strings.podcastsDeleteManually.localized
        case .afterCompletion:
            return Strings.podcastsDeleteAfterCompletion.localized
        }
    }
}

class PodcastSettings {
    var seekBackBy: Int {
        get {
            return UserDefaults.audioSeekBackBy
        }
        set(value) {
            UserDefaults.audioSeekBackBy = value
        }
    }
    var seekForwardBy: Int {
        get {
            return UserDefaults.audioSeekForwardBy
        }
        set(value) {
            UserDefaults.audioSeekForwardBy = value
        }
    }

    var newEpisodeDownloadType: PodcastEpisodeDownloadType {
        get {
            return PodcastEpisodeDownloadType(rawValue: UserDefaults.newEpisodeDownloadType)
                ?? .streamOnly
        }
        set(value) {

            switch value {
            case .streamOnly:
                UserDefaults.episodeDownloadTypeChangeDate = .distantPast
            case .downloadOnWifi, .downloadOnWifiOrCelluar:
                UserDefaults.episodeDownloadTypeChangeDate = Date()
            }

            UserDefaults.newEpisodeDownloadType = value.rawValue
        }
    }

    var episodeDeleteType: PodcastEpisodeDeleteType {
        get {
            return PodcastEpisodeDeleteType(rawValue: UserDefaults.episodeDeleteType) ?? .manually
        }
        set(value) {
            UserDefaults.episodeDeleteType = value.rawValue
        }
    }

}
