package com.theathletic.followables.data

import com.theathletic.FollowableItemsQuery
import com.theathletic.UserFollowingQuery
import com.theathletic.entity.main.League.UNKNOWN
import com.theathletic.followable.Followable
import com.theathletic.fragment.FollowResponseFragment
import com.theathletic.repository.user.AuthorLocal
import com.theathletic.repository.user.FollowableItems
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.repository.user.TeamLocal
import com.theathletic.repository.user.UserFollowingItem
import com.theathletic.scores.remote.toLocalLeague
import com.theathletic.user.LocalFollowableNotificationSettings

fun FollowableItemsQuery.FollowableItems.toLocalFollowedModels() = FollowableItems(
    teams = teams.map { it.toLocalModel() },
    leagues = leagues.map { it.toLocalModel() },
    authors = authors.map { it.toLocalModel() }
)

fun FollowResponseFragment.Following.toLocalFollowedModels(): List<UserFollowingItem> {
    val teams = teams.map {
        UserFollowingItem(
            id = Followable.Id(
                id = it.fragments.userTopicTeamFragment.id,
                type = Followable.Type.TEAM
            )
        )
    }
    val leagues = leagues.map {
        UserFollowingItem(
            id = Followable.Id(
                id = it.fragments.userTopicLeagueFragment.id,
                type = Followable.Type.LEAGUE
            )
        )
    }
    val authors = authors.map {
        UserFollowingItem(
            id = Followable.Id(
                id = it.fragments.userTopicAuthorFragment.id,
                type = Followable.Type.AUTHOR
            )
        )
    }
    return teams + leagues + authors
}

fun FollowableItemsQuery.Team.toLocalModel() = TeamLocal(
    id = Followable.Id(id, Followable.Type.TEAM),
    name = teamv2?.name ?: name.orEmpty(),
    shortName = teamv2?.alias ?: shortname.orEmpty(),
    url = url.orEmpty(),
    searchText = search_text ?: name.orEmpty(),
    colorScheme = TeamLocal.ColorScheme(
        primaryColor = color_primary,
        iconContrastColor = icon_contrast_color,
    ),
    displayName = teamv2?.display_name ?: name.orEmpty(),
    leagueId = Followable.Id(league_id ?: UNKNOWN.leagueId.toString(), Followable.Type.LEAGUE),
    graphqlId = teamv2?.id
)

fun FollowableItemsQuery.Author.toLocalModel() = AuthorLocal(
    id = Followable.Id(id, Followable.Type.AUTHOR),
    name = name.orEmpty(),
    shortName = shortname.orEmpty(),
    searchText = search_text ?: name.orEmpty(),
    imageUrl = image_url.orEmpty(),
    url = url.orEmpty()
)

fun FollowableItemsQuery.League.toLocalModel() = LeagueLocal(
    id = Followable.Id(id, Followable.Type.LEAGUE),
    name = leaguev2?.name ?: name.orEmpty(),
    shortName = leaguev2?.alias ?: shortname.orEmpty(),
    url = url.orEmpty(),
    sportType = sport_type,
    searchText = name.orEmpty(),
    league = league_code?.toLocalLeague ?: UNKNOWN,
    hasActiveBracket = current_season?.active_bracket ?: false,
    hasScores = has_gqlscores ?: false,
    displayName = leaguev2?.display_name ?: name.orEmpty()
)

fun UserFollowingQuery.Team.toLocalModel() = UserFollowingItem(
    id = Followable.Id(id, Followable.Type.TEAM)
)

fun UserFollowingQuery.League1.toLocalModel() = UserFollowingItem(
    id = Followable.Id(id, Followable.Type.LEAGUE)
)

fun UserFollowingQuery.Author.toLocalModel() = UserFollowingItem(
    id = Followable.Id(id, Followable.Type.AUTHOR)
)

fun UserFollowingQuery.Team.toLocalNotificationSettings() = LocalFollowableNotificationSettings(
    id = Followable.Id(id, Followable.Type.TEAM).toString(),
    notifyStories = notif_stories ?: false,
    notifyGames = notif_games ?: false,
    notifyGamesStart = notif_games_start ?: false,
)

fun UserFollowingQuery.League1.toLocalNotificationSettings() = LocalFollowableNotificationSettings(
    id = Followable.Id(id, Followable.Type.LEAGUE).toString(),
    notifyStories = notif_stories ?: false,
    notifyGames = false,
    notifyGamesStart = false,
)

fun UserFollowingQuery.Author.toLocalNotificationSettings() = LocalFollowableNotificationSettings(
    id = Followable.Id(id, Followable.Type.AUTHOR).toString(),
    notifyStories = notif_stories ?: false,
    notifyGames = false,
    notifyGamesStart = false,
)

fun UserFollowingQuery.Following.extractNotificationSettings(): List<LocalFollowableNotificationSettings> {
    return (
        authors.map { it.toLocalNotificationSettings() } +
            leagues.map { it.toLocalNotificationSettings() } +
            teams.map { it.toLocalNotificationSettings() }
        )
}