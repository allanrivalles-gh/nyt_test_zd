package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.boxscore.ui.modules.SeasonStatsModule
import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.FeedUiV2
import com.theathletic.gamedetail.boxscore.ui.baseball.BoxScoreBaseballRenderer
import com.theathletic.gamedetail.boxscore.ui.basketball.BoxScoreBasketballRenderer
import com.theathletic.gamedetail.boxscore.ui.football.BoxScoreFootballRenderer
import com.theathletic.gamedetail.boxscore.ui.hockey.BoxScoreHockeyRenderer
import com.theathletic.gamedetail.boxscore.ui.soccer.BoxScoreSoccerRenderer
import com.theathletic.ui.Transformer

@SuppressWarnings("LongParameterList")
class BoxScoreTransformer @AutoKoin constructor(
    private val soccerRender: BoxScoreSoccerRenderer,
    private val footballRenderer: BoxScoreFootballRenderer,
    private val basketballRenderer: BoxScoreBasketballRenderer,
    private val hockeyRenderer: BoxScoreHockeyRenderer,
    private val baseballRenderer: BoxScoreBaseballRenderer,
) : Transformer<BoxScoreState, BoxScoreContract.ViewState> {

    override fun transform(data: BoxScoreState): BoxScoreContract.ViewState {
        val modules = renderModules(data)
        return BoxScoreContract.ViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            feedUiModel = FeedUiV2(modules = modules),
            boxScoreModalSheet = data.boxScoreModalSheet,
            boxScoreModalSheetOptions = data.boxScoreModalSheetOptions,
            commentFlagState = data.commentFlagState,
            scrollTo = modules.toScrollIndex(data.scrollToModule),
            finishedInitialLoading = data.finishedInitialLoading
        )
    }

    private fun renderModules(data: BoxScoreState): List<FeedModuleV2> {
        return when (data.game?.sport) {
            Sport.SOCCER -> soccerRender.renderModules(data)
            Sport.FOOTBALL -> footballRenderer.renderModules(data)
            Sport.BASKETBALL -> basketballRenderer.renderModules(data)
            Sport.BASEBALL -> baseballRenderer.renderModules(data)
            Sport.HOCKEY -> hockeyRenderer.renderModules(data)
            else -> emptyList()
        }
    }

    private fun List<FeedModuleV2>.toScrollIndex(scrollToModule: ScrollToModule) = when (scrollToModule) {
        ScrollToModule.FIRST_ITEM -> 0
        ScrollToModule.SEASON_STATS -> indexOfFirst { it is SeasonStatsModule }.inc()
        ScrollToModule.NONE -> -1
    }
}