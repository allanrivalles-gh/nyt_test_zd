//
//  GQL.CardType+isRedCard.swift
//
//
//  Created by Leonardo da Silva on 19/04/23.
//

import AthleticApolloTypes

extension GQL.CardType {
    public var isRedCard: Bool {
        [.rc, .y2c].contains(self)
    }
}
