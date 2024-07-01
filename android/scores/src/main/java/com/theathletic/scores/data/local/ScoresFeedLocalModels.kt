package com.theathletic.scores.data.local

import com.theathletic.data.SizedImages
import com.theathletic.datetime.Datetime
import com.theathletic.entity.main.League
import com.theathletic.followable.Followable
import com.theathletic.gamedetail.data.local.GameStatus

data class ScoresFeedLocalModel(
    val id: String,
    val days: List<ScoresFeedDay>,
    val navigationBar: List<Followable.Id>
)

data class ScoresFeedDay(
    val id: String,
    val day: String,
    val isTopGames: Boolean,
    val groups: List<ScoresFeedGroup>
)

interface ScoresFeedGroup {
    val id: String
    val title: String?
    val subTitle: String?
    val blocks: List<ScoresFeedBlock>
    val widget: ScoresFeedWidgetBlock?
}

data class ScoresFeedBaseGroup(
    override val id: String,
    override val title: String?,
    override val subTitle: String?,
    override val blocks: List<ScoresFeedBlock>,
    override val widget: ScoresFeedWidgetBlock?
) : ScoresFeedGroup

data class ScoresFeedFollowingGroup(
    override val id: String,
    override val title: String?,
    override val subTitle: String?,
    override val blocks: List<ScoresFeedBlock>,
    override val widget: ScoresFeedWidgetBlock?
) : ScoresFeedGroup

data class ScoresFeedLeagueGroup(
    override val id: String,
    override val title: String?,
    override val subTitle: String?,
    val league: ScoresFeedLeague,
    override val blocks: List<ScoresFeedBlock>,
    override val widget: ScoresFeedWidgetBlock?
) : ScoresFeedGroup

data class ScoresFeedLeague(
    val league: League,
    val legacyId: Long?,
    val displayName: String
)

data class ScoresFeedBlock(
    val id: String,
    val gameId: String,
    val header: String?,
    val footer: String?,
    val gameBlock: ScoresFeedGameBlock,
    val infoBlock: ScoresFeedInfoBlock,
    val widget: ScoresFeedWidgetBlock?,
    val willUpdate: Boolean
)

interface ScoresFeedWidgetBlock {
    val id: String
}

data class ScoresFeedAllGamesWidgetBlock(
    override val id: String,
    val linkText: String,
) : ScoresFeedWidgetBlock

data class ScoresFeedBaseballWidgetBlock(
    override val id: String,
    val loadedBases: List<Int>
) : ScoresFeedWidgetBlock

data class ScoresFeedDiscussionWidgetBlock(
    override val id: String,
    val text: String,
) : ScoresFeedWidgetBlock

data class ScoresFeedGameBlock(
    val id: String,
    val gameStatus: GameStatus,
    val startedAt: Datetime,
    val firstTeam: ScoresFeedTeamBlock,
    val secondTeam: ScoresFeedTeamBlock,
)

data class ScoresFeedTeamBlock(
    val id: String,
    val name: String,
    val teamInfo: ScoresFeedTeamInfoBlock?,
    val logos: SizedImages,
    val icons: List<ScoresFeedTeamIcon>,
    val ranking: Int?,
    val isTbd: Boolean
)

interface ScoresFeedTeamInfoBlock {
    val id: String
}

data class ScoresFeedTeamGameInfoBlock(
    override val id: String,
    val score: String?,
    val penaltyScore: String?,
    val isWinner: Boolean
) : ScoresFeedTeamInfoBlock

data class ScoresFeedTeamPregameInfoBlock(
    override val id: String,
    val text: String
) : ScoresFeedTeamInfoBlock

data class ScoresFeedInfoBlock(
    val id: String,
    val text: List<ScoresFeedTextBlock>,
    val widget: ScoresFeedWidgetBlock?
)

interface ScoresFeedTextBlock {
    val id: String
    val type: ScoresFeedTextType
}

data class ScoresFeedDateTimeTextBlock(
    override val id: String,
    override val type: ScoresFeedTextType,
    val format: ScoresFeedDateTimeFormat,
    val dateTime: Datetime,
    val isTimeTbd: Boolean,
) : ScoresFeedTextBlock

data class ScoresFeedOddsTextBlock(
    override val id: String,
    override val type: ScoresFeedTextType,
    val decimalOdds: String,
    val fractionOdds: String,
    val usOdds: String,
) : ScoresFeedTextBlock

data class ScoresFeedStandardTextBlock(
    override val id: String,
    override val type: ScoresFeedTextType,
    val text: String,
) : ScoresFeedTextBlock

enum class ScoresFeedTeamIcon {
    AmericanFootballPossession,
    SoccerRedCard,
    Unknown
}

enum class ScoresFeedDateTimeFormat {
    Date,
    DateAndTime,
    Time,
    Unknown
}

enum class ScoresFeedTextType {
    DateTime,
    Default,
    Live,
    Situation,
    Status,
    Unknown
}