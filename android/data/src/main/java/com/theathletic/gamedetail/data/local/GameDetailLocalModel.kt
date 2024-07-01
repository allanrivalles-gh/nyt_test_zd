package com.theathletic.gamedetail.data.local

import com.theathletic.data.LocalModel
import com.theathletic.data.SizedImages
import com.theathletic.datetime.Datetime
import com.theathletic.entity.main.Sport
import com.theathletic.type.GameStatCategory
import java.util.concurrent.TimeUnit

data class GameDetailLocalModel(
    val id: String,
    val awayTeam: GameTeam?,
    val homeTeam: GameTeam?,
    val status: GameStatus,
    val scheduleAt: Datetime,
    val isScheduledTimeTbd: Boolean,
    val league: League,
    val sport: Sport,
    val venue: String?,
    val venueCity: String?,
    val clock: String?,
    val period: Period,
    val permalink: String?,
    val events: List<GameEvent>,
    val timeline: List<TimelineEvent>,
    val awayTeamHomeTeamStats: List<Pair<Statistic, Statistic>>,
    val awayTeamHomeTeamSeasonStats: List<Pair<RankedStat, RankedStat>>,
    val oddsPregame: List<GameOdds>,
    val broadcastNetwork: String?,
    val coverage: List<CoverageDataType>,
    val gradeStatus: GradeStatus?,
    val sportExtras: SportExtras?,
    val gameTicket: GameTicket?,
    val seasonName: String?,
    val topComments: List<TopComment>,
    val areCommentsDiscoverable: Boolean
) : LocalModel {

    interface SportExtras

    data class AmericanFootballExtras(
        val possession: Possession?,
        val lastPlay: String?,
        val scoringPlays: List<ScoringPlay>,
        val recentPlays: List<AmericanFootballPlay>,
        val weather: Weather?
    ) : SportExtras

    data class BasketballExtras(
        val recentPlays: List<BasketballPlay>
    ) : SportExtras

    data class HockeyExtras(
        val scoringPlays: List<HockeyPlay>,
        val recentPlays: List<HockeyPlay>
    ) : SportExtras

    data class BaseballExtras(
        val scoringPlays: List<BaseballTeamPlay>,
        val inningPlays: List<BaseballInningPlay>,
        val currentInningPlays: List<BaseballPlay>,
        val outcome: BaseballOutcome?,
        val pitching: BaseballPitching?,
        val inning: Int?,
        val inningHalf: InningHalf?
    ) : SportExtras

    data class SoccerExtras(
        val matchOfficials: List<SoccerOfficial>,
        val keyMoments: List<SoccerPlay>,
        val recentMoments: List<SoccerPlay>,
    ) : SportExtras

    interface GameTeam {
        val id: String
        val team: Team?
        val score: Int?
        val periodScore: List<PeriodScore>
        val lineUp: LineUp?
        val recentGames: List<RecentGame>
        val currentRecord: String?
        val teamLeaders: List<StatLeader>
        val topPerformers: List<StatLeader>
        val injuries: List<Injury>?
        val players: List<PlayerGradesLocalModel.Player>
    }

    data class GameDetailsTeam(
        override val id: String,
        override val team: Team?,
        override val score: Int?,
        override val periodScore: List<PeriodScore> = emptyList(),
        override val lineUp: LineUp? = null,
        override val recentGames: List<RecentGame> = emptyList(),
        override val currentRecord: String? = null,
        override val teamLeaders: List<StatLeader> = emptyList(),
        override val topPerformers: List<StatLeader> = emptyList(),
        override val injuries: List<Injury>?,
        override val players: List<PlayerGradesLocalModel.Player> = emptyList()
    ) : GameTeam

    data class AmericanFootballGameTeam(
        override val id: String,
        override val team: Team?,
        override val score: Int?,
        override val periodScore: List<PeriodScore>,
        override val lineUp: LineUp?,
        override val recentGames: List<RecentGame>,
        override val currentRecord: String?,
        override val teamLeaders: List<StatLeader>,
        override val topPerformers: List<StatLeader>,
        override val injuries: List<Injury>?,
        override val players: List<PlayerGradesLocalModel.Player>,
        val currentRanking: Int?,
        val remainingTimeouts: Int,
        val usedTimeouts: Int
    ) : GameTeam

    data class SoccerGameTeam(
        override val id: String,
        override val team: Team?,
        override val score: Int?,
        override val periodScore: List<PeriodScore>,
        override val lineUp: LineUp?,
        override val recentGames: List<RecentGame>,
        override val currentRecord: String?,
        override val teamLeaders: List<StatLeader>,
        override val topPerformers: List<StatLeader>,
        override val injuries: List<Injury>?,
        override val players: List<PlayerGradesLocalModel.Player>,
        val penaltyScore: Int?,
        val expectedGoals: Statistic?,
    ) : GameTeam

    data class BasketballGameTeam(
        override val id: String,
        override val team: Team?,
        override val score: Int?,
        override val periodScore: List<PeriodScore>,
        override val lineUp: LineUp?,
        override val recentGames: List<RecentGame>,
        override val currentRecord: String?,
        override val teamLeaders: List<StatLeader>,
        override val topPerformers: List<StatLeader>,
        override val injuries: List<Injury>?,
        override val players: List<PlayerGradesLocalModel.Player>,
        val currentRanking: Int?,
        val remainingTimeouts: Int,
        val usedTimeouts: Int
    ) : GameTeam

    data class HockeyGameTeam(
        override val id: String,
        override val team: Team?,
        override val score: Int?,
        override val periodScore: List<PeriodScore>,
        override val lineUp: LineUp?,
        override val recentGames: List<RecentGame>,
        override val currentRecord: String?,
        override val teamLeaders: List<StatLeader>,
        override val topPerformers: List<StatLeader>,
        override val injuries: List<Injury>?,
        override val players: List<PlayerGradesLocalModel.Player>,
        val strength: HockeyStrength?
    ) : GameTeam

    data class BaseballGameTeam(
        override val id: String,
        override val team: Team?,
        override val score: Int?,
        override val periodScore: List<PeriodScore>,
        override val lineUp: LineUp?,
        override val recentGames: List<RecentGame>,
        override val currentRecord: String?,
        override val teamLeaders: List<StatLeader>,
        override val topPerformers: List<StatLeader>,
        override val injuries: List<Injury>?,
        override val players: List<PlayerGradesLocalModel.Player>,
        val totalRuns: Int?,
        val totalHits: Int?,
        val totalErrors: Int?,
        val startingPitcher: BaseballPlayer?,
        val pitcherState: PitcherState,
        val inningScores: List<InningScore>
    ) : GameTeam

    data class Team(
        val id: String,
        val alias: String,
        val name: String,
        val displayName: String,
        val logos: SizedImages,
        val primaryColor: String?,
        val accentColor: String?,
        val currentRanking: Int?
    )

    data class League(
        val legacyLeague: com.theathletic.entity.main.League,
        val id: String,
        val alias: String,
        val displayName: String
    )

    interface TeamMember {
        val id: String
        val country: String?
        val displayName: String?
        val firstName: String?
        val lastName: String?
        val position: PlayerPosition?
    }

    data class GenericTeamMember(
        override val id: String,
        override val country: String?,
        override val displayName: String?,
        override val firstName: String?,
        override val lastName: String?,
        override val position: PlayerPosition?
    ) : TeamMember

    data class BaseballTeamMember(
        override val id: String,
        override val country: String?,
        override val displayName: String?,
        override val firstName: String?,
        override val lastName: String?,
        override val position: PlayerPosition?,
        val headshots: SizedImages,
        val batHandedness: Handedness,
        val throwHandedness: Handedness,
        val seasonStats: List<Statistic>,
        val teamColor: String?
    ) : TeamMember

    interface GameEvent {
        val id: String
        val team: Team
        val occurredAt: Datetime
        val matchTimeDisplay: String
        val period: Period
    }

    interface TimelineEvent {
        val id: String
        val occurredAt: Datetime
        val period: Period
    }

    data class CardEvent(
        override val id: String,
        override val occurredAt: Datetime,
        override val period: Period,
        override val team: Team,
        override val matchTimeDisplay: String,
        val cardedPlayer: GenericTeamMember,
        val cardType: CardType
    ) : GameEvent, TimelineEvent

    data class GoalEvent(
        override val id: String,
        override val occurredAt: Datetime,
        override val period: Period,
        override val team: Team,
        override val matchTimeDisplay: String,
        val scorer: GenericTeamMember,
        val goalType: GoalType
    ) : GameEvent, TimelineEvent

    data class SubstitutionEvent(
        override val id: String,
        override val occurredAt: Datetime,
        override val period: Period,
        override val team: Team,
        override val matchTimeDisplay: String,
        val playerOn: GenericTeamMember,
        val playerOff: GenericTeamMember
    ) : GameEvent, TimelineEvent

    data class PenaltyShotEvent(
        override val id: String,
        override val occurredAt: Datetime,
        override val period: Period,
        override val team: Team,
        override val matchTimeDisplay: String,
        val penaltyTaker: GenericTeamMember,
        val outcome: PenaltyOutcome
    ) : GameEvent, TimelineEvent

    data class PeriodEvent(
        override val id: String,
        override val occurredAt: Datetime,
        override val period: Period,
    ) : TimelineEvent

    data class Player(
        val id: String,
        val displayName: String?,
        val jerseyNumber: String?,
        val place: Int,
        val position: PlayerPosition,
        val regularPosition: PlayerPosition,
        val statistics: List<Statistic>,
        val starter: Boolean,
        val playerOrder: Int,
        val outcome: String?,
        val captain: Boolean = false,
        val grade: PlayerGrade? = null
    )

    data class PlayerGrade(
        val averageDisplay: String,
        val gameId: String,
        val grade: Int,
        val order: Int,
        val playerId: String,
        val total: Int,
    )

    data class LineUp(
        val formation: String?,
        val formationImage: String?,
        val players: List<Player>,
        val manager: String?
    )

    data class RankedStat(
        val id: String,
        val parentStatType: String?,
        val parentStatCategory: GameStatCategory?,
        val rank: Int,
        val statCategory: GameStatCategory?,
        val statHeaderLabel: String?,
        val statLabel: String,
        val statType: String,
        val statValue: String
    )

    interface Statistic {
        val id: String
        val category: StatisticCategory
        val headerLabel: String?
        val longHeaderLabel: String?
        val label: String
        val type: String
        val lessIsBest: Boolean
        val isChildStat: Boolean
        val referenceOnly: Boolean
    }

    data class DecimalStatistic(
        override val id: String,
        override val label: String,
        override val type: String,
        override val category: StatisticCategory,
        override val headerLabel: String?,
        override val longHeaderLabel: String?,
        override val lessIsBest: Boolean,
        override val isChildStat: Boolean,
        override val referenceOnly: Boolean = false,
        val decimalValue: Double?,
        val stringValue: String?
    ) : Statistic, Comparable<DecimalStatistic> {
        override fun compareTo(other: DecimalStatistic): Int {
            return other.decimalValue?.let { this.decimalValue?.compareTo(it) } ?: 0
        }
    }

    data class FractionStatistic(
        override val id: String,
        override val label: String,
        override val type: String,
        override val category: StatisticCategory,
        override val headerLabel: String?,
        override val longHeaderLabel: String?,
        override val lessIsBest: Boolean,
        override val isChildStat: Boolean,
        override val referenceOnly: Boolean = false,
        val denominatorValue: Int?,
        val numeratorValue: Int?,
        val separator: FractionSeparator
    ) : Statistic, Comparable<FractionStatistic> {
        override fun compareTo(other: FractionStatistic): Int {
            return other.numeratorValue?.let { this.numeratorValue?.compareTo(it) } ?: 0
        }
    }

    data class IntegerStatistic(
        override val id: String,
        override val label: String,
        override val type: String,
        override val category: StatisticCategory,
        override val headerLabel: String?,
        override val longHeaderLabel: String?,
        override val lessIsBest: Boolean,
        override val isChildStat: Boolean,
        override val referenceOnly: Boolean = false,
        val intValue: Int?
    ) : Statistic, Comparable<IntegerStatistic> {
        override fun compareTo(other: IntegerStatistic): Int {
            return other.intValue?.let { this.intValue?.compareTo(it) } ?: 0
        }
    }

    data class PercentageStatistic(
        override val id: String,
        override val label: String,
        override val type: String,
        override val category: StatisticCategory,
        override val headerLabel: String?,
        override val longHeaderLabel: String?,
        override val lessIsBest: Boolean,
        override val isChildStat: Boolean,
        override val referenceOnly: Boolean = false,
        val decimalValue: Double?,
        val stringValue: String?
    ) : Statistic, Comparable<PercentageStatistic> {
        override fun compareTo(other: PercentageStatistic): Int {
            return other.decimalValue?.let { this.decimalValue?.compareTo(it) } ?: 0
        }
    }

    data class StringStatistic(
        override val id: String,
        override val category: StatisticCategory,
        override val headerLabel: String?,
        override val longHeaderLabel: String?,
        override val label: String,
        override val type: String,
        override val lessIsBest: Boolean,
        override val isChildStat: Boolean,
        override val referenceOnly: Boolean = false,
        val value: String?
    ) : Statistic, Comparable<StringStatistic> {
        override fun compareTo(other: StringStatistic): Int {
            return other.value?.let { this.value?.compareTo(it) } ?: 0
        }
    }

    data class TimeStatistic(
        override val id: String,
        override val category: StatisticCategory,
        override val headerLabel: String?,
        override val longHeaderLabel: String?,
        override val label: String,
        override val type: String,
        override val lessIsBest: Boolean,
        override val isChildStat: Boolean,
        override val referenceOnly: Boolean = false,
        val hours: Int,
        val minutes: Int,
        val seconds: Int,
        val stringValue: String?
    ) : Statistic, Comparable<TimeStatistic> {
        override fun compareTo(other: TimeStatistic): Int {
            val totalSeconds = convertToSeconds(hours, minutes, seconds)
            val totalOtherSeconds = convertToSeconds(other.hours, other.minutes, other.seconds)
            return totalSeconds.compareTo(totalOtherSeconds)
        }

        private fun convertToSeconds(hrs: Int, mins: Int, secs: Int): Long {
            return TimeUnit.HOURS.toSeconds(hrs.toLong()) +
                TimeUnit.MINUTES.toSeconds(mins.toLong()) + secs.toLong()
        }
    }

    interface ScoreType

    data class PeriodScore(
        val id: String,
        val period: Period,
        val scoreDisplay: String?
    ) : ScoreType

    data class Possession(
        val down: Int?,
        val locationTeam: Team?,
        val locationYardLine: Int?,
        val team: Team?,
        val yards: Int?,
        val goalToGo: Boolean,
        val driveInfo: DriveInfo?
    )

    data class DriveInfo(
        val duration: String?,
        val playCount: Int,
        val yards: Int,
    )

    interface GameOdds {
        val id: String
        val balancedLine: Boolean
        val bettingOpen: Boolean
        val betPeriod: String?
        val line: String?
        val price: GameOddsPrice
    }

    data class GameOddsMoneyLine(
        override val id: String,
        override val balancedLine: Boolean,
        override val bettingOpen: Boolean,
        override val betPeriod: String?,
        override val line: String?,
        override val price: GameOddsPrice,
        val team: Team?
    ) : GameOdds

    data class GameOddsSpread(
        override val id: String,
        override val balancedLine: Boolean,
        override val bettingOpen: Boolean,
        override val betPeriod: String?,
        override val line: String?,
        override val price: GameOddsPrice,
        val team: Team?
    ) : GameOdds

    data class GameOddsTotals(
        override val id: String,
        override val balancedLine: Boolean,
        override val bettingOpen: Boolean,
        override val betPeriod: String?,
        override val line: String?,
        override val price: GameOddsPrice,
        val direction: String?
    ) : GameOdds

    data class GameOddsPrice(
        val oddsFraction: String?,
        val oddsDecimal: String?,
        val oddsUs: String?
    )

    interface Play {
        val id: String
        val description: String
        val headerLabel: String?
        val occurredAt: Datetime
    }

    data class ScoringPlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        val period: Period,
        val team: Team?,
        val awayTeamScore: Int,
        val homeTeamScore: Int,
        val clock: String,
        val plays: Int,
        val yards: Int,
        val scoreType: AmericanFootballScoreType
    ) : Play

    data class BasketballPlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        val period: Period,
        val team: Team?,
        val awayTeamScore: Int,
        val homeTeamScore: Int,
        val clock: String,
        val playType: BasketballPlayType
    ) : Play

    interface HockeyPlay : Play {
        val period: Period
        val awayTeamScore: Int
        val homeTeamScore: Int
        val team: Team?
    }

    interface SoccerPlay : Play {
        val awayTeamScore: Int
        val homeTeamScore: Int
        val period: Period
        val team: Team?
        val gameTime: String?
        val playType: SoccerPlayType
    }

    data class SoccerKeyPlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        override val awayTeamScore: Int,
        override val homeTeamScore: Int,
        override val team: Team?,
        override val gameTime: String?,
        override val playType: SoccerPlayType,
        override val period: Period,
        val keyPlay: Boolean,
        val awayChancesCreated: Int,
        val homeChancesCreated: Int,
    ) : SoccerPlay

    data class SoccerShootoutPlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        override val awayTeamScore: Int,
        override val homeTeamScore: Int,
        override val team: Team?,
        override val gameTime: String?,
        override val playType: SoccerPlayType,
        override val period: Period,
        val shooter: GenericTeamMember?,
        val awayShootoutGoals: Int,
        val homeShootoutGoals: Int,
    ) : SoccerPlay

    data class HockeyTeamPlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        override val period: Period,
        override val awayTeamScore: Int,
        override val homeTeamScore: Int,
        override val team: Team?,
        val clock: String,
        val strength: HockeyStrength,
        val type: HockeyPlayType
    ) : HockeyPlay

    data class HockeyStandardPlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        override val period: Period,
        override val awayTeamScore: Int,
        override val homeTeamScore: Int,
        override val team: Team?,
        val clock: String,
        val type: HockeyPlayType,
        val awayShotsAtGoal: Int?,
        val homeShotsAtGoal: Int?
    ) : HockeyPlay

    data class HockeyShootoutPlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        override val period: Period,
        override val awayTeamScore: Int,
        override val homeTeamScore: Int,
        override val team: Team?,
        val type: HockeyPlayType,
        val awayShootoutGoals: Int,
        val awayShootoutShots: Int,
        val homeShootoutGoals: Int,
        val homeShootoutShots: Int,
        val playerHeadshots: SizedImages?,
        @Deprecated("Use playerHeadshots above") val playerHeadshotUri: String?
    ) : HockeyPlay

    interface BaseballPlay : Play

    interface BaseballPlayWithInnings : BaseballPlay {
        val inning: Int
        val inningHalf: InningHalf?
    }

    interface BaseballPlayWithScores : BaseballPlayWithInnings {
        val awayTeamScore: Int
        val homeTeamScore: Int
        val team: Team?
    }

    data class BaseballLineUpChangePlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        override val inning: Int,
        override val inningHalf: InningHalf?
    ) : BaseballPlayWithInnings

    data class BaseballPitchPlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        val bases: List<Int>,
        val hitZone: Int?,
        val number: Int,
        val pitchDescription: String?,
        val pitchOutcome: BaseballPitchOutcome,
        val pitchZone: Int?
    ) : BaseballPlay

    data class BaseballStandardPlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        override val inning: Int,
        override val inningHalf: InningHalf?,
        val plays: List<BaseballPlay>
    ) : BaseballPlayWithInnings

    data class BaseballTeamPlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        override val team: Team?,
        override val awayTeamScore: Int,
        override val homeTeamScore: Int,
        override val inning: Int,
        override val inningHalf: InningHalf?,
        val plays: List<BaseballPlay>
    ) : BaseballPlayWithScores

    @Deprecated("This class is no longer supported, use BaseballStandardPlay instead")
    data class BaseballInningPlay(
        val id: String,
        val description: String,
        val occurredAt: Datetime,
        val isLineUpChange: Boolean
    )

    data class AmericanFootballDrivePlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        val awayTeamScore: Int,
        val homeTeamScore: Int,
        val duration: String?,
        val period: Period,
        val playCount: Int,
        val yards: Int,
        val team: Team,
        val plays: List<AmericanFootballPlay>
    ) : Play

    data class AmericanFootballPlay(
        override val id: String,
        override val description: String,
        override val headerLabel: String?,
        override val occurredAt: Datetime,
        val awayTeamScore: Int,
        val homeTeamScore: Int,
        val clock: String,
        val period: Period?,
        val possession: Possession?,
        val isScoringPlay: Boolean,
        val playType: AmericanFootballPlayType,
        val team: Team?
    ) : Play

    data class BaseballOutcome(
        val id: String,
        val inning: Int,
        val inningHalf: InningHalf?,
        val balls: Int,
        val strikes: Int,
        val outs: Int,
        val batter: BaseballPlayer?,
        val pitcher: BaseballPlayer?,
        val nextBatter: BaseballPlayer?,
        val loadedBases: List<Int>
    )

    data class BaseballPitching(
        val winPitcher: BaseballPlayer?,
        val lossPitcher: BaseballPlayer?,
        val savePitcher: BaseballPlayer?
    )

    data class StatLeader(
        val id: String,
        val playerName: String?,
        val jerseyNumber: String?,
        val playerPosition: PlayerPosition?,
        val headshots: SizedImages,
        val statLabel: String?,
        val stats: List<Statistic>,
        val teamAlias: String?,
        val teamLogos: SizedImages?,
        val primaryColor: String?
    )

    data class Injury(
        val injury: String,
        val comment: String?,
        val playerName: String?,
        val playerPosition: PlayerPosition?,
        val headshots: SizedImages,
        val status: InjuryStatus
    )

    data class RecentGame(
        val id: String,
        val period: Period,
        val scheduleAt: Datetime,
        val awayTeam: RecentGameTeam?,
        val homeTeam: RecentGameTeam?
    )

    data class RecentGameTeam(
        val id: String,
        val score: Int?,
        val alias: String,
        val logos: SizedImages
    )

    data class Weather(
        val id: String,
        val outlook: String?,
        val tempCelsius: Int?,
        val tempFahrenheit: Int?
    )

    data class BaseballPlayer(
        val id: String,
        val player: BaseballTeamMember,
        val gameStats: List<Statistic>,
        val seasonAvg: Statistic?
    )

    data class InningScore(
        val id: String,
        val inning: Int,
        val runs: Int,
        val hits: Int,
        val errors: Int
    ) : ScoreType

    data class GameScore(
        val score: Int,
    ) : ScoreType

    data class Logo(
        val width: Int,
        val height: Int,
        val uri: String
    )

    data class Headshot(
        val width: Int,
        val height: Int,
        val uri: String
    )

    data class SoccerOfficial(
        val name: String,
        val officialType: SoccerOfficialType
    )

    data class GameTicket(
        val logoDarkMode: SizedImages,
        val logoLightMode: SizedImages,
        val minPrice: List<GameTicketPrice>,
        val url: String,
        val provider: String
    )

    data class GameTicketPrice(
        val amount: Double,
        val currency: GameTicketCurrency
    )

    data class TopComment(
        val id: String,
        val authorName: String,
        val authorGameFlairs: List<AuthorGameFlair>,
        val authorUserLevel: Int,
        val avatarUrl: String?,
        val comment: String,
        val commentedAt: Long,
        val commentMetadata: String? = null,
        val hasUserLiked: Boolean = false,
        val likesCount: Int,
        val parentId: String,
        val permalink: String
    ) {
        val isStaff = authorUserLevel > 0
    }

    data class AuthorGameFlair(
        val name: String,
        val iconContrastColor: String
    )

    val firstTeam = when {
        sport.homeTeamFirst -> homeTeam
        else -> awayTeam
    }

    val secondTeam = when {
        sport.homeTeamFirst -> awayTeam
        else -> homeTeam
    }

    val isGameInProgressOrCompleted =
        status == GameStatus.IN_PROGRESS || status == GameStatus.FINAL

    val isGameScheduled = status == GameStatus.SCHEDULED

    val isGameInProgress = status == GameStatus.IN_PROGRESS

    val isGameCompleted = status == GameStatus.FINAL
}

