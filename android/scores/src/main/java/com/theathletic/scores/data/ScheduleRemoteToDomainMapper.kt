package com.theathletic.scores.data

import com.theathletic.GetLeagueScheduleQuery
import com.theathletic.GetScheduleFeedGroupQuery
import com.theathletic.GetTeamScheduleQuery
import com.theathletic.data.SizedImage
import com.theathletic.datetime.Datetime
import com.theathletic.fragment.GameTicketsLogo
import com.theathletic.fragment.ScoresFeedBlock
import com.theathletic.fragment.ScoresFeedDayGrouping
import com.theathletic.fragment.ScoresFeedDefaultGrouping
import com.theathletic.fragment.ScoresFeedFilter
import com.theathletic.fragment.ScoresFeedGroup
import com.theathletic.fragment.ScoresFeedInfoBlock
import com.theathletic.fragment.ScoresFeedTeamBlock
import com.theathletic.fragment.ScoresFeedTeamInfoBlock
import com.theathletic.fragment.ScoresFeedTextBlock
import com.theathletic.fragment.ScoresFeedWidgetBlock
import com.theathletic.fragment.TeamLogo
import com.theathletic.scores.data.local.Schedule
import com.theathletic.scores.data.remote.toLocalLeague
import com.theathletic.type.GameState
import com.theathletic.type.ScoresFeedDateTimeFormat
import com.theathletic.type.ScoresFeedTeamIcon
import com.theathletic.type.ScoresFeedTextType

fun GetTeamScheduleQuery.ScheduleFeed.toDomain() = Schedule(
    id = id,
    groups = groupings.mapNotNull { it.toDomain() },
    filters = null
)

fun GetLeagueScheduleQuery.ScheduleFeed.toDomain(): Schedule {
    val domainFilters = filters.map { it.fragments.scoresFeedFilter.toDomain() }
    val firstDefaultFilter = domainFilters.firstOrNull()?.values?.firstOrNull { it.isDefault }?.id

    return Schedule(
        id = id,
        groups = groupings.mapNotNull { it.toDomain(firstDefaultFilter) },
        filters = domainFilters
    )
}

fun ScoresFeedFilter.toDomain() = Schedule.Filter(
    id = this.id,
    values = this.values.map {
        Schedule.FilterValue(
            id = it.id,
            isDefault = it.is_default,
            label = it.label
        )
    }
)

fun List<GetScheduleFeedGroupQuery.ScoresFeedGroup>.toDomain() =
    mapNotNull { it.fragments.scoresFeedGroup.toDomain() }

private fun GetTeamScheduleQuery.Grouping.toDomain(): Schedule.Group? {
    fragments.scoresFeedGrouping.fragments.scoresFeedDayGrouping?.let { grouping ->
        return grouping.toDomain(null)
    }
    fragments.scoresFeedGrouping.fragments.scoresFeedDefaultGrouping?.let { grouping ->
        return grouping.toDomain(null)
    }
    return null
}

private fun GetLeagueScheduleQuery.Grouping.toDomain(firstDefaultFilter: String?): Schedule.Group? {
    fragments.scoresFeedGrouping.fragments.scoresFeedDayGrouping?.let { grouping ->
        return grouping.toDomain(firstDefaultFilter)
    }
    fragments.scoresFeedGrouping.fragments.scoresFeedDefaultGrouping?.let { grouping ->
        return grouping.toDomain(firstDefaultFilter)
    }
    return null
}

private fun ScoresFeedDayGrouping.toDomain(firstDefaultFilter: String?) = Schedule.Group(
    navItem = Schedule.NavItem(
        id = id,
        isDefault = default_grouping,
        day = day.toString(),
        primaryLabel = null,
        secondaryLabel = null,
        filterSelected = firstDefaultFilter
    ),
    sections = groups.mapNotNull { it.fragments.scoresFeedGroup.toDomain() }
)

private fun ScoresFeedDefaultGrouping.toDomain(firstDefaultFilter: String?) = Schedule.Group(
    navItem = Schedule.NavItem(
        id = id,
        isDefault = default_grouping,
        day = null,
        primaryLabel = title,
        secondaryLabel = subtitle,
        filterSelected = firstDefaultFilter
    ),
    sections = groups.mapNotNull { it.fragments.scoresFeedGroup.toDomain() }
)

private fun ScoresFeedGroup.toDomain(): Schedule.Section? {
    fragments.scoresFeedBaseGroup?.let { group ->
        return Schedule.Section(
            id = group.id,
            title = group.title,
            subTitle = group.subtitle,
            games = group.blocks.map { it.fragments.scoresFeedBlock.toDomain() },
            widget = group.widget?.fragments?.scoresFeedWidgetBlock?.toDomain()
        )
    }
    fragments.scoresFeedFollowingGroup?.let { group ->
        return Schedule.Section(
            id = group.id,
            title = group.title,
            subTitle = group.subtitle,
            games = group.blocks.map { it.fragments.scoresFeedBlock.toDomain() },
            widget = group.widget?.fragments?.scoresFeedWidgetBlock?.toDomain()
        )
    }
    fragments.scoresFeedLeagueGroup?.let { group ->
        return Schedule.Section(
            id = group.id,
            title = group.title,
            subTitle = group.subtitle,
            league = group.league.id.toLocalLeague,
            games = group.blocks.map { it.fragments.scoresFeedBlock.toDomain() },
            widget = group.widget?.fragments?.scoresFeedWidgetBlock?.toDomain()
        )
    }
    return null
}

