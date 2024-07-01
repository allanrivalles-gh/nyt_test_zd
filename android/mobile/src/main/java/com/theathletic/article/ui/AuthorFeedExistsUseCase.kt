package com.theathletic.article.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.FollowableId
import com.theathletic.followable.FollowableType
import com.theathletic.followables.data.FollowableRepository

class AuthorFeedExistsUseCase @AutoKoin constructor(
    private val followableRepository: FollowableRepository,
) {
    suspend operator fun invoke(authorId: Long?): Boolean {
        return authorId != null && followableRepository.getAuthorFromId(
            FollowableId(
                id = authorId.toString(),
                type = FollowableType.AUTHOR
            )
        ) != null
    }
}