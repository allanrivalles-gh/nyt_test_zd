package com.theathletic.followables.test.fixtures

import com.theathletic.entity.main.League
import com.theathletic.followable.FollowableId
import com.theathletic.followable.FollowableType
import com.theathletic.followables.data.domain.Followable
import com.theathletic.repository.user.AuthorLocal
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.repository.user.TeamLocal

fun localTeamFixture(
    id: FollowableId = teamIdFixture(),
    leagueId: FollowableId = leagueIdFixture(),
    name: String = "Lakers",
    displayName: String = "LAL",
    shortName: String = "LAL",
    colorScheme: TeamLocal.ColorScheme = TeamLocal.ColorScheme("primaryColor", "hexColor")
) = TeamLocal(
    id = id,
    leagueId = leagueId,
    name = name,
    displayName = displayName,
    shortName = shortName,
    searchText = "",
    colorScheme = colorScheme
)

fun localLeagueFixture(
    id: FollowableId = leagueIdFixture(),
    name: String = "National Basketball Association",
    shortName: String = "NBA",
    searchText: String = "",
    displayName: String = "NBA"
) = LeagueLocal(id, name, shortName, searchText, League.NCAA_BB, displayName = displayName)

fun localAuthorFixture(
    id: FollowableId = authorIdFixture(),
    name: String = "Ronaldo de Assis Moreira",
    shortName: String = "R.Gauc",
    searchText: String = "",
    imageUrl: String = ""
) = AuthorLocal(
    id, name, shortName, searchText, imageUrl
)

fun leagueFixture(
    id: FollowableId = leagueIdFixture(),
    name: String = "National Basketball Association",
    shortName: String = "NBA",
    searchText: String = "",
    displayName: String = "NBA"
) = Followable.League(
    id = id,
    name = name,
    shortName = shortName,
    displayName = displayName,
    color = "",
    url = "",
    sportType = "",
    hasScores = true,
    searchText = searchText
)

fun authorFixture(
    id: FollowableId = authorIdFixture(),
    name: String = "Ronaldo de Assis Moreira",
    shortName: String = "R.Gauc",
    searchText: String = "",
    imageUrl: String = ""
) = Followable.Author(
    id = id,
    name = name,
    shortName = shortName,
    color = "",
    imageUrl = imageUrl,
    searchText = searchText
)

fun teamFixture(
    id: FollowableId = teamIdFixture(),
    leagueId: FollowableId = leagueIdFixture(),
    name: String = "Lakers",
    displayName: String = "LAL",
    shortName: String = "LAL",
    color: String = "hexColor"
) = Followable.Team(
    id = id,
    leagueId = leagueId,
    name = name,
    displayName = displayName,
    shortName = shortName,
    searchText = "",
    color = color,
    graphqlId = ""
)

fun teamIdFixture(id: String = "teamId") = FollowableId(id, FollowableType.TEAM)
fun leagueIdFixture(id: String = "leagueId") = FollowableId(id, FollowableType.LEAGUE)
fun authorIdFixture(id: String = "authorId") = FollowableId(id, FollowableType.AUTHOR)