val listOfSportsWithStatsTab = listOf(
    Sport.FOOTBALL,
    Sport.BASKETBALL,
    Sport.HOCKEY,
    Sport.BASEBALL
)

data class GameLineUpAndStats(
    val awayTeamLineUp: GameDetailLocalModel.LineUp?,
    val homeTeamLineUp: GameDetailLocalModel.LineUp?,
    val teamStats: List<Pair<GameDetailLocalModel.Statistic, GameDetailLocalModel.Statistic>>
) : LocalModel {
    fun firstTeamLineUp(sport: Sport) = when {
        sport.homeTeamFirst -> homeTeamLineUp
        else -> awayTeamLineUp
    }

    fun secondTeamLineUp(sport: Sport) = when {
        sport.homeTeamFirst -> awayTeamLineUp
        else -> homeTeamLineUp
    }
}

enum class GameStatus {
    CANCELED,
    FINAL,
    IN_PROGRESS,
    IF_NECESSARY,
    POSTPONED,
    SCHEDULED,
    SUSPENDED,
    DELAYED,
    UNKNOWN
}

enum class GradeStatus {
    DISABLED,
    ENABLED,
    LOCKED,
    UNSUPPORTED, // For sports and leagues that don't support player grades. Determined client side.
    UNKNOWN
}

