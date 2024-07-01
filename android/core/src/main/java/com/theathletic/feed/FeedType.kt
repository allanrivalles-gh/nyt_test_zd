package com.theathletic.feed

import com.theathletic.entity.main.FEED_MY_FEED_ID
import com.theathletic.entity.settings.UserTopicsBaseItem
import com.theathletic.entity.settings.UserTopicsItemAuthor
import com.theathletic.entity.settings.UserTopicsItemCategory
import com.theathletic.entity.settings.UserTopicsItemInkStories
import com.theathletic.entity.settings.UserTopicsItemLeague
import com.theathletic.entity.settings.UserTopicsItemTeam
import com.theathletic.followable.Followable
import java.io.Serializable

private const val FRONTPAGE_KEY = "Frontpage"

sealed class FeedType(open val id: Long) : Serializable {
    object User : FeedType(FEED_MY_FEED_ID)
    data class Team(override val id: Long) : FeedType(id)
    data class League(override val id: Long) : FeedType(id)
    data class Author(override val id: Long) : FeedType(id)
    data class Category(override val id: Long, val name: String) : FeedType(id)
    data class Ink(val name: String) : FeedType(name.hashCode().toLong())

    data class Tag(override val id: Long) : FeedType(id)

    object ScoresFollowing : FeedType(0)
    data class ScoresToday(override val id: Long) : FeedType(id)
    data class ScoresTeam(override val id: Long, val gqlId: String? = null) : FeedType(id) {
        override val compositeId: String
            get() = if (gqlId != null) {
                "${this.javaClass.simpleName}_$gqlId"
            } else {
                super.compositeId
            }
    }
    data class ScoresLeague(override val id: Long) : FeedType(id)

    object Frontpage : FeedType(FRONTPAGE_KEY.hashCode().toLong()) {
        override val compositeId = FRONTPAGE_KEY
    }

    // TODO (matt): Create a more sustainable solution than using `hashCode()`
    data class GameFeed(val gameId: String) : FeedType(gameId.hashCode().toLong())

    open val compositeId: String
        get() = "${this.javaClass.simpleName}_$id"

    val asFollowableId: Followable.Id?
        get() {
            val type = when (this) {
                is Team -> Followable.Type.TEAM
                is League -> Followable.Type.LEAGUE
                is Author -> Followable.Type.AUTHOR
                else -> return null
            }
            return Followable.Id(
                id = id.toString(),
                type = type,
            )
        }

    companion object {
        fun fromUserTopic(userTopic: UserTopicsBaseItem): FeedType {
            return when {
                userTopic.isMyFeedItem() -> User
                userTopic is UserTopicsItemTeam -> Team(userTopic.id)
                userTopic is UserTopicsItemLeague -> League(userTopic.id)
                userTopic is UserTopicsItemAuthor -> Author(userTopic.id)
                userTopic is UserTopicsItemCategory -> Category(userTopic.id, userTopic.name)
                userTopic is UserTopicsItemInkStories -> Ink(userTopic.name)
                else -> throw IllegalStateException(
                    "Cannot call fromUserTopic with ${userTopic::javaClass.name}"
                )
            }
        }

        fun fromFollowable(id: Followable.Id) = when (id.type) {
            Followable.Type.TEAM -> Team(id.id.toLong())
            Followable.Type.LEAGUE -> League(id.id.toLong())
            Followable.Type.AUTHOR -> Author(id.id.toLong())
        }
    }
}