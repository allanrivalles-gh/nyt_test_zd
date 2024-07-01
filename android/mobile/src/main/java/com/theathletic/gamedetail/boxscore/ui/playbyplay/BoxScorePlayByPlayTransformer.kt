package com.theathletic.gamedetail.boxscore.ui.playbyplay

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.FeedModule
import com.theathletic.feed.ui.FeedUi
import com.theathletic.gamedetail.boxscore.ui.baseball.BaseballPlayByPlayRenderers
import com.theathletic.gamedetail.boxscore.ui.basketball.BasketballPlayByPlayRenderers
import com.theathletic.gamedetail.boxscore.ui.football.FootballPlayByPlayRenderers
import com.theathletic.gamedetail.boxscore.ui.hockey.HockeyPlayByPlayRenderers
import com.theathletic.gamedetail.boxscore.ui.soccer.SoccerPlayByPlayRenderers
import com.theathletic.ui.Transformer

class BoxScorePlayByPlayTransformer @AutoKoin constructor(
    private val basketballPlayByPlayRenderers: BasketballPlayByPlayRenderers,
    private val hockeyPlayByPlayRenderers: HockeyPlayByPlayRenderers,
    private val baseballPlayByPlayRenderers: BaseballPlayByPlayRenderers,
    private val footballPlayByPlayRenderers: FootballPlayByPlayRenderers,
    private val soccerPlayByPlayRenderers: SoccerPlayByPlayRenderers,
) :
    Transformer<BoxScorePlayByPlayState, BoxScorePlayByPlayContract.ViewState> {

    override fun transform(data: BoxScorePlayByPlayState): BoxScorePlayByPlayContract.ViewState {
        return BoxScorePlayByPlayContract.ViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            feedUiModel = FeedUi(modules = renderModules(data))
        )
    }

    private fun renderModules(data: BoxScorePlayByPlayState): List<FeedModule> {
        return when (data.sport) {
            Sport.BASKETBALL -> basketballPlayByPlayRenderers.renderModules(data)
            Sport.HOCKEY -> hockeyPlayByPlayRenderers.renderModules(data)
            Sport.BASEBALL -> baseballPlayByPlayRenderers.renderModules(data)
            Sport.FOOTBALL -> footballPlayByPlayRenderers.renderModules(data)
            Sport.SOCCER -> soccerPlayByPlayRenderers.renderModules(data)
            else -> emptyList()
        }
    }
}