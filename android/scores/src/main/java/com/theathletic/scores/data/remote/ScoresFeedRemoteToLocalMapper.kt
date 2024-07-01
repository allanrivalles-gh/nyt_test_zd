package com.theathletic.scores.data.remote

import com.theathletic.ScoresFeedDayQuery
import com.theathletic.ScoresFeedQuery
import com.theathletic.data.SizedImage
import com.theathletic.datetime.Datetime
import com.theathletic.entity.main.League
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.fragment.ScoresFeedBlock
import com.theathletic.fragment.ScoresFeedDay
import com.theathletic.fragment.ScoresFeedGameBlock
import com.theathletic.fragment.ScoresFeedGroup
import com.theathletic.fragment.ScoresFeedInfoBlock
import com.theathletic.fragment.ScoresFeedNavItem
import com.theathletic.fragment.ScoresFeedTeamBlock
import com.theathletic.fragment.ScoresFeedTeamInfoBlock
import com.theathletic.fragment.ScoresFeedTextBlock
import com.theathletic.fragment.ScoresFeedWidgetBlock
import com.theathletic.fragment.TeamLogo
import com.theathletic.gamedetail.data.remote.toStatusLocalModel
import com.theathletic.scores.data.local.ScoresFeedAllGamesWidgetBlock
import com.theathletic.scores.data.local.ScoresFeedBaseGroup
import com.theathletic.scores.data.local.ScoresFeedBaseballWidgetBlock
import com.theathletic.scores.data.local.ScoresFeedDateTimeTextBlock
import com.theathletic.scores.data.local.ScoresFeedDiscussionWidgetBlock
import com.theathletic.scores.data.local.ScoresFeedFollowingGroup
import com.theathletic.scores.data.local.ScoresFeedLeague
import com.theathletic.scores.data.local.ScoresFeedLeagueGroup
import com.theathletic.scores.data.local.ScoresFeedLocalModel
import com.theathletic.scores.data.local.ScoresFeedOddsTextBlock
import com.theathletic.scores.data.local.ScoresFeedStandardTextBlock
import com.theathletic.scores.data.local.ScoresFeedTeamGameInfoBlock
import com.theathletic.scores.data.local.ScoresFeedTeamPregameInfoBlock
import com.theathletic.type.LeagueCode
import com.theathletic.type.ScoresFeedDateTimeFormat
import com.theathletic.type.ScoresFeedTeamIcon
import com.theathletic.type.ScoresFeedTextType

private typealias ScoresFeedDayLocal = com.theathletic.scores.data.local.ScoresFeedDay
private typealias ScoresFeedGroupLocal = com.theathletic.scores.data.local.ScoresFeedGroup
private typealias ScoresFeedTextTypeLocal = com.theathletic.scores.data.local.ScoresFeedTextType
private typealias ScoresFeedGameBlockLocal = com.theathletic.scores.data.local.ScoresFeedGameBlock
private typealias ScoresFeedTeamBlockLocal = com.theathletic.scores.data.local.ScoresFeedTeamBlock
private typealias ScoresFeedTeamInfoBlockLocal = com.theathletic.scores.data.local.ScoresFeedTeamInfoBlock
private typealias ScoresFeedTeamIconLocal = com.theathletic.scores.data.local.ScoresFeedTeamIcon
private typealias ScoresFeedWidgetBlockLocal = com.theathletic.scores.data.local.ScoresFeedWidgetBlock
private typealias ScoresFeedInfoBlockLocal = com.theathletic.scores.data.local.ScoresFeedInfoBlock
private typealias ScoresFeedTextBlockLocal = com.theathletic.scores.data.local.ScoresFeedTextBlock
private typealias ScoresFeedBlockLocal = com.theathletic.scores.data.local.ScoresFeedBlock
private typealias ScoresFeedDateTimeFormatLocal = com.theathletic.scores.data.local.ScoresFeedDateTimeFormat