enum class SoccerOfficialType(val label: String) {
    ASSISTANT_REFEREE("Assistant"),
    FOURTH_OFFICIAL("Fourth"),
    REFEREE("Referee"),
    ASSISTANT_VAR("Assistant VAR"),
    VAR("VAR"),
    UNKNOWN("Unknown")
}

enum class PlayerPosition(
    val alias: String,
    val order: Int
) {
    // SOCCER
    GOALKEEPER("GK", 1),
    DEFENDER("D", 2),
    WING_BACK("D", 3),
    DEFENSIVE_MIDFIELDER("M", 4),
    MIDFIELDER("M", 5),
    ATTACKING_MIDFIELDER("M", 6),
    ATTACKER("A", 7),
    STRIKER("A", 8),
    SUBSTITUTE("S", 9),

    // AMERICAN FOOTBALL - Order not important
    CORNER_BACK("CB", Int.MAX_VALUE),
    DEFENSIVE_BACK("DB", Int.MAX_VALUE),
    DEFENSIVE_END("DE", Int.MAX_VALUE),
    DEFENSIVE_LINEMAN("DL", Int.MAX_VALUE),
    DEFENSIVE_TACKLE("DT", Int.MAX_VALUE),
    FULLBACK("FB", Int.MAX_VALUE),
    FREE_SAFETY("FS", Int.MAX_VALUE),
    KICKER("K", Int.MAX_VALUE),
    INSIDE_LINEBACKER("ILB", Int.MAX_VALUE),
    LINEBACKER("LB", Int.MAX_VALUE),
    LONG_SNAPPER("LS", Int.MAX_VALUE),
    MIDDLE_LINEBACKER("MLB", Int.MAX_VALUE),
    NOSE_TACKLE("NT", Int.MAX_VALUE),
    OFFENSIVE_GUARD("OG", Int.MAX_VALUE),
    OFFENSIVE_LINEMAN("OL", Int.MAX_VALUE),
    OFFENSIVE_TACKLE("T", Int.MAX_VALUE),
    OUTSIDE_LINEBACKER("OLB", Int.MAX_VALUE),
    PUNTER("P", Int.MAX_VALUE),
    QUARTERBACK("QB", Int.MAX_VALUE),
    RUNNING_BACK("RB", Int.MAX_VALUE),
    SAFETY("S", Int.MAX_VALUE),
    STRONG_SAFETY("SS", Int.MAX_VALUE),
    TIGHT_END("TE", Int.MAX_VALUE),
    WIDE_RECEIVER("WR", Int.MAX_VALUE),

    // BASKETBALL
    POINT_GUARD("PG", 1),
    POWER_FORWARD("PF", 4),
    SHOOTING_GUARD("SG", 2),
    SMALL_FORWARD("SF", 3),
    FORWARD_CENTER("FC", 5),
    FORWARD_GUARD("FG", 6),
    GUARD("G", 7),
    GUARD_FORWARD("GF", 8),

    // HOCKEY
    GOALIE("G", Int.MAX_VALUE),
    DEFENSE("D", Int.MAX_VALUE),
    FORWARD("F", Int.MAX_VALUE),
    LEFT_WING("LW", Int.MAX_VALUE),
    RIGHT_WING("RW", Int.MAX_VALUE),

    // BASEBALL
    CATCHER("C", Int.MAX_VALUE),
    CENTER_FIELD("CF", Int.MAX_VALUE),
    DESIGNATED_HITTER("DH", Int.MAX_VALUE),
    FIRST_BASE("1B", Int.MAX_VALUE),
    LEFT_FIELD("LF", Int.MAX_VALUE),
    PINCH_HITTER("PH", Int.MAX_VALUE),
    PINCH_RUNNER("PR", Int.MAX_VALUE),
    PITCHER("P", Int.MAX_VALUE),
    RELIEF_PITCHER("RP", Int.MAX_VALUE),
    RIGHT_FIELD("RF", Int.MAX_VALUE),
    SECOND_BASE("2B", Int.MAX_VALUE),
    SHORTSTOP("SS", Int.MAX_VALUE),
    STARTING_PITCHER("SP", Int.MAX_VALUE),
    THIRD_BASE("3B", Int.MAX_VALUE),

    // SHARED POSITIONS
    CENTER("C", Int.MAX_VALUE), // AF, BB, HK

    // STAFF
    HEAD_COACH("HC", Int.MAX_VALUE),
    COACH("C", Int.MAX_VALUE),
    OFFENSIVE_COORDINATOR("OC", Int.MAX_VALUE),
    DEFENSIVE_COORDINATOR("DC", Int.MAX_VALUE),

    UNKNOWN("UNK", Int.MAX_VALUE)
}

