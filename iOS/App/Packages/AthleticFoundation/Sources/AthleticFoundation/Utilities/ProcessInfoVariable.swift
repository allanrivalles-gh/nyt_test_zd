//
//  ProcessInfoVariable.swift
//
//
//  Created by Kyle Browning on 5/12/22.
//

import Foundation

public enum ProcessInfoVariable: String {
    // clean read articles for free user
    case shouldCleanReadArticles = "SHOULD_CLEAN_READ_ARTICLES"
    case shouldClearKeychainSubscription = "SHOULD_CLEAR_KEYCHAIN_SUBSCRIPTION"
    case shouldShowJSONResponse = "SHOULD_SHOW_JSON"
    case verboseLogging = "VERBOSE_LOGGING"
}

extension ProcessInfo {
    public func isEnabled(_ variable: ProcessInfoVariable) -> Bool {
        guard let valueString = environment[variable.rawValue] else {
            return false
        }

        if let value = valueString.bool {
            return value
        } else {
            return false
        }
    }
}
