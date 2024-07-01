package com.theathletic.scores.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.AllGameUpdatesForAmericanFootballSubscription
import com.theathletic.AllGameUpdatesForBaseballSubscription
import com.theathletic.AllGameUpdatesForBasketballSubscription
import com.theathletic.AllGameUpdatesForHockeySubscription
import com.theathletic.AllGameUpdatesForSoccerSubscription
import com.theathletic.AmericanFootballPlayUpdatesSubscription
import com.theathletic.BaseballPlayUpdatesSubscription
import com.theathletic.BaseballPlayerStatsUpdatesSubscription
import com.theathletic.BasketballPlayUpdatesSubscription
import com.theathletic.GameSummaryUpdatesSubscription
import com.theathletic.GetAmericanFootballGameQuery
import com.theathletic.GetAmericanFootballPlayByPlaysQuery
import com.theathletic.GetBaseballGameQuery
import com.theathletic.GetBaseballPlayByPlaysQuery
import com.theathletic.GetBaseballStatsQuery
import com.theathletic.GetBasketballGameQuery
import com.theathletic.GetBasketballPlayByPlaysQuery
import com.theathletic.GetGameArticlesQuery
import com.theathletic.GetGameSummaryQuery
import com.theathletic.GetHockeyGameQuery
import com.theathletic.GetHockeyPlayByPlaysQuery
import com.theathletic.GetPlayerStatsQuery
import com.theathletic.GetSoccerGameQuery
import com.theathletic.GetSoccerPlayByPlaysQuery
import com.theathletic.GetStandingsQuery
import com.theathletic.GetTeamDetailsQuery
import com.theathletic.HockeyPlayUpdatesSubscription
import com.theathletic.LiveGamesSubscription
import com.theathletic.PlayerStatsUpdatesSubscription
import com.theathletic.SoccerPlayUpdatesSubscription
import com.theathletic.TeamScheduleQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.main.League
import com.theathletic.network.apollo.notPersistedSubscription
import com.theathletic.type.LeagueCode
import com.theathletic.utility.coroutines.retryWithBackoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class ScoresGraphqlApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {
    suspend fun getAmericanFootballGame(
        gameId: String
    ): ApolloResponse<GetAmericanFootballGameQuery.Data> {
        return client.query(
            GetAmericanFootballGameQuery(gameId)
        ).execute()
    }

    suspend fun getBasketballGame(
        gameId: String
    ): ApolloResponse<GetBasketballGameQuery.Data> {
        return client.query(
            GetBasketballGameQuery(gameId)
        ).execute()
    }

    suspend fun getHockeyGame(
        gameId: String,
    ): ApolloResponse<GetHockeyGameQuery.Data> {
        return client.query(
            GetHockeyGameQuery(gameId)
        ).execute()
    }

    suspend fun getBaseballGame(
        gameId: String
    ): ApolloResponse<GetBaseballGameQuery.Data> {
        return client.query(
            GetBaseballGameQuery(gameId)
        ).execute()
    }

    suspend fun getSoccerGame(
        gameId: String
    ): ApolloResponse<GetSoccerGameQuery.Data> {
        return client.query(
            GetSoccerGameQuery(gameId)
        ).execute()
    }

    suspend fun getGameSummary(gameId: String): ApolloResponse<GetGameSummaryQuery.Data> {
        return client.query(
            GetGameSummaryQuery(gameId)
        ).execute()
    }

    fun getGameSummaryUpdatesSubscription(
        gameId: String
    ): Flow<GameSummaryUpdatesSubscription.Data> {
        return client.notPersistedSubscription(GameSummaryUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    fun getLiveGamesSubscription(gameIds: List<String>): Flow<LiveGamesSubscription.Data> {
        return client.notPersistedSubscription(LiveGamesSubscription(gameIds))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    fun getAllGameUpdatesForAmericanFootballSubscription(
        gameId: String
    ): Flow<AllGameUpdatesForAmericanFootballSubscription.Data> {
        return client.notPersistedSubscription(AllGameUpdatesForAmericanFootballSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    fun getAllGameUpdateForBasketballSubscription(
        gameId: String,
    ): Flow<AllGameUpdatesForBasketballSubscription.Data> {
        return client.notPersistedSubscription(AllGameUpdatesForBasketballSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    fun getAllGameUpdateForBaseballSubscription(
        gameId: String,
    ): Flow<AllGameUpdatesForBaseballSubscription.Data> {
        return client.notPersistedSubscription(AllGameUpdatesForBaseballSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    fun getAllGameUpdateForHockeySubscription(
        gameId: String,
    ): Flow<AllGameUpdatesForHockeySubscription.Data> {
        return client.notPersistedSubscription(AllGameUpdatesForHockeySubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    fun getAllGameUpdateForSoccerSubscription(
        gameId: String,
    ): Flow<AllGameUpdatesForSoccerSubscription.Data> {
        return client.notPersistedSubscription(AllGameUpdatesForSoccerSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    fun getBaseballPlayerStatsUpdatesSubscription(gameId: String): Flow<BaseballPlayerStatsUpdatesSubscription.Data> {
        return client.notPersistedSubscription(BaseballPlayerStatsUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    fun getPlayerStatsUpdatesSubscription(gameId: String): Flow<PlayerStatsUpdatesSubscription.Data> {
        return client.notPersistedSubscription(PlayerStatsUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun getTeamSchedule(teamId: String): ApolloResponse<TeamScheduleQuery.Data> {
        return client.query(
            TeamScheduleQuery(teamId)
        ).execute()
    }

    suspend fun getStandings(leagueCode: LeagueCode): ApolloResponse<GetStandingsQuery.Data> {
        return client.query(GetStandingsQuery(leagueCode)).execute()
    }

    suspend fun getGameArticles(gameId: String, leagueId: String): ApolloResponse<GetGameArticlesQuery.Data> {
        return client.query(
            GetGameArticlesQuery(gameId, leagueId)
        ).execute()
    }

    suspend fun getPlayerStats(gameId: String): ApolloResponse<GetPlayerStatsQuery.Data> {
        return client.query(GetPlayerStatsQuery(gameId)).execute()
    }

    suspend fun getBaseballStats(gameId: String, isPostGame: Boolean): ApolloResponse<GetBaseballStatsQuery.Data> {
        return client.query(GetBaseballStatsQuery(gameId, isPostGame)).execute()
    }

    suspend fun getTeamDetails(teamId: String): ApolloResponse<GetTeamDetailsQuery.Data> {
        return client.query(GetTeamDetailsQuery(teamId)).execute()
    }

    /* Play By Plays */

    suspend fun getBasketballPlayByPlays(gameId: String): ApolloResponse<GetBasketballPlayByPlaysQuery.Data> {
        return client.query(GetBasketballPlayByPlaysQuery(gameId)).execute()
    }

    fun getBasketballPlayUpdatesSubscription(gameId: String): Flow<BasketballPlayUpdatesSubscription.Data> {
        return client.notPersistedSubscription(BasketballPlayUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun getHockeyPlayByPlays(gameId: String): ApolloResponse<GetHockeyPlayByPlaysQuery.Data> {
        return client.query(GetHockeyPlayByPlaysQuery(gameId)).execute()
    }

    fun getHockeyPlayUpdatesSubscription(gameId: String): Flow<HockeyPlayUpdatesSubscription.Data> {
        return client.notPersistedSubscription(HockeyPlayUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun getBaseballPlayByPlays(gameId: String): ApolloResponse<GetBaseballPlayByPlaysQuery.Data> {
        return client.query(GetBaseballPlayByPlaysQuery(gameId)).execute()
    }

    fun getBaseballPlayUpdatesSubscription(gameId: String): Flow<BaseballPlayUpdatesSubscription.Data> {
        return client.notPersistedSubscription(BaseballPlayUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun getAmericanFootballPlayByPlays(gameId: String): ApolloResponse<GetAmericanFootballPlayByPlaysQuery.Data> {
        return client.query(GetAmericanFootballPlayByPlaysQuery(gameId)).execute()
    }

    fun getAmericanFootballPlayUpdatesSubscription(gameId: String): Flow<AmericanFootballPlayUpdatesSubscription.Data> {
        return client.notPersistedSubscription(AmericanFootballPlayUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }

    suspend fun getSoccerPlayByPlays(gameId: String): ApolloResponse<GetSoccerPlayByPlaysQuery.Data> {
        return client.query(GetSoccerPlayByPlaysQuery(gameId)).execute()
    }

    fun getSoccerPlayUpdatesSubscription(gameId: String): Flow<SoccerPlayUpdatesSubscription.Data> {
        return client.notPersistedSubscription(SoccerPlayUpdatesSubscription(gameId))
            .toFlow()
            .retryWithBackoff()
            .mapNotNull { it.data }
    }
}

/*
    NOTE: These mappers below also live in scores:com.theathletic.scores.data.remote.ScoresFeedRemoteToLocalMapper.kt
    so if you update these you need to update that file too. These below will become deprecated soon.
 */

@Deprecated("Use League.toGraphqlLeagueCode in scores:com.theathletic.scores.data.remote.ScoresFeedRemoteToLocalMapper.kt")
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

@Deprecated("Use LeagueCode.toLocalLeague in scores:com.theathletic.scores.data.remote.ScoresFeedRemoteToLocalMapper.kt")
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