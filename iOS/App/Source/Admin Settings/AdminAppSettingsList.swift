//
//  AdminAppSettingsList.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 12/15/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloNetworking
import Embrace
import SwiftUI

struct AdminAppSettingsList: View {
    var body: some View {
        List {
            Section("Developer") {
                Toggle(isOn: UserDefaults.$adminEnableTestFlightInAppPurchase) {
                    Text("In-App Purchases")
                }

                Toggle(isOn: UserDefaults.$adminIsFeedDebugEnabled) {
                    Text("Test Feed")
                }

                Toggle(isOn: UserDefaults.$adminEnableResponseStubbing) {
                    VStack(alignment: .leading) {
                        Text("Stub Network Responses")
                        Text(
                            "Place stubbed json files with the operation name in StubbedResponses.xcassets"
                        )
                        .fontStyle(.calibreUtility.s.regular)
                    }
                }
            }

            Section("Actions") {
                Button(
                    action: {
                        Task {
                            await AppEnvironment.shared.feedV2NetworkActor.resetForLogout()
                            UINotificationFeedbackGenerator().notificationOccurred(.success)
                        }
                    }
                ) {
                    Text("Reset Feed Fetch Times")
                        .foregroundColor(.chalk.blue)
                }

                Button(
                    action: {
                        AppEnvironment.shared.network.apolloClient.clearCache(
                            callbackQueue: .main,
                            completion: nil
                        )
                        UINotificationFeedbackGenerator().notificationOccurred(.success)
                    }
                ) {
                    Text("Clear Graph Cache")
                        .foregroundColor(.chalk.blue)
                }

                Button(
                    action: {
                        UINotificationFeedbackGenerator().notificationOccurred(.success)
                        Embrace.sharedInstance().crash()
                    }
                ) {
                    Text("Force App Crash")
                        .foregroundColor(.chalk.red)
                }
            }
        }
        .fontStyle(.calibreUtility.l.regular)
        .foregroundColor(.chalk.dark800)
        .navigationTitle("App")
        .navigationBarDefaultBackgroundColor()
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct AdminAppSettingsList_Previews: PreviewProvider {
    static var previews: some View {
        AdminAppSettingsList()
    }
}