fun ScoresFeedQuery.Data.toLocalModel() = ScoresFeedLocalModel(
    id = scoresFeed.id,
    days = scoresFeed.days.map { it.fragments.scoresFeedDay.toLocalModel },
    navigationBar = scoresFeed.nav.mapNotNull { it.fragments.scoresFeedNavItem.toFollowableId }
)

fun ScoresFeedDayQuery.Data.toLocalModel(): List<ScoresFeedGroupLocal> {
    return scoresFeedDay.mapNotNull { it.fragments.scoresFeedGroup.toLocalModel }
}

private val ScoresFeedDay.toLocalModel: ScoresFeedDayLocal
    get() = ScoresFeedDayLocal(
        id = id,
        day = day.toString(),
        isTopGames = top_games,
        groups = groups.mapNotNull { it.fragments.scoresFeedGroup.toLocalModel }
    )

private val ScoresFeedGroup.toLocalModel: ScoresFeedGroupLocal?
    get() {
        fragments.scoresFeedBaseGroup?.let { group ->
            return ScoresFeedBaseGroup(
                id = group.id,
                title = group.title,
                subTitle = group.subtitle,
                blocks = group.blocks.map { it.fragments.scoresFeedBlock.toLocalModel },
                widget = group.widget?.fragments?.scoresFeedWidgetBlock?.toLocalModel
            )
        }
        fragments.scoresFeedFollowingGroup?.let { group ->
            return ScoresFeedFollowingGroup(
                id = group.id,
                title = group.title,
                subTitle = group.subtitle,
                blocks = group.blocks.map { it.fragments.scoresFeedBlock.toLocalModel },
                widget = group.widget?.fragments?.scoresFeedWidgetBlock?.toLocalModel
            )
        }
        fragments.scoresFeedLeagueGroup?.let { group ->
            return ScoresFeedLeagueGroup(
                id = group.id,
                title = group.title,
                subTitle = group.subtitle,
                league = ScoresFeedLeague(
                    league = group.league.id.toLocalLeague,
                    legacyId = group.league.legacy_id?.toLong(),
                    displayName = group.league.display_name,
                ),
                blocks = group.blocks.map { it.fragments.scoresFeedBlock.toLocalModel },
                widget = group.widget?.fragments?.scoresFeedWidgetBlock?.toLocalModel
            )
        }
        return null
    }

val ScoresFeedBlock.toLocalModel: ScoresFeedBlockLocal
    get() = ScoresFeedBlockLocal(
        id = id,
        gameId = game_id,
        header = header,
        footer = footer,
        gameBlock = game_block.fragments.scoresFeedGameBlock.toLocalModel,
        infoBlock = info_block.fragments.scoresFeedInfoBlock.toLocalModel,
        widget = widget?.fragments?.scoresFeedWidgetBlock?.toLocalModel,
        willUpdate = will_update
    )

private val ScoresFeedGameBlock.toLocalModel: ScoresFeedGameBlockLocal
    get() = ScoresFeedGameBlockLocal(
        id = id,
        gameStatus = game_status.toStatusLocalModel(),
        startedAt = Datetime(started_at ?: 0),
        firstTeam = team1.fragments.scoresFeedTeamBlock.toLocalModel,
        secondTeam = team2.fragments.scoresFeedTeamBlock.toLocalModel
    )

private val ScoresFeedTeamBlock.toLocalModel: ScoresFeedTeamBlockLocal
    get() = ScoresFeedTeamBlockLocal(
        id = id,
        name = name,
        teamInfo = team_info?.fragments?.scoresFeedTeamInfoBlock?.toLocalModel,
        logos = logos.map { it.fragments.teamLogo.toSizedImage() },
        icons = icons.map { it.toLocalType },
        ranking = ranking,
        isTbd = is_tbd
    )

private val ScoresFeedTeamInfoBlock.toLocalModel: ScoresFeedTeamInfoBlockLocal?
    get() {
        fragments.scoresFeedTeamPregameInfoBlock?.let { block ->
            return ScoresFeedTeamPregameInfoBlock(
                id = block.id,
                text = block.text
            )
        }
        fragments.scoresFeedTeamGameInfoBlock?.let { block ->
            return ScoresFeedTeamGameInfoBlock(
                id = block.id,
                score = block.score,
                penaltyScore = block.penalty_score,
                isWinner = block.is_winner ?: false
            )
        }
        return null
    }

