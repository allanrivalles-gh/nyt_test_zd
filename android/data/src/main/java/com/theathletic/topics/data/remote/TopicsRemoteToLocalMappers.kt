package com.theathletic.topics.data.remote

import com.theathletic.entity.settings.UserTopics
import com.theathletic.entity.settings.UserTopicsItemAuthor
import com.theathletic.entity.settings.UserTopicsItemLeague
import com.theathletic.entity.settings.UserTopicsItemTeam
import com.theathletic.fragment.FollowResponseFragment
import com.theathletic.fragment.UserTopicAuthorFragment
import com.theathletic.fragment.UserTopicLeagueFragment
import com.theathletic.fragment.UserTopicTeamFragment

fun FollowResponseFragment.toLocalFollowedModels() = UserTopics(
    teams = following.teams.map { it.fragments.userTopicTeamFragment.toLocalModel(true) },
    leagues = following.leagues.map { it.fragments.userTopicLeagueFragment.toLocalModel(true) }.toMutableList(),
    authors = following.authors.map { it.fragments.userTopicAuthorFragment.toLocalModel(true) },
    podcasts = mutableListOf(),
)

fun UserTopicTeamFragment.toLocalModel(isFollowed: Boolean): UserTopicsItemTeam {
    return UserTopicsItemTeam().also { local ->
        local.id = id.toLong()
        local.name = name.orEmpty()
        local.shortname = shortname.orEmpty()
        local.notifyStories = notif_stories ?: false
        local.notifyGames = notif_games ?: false
        local.color = color_primary
        local.leagueId = league_id?.toLongOrNull() ?: -1
        local.colorGradient = color_gradient
        local.searchText = search_text
        local.isFollowed = isFollowed
        local.graphqlId = ath_team_id
    }
}

fun UserTopicLeagueFragment.toLocalModel(isFollowed: Boolean): UserTopicsItemLeague {
    return UserTopicsItemLeague().also { local ->
        local.id = id.toLong()
        local.name = name.orEmpty()
        local.shortname = shortname.orEmpty()
        local.notifyStories = notif_stories ?: false
        local.hasScores = has_scores ?: false
        local.isFollowed = isFollowed
    }
}

fun UserTopicAuthorFragment.toLocalModel(isFollowed: Boolean): UserTopicsItemAuthor {
    return UserTopicsItemAuthor().also { local ->
        local.id = id.toLong()
        local.name = name.orEmpty()
        local.shortname = shortname.orEmpty()
        local.imgUrl = image_url.orEmpty()
        local.notifyStories = notif_stories ?: false
        local.searchText = search_text
        local.isFollowed = isFollowed
    }
}