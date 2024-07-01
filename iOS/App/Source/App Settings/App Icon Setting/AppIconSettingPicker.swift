//
//  AppIconSettingPicker.swift
//  AppIconSettingPicker
//
//  Created by Kyle Browning on 8/20/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticUI
import SwiftUI

struct AppIconSettingPicker: View {
    @Binding var selection: AppIconSetting?
    @Binding var settings: [AppIconSetting]
    @Binding var nhlSettings: LeagueAppIconSettings
    @Binding var nflSettings: LeagueAppIconSettings
    @Binding var wnbaSettings: LeagueAppIconSettings
    @Binding var nbaSettings: LeagueAppIconSettings
    @Binding var mlbSettings: LeagueAppIconSettings
    @Binding var mlsSettings: LeagueAppIconSettings

    var body: some View {
        List {
            if let selection = selection {
                Section(header: Text("Selection")) {
                    AppIconSettingRow(setting: selection, selectedIcon: self.$selection)
                }
            }

            Section(header: Text(Strings.theAthletic.localized)) {
                let _ = DuplicateIDLogger.logDuplicates(in: settings)
                ForEach(settings) { setting in
                    AppIconSettingRow(setting: setting, selectedIcon: $selection)
                }
            }

            Section(header: Text(nflSettings.title)) {
                let _ = DuplicateIDLogger.logDuplicates(in: nflSettings.appIcons)
                ForEach(nflSettings.appIcons) { setting in
                    AppIconSettingRow(setting: setting, selectedIcon: $selection)
                }
            }

            Section(header: Text(nhlSettings.title)) {
                let _ = DuplicateIDLogger.logDuplicates(in: nhlSettings.appIcons)
                ForEach(nhlSettings.appIcons) { setting in
                    AppIconSettingRow(setting: setting, selectedIcon: $selection)
                }
            }

            Section(header: Text(mlbSettings.title)) {
                let _ = DuplicateIDLogger.logDuplicates(in: mlbSettings.appIcons)
                ForEach(mlbSettings.appIcons) { setting in
                    AppIconSettingRow(setting: setting, selectedIcon: $selection)
                }
            }

            Section(header: Text(nbaSettings.title)) {
                let _ = DuplicateIDLogger.logDuplicates(in: nbaSettings.appIcons)
                ForEach(nbaSettings.appIcons) { setting in
                    AppIconSettingRow(setting: setting, selectedIcon: $selection)
                }
            }

            Section(header: Text(wnbaSettings.title)) {
                let _ = DuplicateIDLogger.logDuplicates(in: wnbaSettings.appIcons)
                ForEach(wnbaSettings.appIcons) { setting in
                    AppIconSettingRow(setting: setting, selectedIcon: $selection)
                }
            }

            Section(header: Text(mlsSettings.title)) {
                let _ = DuplicateIDLogger.logDuplicates(in: mlsSettings.appIcons)
                ForEach(mlsSettings.appIcons) { setting in
                    AppIconSettingRow(setting: setting, selectedIcon: $selection)
                }
            }
        }
        .listStyle(InsetGroupedListStyle())
        .navigationBarDefaultBackgroundColor()
        .onAppear {
            Analytics.track(
                event: .init(
                    verb: .view,
                    view: .appIconSetting
                )
            )
        }
    }
}