fun ScoresFeedBlock.toDomain() = Schedule.Game(
    gameId = game_id,
    state = game_block.fragments.scoresFeedGameBlock.game_state.toDomain(),
    team1 = game_block.fragments.scoresFeedGameBlock.team1.fragments.scoresFeedTeamBlock.toDomain(),
    team2 = game_block.fragments.scoresFeedGameBlock.team2.fragments.scoresFeedTeamBlock.toDomain(),
    info = info_block.fragments.scoresFeedInfoBlock.toDomain(),
    widget = widget?.fragments?.scoresFeedWidgetBlock?.toDomain(),
    header = header,
    footer = footer,
    willUpdate = will_update
)

private fun ScoresFeedTeamBlock.toDomain() = Schedule.Team(
    name = name,
    logos = logos.map { it.fragments.teamLogo.toSizedImage() },
    icons = icons.map { it.toDomain() },
    ranking = ranking,
    isTbd = is_tbd,
    info = team_info?.fragments?.scoresFeedTeamInfoBlock?.toDomain()
)

private fun ScoresFeedTeamInfoBlock.toDomain(): Schedule.TeamInfo? {
    fragments.scoresFeedTeamPregameInfoBlock?.let { info ->
        return Schedule.PreGame(
            text = info.text
        )
    }
    fragments.scoresFeedTeamGameInfoBlock?.let { info ->
        return Schedule.LivePostGame(
            score = info.score,
            penaltyScore = info.penalty_score,
            isWinner = info.is_winner ?: false
        )
    }
    return null
}

private fun ScoresFeedWidgetBlock.toDomain(): Schedule.Widget? {
    fragments.scoresFeedAllGamesWidgetBlock?.let { block ->
        return Schedule.Widget.AllGamesLink(
            linkText = block.link_text,
        )
    }
    fragments.scoresFeedBaseballWidgetBlock?.let { block ->
        return Schedule.Widget.BaseballBases(
            loadedBases = block.loaded_bases
        )
    }
    fragments.scoresFeedDiscussionWidgetBlock?.let { block ->
        return Schedule.Widget.DiscussionLink(
            text = block.text
        )
    }
    fragments.gameTicketsWidget?.let { block ->
        return Schedule.Widget.GameTickets(
            text = block.text,
            provider = block.provider,
            uri = block.uri,
            logosDark = block.logos_dark_mode.map { it.fragments.gameTicketsLogo.toSizedImage() },
            logosLight = block.logos_light_mode.map { it.fragments.gameTicketsLogo.toSizedImage() }
        )
    }
    return null
}

private fun ScoresFeedInfoBlock.toDomain() = Schedule.GameInfo(
    textContent = text.mapNotNull { it.fragments.scoresFeedTextBlock.toDomain() },
    widget = widget?.fragments?.scoresFeedWidgetBlock?.toDomain()
)

private fun ScoresFeedTextBlock.toDomain(): Schedule.TextContent? {
    fragments.scoresFeedDateTimeTextBlock?.let { block ->
        return Schedule.TextContent.DateTime(
            format = block.format.toDomain(),
            type = block.type.toDomain(),
            dateTime = Datetime(block.timestamp),
            isTimeTbd = block.time_tbd,
        )
    }
    fragments.scoresFeedOddsTextBlock?.let { block ->
        return Schedule.TextContent.Odds(
            type = block.type.toDomain(),
            decimalOdds = block.odds.decimal_odds,
            fractionOdds = block.odds.fraction_odds,
            usOdds = block.odds.us_odds,
        )
    }
    fragments.scoresFeedStandardTextBlock?.let { block ->
        return Schedule.TextContent.Standard(
            type = block.type.toDomain(),
            text = block.text,
        )
    }
    return null
}

private fun GameState.toDomain() = when (this) {
    GameState.pre -> Schedule.GameState.PREGAME
    GameState.live -> Schedule.GameState.LIVE_GAME
    GameState.post -> Schedule.GameState.POST_GAME
    else -> Schedule.GameState.UNKNOWN
}

private fun ScoresFeedTeamIcon.toDomain() = when (this) {
    ScoresFeedTeamIcon.american_football_possession -> Schedule.TeamIcon.AMERICAN_FOOTBALL_POSSESSION
    ScoresFeedTeamIcon.soccer_redcard -> Schedule.TeamIcon.SOCCER_RED_CARD
    else -> Schedule.TeamIcon.UNKNOWN
}

private fun ScoresFeedTextType.toDomain() = when (this) {
    ScoresFeedTextType.datetime -> Schedule.TextContent.TextType.DATE_TIME
    ScoresFeedTextType.default -> Schedule.TextContent.TextType.DEFAULT
    ScoresFeedTextType.live -> Schedule.TextContent.TextType.LIVE
    ScoresFeedTextType.situation -> Schedule.TextContent.TextType.SITUATION
    ScoresFeedTextType.status -> Schedule.TextContent.TextType.STATUS
    else -> Schedule.TextContent.TextType.DEFAULT
}

private fun ScoresFeedDateTimeFormat.toDomain() = when (this) {
    ScoresFeedDateTimeFormat.date -> Schedule.TextContent.DateType.DATE
    ScoresFeedDateTimeFormat.time -> Schedule.TextContent.DateType.TIME
    ScoresFeedDateTimeFormat.datetime -> Schedule.TextContent.DateType.DATE_AND_TIME
    else -> Schedule.TextContent.DateType.UNKNOWN
}

private fun TeamLogo.toSizedImage() = SizedImage(
    width = width,
    height = height,
    uri = uri
)

private fun GameTicketsLogo.toSizedImage() = SizedImage(
    width = width,
    height = height,
    uri = uri
)