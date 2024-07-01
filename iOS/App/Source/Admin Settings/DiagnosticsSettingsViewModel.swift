//
//  DiagnosticSettingsViewModel.swift
//  theathletic-ios
//
//  Created by Duncan Lau on 3/11/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import Foundation

class DiagnosticsSettingsViewModel: ObservableObject {
    @Published var adminEnableNetworkDiskLogging: Bool {
        didSet {
            UserDefaults.adminEnableNetworkDiskLogging = adminEnableNetworkDiskLogging
        }
    }

    @Published var adminEnableDiagnosticModalView: Bool {
        didSet {
            UserDefaults.adminEnableDiagnosticModalView = adminEnableDiagnosticModalView
        }
    }

    @Published var adminEnableNetworkConsoleLogging: Bool {
        didSet {
            UserDefaults.adminEnableNetworkConsoleLogging = adminEnableNetworkConsoleLogging
        }
    }

    init() {
        self.adminEnableNetworkDiskLogging = UserDefaults.adminEnableNetworkDiskLogging
        self.adminEnableDiagnosticModalView = UserDefaults.adminEnableDiagnosticModalView
        self.adminEnableNetworkConsoleLogging = UserDefaults.adminEnableNetworkConsoleLogging
    }
}
