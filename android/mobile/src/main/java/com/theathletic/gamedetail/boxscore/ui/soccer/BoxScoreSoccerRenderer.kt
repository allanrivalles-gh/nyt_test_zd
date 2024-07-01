package com.theathletic.gamedetail.boxscore.ui.soccer

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
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.utility.transformIfNotEmptyElseNull
import java.util.concurrent.atomic.AtomicInteger

@SuppressWarnings("LongParameterList")
class BoxScoreSoccerRenderer @AutoKoin constructor(
    private val lineUpRenderers: BoxScoreSoccerLineUpRenderers,
    private val gameDetailsRenderers: BoxScoreGameDetailsRenderers,
    private val seasonStatsRenderers: BoxScoreSeasonStatsRenderers,
    private val statsRenderers: BoxScoreTeamStatsRenderers,
    private val leadersRenderers: BoxScoreLeadersRenderers,
    private val injuryReportRenderers: BoxScoreInjuryReportRenderers,
    private val relatedStoriesRenderers: BoxScoreRelatedStoriesRenderers,
    private val recentGamesRenderers: BoxScoreRecentGamesRenderers,
    private val scoringRenderers: BoxScoreScoringRenderers,
    private val playByPlayRenderers: SoccerPlayByPlayRenderers,
    private val timelineSummaryRenderer: BoxScoreSoccerTimelineSummaryRenderer,
    private val boxScorePlayerGradeRenderers: BoxScorePlayerGradeRenderers,
    private val boxScoreTicketsRenderers: BoxScoreTicketsRenderers,
    private val boxScoreTopCommentsRenderer: BoxScoreTopCommentsRenderer,
    private val boxScoreSlideStoriesRenderers: BoxScoreSlideStoriesRenderers,
    private val featureSwitches: FeatureSwitches,
) {

    fun renderModules(data: BoxScoreState): List<FeedModuleV2> {
        val game = data.game ?: return emptyList()
        val pageOrder = AtomicInteger(0)
        return listOfNotNull(
            timelineSummaryRenderer.createTimelineSummaryModule(data.game, pageOrder),
            addSlideStories(),
            addLatestNews(data.boxScoreUi, pageOrder),
            addTickets(game, data.contentRegion, pageOrder),
            createRecentMomentsModule(game, pageOrder),
            scoringRenderers.createScoringSummaryModule(game, pageOrder),
            addTopCommentsIfNecessary(
                game,
                pageOrder,
                TopCommentsPositionType.AFTER_MOMENTS,
                data.likeActionUiState
            ),
            addPlayerGradeModule(data.game, pageOrder, data.isFirstTeamSelected),
            addTopCommentsIfNecessary(
                game,
                pageOrder,
                TopCommentsPositionType.AFTER_PLAYER_GRADES,
                data.likeActionUiState
            ),
            lineUpRenderers.createPlayerLineModule(data, pageOrder),
            leadersRenderers.createTopPerformersModule(game, pageOrder),
            statsRenderers.createTeamStatsModule(game, pageOrder),
            leadersRenderers.createTeamLeadersModule(game, pageOrder),
            seasonStatsRenderers.createSeasonStatsModule(game, pageOrder),
            addTopCommentsIfNecessary(
                game,
                pageOrder,
                TopCommentsPositionType.AFTER_SEASON_STATS,
                data.likeActionUiState
            ),
            recentGamesRenderers.createRecentGamesModule(game, pageOrder),
            injuryReportRenderers.createInjuryReportModule(game, pageOrder),
            gameDetailsRenderers.createGameDetailsModule(game, pageOrder),
            relatedStoriesRenderers.createRelatedStoriesModuleLegacy(game, data.articles, pageOrder),
        )
    }

    private fun addSlideStories() = boxScoreSlideStoriesRenderers.createSlideStoriesModule()

    private fun addLatestNews(
        boxScore: BoxScoreUiModel?,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (featureSwitches.isFeatureEnabled(FeatureSwitch.BOX_SCORE_LATEST_NEWS)) {
            val gameSection = boxScore?.sections?.firstOrNull() { it.type == SectionType.GAME }
            val latestNewsRoom = gameSection?.modules?.filterIsInstance<BoxScoreUiModel.LatestNewsUiModel>()?.first()
                ?: return null
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

    private fun createRecentMomentsModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        if (game.isGameInProgress.not()) return null
        val extras = game.sportExtras as? GameDetailLocalModel.SoccerExtras ?: return null
        return extras.recentMoments.transformIfNotEmptyElseNull { recentMoments ->
            pageOrder.incrementAndGet()
            playByPlayRenderers.createSoccerRecentMomentsModule(
                game = game,
                recentMoments = recentMoments
            )
        }
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

    private fun addPlayerGradeModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger,
        showFirstTeam: Boolean
    ): FeedModuleV2? {
        if (game.gradeStatus.gradingIsActive()) {
            return boxScorePlayerGradeRenderers.createPlayerGradeCarousel(
                game,
                pageOrder,
                showFirstTeam
            )
        }
        return null
    }
}