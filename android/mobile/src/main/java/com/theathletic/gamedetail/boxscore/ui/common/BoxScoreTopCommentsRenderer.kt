package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.TopCommentsUiModel
import com.theathletic.boxscore.ui.modules.TopCommentsModule
import com.theathletic.comments.ui.LikeActionUiState
import com.theathletic.comments.utility.CommentsDateFormatter
import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.ui.toCommentUiModel
import com.theathletic.repository.user.IUserDataRepository

class BoxScoreTopCommentsRenderer @AutoKoin constructor(
    private val dateFormatter: CommentsDateFormatter,
    private val userDataRepository: IUserDataRepository
) {

    fun createTopCommentsModule(
        game: GameDetailLocalModel,
        likeActionUiState: LikeActionUiState
    ): FeedModuleV2 {
        val boxScoreTopComments = game.topComments.map {
            val hasUserLiked = userDataRepository.isCommentLiked(it.id.toLong())
            it.toCommentUiModel(dateFormatter, hasUserLiked)
        }
        val uiModel = TopCommentsUiModel(
            comments = boxScoreTopComments,
            showJoinDiscussionIndicator = game.areCommentsDiscoverable
        )
        return TopCommentsModule(game.id, uiModel, likeActionUiState)
    }

    companion object {

        enum class TopCommentsPositionType {
            AFTER_MOMENTS,
            AFTER_PLAYER_GRADES,
            AFTER_RECENT_PLAYS,
            AFTER_SCORING_SUMMARY,
            AFTER_SEASON_STATS,
            AFTER_TEAM_LEADERS
        }

        fun getTopCommentsPosition(game: GameDetailLocalModel): TopCommentsPositionType {
            return when (game.sport) {
                Sport.SOCCER -> getSoccerPosition(game)
                Sport.BASKETBALL -> getBasketballPosition(game)
                else -> getCommonPosition(game)
            }
        }

        private fun getSoccerPosition(game: GameDetailLocalModel): TopCommentsPositionType {
            return if (game.isGameInProgress) {
                TopCommentsPositionType.AFTER_MOMENTS
            } else if (game.isGameCompleted) {
                TopCommentsPositionType.AFTER_PLAYER_GRADES
            } else {
                TopCommentsPositionType.AFTER_SEASON_STATS
            }
        }

        private fun getBasketballPosition(game: GameDetailLocalModel): TopCommentsPositionType {
            return if (game.isGameInProgress) {
                TopCommentsPositionType.AFTER_RECENT_PLAYS
            } else if (game.isGameCompleted) {
                TopCommentsPositionType.AFTER_PLAYER_GRADES
            } else {
                TopCommentsPositionType.AFTER_TEAM_LEADERS
            }
        }

        // Common position for Baseball, Football, and Hockey
        private fun getCommonPosition(game: GameDetailLocalModel): TopCommentsPositionType {
            return if (game.isGameInProgress) {
                TopCommentsPositionType.AFTER_SCORING_SUMMARY
            } else if (game.isGameCompleted) {
                TopCommentsPositionType.AFTER_PLAYER_GRADES
            } else {
                TopCommentsPositionType.AFTER_TEAM_LEADERS
            }
        }
    }
}