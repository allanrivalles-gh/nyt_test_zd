//
//  LiveScoreAttributes.swift
//  HeadlinesWidgetExtension
//
//  Created by Duncan Lau on 9/2/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import ActivityKit
import AthleticFoundation
import Foundation
import SwiftUI

@available(iOS 16.2, *)
struct LiveScoreAttributes: ActivityAttributes {

    static let rootDirectory = Global.Widget.LiveActivity.rootDirectory

    public struct ContentState: Codable, Hashable {
        var homeTeamScore: Int
        var awayTeamScore: Int
    }

    struct TeamInfo: Codable {
        var teamName: String
        var logoFileName: String

        var teamLogo: UIImage? {
            guard
                let data = FileManager.default.contents(
                    atPath: LiveScoreAttributes.rootDirectory.appending(
                        path: logoFileName,
                        directoryHint: .notDirectory
                    ).path
                ),
                let teamLogo = UIImage(data: data)
            else {
                return nil
            }

            return teamLogo
        }
    }

    var firstTeamInfo: TeamInfo
    var secondTeamInfo: TeamInfo

    var gameId: String
}
