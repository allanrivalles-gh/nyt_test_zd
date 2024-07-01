//
//  TeamSpecificThreadViewModel.swift
//
//
//  Created by kevin fremgen on 3/30/23.
//

import AthleticApolloTypes
import AthleticUI
import SwiftUI

public struct TeamSpecificThreadViewModel {
    let longTitle: String
    let shortTitle: String
    let teamId: String
    let legacyId: Int?
    let teamLogos: [ATHImageResource]
    let teamColor: Color

    public init?(thread: GQL.TeamSpecificThread) {
        guard let team = thread.team?.fragments.teamV2 else {
            return nil
        }

        longTitle = thread.label
        shortTitle = String(format: Strings.teamThreadsFollowers.localized, team.displayName ?? "")
        teamId = team.id
        legacyId = Int(thread.team?.legacyTeam?.id ?? "") ?? nil
        teamLogos = team.teamLogos
        teamColor = thread.team?.colorContrast.map { Color(hex: $0) } ?? .chalk.dark400
    }
}

extension TeamSpecificThreadViewModel: Equatable {
    public static func == (lhs: TeamSpecificThreadViewModel, rhs: TeamSpecificThreadViewModel)
        -> Bool
    {
        return lhs.teamId == rhs.teamId
    }
}

extension GQL.TeamV2 {
    var teamLogos: [ATHImageResource] {
        logos.map { ATHImageResource(entity: $0.fragments.teamLogo) }
    }
}
