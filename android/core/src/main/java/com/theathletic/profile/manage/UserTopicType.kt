package com.theathletic.profile.manage

import com.theathletic.entity.settings.UserTopicsBaseItem
import com.theathletic.entity.settings.UserTopicsItemAuthor
import com.theathletic.entity.settings.UserTopicsItemLeague
import com.theathletic.entity.settings.UserTopicsItemTeam
import java.io.Serializable

sealed class UserTopicId(open val id: Long) : Serializable {
    data class League(override val id: Long) : UserTopicId(id)
    data class Team(override val id: Long) : UserTopicId(id)
    data class Author(override val id: Long) : UserTopicId(id)

    fun matchesModel(model: UserTopicsBaseItem?) = when {
        model == null -> false
        model is UserTopicsItemLeague && this !is League -> false
        model is UserTopicsItemTeam && this !is Team -> false
        model is UserTopicsItemAuthor && this !is Author -> false
        else -> model.id == id
    }
}

val UserTopicsBaseItem.userTopicId get() = when (this) {
    is UserTopicsItemLeague -> UserTopicId.League(id)
    is UserTopicsItemTeam -> UserTopicId.Team(id)
    is UserTopicsItemAuthor -> UserTopicId.Author(id)
    else -> null
}

val UserTopicId.analyticsType get() = when (this) {
    is UserTopicId.Team -> "team_id"
    is UserTopicId.League -> "league_id"
    is UserTopicId.Author -> "author_id"
}

enum class UserTopicType {
    LEAGUE,
    TEAM,
    AUTHOR
}