enum class CardType {
    YELLOW,
    YELLOW_2ND,
    RED,
    UNKNOWN
}

enum class GoalType {
    GOAL,
    OWN_GOAL,
    PENALTY_GOAL,
    UNKNOWN
}

enum class PenaltyOutcome {
    MISSED,
    SAVED,
    SCORED,
    UNKNOWN
}

enum class Period {
    KICK_OFF,
    FIRST_HALF,
    SECOND_HALF,
    EXTRA_TIME_FIRST_HALF,
    EXTRA_TIME_SECOND_HALF,
    PENALTY_SHOOTOUT,
    FIRST_QUARTER,
    SECOND_QUARTER,
    THIRD_QUARTER,
    FOURTH_QUARTER,
    HALF_TIME,
    FULL_TIME,
    FULL_TIME_OT,
    FULL_TIME_OT_2,
    FULL_TIME_OT_3,
    FULL_TIME_OT_4,
    FULL_TIME_OT_5,
    FULL_TIME_OT_6,
    FULL_TIME_OT_7,
    FULL_TIME_OT_8,
    FULL_TIME_OT_9,
    FULL_TIME_OT_10,
    OVER_TIME,
    OVER_TIME_2,
    OVER_TIME_3,
    OVER_TIME_4,
    OVER_TIME_5,
    OVER_TIME_6,
    OVER_TIME_7,
    OVER_TIME_8,
    OVER_TIME_9,
    OVER_TIME_10,
    PRE_GAME,
    FIRST_PERIOD,
    SECOND_PERIOD,
    THIRD_PERIOD,
    SHOOTOUT,
    FULL_TIME_SO,
    UNKNOWN
}

