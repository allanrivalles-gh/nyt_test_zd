//
//  LoadingState.swift
//
//
//  Created by Duncan Lau on 14/6/2023.
//

import Foundation

public enum LoadingState: Equatable {
    case initial
    case loading(isInteractive: Bool = false, showPlaceholders: Bool = false)
    case loaded
    case failed

    public var isLoading: Bool {
        switch self {
        case .initial, .loaded, .failed:
            return false
        case .loading:
            return true
        }
    }
}
