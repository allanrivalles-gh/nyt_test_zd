//
//  BracketTab.swift
//  theathletic-ios
//
//  Created by Jason Xu on 10/29/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticUI
import Foundation
import SwiftUI

final public class BracketTab: PagingTab, Hashable {

    public enum BracketRound: Int {
        case round1 = 1
        case round2 = 2
        case round3 = 3
        case round4 = 4
        case round5 = 5
        case round6 = 6
        case round7 = 7
    }

    public let id: String
    public let title: String
    public let bracketRound: BracketRound?
    var isLive: Bool
    public var shouldShowBadge: Bool {
        get {
            isLive
        }
        set {}
    }

    init(id: String, title: String, bracketRound: BracketRound? = nil, isLive: Bool = false) {
        self.title = title.uppercased()
        self.isLive = isLive
        self.bracketRound = bracketRound
        self.id = id
    }

    public var badge: some View {
        RedCircleBadge()
    }

    public static func == (lhs: BracketTab, rhs: BracketTab) -> Bool {
        lhs.id == rhs.id
    }

    public func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
}
