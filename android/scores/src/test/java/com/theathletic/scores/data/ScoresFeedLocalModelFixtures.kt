package com.theathletic.scores.data

import com.theathletic.datetime.Datetime
import com.theathletic.entity.main.League
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.scores.data.local.ScoresFeedBaseballWidgetBlock
import com.theathletic.scores.data.local.ScoresFeedBlock
import com.theathletic.scores.data.local.ScoresFeedDateTimeFormat
import com.theathletic.scores.data.local.ScoresFeedDateTimeTextBlock
import com.theathletic.scores.data.local.ScoresFeedDay
import com.theathletic.scores.data.local.ScoresFeedDiscussionWidgetBlock
import com.theathletic.scores.data.local.ScoresFeedFollowingGroup
import com.theathletic.scores.data.local.ScoresFeedGameBlock
import com.theathletic.scores.data.local.ScoresFeedGroup
import com.theathletic.scores.data.local.ScoresFeedInfoBlock
import com.theathletic.scores.data.local.ScoresFeedLeague
import com.theathletic.scores.data.local.ScoresFeedLeagueGroup
import com.theathletic.scores.data.local.ScoresFeedOddsTextBlock
import com.theathletic.scores.data.local.ScoresFeedStandardTextBlock
import com.theathletic.scores.data.local.ScoresFeedTeamBlock
import com.theathletic.scores.data.local.ScoresFeedTeamGameInfoBlock
import com.theathletic.scores.data.local.ScoresFeedTeamIcon
import com.theathletic.scores.data.local.ScoresFeedTeamInfoBlock
import com.theathletic.scores.data.local.ScoresFeedTeamPregameInfoBlock
import com.theathletic.scores.data.local.ScoresFeedTextBlock
import com.theathletic.scores.data.local.ScoresFeedTextType
import com.theathletic.scores.data.local.ScoresFeedWidgetBlock

fun scoresFeedDayFixture(
    day: String,
    isTopGames: Boolean = false,
    groups: List<ScoresFeedGroup>
) = ScoresFeedDay(
    id = day,
    day = day,
    isTopGames = isTopGames,
    groups = groups
)

fun scoresFeedFollowingGroupFixture(
    title: String,
    subTitle: String? = null,
    blocks: List<ScoresFeedBlock> = emptyList(),
    widget: ScoresFeedWidgetBlock? = null
) = ScoresFeedFollowingGroup(
    id = title,
    title = title,
    subTitle = subTitle,
    blocks = blocks,
    widget = widget
)

fun scoresFeedLeagueGroupFixture(
    title: String,
    subTitle: String? = null,
    league: ScoresFeedLeague,
    blocks: List<ScoresFeedBlock> = emptyList(),
    widget: ScoresFeedWidgetBlock? = null
) = ScoresFeedLeagueGroup(
    id = title,
    title = title,
    subTitle = subTitle,
    league = league,
    blocks = blocks,
    widget = widget
)

fun scoresFeedLeagueFixture(
    displayName: String
) = ScoresFeedLeague(
    league = League.UNKNOWN,
    legacyId = null,
    displayName = displayName
)

fun scoresFeedBlockFixture(
    gameId: String,
    header: String? = null,
    footer: String? = null,
    gameBlock: ScoresFeedGameBlock,
    infoBlock: ScoresFeedInfoBlock,
    widget: ScoresFeedWidgetBlock? = null
) = ScoresFeedBlock(
    id = gameId,
    gameId = gameId,
    header = header,
    footer = footer,
    gameBlock = gameBlock,
    infoBlock = infoBlock,
    widget = widget,
    willUpdate = false
)

fun scoresFeedGameBlockFixture(
    gameStatus: GameStatus,
    startedAt: Datetime,
    firstTeam: ScoresFeedTeamBlock,
    secondTeam: ScoresFeedTeamBlock,
) = ScoresFeedGameBlock(
    id = "ScoresFeedGameBlock",
    gameStatus = gameStatus,
    startedAt = startedAt,
    firstTeam = firstTeam,
    secondTeam = secondTeam,
)

fun scoresFeedTeamBlockFixture(
    name: String,
    teamInfo: ScoresFeedTeamInfoBlock? = null,
    icons: List<ScoresFeedTeamIcon> = emptyList(),
    ranking: Int? = null,
    isTbd: Boolean = false,
) = ScoresFeedTeamBlock(
    id = name,
    name = name,
    teamInfo = teamInfo,
    logos = emptyList(),
    icons = icons,
    ranking = ranking,
    isTbd = isTbd
)

fun scoresFeedTeamGameInfoBlockFixture(
    score: String? = null,
    penaltyScore: String? = null,
    isWinner: Boolean = false,
) = ScoresFeedTeamGameInfoBlock(
    id = "ScoresFeedTeamGameInfoBlock",
    score = score,
    penaltyScore = penaltyScore,
    isWinner = isWinner
)

fun scoresFeedTeamPregameInfoBlockFixture(
    text: String
) = ScoresFeedTeamPregameInfoBlock(
    id = "ScoresFeedTeamPregameInfoBlock",
    text = text
)

fun scoresFeedInfoBlockFixture(
    text: List<ScoresFeedTextBlock> = emptyList(),
    widget: ScoresFeedWidgetBlock? = null
) = ScoresFeedInfoBlock(
    id = "ScoresFeedInfoBlock",
    text = text,
    widget = widget
)

fun scoresFeedDateTimeTextBlockFixture(
    format: ScoresFeedDateTimeFormat,
    timestamp: Long,
    type: ScoresFeedTextType = ScoresFeedTextType.DateTime,
    isTimeTbd: Boolean = false
) = ScoresFeedDateTimeTextBlock(
    id = "ScoresFeedDateTimeTextBlock",
    type = type,
    format = format,
    dateTime = Datetime(timestamp),
    isTimeTbd = isTimeTbd
)

fun scoresFeedStandardTextBlockFixture(
    text: String
) = ScoresFeedStandardTextBlock(
    id = "ScoresFeedStandardTextBlock-Default",
    type = ScoresFeedTextType.Default,
    text = text
)

fun scoresFeedLiveTextBlockFixture(
    text: String
) = ScoresFeedStandardTextBlock(
    id = "ScoresFeedStandardTextBlock-Live",
    type = ScoresFeedTextType.Live,
    text = text
)

fun scoresFeedStatusTextBlockFixture(
    text: String
) = ScoresFeedStandardTextBlock(
    id = "ScoresFeedStandardTextBlock-Status",
    type = ScoresFeedTextType.Status,
    text = text
)

fun scoresFeedOddsTextBlockFixture() =
    ScoresFeedOddsTextBlock(
        id = "ScoresFeedOddsTextBlock",
        type = ScoresFeedTextType.Default,
        decimalOdds = "BOS 0.25",
        fractionOdds = "BOS +1/4",
        usOdds = "BOS -10.5"
    )

fun scoresFeedBaseballWidgetBlock() = ScoresFeedBaseballWidgetBlock(
    id = "ScoresFeedBaseballWidgetBlock",
    loadedBases = listOf(1, 3)
)

fun scoresFeedDiscussionWidgetBlockFixture() = ScoresFeedDiscussionWidgetBlock(
    id = "ScoresFeedDiscussionWidgetBlock",
    text = "Join the Discussion"
)