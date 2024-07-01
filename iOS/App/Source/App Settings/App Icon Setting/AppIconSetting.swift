//
//  AppIconSetting.swift
//  AppIconSetting
//
//  Created by Kyle Browning on 8/20/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI

struct AppIconSetting: Identifiable, Hashable, Codable {

    static let original: AppIconSetting = .init(
        title: "Default",
        iconImageName: "icon-default",
        isDefault: true
    )

    static let iconSettings: [AppIconSetting] = [
        .original,
        .init(title: "Dark", iconImageName: "icon-dark-flat", isDefault: false),
        .init(title: "Light", iconImageName: "icon-light-flat", isDefault: false),
        .init(title: "Maroon", iconImageName: "icon-maroon", isDefault: false),
        .init(title: "Red", iconImageName: "icon-red", isDefault: false),
        .init(title: "Orange", iconImageName: "icon-orange", isDefault: false),
        .init(title: "Yellow", iconImageName: "icon-yellow", isDefault: false),
        .init(title: "Bright Green", iconImageName: "icon-bright-green", isDefault: false),
        .init(title: "Green", iconImageName: "icon-green", isDefault: false),
        .init(title: "Turquoise", iconImageName: "icon-turquoise", isDefault: false),
        .init(title: "Royal", iconImageName: "icon-royal", isDefault: false),
        .init(title: "Navy", iconImageName: "icon-navy", isDefault: false),
        .init(title: "Purple", iconImageName: "icon-purple", isDefault: false),

    ]
    static let nhlIconSettings = LeagueAppIconSettings(league: .nhl)
    static let mlbIconSettings = LeagueAppIconSettings(league: .mlb)
    static let nflIconSettings = LeagueAppIconSettings(league: .nfl)
    static let nbaIconSettings = LeagueAppIconSettings(league: .nba)
    static let wnbaIconSettings = LeagueAppIconSettings(league: .wnba)
    static let mlsIconSettings = LeagueAppIconSettings(league: .mls)

    var id: String {
        iconImageName
    }
    let title: String
    let iconImageName: String
    let isDefault: Bool
}

extension AppIconSetting {

    private static var allIcons: [AppIconSetting] {
        iconSettings
            + nhlIconSettings.appIcons
            + nflIconSettings.appIcons
            + wnbaIconSettings.appIcons
            + nbaIconSettings.appIcons
            + mlbIconSettings.appIcons
            + mlsIconSettings.appIcons
    }

    static func getSelectedIcon() -> AppIconSetting {
        if let alternateIconName = UIApplication.shared.alternateIconName,
            let setting = allIcons.first(where: { $0.iconImageName == alternateIconName })
        {
            return setting
        } else {
            return .original
        }
    }
}
