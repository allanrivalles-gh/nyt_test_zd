package com.theathletic.scores.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.main.FeedResponse
import com.theathletic.entity.main.League
import com.theathletic.entity.main.Sport
import com.theathletic.feed.FeedType
import com.theathletic.feed.data.local.FeedLocalDataSource
import com.theathletic.gamedetail.data.local.GameArticlesLocalDataSource
import com.theathletic.gamedetail.data.local.GameDetailLocalDataSource
import com.theathletic.gamedetail.data.local.GameSummaryLocalDataSource
import com.theathletic.gamedetail.data.local.LineUpAndStatsLocalDataSource
import com.theathletic.gamedetail.data.local.PlayByPlaysLocalDataSource
import com.theathletic.gamedetail.data.remote.AmericanFootballPlayByPlaysFetcher
import com.theathletic.gamedetail.data.remote.AmericanFootballPlayByPlaysSubscriber
import com.theathletic.gamedetail.data.remote.BaseballPlayByPlaysFetcher
import com.theathletic.gamedetail.data.remote.BaseballPlayByPlaysSubscriber
import com.theathletic.gamedetail.data.remote.BaseballPlayerStatsUpdatesSubscriber
import com.theathletic.gamedetail.data.remote.BaseballStatsFetcher
import com.theathletic.gamedetail.data.remote.BasketballPlayByPlaysFetcher
import com.theathletic.gamedetail.data.remote.BasketballPlayByPlaysSubscriber
import com.theathletic.gamedetail.data.remote.GameAmericanFootballFetcher
import com.theathletic.gamedetail.data.remote.GameArticlesFetcher
import com.theathletic.gamedetail.data.remote.GameBaseballFetcher
import com.theathletic.gamedetail.data.remote.GameBasketballFetcher
import com.theathletic.gamedetail.data.remote.GameHockeyFetcher
import com.theathletic.gamedetail.data.remote.GamePlayerStatsUpdatesSubscriber
import com.theathletic.gamedetail.data.remote.GameSoccerFetcher
import com.theathletic.gamedetail.data.remote.GameSummaryFetcher
import com.theathletic.gamedetail.data.remote.HockeyPlayByPlaysFetcher
import com.theathletic.gamedetail.data.remote.HockeyPlayByPlaysSubscriber
import com.theathletic.gamedetail.data.remote.PlayerStatsFetcher
import com.theathletic.gamedetail.data.remote.SoccerPlayByPlaysFetcher
import com.theathletic.gamedetail.data.remote.SoccerPlayByPlaysSubscriber
import com.theathletic.gamedetail.data.remote.toGameFeedItem
import com.theathletic.gamedetail.data.remote.toLocalModel
import com.theathletic.repository.CoroutineRepository
import com.theathletic.scores.data.local.TeamDetailsLocalModel
import com.theathletic.scores.data.remote.TeamDetailsFetcher
import com.theathletic.scores.remote.ScoresGraphqlApi
import com.theathletic.scores.standings.data.local.ScoresStandingsLocalDataSource
import com.theathletic.scores.standings.data.remote.ScoresStandingsFetcher
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressWarnings("LongParameterList")
class ScoresRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val gameAmericanFootballFetcher: GameAmericanFootballFetcher,
    private val gameBasketballFetcher: GameBasketballFetcher,
    private val gameHockeyFetcher: GameHockeyFetcher,
    private val gameBaseballFetcher: GameBaseballFetcher,
    private val gameSoccerFetcher: GameSoccerFetcher,
    private val gameSummaryFetcher: GameSummaryFetcher,
    private val baseballPlayerStatsUpdatesSubscriber: BaseballPlayerStatsUpdatesSubscriber,
    private val gamePlayerStatsUpdatesUpdatesSubscriber: GamePlayerStatsUpdatesSubscriber,
    private val gamePlayerStatsFetcher: PlayerStatsFetcher,
    private val baseballStatsFetcher: BaseballStatsFetcher,
    private val standingsFetcher: ScoresStandingsFetcher,
    private val gameArticlesFetcher: GameArticlesFetcher,
    private val gameArticlesLocalDataSource: GameArticlesLocalDataSource,
    private val localDataSource: GameDetailLocalDataSource,
    private val gameSummaryLocalDataSource: GameSummaryLocalDataSource,
    private val standingsLocalDataSource: ScoresStandingsLocalDataSource,
    private val lineUpLocalDataSource: LineUpAndStatsLocalDataSource,
    private val teamDetailsFetcher: TeamDetailsFetcher,
    private val basketballPlayByPlaysFetcher: BasketballPlayByPlaysFetcher,
    private val basketballPlayByPlaysSubscriber: BasketballPlayByPlaysSubscriber,
    private val hockeyPlayByPlaysFetcher: HockeyPlayByPlaysFetcher,
    private val hockeyPlayByPlaysSubscriber: HockeyPlayByPlaysSubscriber,
    private val baseballPlayByPlaysFetcher: BaseballPlayByPlaysFetcher,
    private val baseballPlayByPlaysSubscriber: BaseballPlayByPlaysSubscriber,
    private val americanFootballPlayByPlaysFetcher: AmericanFootballPlayByPlaysFetcher,
    private val americanFootballPlayByPlaysSubscriber: AmericanFootballPlayByPlaysSubscriber,
    private val soccerPlayByPlaysFetcher: SoccerPlayByPlaysFetcher,
    private val soccerPlayByPlaysSubscriber: SoccerPlayByPlaysSubscriber,
    private val playByPlaysLocalDataSource: PlayByPlaysLocalDataSource,
    private val feedLocalDataSource: FeedLocalDataSource,
    private val entityDataSource: EntityDataSource,
    private val scoresGraphqlApi: ScoresGraphqlApi
) : CoroutineRepository {

    class ScoresException(message: String) : Exception(message)

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    fun fetchGame(
        id: String,
        sport: Sport
    ) = when (sport) {
        Sport.FOOTBALL -> fetchAmericanFootballGame(id)
        Sport.BASKETBALL -> fetchBasketballGame(id)
        Sport.HOCKEY -> fetchHockeyGame(id)
        Sport.BASEBALL -> fetchBaseballGame(id)
        Sport.SOCCER -> fetchSoccerGame(id)
        else -> {
            Timber.e("Sport $sport not handle by fetchGame")
            null
        }
    }

    private fun fetchAmericanFootballGame(id: String) =
        repositoryScope.launch {
            gameAmericanFootballFetcher.fetchRemote(
                GameAmericanFootballFetcher.Params(gameId = id)
            )
        }

    private fun fetchBasketballGame(id: String) =
        repositoryScope.launch {
            gameBasketballFetcher.fetchRemote(
                GameBasketballFetcher.Params(gameId = id)
            )
        }

    private fun fetchHockeyGame(id: String) =
        repositoryScope.launch {
            gameHockeyFetcher.fetchRemote(
                GameHockeyFetcher.Params(gameId = id)
            )
        }

    private fun fetchBaseballGame(id: String) =
        repositoryScope.launch {
            gameBaseballFetcher.fetchRemote(
                GameBaseballFetcher.Params(gameId = id)
            )
        }

    private fun fetchSoccerGame(id: String) =
        repositoryScope.launch {
            gameSoccerFetcher.fetchRemote(
                GameSoccerFetcher.Params(gameId = id)
            )
        }

    fun fetchGameSummary(id: String) = repositoryScope.launch {
        gameSummaryFetcher.fetchRemote(GameSummaryFetcher.Params(gameId = id))
    }

    fun getGameSummary(id: String) = gameSummaryLocalDataSource.observeItem(id)

    suspend fun subscribeToPlayerStatsUpdates(gameId: String, sport: Sport) {
        when (sport) {
            Sport.BASEBALL -> baseballPlayerStatsUpdatesSubscriber.subscribe(
                BaseballPlayerStatsUpdatesSubscriber.Params(gameId)
            )
            else -> gamePlayerStatsUpdatesUpdatesSubscriber.subscribe(
                GamePlayerStatsUpdatesSubscriber.Params(gameId)
            )
        }
    }

    fun observeGame(id: String) = localDataSource.observeItem(id)

    fun getStandings(league: League) =
        standingsLocalDataSource.observeItem(league)

    fun fetchStandings(league: League) = repositoryScope.launch {
        standingsFetcher.fetchRemote(ScoresStandingsFetcher.Params(league))
    }

    fun getGameArticles(gameId: String) = gameArticlesLocalDataSource.observeItem(gameId)

    fun fetchGameArticles(gameId: String, leagueId: Long) = repositoryScope.launch {
        gameArticlesFetcher.fetchRemote(
            GameArticlesFetcher.Params(
                gameId = gameId,
                leagueId = leagueId
            )
        )
    }

    fun fetchPlayerStats(gameId: String, sport: Sport, isPostGame: Boolean) =
        repositoryScope.launch {
            if (sport == Sport.BASEBALL) {
                baseballStatsFetcher.fetchRemote(
                    BaseballStatsFetcher.Params(gameId, isPostGame)
                )
            } else {
                gamePlayerStatsFetcher.fetchRemote(
                    PlayerStatsFetcher.Params(
                        gameId = gameId
                    )
                )
            }
        }

    fun getPlayerStats(gameId: String) = lineUpLocalDataSource.observeItem(gameId)

    suspend fun getTeamDetails(teamId: String): TeamDetailsLocalModel? {
        return teamDetailsFetcher.fetchRemote(
            TeamDetailsFetcher.Params(teamId)
        )
    }

    fun fetchPlayByPlays(gameId: String, sport: Sport) = when (sport) {
        Sport.BASKETBALL -> fetchBasketballPlayByPlays(gameId)
        Sport.HOCKEY -> fetchHockeyPlayByPlays(gameId)
        Sport.BASEBALL -> fetchBaseballPlayByPlays(gameId)
        Sport.FOOTBALL -> fetchAmericanFootballPlayByPlays(gameId)
        Sport.SOCCER -> fetchSoccerPlayByPlays(gameId)
        else -> { null /* Not Supported */ }
    }

    fun getPlayByPlays(gameId: String) = playByPlaysLocalDataSource.observeItem(gameId)

    suspend fun subscribeForPlayUpdates(gameId: String, sport: Sport) {
        when (sport) {
            Sport.BASKETBALL -> subscribeToBasketballPlayUpdates(gameId)
            Sport.HOCKEY -> subscribeToHockeyPlayUpdates(gameId)
            Sport.BASEBALL -> subscribeToBaseballPlayUpdates(gameId)
            Sport.FOOTBALL -> subscribeToAmericanFootballPlayUpdates(gameId)
            Sport.SOCCER -> subscribeToSoccerPlayUpdates(gameId)
            else -> { /* Not Supported */ }
        }
    }

    private fun fetchBasketballPlayByPlays(gameId: String) =
        repositoryScope.launch {
            basketballPlayByPlaysFetcher.fetchRemote(
                BasketballPlayByPlaysFetcher.Params(gameId)
            )
        }

    private suspend fun subscribeToBasketballPlayUpdates(gameId: String) {
        basketballPlayByPlaysSubscriber.subscribe(
            BasketballPlayByPlaysSubscriber.Params(gameId)
        )
    }

    private fun fetchHockeyPlayByPlays(gameId: String) =
        repositoryScope.launch {
            hockeyPlayByPlaysFetcher.fetchRemote(
                HockeyPlayByPlaysFetcher.Params(gameId)
            )
        }

    private suspend fun subscribeToHockeyPlayUpdates(gameId: String) {
        hockeyPlayByPlaysSubscriber.subscribe(
            HockeyPlayByPlaysSubscriber.Params(gameId)
        )
    }

    private fun fetchBaseballPlayByPlays(gameId: String) =
        repositoryScope.launch {
            baseballPlayByPlaysFetcher.fetchRemote(
                BaseballPlayByPlaysFetcher.Params(gameId)
            )
        }

    private suspend fun subscribeToBaseballPlayUpdates(gameId: String) {
        baseballPlayByPlaysSubscriber.subscribe(
            BaseballPlayByPlaysSubscriber.Params(gameId)
        )
    }

    private fun fetchAmericanFootballPlayByPlays(gameId: String) =
        repositoryScope.launch {
            americanFootballPlayByPlaysFetcher.fetchRemote(
                AmericanFootballPlayByPlaysFetcher.Params(gameId)
            )
        }

    private suspend fun subscribeToAmericanFootballPlayUpdates(gameId: String) {
        americanFootballPlayByPlaysSubscriber.subscribe(
            AmericanFootballPlayByPlaysSubscriber.Params(gameId)
        )
    }

    private fun fetchSoccerPlayByPlays(gameId: String) =
        repositoryScope.launch {
            soccerPlayByPlaysFetcher.fetchRemote(
                SoccerPlayByPlaysFetcher.Params(gameId)
            )
        }

    private suspend fun subscribeToSoccerPlayUpdates(gameId: String) {
        soccerPlayByPlaysSubscriber.subscribe(
            SoccerPlayByPlaysSubscriber.Params(gameId)
        )
    }

    suspend fun subscribeToAllGameUpdates(gameId: String, sport: Sport): Result<Unit> {
        return try {
            when (sport) {
                Sport.FOOTBALL -> subscribeToAllGameUpdatesForAmericanFootball(gameId)
                Sport.BASKETBALL -> subscribeAllGameUpdateForBasketball(gameId)
                Sport.BASEBALL -> subscribeAllGameUpdateForBaseball(gameId)
                Sport.HOCKEY -> subscribeAllGameUpdateForHockey(gameId)
                Sport.SOCCER -> subscribeAllGameUpdateForSoccer(gameId)
                else -> { /* Not support */ }
            }
            Result.success(Unit)
        } catch (error: Exception) {
            Timber.e(error)
            Result.failure(error)
        }
    }

    private suspend fun subscribeToAllGameUpdatesForAmericanFootball(gameId: String) {
        try {
            scoresGraphqlApi.getAllGameUpdatesForAmericanFootballSubscription(gameId).collect { data ->
                data.liveScoreUpdates?.let { updates ->
                    updates.fragments.gameSummary.toLocalModel()?.let { model ->
                        gameSummaryLocalDataSource.update(gameId, model)
                    }
                    updates.fragments.americanFootballGameFragment?.toLocalModel()?.let { model ->
                        localDataSource.update(gameId, model)
                    }
                }
            }
        } catch (error: Throwable) {
            throw ScoresException(
                "Subscribing to Football game updates failed for id: $gameId with error: ${error.message}"
            )
        }
    }

    private suspend fun subscribeAllGameUpdateForBasketball(gameId: String) {
        try {
            scoresGraphqlApi.getAllGameUpdateForBasketballSubscription(gameId).collect { data ->
                data.liveScoreUpdates?.let { updates ->
                    updates.fragments.gameSummary.toLocalModel()?.let { model ->
                        gameSummaryLocalDataSource.update(gameId, model)
                    }
                    updates.fragments.basketballGameFragment?.toLocalModel()?.let { model ->
                        localDataSource.update(gameId, model)
                    }
                }
            }
        } catch (error: Throwable) {
            throw ScoresException(
                "Subscribing to Basketball game updates failed for id: $gameId with error: ${error.message}"
            )
        }
    }

    private suspend fun subscribeAllGameUpdateForBaseball(gameId: String) {
        try {
            scoresGraphqlApi.getAllGameUpdateForBaseballSubscription(gameId).collect { data ->
                data.liveScoreUpdates?.let { updates ->
                    updates.fragments.gameSummary.toLocalModel()?.let { model ->
                        gameSummaryLocalDataSource.update(gameId, model)
                    }
                    updates.fragments.baseballGameFragment?.toLocalModel()?.let { model ->
                        localDataSource.update(gameId, model)
                    }
                }
            }
        } catch (error: Throwable) {
            throw ScoresException(
                "Subscribing to Baseball game updates failed for id: $gameId with error: ${error.message}"
            )
        }
    }

    private suspend fun subscribeAllGameUpdateForHockey(gameId: String) {
        try {
            scoresGraphqlApi.getAllGameUpdateForHockeySubscription(gameId).collect { data ->
                data.liveScoreUpdates?.let { updates ->
                    updates.fragments.gameSummary.toLocalModel()?.let { model ->
                        gameSummaryLocalDataSource.update(gameId, model)
                    }
                    updates.fragments.hockeyGameFragment?.toLocalModel()?.let { model ->
                        localDataSource.update(gameId, model)
                    }
                }
            }
        } catch (error: Throwable) {
            throw ScoresException(
                "Subscribing to Hockey game updates failed for id: $gameId with error: ${error.message}"
            )
        }
    }

    private suspend fun subscribeAllGameUpdateForSoccer(gameId: String) {
        try {
            scoresGraphqlApi.getAllGameUpdateForSoccerSubscription(gameId).collect { data ->
                data.liveScoreUpdates?.let { updates ->
                    updates.fragments.gameSummary.toLocalModel()?.let { model ->
                        gameSummaryLocalDataSource.update(gameId, model)
                    }
                    updates.fragments.soccerGameFragment?.toLocalModel()?.let { model ->
                        localDataSource.update(gameId, model)
                    }
                    val feedResponse = FeedResponse(
                        feedId = FeedType.GameFeed(gameId).compositeId,
                        feed = updates.feed.mapIndexedNotNull { index, item ->
                            item.fragments.gameFeedItemFragment.toGameFeedItem(index, gameId)
                        }.toMutableList()
                    )
                    entityDataSource.insertOrUpdate(feedResponse.allEntities)

                    feedLocalDataSource.insertFullFeedResponse(
                        feedResponse,
                        feedResponse.feed,
                        true
                    )
                }
            }
        } catch (error: Throwable) {
            throw ScoresException(
                "Subscribing to Soccer game updates failed for id: $gameId with error: ${error.message}"
            )
        }
    }
}