private val ScoresFeedInfoBlock.toLocalModel: ScoresFeedInfoBlockLocal
    get() = ScoresFeedInfoBlockLocal(
        id = id,
        text = text.mapNotNull { it.fragments.scoresFeedTextBlock.toLocalModel },
        widget = widget?.fragments?.scoresFeedWidgetBlock?.toLocalModel
    )

private val ScoresFeedTextBlock.toLocalModel: ScoresFeedTextBlockLocal?
    get() {
        fragments.scoresFeedDateTimeTextBlock?.let { block ->
            return ScoresFeedDateTimeTextBlock(
                id = block.id,
                format = block.format?.toLocalType ?: ScoresFeedDateTimeFormatLocal.Unknown,
                type = block.type.toLocalType,
                dateTime = Datetime(block.timestamp),
                isTimeTbd = block.time_tbd,
            )
        }
        fragments.scoresFeedOddsTextBlock?.let { block ->
            return ScoresFeedOddsTextBlock(
                id = block.id,
                type = block.type.toLocalType,
                decimalOdds = block.odds.decimal_odds,
                fractionOdds = block.odds.fraction_odds,
                usOdds = block.odds.us_odds,
            )
        }
        fragments.scoresFeedStandardTextBlock?.let { block ->
            return ScoresFeedStandardTextBlock(
                id = block.id,
                type = block.type.toLocalType,
                text = block.text,
            )
        }
        return null
    }

private val ScoresFeedWidgetBlock.toLocalModel: ScoresFeedWidgetBlockLocal?
    get() {
        fragments.scoresFeedAllGamesWidgetBlock?.let { block ->
            return ScoresFeedAllGamesWidgetBlock(
                id = block.id,
                linkText = block.link_text,
            )
        }
        fragments.scoresFeedBaseballWidgetBlock?.let { block ->
            return ScoresFeedBaseballWidgetBlock(
                id = block.id,
                loadedBases = block.loaded_bases
            )
        }
        fragments.scoresFeedDiscussionWidgetBlock?.let { block ->
            return ScoresFeedDiscussionWidgetBlock(
                id = block.id,
                text = block.text
            )
        }
        return null
    }

private fun TeamLogo.toSizedImage() = SizedImage(
    width = width,
    height = height,
    uri = uri
)

private val ScoresFeedNavItem.toFollowableId: FollowableId?
    get() {
        fragments.scoresFeedLeagueNavItem?.let { league ->
            return FollowableId(
                id = league.league.legacy_id.toString(),
                type = Followable.Type.LEAGUE
            )
        }
        fragments.scoresFeedTeamNavItem?.let { team ->
            return team.team.legacy_team?.id?.let { legacyId ->
                FollowableId(id = legacyId, type = Followable.Type.TEAM)
            }
        }
        return null
    }

private val ScoresFeedTeamIcon.toLocalType: ScoresFeedTeamIconLocal
    get() = when (this) {
        ScoresFeedTeamIcon.american_football_possession -> ScoresFeedTeamIconLocal.AmericanFootballPossession
        ScoresFeedTeamIcon.soccer_redcard -> ScoresFeedTeamIconLocal.SoccerRedCard
        else -> ScoresFeedTeamIconLocal.Unknown
    }

private val ScoresFeedDateTimeFormat.toLocalType: ScoresFeedDateTimeFormatLocal
    get() = when (this) {
        ScoresFeedDateTimeFormat.date -> ScoresFeedDateTimeFormatLocal.Date
        ScoresFeedDateTimeFormat.time -> ScoresFeedDateTimeFormatLocal.Time
        ScoresFeedDateTimeFormat.datetime -> ScoresFeedDateTimeFormatLocal.DateAndTime
        else -> ScoresFeedDateTimeFormatLocal.Unknown
    }

