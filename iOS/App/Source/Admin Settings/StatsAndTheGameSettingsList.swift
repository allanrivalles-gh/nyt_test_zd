//
//  StatsAndTheGameSettingsList.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/15/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

// MARK: TODO - Convert to Feature Flags
struct StatsAndTheGameSettingsList: View {
    var body: some View {
        List {
            Toggle(isOn: UserDefaults.$adminEnableSubscriptionForAllGames) {
                Text("All Games Subscribe for Updates")
            }

            Toggle(isOn: UserDefaults.$adminEnableTeamThreads) {
                Text("Team Threads")
            }
        }
        .fontStyle(.calibreUtility.l.regular)
        .foregroundColor(.chalk.dark800)
        .navigationTitle("Stats & the Game")
        .navigationBarDefaultBackgroundColor()
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct StatsAndTheGameSettingsList_Previews: PreviewProvider {
    static var previews: some View {
        StatsAndTheGameSettingsList()
    }
}
