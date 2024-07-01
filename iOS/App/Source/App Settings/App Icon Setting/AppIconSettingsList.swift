//
//  AppSettingsList.swift
//  AppSettingsList
//
//  Created by Kyle Browning on 8/20/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Combine
import SwiftUI

struct AppIconSettingsList: View {
    @State private var selection: AppIconSetting? = AppIconSetting.getSelectedIcon()
    @State private var searchText = ""
    @State private var settings = AppIconSetting.iconSettings
    @State private var nhlSettings: LeagueAppIconSettings
    @State private var nflSettings: LeagueAppIconSettings
    @State private var wnbaSettings: LeagueAppIconSettings
    @State private var nbaSettings: LeagueAppIconSettings
    @State private var mlbSettings: LeagueAppIconSettings
    @State private var mlsSettings: LeagueAppIconSettings

    init() {
        nhlSettings = AppIconSetting.nhlIconSettings
        nflSettings = AppIconSetting.nflIconSettings
        nbaSettings = AppIconSetting.nbaIconSettings
        wnbaSettings = AppIconSetting.wnbaIconSettings
        mlsSettings = AppIconSetting.mlsIconSettings
        mlbSettings = AppIconSetting.mlbIconSettings
    }

    var body: some View {
        AppIconSettingPicker(
            selection: $selection,
            settings: $settings,
            nhlSettings: $nhlSettings,
            nflSettings: $nflSettings,
            wnbaSettings: $wnbaSettings,
            nbaSettings: $nbaSettings,
            mlbSettings: $mlbSettings,
            mlsSettings: $mlsSettings
        )
        .searchable(text: $searchText)
        .onChange(of: searchText) { searchText in

            if !searchText.isEmpty {
                /// hide The Athletic icon settings since they're probably
                /// not searching through that short of a list
                settings = []
                nhlSettings = LeagueAppIconSettings(
                    league: .nhl,
                    appIcons: nhlSettings
                        .appIcons.filter {
                            $0.title.localizedCaseInsensitiveContains(searchText)
                        }
                )
                nflSettings = LeagueAppIconSettings(
                    league: .nfl,
                    appIcons: nflSettings
                        .appIcons.filter {
                            $0.title.localizedCaseInsensitiveContains(searchText)
                        }
                )
                wnbaSettings = LeagueAppIconSettings(
                    league: .wnba,
                    appIcons: wnbaSettings
                        .appIcons.filter {
                            $0.title.localizedCaseInsensitiveContains(searchText)
                        }
                )

                nbaSettings = LeagueAppIconSettings(
                    league: .nba,
                    appIcons: nbaSettings
                        .appIcons.filter {
                            $0.title.localizedCaseInsensitiveContains(searchText)
                        }
                )

                mlbSettings = LeagueAppIconSettings(
                    league: .mlb,
                    appIcons: mlbSettings
                        .appIcons.filter {
                            $0.title.localizedCaseInsensitiveContains(searchText)
                        }
                )
                mlsSettings = LeagueAppIconSettings(
                    league: .mls,
                    appIcons: mlsSettings
                        .appIcons.filter {
                            $0.title.localizedCaseInsensitiveContains(searchText)
                        }
                )

            } else {
                settings = AppIconSetting.iconSettings
                nhlSettings = AppIconSetting.nhlIconSettings
                nflSettings = AppIconSetting.nflIconSettings
                nbaSettings = AppIconSetting.nbaIconSettings
                wnbaSettings = AppIconSetting.wnbaIconSettings
                mlsSettings = AppIconSetting.mlsIconSettings
                mlbSettings = AppIconSetting.mlbIconSettings
            }
        }
    }
}
