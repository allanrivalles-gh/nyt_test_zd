//
//  DiagnosticsSettingsView.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/15/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloNetworking
import AthleticNavigation
import AthleticUI
import SwiftUI

struct DiagnosticsSettingsView: View {
    @StateObject private var settings = DiagnosticsSettingsViewModel()

    var body: some View {
        List {
            Section("Network Logs") {
                navigationLinkFor(
                    url: ResponseDiskWriter.directory(for: Date()),
                    title: "Today"
                )
                navigationLinkFor(
                    url: ResponseDiskWriter.directory(for: Date().add(days: -1)),
                    title: "Yesterday"
                )
                navigationLinkFor(
                    url: nil,
                    title: "Browse Disk Logs"
                )
            }

            Section("Network Logging Settings") {
                Toggle(isOn: $settings.adminEnableNetworkDiskLogging) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Enable Network Logging")
                            .fontStyle(.calibreUtility.l.regular)
                        Text(
                            "Writes network requests & responses to disk for future debugging. Will incur a performance hit."
                        )
                        .fontStyle(.calibreUtility.xs.regular)
                        .foregroundColor(.chalk.dark500)
                    }
                }

                Toggle(isOn: $settings.adminEnableDiagnosticModalView) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Enable Diagnostic Pop up")
                            .fontStyle(.calibreUtility.l.regular)
                        Text(
                            "Shake device or use keyboard shortcut Ctrl-Cmd-Z to open diagnostic modal view.\n\nImportant: Once disabled, it can only be re-enabled via Admin Settings. Terminate and relaunch App to take effect."
                        )
                        .fontStyle(.calibreUtility.xs.regular)
                        .foregroundColor(.chalk.dark500)
                    }
                }
            }

            Section("Xcode Console Settings") {
                Toggle(isOn: $settings.adminEnableNetworkConsoleLogging) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Enable Xcode Console Logging")
                            .fontStyle(.calibreUtility.l.regular)
                        Text("Prints verbose network request/response logs to the Xcode console")
                            .fontStyle(.calibreUtility.xs.regular)
                            .foregroundColor(.chalk.dark500)
                    }
                }
            }

            Section {
                AdminSocketStateView()
            }
        }
        .listStyle(.insetGrouped)
        .navigationTitle("Diagnostics")
        .navigationBarDefaultBackgroundColor()
        .navigationBarTitleDisplayMode(.inline)
        .fontStyle(.calibreUtility.l.regular)
        .foregroundColor(.chalk.dark800)
    }

    @ViewBuilder
    private func navigationLinkFor(url: URL?, title: String) -> some View {
        NavigationLink(
            screen: .account(
                .adminSettings(
                    .diagnostics(
                        .filesList(url)
                    )
                )
            )
        ) {
            VStack(alignment: .leading) {
                Text(title)
                    .fontStyle(.calibreUtility.l.regular)
            }
        }
    }
}

struct DiagnosticsSettingsView_Previews: PreviewProvider {
    static var previews: some View {
        DiagnosticsSettingsView()
    }
}
