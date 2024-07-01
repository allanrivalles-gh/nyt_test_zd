//
//  GQLArticleLiteAuthor+MakeMock.swift
//
//
//  Created by Jason Leyrer on 10/9/23.
//

import AthleticApolloTypes
import Foundation

@testable import AthleticNotificationCenter

extension GQL.ArticleLiteAuthor {
    static func makeMock(
        id: String = "1",
        title: String = "Test Article Title",
        imageUri: String = "https://theathletic.com",
        primaryTagString: String = "NHL",
        commentCount: Int = 2,
        lockComments: Bool = false,
        disableComments: Bool = false,
        publishedAt: Date = Date(),
        permalink: String = "",
        excerpt: String = "",
        excerptPlaintext: String = "",
        postTypeId: String? = nil,
        author: GQL.ArticleLiteAuthor.Author =
            .makeStaff(
                id: "2",
                name: "A. Author",
                firstName: "",
                lastName: "",
                role: .author
            )
    ) -> GQL.ArticleLiteAuthor {
        .init(
            id: id,
            title: title,
            imageUri: imageUri,
            primaryTagString: primaryTagString,
            commentCount: commentCount,
            lockComments: lockComments,
            disableComments: disableComments,
            publishedAt: publishedAt,
            permalink: permalink,
            excerpt: excerpt,
            excerptPlaintext: excerptPlaintext,
            postTypeId: postTypeId,
            author: author
        )
    }
}