enum class StatisticCategory {
    STANDARD,
    ADVANCED,
    SUMMARY,
    PASSING,
    RUSHING,
    RECEIVING,
    PUNTS,
    PUNT_RETURNS,
    PENALTIES,
    MISC_RETURNS,
    KICKING,
    KICKOFFS,
    KICK_RETURNS,
    INT_RETURNS,
    FUMBLES,
    FIELD_GOALS,
    EXTRA_POINTS_KICKS,
    EXTRA_POINTS_CONVERSIONS,
    DEFENSE,
    EFFICIENCY_GOAL_TO_GO,
    EFFICIENCY_RED_ZONE,
    EFFICIENCY_THIRD_DOWN,
    EFFICIENCY_FOURTH_DOWN,
    FIRST_DOWNS,
    INTERCEPTIONS,
    TOUCHDOWNS,
    STARTERS,
    BENCH,
    GOALIES,
    SKATERS,
    BATTING,
    PITCHING,
    UNKNOWN
}

enum class AmericanFootballScoreType {
    FIELD_GOAL,
    SAFETY,
    TOUCHDOWN,
    UNKNOWN
}

enum class BasketballPlayType {
    CHALLENGE_REVIEW,
    CHALLENGE_TIMEOUT,
    CLEAR_PATH_FOUL,
    DEAD_BALL,
    DEFAULT_VIOLATION,
    DEFENSIVE_GOAL_TENDING,
    DEFENSIVE_THREE_SECONDS,
    DELAY,
    DOUBLE_LANE,
    EJECTION,
    END_PERIOD,
    FLAG_RANT_ONE,
    FLAG_RANT_TWO,
    FREE_THROW_MADE,
    FREE_THROW_MISS,
    JUMP_BALL,
    JUMP_BALL_VIOLATION,
    KICK_BALL,
    LANE,
    LINE_UP_CHANGE,
    MINOR_TECHNICAL_FOUL,
    OFFENSIVE_FOUL,
    OFFICIAL_TIMEOUT,
    OPEN_INBOUND,
    OPEN_TIP,
    PERSONAL_FOUL,
    POSSESSION,
    REQUEST_REVIEW,
    REVIEW,
    REBOUND,
    SHOOTING_FOUL,
    STOPPAGE,
    TEAM_TIMEOUT,
    TECHNICAL_FOUL,
    THREE_POINT_MADE,
    THREE_POINT_MISS,
    TURNOVER,
    TV_TIMEOUT,
    TWO_POINT_MADE,
    TWO_POINT_MISS,
    UNKNOWN
}

