//
//  GQL.LiveBlogContent+FindGameId.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 17/10/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes

extension GQL.LiveBlogContent {
    func findGameId() -> String? {
        for tag in liveBlogTags {
            if tag.type == "game" {
                return tag.id
            }
        }
        return nil
    }
}

extension GQL.LiveBlogConsumable {
    func findGameId() -> String? {
        for tag in liveBlogTags {
            if tag.type == "game" {
                return tag.id
            }
        }
        return nil
    }
}