private val ScoresFeedTextType.toLocalType: ScoresFeedTextTypeLocal
    get() = when (this) {
        ScoresFeedTextType.datetime -> ScoresFeedTextTypeLocal.DateTime
        ScoresFeedTextType.default -> ScoresFeedTextTypeLocal.Default
        ScoresFeedTextType.live -> ScoresFeedTextTypeLocal.Live
        ScoresFeedTextType.situation -> ScoresFeedTextTypeLocal.Situation
        ScoresFeedTextType.status -> ScoresFeedTextTypeLocal.Status
        else -> ScoresFeedTextTypeLocal.Unknown
    }

// TODO: Need to work out the best place for this to live so assessable by all modules
//  once the ScoresGraphqlApi becomes Deprecated
val League.toGraphqlLeagueCode: LeagueCode
    get() = when (this) {
        // Football - Soccer
        League.EPL -> LeagueCode.epl
        League.CHAMPIONS_LEAGUE -> LeagueCode.ucl
        League.INTERNATIONAL -> LeagueCode.euc
        League.INTERNATIONAL_FRIENDLIES -> LeagueCode.fri
        League.MLS -> LeagueCode.mls
        League.UEL -> LeagueCode.uel
        League.SCOTTISH_PREMIERE -> LeagueCode.pre
        League.NWSL -> LeagueCode.nws
        League.UWC -> LeagueCode.uwc
        League.WORLD_CUP -> LeagueCode.woc
        League.WOMENS_WORLD_CUP -> LeagueCode.wwc
        League.EFL -> LeagueCode.cha
        League.LEAGUE_ONE -> LeagueCode.leo
        League.LEAGUE_TWO -> LeagueCode.let
        League.FA_CUP -> LeagueCode.fac
        League.CARABAO_CUP -> LeagueCode.lec
        League.LA_LIGA -> LeagueCode.prd
        League.COPA_DEL_REY -> LeagueCode.cdr
        // American Football
        League.NFL -> LeagueCode.nfl
        League.NCAA_FB -> LeagueCode.ncaafb
        // Basketball
        League.NBA -> LeagueCode.nba
        League.WNBA -> LeagueCode.wnba
        League.NCAA_BB -> LeagueCode.ncaamb
        League.NCAA_WB -> LeagueCode.ncaawb
        // Hockey
        League.NHL -> LeagueCode.nhl
        // Baseball
        League.MLB -> LeagueCode.mlb
        else -> LeagueCode.UNKNOWN__
    }

val LeagueCode.toLocalLeague: League
    get() = when (this) {
        // Football - Soccer
        LeagueCode.epl -> League.EPL
        LeagueCode.ucl -> League.CHAMPIONS_LEAGUE
        LeagueCode.euc -> League.INTERNATIONAL
        LeagueCode.fri -> League.INTERNATIONAL_FRIENDLIES
        LeagueCode.mls -> League.MLS
        LeagueCode.uel -> League.UEL
        LeagueCode.pre -> League.SCOTTISH_PREMIERE
        LeagueCode.nws -> League.NWSL
        LeagueCode.uwc -> League.UWC
        LeagueCode.woc -> League.WORLD_CUP
        LeagueCode.wwc -> League.WOMENS_WORLD_CUP
        LeagueCode.cha -> League.EFL
        LeagueCode.leo -> League.LEAGUE_ONE
        LeagueCode.let -> League.LEAGUE_TWO
        LeagueCode.fac -> League.FA_CUP
        LeagueCode.lec -> League.CARABAO_CUP
        LeagueCode.cdr -> League.COPA_DEL_REY
        LeagueCode.prd -> League.LA_LIGA
        // American Football
        LeagueCode.nfl -> League.NFL
        LeagueCode.ncaafb -> League.NCAA_FB
        // Basketball
        LeagueCode.nba -> League.NBA
        LeagueCode.wnba -> League.WNBA
        LeagueCode.ncaamb -> League.NCAA_BB
        LeagueCode.ncaawb -> League.NCAA_WB
        // Hockey
        LeagueCode.nhl -> League.NHL
        // Baseball
        LeagueCode.mlb -> League.MLB
        else -> League.UNKNOWN
    }