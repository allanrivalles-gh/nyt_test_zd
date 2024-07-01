package com.theathletic.gamedetail.boxscore.ui.baseball

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.data.local.SectionType
import com.theathletic.boxscore.ui.BoxScoreUiModel
import com.theathletic.boxscore.ui.modules.LatestNewsModule
import com.theathletic.comments.ui.LikeActionUiState
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.boxscore.ui.BoxScoreState
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreGameDetailsRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreGameOddsRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreInjuryReportRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreLeadersRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScorePlayerGradeRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreRecentGamesRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreRelatedStoriesRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreScoringRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreSeasonStatsRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreSlideStoriesRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreTeamStatsRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreTicketsRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreTopCommentsRenderer
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreTopCommentsRenderer.Companion.TopCommentsPositionType
import com.theathletic.gamedetail.boxscore.ui.common.gradingIsActive
import com.theathletic.gamedetail.data.local.GameArticlesLocalModel
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.scores.data.SupportedLeagues
import java.util.concurrent.atomic.AtomicInteger

@SuppressWarnings("LongParameterList")
class BoxScoreBaseballRenderer @AutoKoin constructor(
    private val scoringRenderers: BoxScoreScoringRenderers,
    private val statsRenderers: BoxScoreTeamStatsRenderers,
    private val gameOddsRenderers: BoxScoreGameOddsRenderers,
    private val recentGamesRenderers: BoxScoreRecentGamesRenderers,
    private val gameDetailsRenderers: BoxScoreGameDetailsRenderers,
    private val relatedStoriesRenderers: BoxScoreRelatedStoriesRenderers,
    private val pitchersWinLossRenderer: BoxScoreBaseballPitchersWinLossRenderer,
    private val leadersRenderers: BoxScoreLeadersRenderers,
    private val injuryReportRenderers: BoxScoreInjuryReportRenderers,
    private val seasonStatsRenderers: BoxScoreSeasonStatsRenderers,
    private val currentInningRenderer: BoxScoreBaseballCurrentInningRenderer,
    private val boxScorePlayerGradeRenderers: BoxScorePlayerGradeRenderers,
    private val boxScoreBaseballPitcherRenderer: BoxScoreBaseballPitcherRenderer,
    private val boxScoreTicketsRenderers: BoxScoreTicketsRenderers,
    private val boxScoreTopCommentsRenderer: BoxScoreTopCommentsRenderer,
    private val boxScoreSlideStoriesRenderers: BoxScoreSlideStoriesRenderers,
    private val supportedLeagues: SupportedLeagues,
    private val featureSwitches: FeatureSwitches,
) {

    fun renderModules(data: BoxScoreState): List<FeedModuleV2> {
        val game = data.game ?: return emptyList()
        val pageOrder = AtomicInteger(0)
        return listOfNotNull(
            addSlideStories(),
            addLatestNews(data.boxScoreUi, pageOrder),
            addTickets(game, data.contentRegion, pageOrder),
            addPitcherWinLose(game, pageOrder),
            addScoring(game, pageOrder),
            addCurrentInnings(game, pageOrder),
            addPlayerGradeModule(game, pageOrder, data.isFirstTeamSelected),
            addTopCommentsIfNecessary(
                game,
                pageOrder,
                TopCommentsPositionType.AFTER_PLAYER_GRADES,
                data.likeActionUiState
            ),
            addTopPerformers(game, pageOrder),
            addBaseballScoringPlays(game, pageOrder),
            addTopCommentsIfNecessary(
                game,
                pageOrder,
                TopCommentsPositionType.AFTER_SCORING_SUMMARY,
                data.likeActionUiState
            ),
            addGameOdds(game, pageOrder),
            addPitchers(game, pageOrder),
            addTeamLeaders(game, pageOrder),
            addTopCommentsIfNecessary(
                game,
                pageOrder,
                TopCommentsPositionType.AFTER_TEAM_LEADERS,
                data.likeActionUiState
            ),
            addSeasonStats(game, pageOrder),
            addTeamStats(game, pageOrder),
            addRecentGames(game, pageOrder),
            addInjuryReport(game, pageOrder),
            addGameDetails(game, pageOrder),
            addRelatedStories(game, data.articles, pageOrder),
        )
    }

    private fun addSlideStories() = boxScoreSlideStoriesRenderers.createSlideStoriesModule()

    private fun addPitchers(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (game.isGameScheduled.not()) return null
        pageOrder.getAndIncrement()
        return boxScoreBaseballPitcherRenderer.createPitcherModule(game)
    }

    private fun addLatestNews(
        boxScore: BoxScoreUiModel?,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (featureSwitches.isFeatureEnabled(FeatureSwitch.BOX_SCORE_LATEST_NEWS)) {
            val gameSection = boxScore?.sections?.firstOrNull { it.type == SectionType.GAME }
            val latestNewsRoom = gameSection?.modules?.filterIsInstance<BoxScoreUiModel.LatestNewsUiModel>()?.first() ?: return null
            pageOrder.getAndIncrement()
            return LatestNewsModule(
                latestNewsUiModel = latestNewsRoom
            )
        }
        return null
    }

    private fun addTickets(
        game: GameDetailLocalModel,
        contentRegion: UserContentEdition,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (game.isGameScheduled.not()) return null
        pageOrder.getAndIncrement()
        return boxScoreTicketsRenderers.createTicketsModule(game, contentRegion)
    }

    private fun addPitcherWinLose(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        if (game.isGameCompleted.not()) return null
        (game.sportExtras as? GameDetailLocalModel.BaseballExtras)?.pitching ?: return null
        pageOrder.getAndIncrement()
        return pitchersWinLossRenderer.createPitcherWinLossModule(game)
    }

    private fun addScoring(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        if (game.isGameInProgressOrCompleted.not()) return null
        pageOrder.incrementAndGet()
        return scoringRenderers.createScoreTableModule(game)
    }

    private fun addTopCommentsIfNecessary(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger,
        positionType: TopCommentsPositionType,
        likeActionUiState: LikeActionUiState
    ): FeedModuleV2? {
        if (!featureSwitches.isFeatureEnabled(FeatureSwitch.BOX_SCORE_TOP_COMMENTS)) return null
        if (game.topComments.isEmpty()) return null
        val position = BoxScoreTopCommentsRenderer.getTopCommentsPosition(game)
        if (position != positionType) return null
        pageOrder.getAndIncrement()
        return boxScoreTopCommentsRenderer.createTopCommentsModule(game, likeActionUiState)
    }

    private fun addCurrentInnings(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        if (!game.isGameInProgress) return null
        (game.sportExtras as? GameDetailLocalModel.BaseballExtras)?.outcome ?: return null
        (game.sportExtras as? GameDetailLocalModel.BaseballExtras)?.currentInningPlays ?: return null
        pageOrder.getAndIncrement()
        return currentInningRenderer.createCurrentInningModule(game)
    }

    private fun addTopPerformers(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        if (game.isGameInProgressOrCompleted.not()) return null
        pageOrder.getAndIncrement()
        return leadersRenderers.createTopPerformersModule(game)
    }

    private fun addBaseballScoringPlays(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (game.isGameInProgressOrCompleted.not()) return null
        pageOrder.getAndIncrement()
        return scoringRenderers.createBaseballScoringSummaryModule(game)
    }

    private fun addGameOdds(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        if (game.isGameScheduled.not() || game.oddsPregame.isEmpty()) return null
        pageOrder.getAndIncrement()
        return gameOddsRenderers.createGameOddsModule(game)
    }

    private fun addTeamLeaders(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        if (game.isGameScheduled.not()) return null
        pageOrder.getAndIncrement()
        return leadersRenderers.createTeamLeadersModule(game)
    }

    private fun addSeasonStats(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        if (game.isGameScheduled.not() || game.awayTeamHomeTeamSeasonStats.isEmpty()) return null
        pageOrder.getAndIncrement()
        return seasonStatsRenderers.createSeasonStatsModule(game)
    }

    private fun addTeamStats(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        if (game.isGameInProgressOrCompleted.not() || game.awayTeamHomeTeamStats.isEmpty()) return null
        pageOrder.getAndIncrement()
        return statsRenderers.createTeamStatsModule(game)
    }

    private fun addRecentGames(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        if (game.isGameScheduled.not() ||
            game.firstTeam?.recentGames.isNullOrEmpty() ||
            game.secondTeam?.recentGames.isNullOrEmpty()
        ) {
            return null
        }
        pageOrder.getAndIncrement()
        return recentGamesRenderers.createRecentGamesModule(game)
    }

    private fun addInjuryReport(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        if (game.isGameCompleted ||
            supportedLeagues.isCollegeLeague(game.league.legacyLeague)
        ) return null
        pageOrder.getAndIncrement()
        return injuryReportRenderers.createInjuryReportModule(game)
    }

    private fun addGameDetails(game: GameDetailLocalModel, pageOrder: AtomicInteger): FeedModuleV2? {
        pageOrder.getAndIncrement()
        return gameDetailsRenderers.createGameDetailsModule(game)
    }

    private fun addRelatedStories(
        game: GameDetailLocalModel,
        gameArticles: List<GameArticlesLocalModel.GameArticle>?,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (gameArticles.isNullOrEmpty()) return null
        pageOrder.getAndIncrement()
        return relatedStoriesRenderers.createRelatedStoriesModule(game, gameArticles, pageOrder.get())
    }

    private fun addPlayerGradeModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger,
        showFirstTeam: Boolean
    ): FeedModuleV2? {
        if (game.gradeStatus.gradingIsActive() && featureSwitches.isFeatureEnabled(FeatureSwitch.PLAYER_GRADES_BASEBALL)) {
            return boxScorePlayerGradeRenderers.createPlayerGradeCarousel(
                game,
                pageOrder,
                showFirstTeam
            )
        }
        return null
    }
}