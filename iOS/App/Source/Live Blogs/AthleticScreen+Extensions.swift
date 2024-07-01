//
//  AthleticScreen+Extensions.swift
//  theathletic-ios
//
//  Created by kevin fremgen on 8/14/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticNavigation
import Foundation

extension AthleticScreen {
    static func liveBlog(
        id: String,
        postId: String? = nil,
        gameId: String? = nil
    ) -> Self {
        if let gameId {
            return .scores(
                .boxScore(.init(gameId: gameId, initialSelectionOverride: .liveBlog))
            )
        } else {
            return .liveBlog(liveBlogId: id, postId: postId)
        }
    }
}