enum class FractionSeparator {
    DASH,
    SLASH
}

enum class HockeyStrength {
    EVEN,
    POWERPLAY,
    SHORTHANDED,
    UNKNOWN
}

enum class SoccerPlayType {
    CORNER,
    END_OF_ET,
    END_OF_FIRST_ET,
    END_OF_GAME,
    END_OF_HALF,
    END_OF_REGULATION,
    FOUL,
    GOAL,
    INJURY_SUBSTITUTION,
    KICKOFF,
    OFFSIDE,
    OWN_GOAL,
    PENALTY_GOAL,
    PENALTY_SHOT_MISSED,
    PENALTY_SHOT_SAVED,
    PENALTY_KICK_AWARDED,
    PLAYER_RETIRED,
    RED_CARD,
    SECOND_YELLOW_CARD,
    SHOT_BLOCKED,
    SHOT_MISSED,
    SHOT_SAVED,
    START_PENALTY_SHOOTOUT,
    STOPPAGE_TIME,
    SUBSTITUTION,
    VAR_GOAL_AWARDED_CANCELLED,
    VAR_GOAL_AWARDED_CONFIRMED,
    VAR_GOAL_NOT_AWARDED_CANCELLED,
    VAR_GOAL_NOT_AWARDED_CONFIRMED,
    VAR_PENALTY_AWARDED_CANCELLED,
    VAR_PENALTY_AWARDED_CONFIRMED,
    VAR_PENALTY_NOT_AWARDED_CANCELLED,
    VAR_PENALTY_NOT_AWARDED_CONFIRMED,
    VAR_RED_CARD_AWARDED_CANCELLED,
    VAR_RED_CARD_AWARDED_CONFIRMED,
    VAR_RED_CARD_NOT_AWARDED_CANCELLED,
    VAR_RED_CARD_NOT_AWARDED_CONFIRMED,
    YELLOW_CARD,
    UNKNOWN,
}

