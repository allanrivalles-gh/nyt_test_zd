//
//  UserDefaults+AdminSettings.swift
//
//
//  Created by Mark Corbyn on 7/2/2023.
//

import AthleticFoundation
import Foundation
import SwiftUI

extension UserDefaults {

    // MARK: Admin Settings

    @AppStorage("adminEnableNetworkConsoleLogging")
    public static var adminEnableNetworkConsoleLogging: Bool = false

    @AppStorage("adminEnableNetworkDiskLogging")
    public static var adminEnableNetworkDiskLogging: Bool = false

    @AppStorage("adminEnableDiagnosticModalView")
    public static var adminEnableDiagnosticModalView: Bool = false

    @AppStorage("adminEnableResponseStubbing")
    public static var adminEnableResponseStubbing: Bool = false

}
