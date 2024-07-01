//
//  EnvironmentSettingsList.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 12/15/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloNetworking
import AthleticFoundation
import AthleticRestNetwork
import SwiftUI

struct EnvironmentSettingsList: View {
    var body: some View {
        VStack {
            Text("Changing the environment requires app restart.")
                .fontStyle(.calibreUtility.xs.regular)
            List {
                GraphSettingsSection()
                RestSettingsSection()
                CompassSettingsSection()
            }
            .listStyle(.insetGrouped)
            .navigationTitle("Environment")
            .navigationBarDefaultBackgroundColor()
            .navigationBarTitleDisplayMode(.inline)
            .fontStyle(.calibreUtility.l.regular)
            .foregroundColor(.chalk.dark800)

        }
    }
}

private struct GraphSettingsSection: View {
    @Preference(\.adminGraphEnvironment) private var graphEnvironment
    @State private var customEnvironment: String = ""
    @FocusState private var isCustomInputFocused: Bool

    private var isCustomEnvironmentValid: Bool {
        /// Ensure we have a valid-ish looking host. If this value is malformed, the app will
        /// crash on next launch. Criteria is at least three components, each of which is one
        /// character or more and does not contain spaces.

        customEnvironment
            .components(separatedBy: ".")
            .map({ $0.isEmpty || $0.firstIndex(of: " ") != nil ? 0 : 1 })
            .reduce(0, +) == 3
    }

    var body: some View {
        Section {
            let _ = DuplicateIDLogger.logDuplicates(
                in: AthleticApolloNetwork.EndpointBase.allCases,
                id: \.self
            )
            ForEach(AthleticApolloNetwork.EndpointBase.allCases, id: \.self) { endpointBase in
                Button(
                    action: {
                        graphEnvironment = endpointBase
                    }
                ) {
                    HStack {
                        Text(endpointBase.description)
                            .contentShape(Rectangle())
                        Spacer()
                        Image(systemName: "checkmark")
                            .opacity(graphEnvironment == endpointBase ? 1 : 0)
                    }
                }
            }

            HStack {
                TextField("custom-env.theathletic.com", text: $customEnvironment)
                    .autocorrectionDisabled(true)
                    .autocapitalization(.none)
                    .keyboardType(.URL)
                    .foregroundColor(
                        isCustomEnvironmentValid ? .chalk.dark800 : .chalk.red
                    )
                    .focused($isCustomInputFocused)

                Spacer()

                Image(systemName: "checkmark")
                    .opacity(graphEnvironment == .custom(customEnvironment) ? 1 : 0)
            }
        } header: {
            Text("GraphQL")
        }
        .onAppear {
            if case .custom(let value) = graphEnvironment {
                customEnvironment = value
            }
        }
        .onChange(of: customEnvironment) { environment in
            if isCustomEnvironmentValid {
                graphEnvironment = .custom(environment)
            }
        }
        .onChange(of: graphEnvironment) { environment in
            if !graphEnvironment.isCustom {
                isCustomInputFocused = false
                customEnvironment = ""
            }
        }
    }
}

private struct RestSettingsSection: View {
    @Preference(\.adminRestEnvironment) private var restEnvironment
    @State private var customEnvironment: String = ""
    @FocusState private var isCustomInputFocused: Bool

    private var isCustomEnvironmentValid: Bool {
        /// Ensure we have a valid-ish looking host. If this value is malformed, the app will
        /// crash on next launch. Criteria is at least three components, each of which is one
        /// character or more and does not contain spaces.

        customEnvironment
            .components(separatedBy: ".")
            .map({ $0.isEmpty || $0.firstIndex(of: " ") != nil ? 0 : 1 })
            .reduce(0, +) == 3
    }

    var body: some View {
        Section {
            let _ = DuplicateIDLogger.logDuplicates(
                in: AthleticRestNetwork.NetworkConfiguration.Environment.allCases,
                id: \.self
            )
            ForEach(AthleticRestNetwork.NetworkConfiguration.Environment.allCases, id: \.self) {
                endpointBase in
                Button(
                    action: {
                        restEnvironment = endpointBase
                    }
                ) {
                    HStack {
                        Text(endpointBase.description)
                            .contentShape(Rectangle())
                        Spacer()
                        Image(systemName: "checkmark")
                            .opacity(restEnvironment == endpointBase ? 1 : 0)
                    }
                }
            }

            HStack {
                TextField("custom-env.theathletic.com", text: $customEnvironment)
                    .autocorrectionDisabled(true)
                    .autocapitalization(.none)
                    .keyboardType(.URL)
                    .foregroundColor(
                        isCustomEnvironmentValid ? .chalk.dark800 : .chalk.red
                    )
                    .focused($isCustomInputFocused)

                Spacer()

                Image(systemName: "checkmark")
                    .opacity(restEnvironment == .custom(customEnvironment) ? 1 : 0)
            }
        } header: {
            Text("REST")
        }
        .onAppear {
            if case .custom(let value) = restEnvironment {
                customEnvironment = value
            }
        }
        .onChange(of: customEnvironment) { environment in
            if isCustomEnvironmentValid {
                restEnvironment = .custom(environment)
            }
        }
        .onChange(of: restEnvironment) { environment in
            if !restEnvironment.isCustom {
                isCustomInputFocused = false
                customEnvironment = ""
            }
        }
    }
}

private struct CompassSettingsSection: View {
    @EnvironmentObject private var compass: Compass

    var body: some View {
        Section {
            Toggle(isOn: UserDefaults.$adminEnableCompassStaging) {
                Text("Compass Staging")
            }

            Button(
                action: {
                    compass.removeAllData()
                    Task {
                        try? await compass.fetchDB()
                    }
                }
            ) {
                Text("Clear Compass configuration")
                    .foregroundColor(.chalk.blue)
            }
        } header: {
            Text("Compass")
        }
    }
}

struct EnvironmentSettingsList_Previews: PreviewProvider {
    static var previews: some View {
        EnvironmentSettingsList()
    }
}
