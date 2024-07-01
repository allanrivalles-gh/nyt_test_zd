//
//  GQL+Team.swift
//
//
//  Created by Mark Corbyn on 19/5/2023.
//

import AthleticApolloTypes
import AthleticUI
import Foundation

extension GQL.GameV2LiteTeam {

    public var teamLite: GQL.TeamV2Lite? {
        team?.fragments.teamV2Lite
    }

}

extension GQL.TeamV2Lite {

    public var teamLogos: [ATHImageResource]? {
        logos.map { ATHImageResource(entity: $0.fragments.teamLogo) }
    }

}

extension GQL.TeamV2 {

    public var teamLogos: [ATHImageResource] {
        logos.map { ATHImageResource(entity: $0.fragments.teamLogo) }
    }

}
