package com.theathletic.gamedetail.boxscore.ui.common

import androidx.compose.ui.graphics.Color
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.InjuryReportUi
import com.theathletic.boxscore.ui.modules.InjuryReportSummaryModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.InjuryStatus
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.asResourceString
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.utility.orShortDash
import java.util.concurrent.atomic.AtomicInteger

class BoxScoreInjuryReportRenderers @AutoKoin constructor(
    private val supportedLeagues: SupportedLeagues
) {

    fun createInjuryReportModule(game: GameDetailLocalModel): FeedModuleV2 = game.injuryReport()

    @Deprecated("Use createInjuryReportModule(game: GameDetailLocalModel)")
    fun createInjuryReportModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (game.isGameCompleted ||
            supportedLeagues.isCollegeLeague(game.league.legacyLeague) ||
            game.hasNoInjuries()
        ) return null
        pageOrder.getAndIncrement()
        return game.injuryReport()
    }

    private fun GameDetailLocalModel.hasNoInjuries() =
        (this.firstTeam?.injuries == null && this.secondTeam?.injuries == null)

    private fun GameDetailLocalModel.injuryReport() = InjuryReportSummaryModule(
        id = id,
        firstTeam = firstTeam?.team.toSummary,
        secondTeam = secondTeam?.team.toSummary,
        firstTeamInjuries = firstTeam?.injuries?.toUi() ?: emptyList(),
        secondTeamInjuries = secondTeam?.injuries?.toUi() ?: emptyList(),
    )
}

val GameDetailLocalModel.Team?.toSummary: InjuryReportUi.TeamDetails
    get() = this?.let { team ->
        InjuryReportUi.TeamDetails(
            name = team.displayName,
            logoUrls = team.logos,
            teamColor = team.primaryColor.parseHexColor()
        )
    } ?: InjuryReportUi.TeamDetails(
        name = "-",
        logoUrls = emptyList(),
        teamColor = Color.Black
    )

fun List<GameDetailLocalModel.Injury>.toUi(): List<InjuryReportUi.PlayerInjury> {
    return map { injury ->
        InjuryReportUi.PlayerInjury(
            name = injury.playerName.orShortDash(),
            position = injury.playerPosition?.alias.orShortDash(),
            injury = injury.comment?.let { comment ->
                StringWithParams(
                    R.string.box_score_injury_comment_formatter,
                    injury.injury,
                    comment
                )
            } ?: injury.injury.asResourceString(),
            headshots = injury.headshots,
            type = injury.status.toUi
        )
    }
}

private val InjuryStatus.toUi: InjuryReportUi.InjuryType
    get() = when (this) {
        InjuryStatus.D7 -> InjuryReportUi.InjuryType.D7
        InjuryStatus.D10 -> InjuryReportUi.InjuryType.D10
        InjuryStatus.D15 -> InjuryReportUi.InjuryType.D15
        InjuryStatus.D60 -> InjuryReportUi.InjuryType.D60
        InjuryStatus.DAY -> InjuryReportUi.InjuryType.DAY
        InjuryStatus.DAY_TO_DAY -> InjuryReportUi.InjuryType.DAY_TO_DAY
        InjuryStatus.DOUBTFUL -> InjuryReportUi.InjuryType.DOUBTFUL
        InjuryStatus.OUT -> InjuryReportUi.InjuryType.OUT
        InjuryStatus.OUT_FOR_SEASON -> InjuryReportUi.InjuryType.OUT_FOR_SEASON
        InjuryStatus.OUT_INDEFINITELY -> InjuryReportUi.InjuryType.OUT_INDEFINITELY
        InjuryStatus.QUESTIONABLE -> InjuryReportUi.InjuryType.QUESTIONABLE
        else -> InjuryReportUi.InjuryType.UNKNOWN
    }