enum class HockeyPlayType {
    AWARDED_GOAL,
    CHALLENGE,
    EMPTY_NET_GOAL,
    END_PERIOD,
    END_SHOOTOUT_PERIOD,
    EVEN_STRENGTH,
    FACE_OFF,
    GAME_SETUP,
    GIVE_AWAY,
    GOAL,
    GOALIE_CHANGE,
    HIT,
    OWN_GOAL,
    PENALTY,
    PENALTY_GOAL,
    PENALTY_SHOT_MISSED,
    PENALTY_SHOT_SAVED,
    POWERPLAY,
    SHOOTOUT_GOAL,
    SHOOTOUT_SHOT_MISSED,
    SHOOTOUT_SHOT_SAVED,
    SHOT_MISSED,
    SHOT_SAVED,
    START_SHOOTOUT_PERIOD,
    STOPPAGE,
    SUBSTITUTION,
    SUBSTITUTIONS,
    TAKEAWAY,
    TEAM_TIMEOUT,
    TV_TIMEOUT,
    UNKNOWN
}

enum class InningHalf {
    BOTTOM,
    MIDDLE,
    OVER,
    TOP,
    UNKNOWN
}

enum class Handedness {
    RIGHT,
    LEFT,
    UNKNOWN
}

enum class CoverageDataType {
    ALL,
    LINE_UP,
    PLAYER_STATS,
    SCORES,
    TEAM_STATS,
    PLAYS,
    COMMENTS,
    TEAM_SPECIFIC_COMMENTS,
    COMMENTS_NAVIGATION
}

enum class InjuryStatus {
    D7,
    D10,
    D15,
    D60,
    DAY,
    DAY_TO_DAY,
    DOUBTFUL,
    OUT,
    OUT_FOR_SEASON,
    OUT_INDEFINITELY,
    QUESTIONABLE,
    UNKNOWN
}

enum class BaseballPitchOutcome {
    BALL,
    DEAD_BALL,
    HIT,
    STRIKE,
    UNKNOWN
}

enum class AmericanFootballPlayType {
    CONVERSION,
    EXTRA_POINT,
    FIELD_GOAL,
    GAME_OVER,
    KICK_OFF,
    PASS,
    PENALTY,
    PERIOD_END,
    PUNT,
    RUSH,
    TIMEOUT,
    TV_TIMEOUT,
    TWO_MINUTE_WARNING,
    UNKNOWN
}

enum class PitcherState {
    CONFIRMED,
    PROBABLE
}

enum class GameTicketCurrency(
    val localSymbol: String,
    val internationalSymbol: String
) {
    USD("$", "USD")
}

val overtimePeriods = listOf(
    Period.FULL_TIME_OT,
    Period.FULL_TIME_OT_2,
    Period.FULL_TIME_OT_3,
    Period.FULL_TIME_OT_4,
    Period.FULL_TIME_OT_5,
    Period.FULL_TIME_OT_6,
    Period.FULL_TIME_OT_7,
    Period.FULL_TIME_OT_8,
    Period.FULL_TIME_OT_9,
    Period.FULL_TIME_OT_10,
    Period.OVER_TIME,
    Period.OVER_TIME_2,
    Period.OVER_TIME_3,
    Period.OVER_TIME_4,
    Period.OVER_TIME_5,
    Period.OVER_TIME_6,
    Period.OVER_TIME_7,
    Period.OVER_TIME_8,
    Period.OVER_TIME_9,
    Period.OVER_TIME_10
)

val shootoutPeriods = listOf(
    Period.SHOOTOUT,
    Period.FULL_TIME_SO
)