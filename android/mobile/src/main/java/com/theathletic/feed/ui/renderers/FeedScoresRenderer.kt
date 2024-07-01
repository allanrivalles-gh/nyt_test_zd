package com.theathletic.feed.ui.renderers

import android.text.format.DateUtils
import com.theathletic.R
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.feed.ui.models.FeedDiscoveryAnalyticsPayload
import com.theathletic.feed.ui.models.FeedScoresAnalyticsPayload
import com.theathletic.feed.ui.models.FeedScoresCarousel
import com.theathletic.feed.ui.models.FeedScoresCarouselItem
import com.theathletic.scores.data.local.BoxScoreEntity
import com.theathletic.scores.data.local.GameState
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.ui.binding.asParameterized
import com.theathletic.utility.datetime.DateUtilityImpl
import com.theathletic.utility.datetime.DateUtilityImpl.isWithinWeek
import java.util.Date

class FeedScoresRenderer @AutoKoin constructor(
    private val dateUtility: DateUtility
) {

    fun renderFeedBoxScoreCarousel(
        entities: List<BoxScoreEntity>,
        moduleIndex: Int,
        displayOrder: Map<AthleticEntity.Id, Short?>,
        showDiscussButtonMap: Map<String, Boolean>
    ): FeedScoresCarousel {
        var firstVisibleIndex = 0
        val itemModels = entities.sortedBy { displayOrder[it.entityId] ?: 0 }
            .mapIndexed { index, entity ->
                if (displayOrder[entity.entityId] == 0.toShort()) {
                    firstVisibleIndex = index
                }
                val showDiscussButton = showDiscussButtonMap[entity.id] ?: false
                when (entity.state) {
                    GameState.FINAL -> renderFinalBoxScore(entity, moduleIndex, index, showDiscussButton)
                    GameState.LIVE -> renderLiveBoxScore(entity, moduleIndex, index, showDiscussButton)
                    else -> renderUpcomingBoxScore(entity, moduleIndex, index, showDiscussButton)
                }
            }
        return FeedScoresCarousel(
            id = moduleIndex,
            carouselItemModels = itemModels,
            firstVisibleIndex = firstVisibleIndex.toShort()
        )
    }

    private fun renderFinalBoxScore(
        entity: BoxScoreEntity,
        moduleIndex: Int,
        hIndex: Int,
        showDiscussButton: Boolean
    ) = FeedScoresCarouselItem(
        id = entity.id,
        leagueId = entity.leagueIds.firstOrNull() ?: 0L,

        topStatusText = DateUtilityImpl.formatGMTDate(
            entity.gameTime.timeMillis,
            DisplayFormat.MONTH_DATE_SHORT
        ).asParameterized(),
        bottomStatusText = entity.scoreStatusText.orEmpty().asParameterized(),
        isTopStatusGreen = false,
        isBottomStatusRed = false,

        topTeamLogoUrl = entity.firstTeam.logo.orEmpty(),
        topTeamName = entity.firstTeam.shortName.asParameterized(),
        topTeamScore = entity.firstTeam.score.toString(),
        topTeamFaded = entity.firstTeam.score < entity.secondTeam.score,

        bottomTeamLogoUrl = entity.secondTeam.logo.orEmpty(),
        bottomTeamName = entity.secondTeam.shortName.asParameterized(),
        bottomTeamScore = entity.secondTeam.score.toString(),
        bottomTeamFaded = entity.secondTeam.score < entity.firstTeam.score,

        showDiscussButton = showDiscussButton,

        scoresAnalyticsPayload = FeedScoresAnalyticsPayload(
            moduleIndex = moduleIndex,
            hIndex = hIndex
        ),
        discoveryAnalyticsPayload = FeedDiscoveryAnalyticsPayload(
            moduleIndex = moduleIndex,
            hIndex = hIndex
        ),
        impressionPayload = createFeedImpressionPayload(
            entity.id,
            moduleIndex,
            hIndex
        )
    )

    private fun renderLiveBoxScore(
        entity: BoxScoreEntity,
        moduleIndex: Int,
        hIndex: Int,
        showDiscussButton: Boolean
    ): FeedScoresCarouselItem {
        return FeedScoresCarouselItem(
            id = entity.id,
            leagueId = entity.leagueIds.firstOrNull() ?: 0L,
            topStatusText = ParameterizedString(R.string.scores_banner_status_live),
            bottomStatusText = entity.scoreStatusText.orEmpty().asParameterized(),
            isTopStatusGreen = true,
            isBottomStatusRed = false,

            topTeamLogoUrl = entity.firstTeam.logo.orEmpty(),
            topTeamName = entity.firstTeam.shortName.asParameterized(),
            topTeamScore = entity.firstTeam.score.toString(),
            topTeamFaded = false,

            bottomTeamLogoUrl = entity.secondTeam.logo.orEmpty(),
            bottomTeamName = entity.secondTeam.shortName.asParameterized(),
            bottomTeamScore = entity.secondTeam.score.toString(),
            bottomTeamFaded = false,

            showDiscussButton = showDiscussButton,

            scoresAnalyticsPayload = FeedScoresAnalyticsPayload(
                moduleIndex = moduleIndex,
                hIndex = hIndex
            ),
            discoveryAnalyticsPayload = FeedDiscoveryAnalyticsPayload(
                moduleIndex = moduleIndex,
                hIndex = hIndex
            ),
            impressionPayload = createFeedImpressionPayload(
                entity.id,
                moduleIndex,
                hIndex
            )
        )
    }

    private fun renderUpcomingBoxScore(
        entity: BoxScoreEntity,
        moduleIndex: Int,
        hIndex: Int,
        showDiscussButton: Boolean
    ) = FeedScoresCarouselItem(
        id = entity.id,
        leagueId = entity.leagueIds.firstOrNull() ?: 0L,

        topStatusText = entity.toTopStatusText,
        bottomStatusText = entity.toBottomStatusText,
        isTopStatusGreen = false,
        isBottomStatusRed = false,

        topTeamLogoUrl = entity.firstTeam.toTeamLogoUrl,
        topTeamName = entity.firstTeam.toTeamName,
        topTeamScore = "",
        topTeamFaded = false,

        bottomTeamLogoUrl = entity.secondTeam.toTeamLogoUrl,
        bottomTeamName = entity.secondTeam.toTeamName,
        bottomTeamScore = "",
        bottomTeamFaded = false,

        showDiscussButton = showDiscussButton,

        scoresAnalyticsPayload = FeedScoresAnalyticsPayload(
            moduleIndex = moduleIndex,
            hIndex = hIndex
        ),
        discoveryAnalyticsPayload = FeedDiscoveryAnalyticsPayload(
            moduleIndex = moduleIndex,
            hIndex = hIndex
        ),
        impressionPayload = createFeedImpressionPayload(
            entity.id,
            moduleIndex,
            hIndex
        )
    )

    private val BoxScoreEntity.TeamStatus.toTeamName: ParameterizedString
        get() = if (isTbd) ParameterizedString(R.string.global_tbc) else shortName.asParameterized()

    private val BoxScoreEntity.TeamStatus.toTeamLogoUrl: String
        get() = if (isTbd) "" else logo.orEmpty()

    private fun createFeedImpressionPayload(
        gameId: String,
        moduleIndex: Int,
        hIndex: Int
    ) = ImpressionPayload(
        element = "box_score",
        container = "box_score",
        objectType = "game_id",
        objectId = gameId,
        pageOrder = moduleIndex,
        hIndex = hIndex.toLong()
    )

    private val BoxScoreEntity.toTopStatusText
        get() = when {
            DateUtils.isToday(gameTime.timeMillis) ->
                ParameterizedString(R.string.global_date_today)

            Date(gameTime.timeMillis).isWithinWeek() ->
                DateUtilityImpl.formatGMTDate(
                    gameTime.timeMillis,
                    DisplayFormat.WEEKDAY_SHORT
                ).asParameterized()

            else ->
                DateUtilityImpl.formatGMTDate(
                    gameTime.timeMillis,
                    DisplayFormat.MONTH_DATE_SHORT
                ).asParameterized()
        }

    private val BoxScoreEntity.toBottomStatusText
        get() = when (state) {
            GameState.CANCELED -> ParameterizedString(R.string.game_detail_pre_game_canceled_label)
            GameState.POSTPONED -> ParameterizedString(R.string.game_detail_pre_game_postponed_label)
            GameState.SUSPENDED -> ParameterizedString(R.string.game_detail_pre_game_suspended_label)
            GameState.IF_NECESSARY -> ParameterizedString(R.string.game_detail_pre_game_if_necessary_shorten_label)
            GameState.DELAYED -> ParameterizedString(R.string.game_detail_delayed_label)
            else -> if (timeTBA) {
                ParameterizedString(R.string.global_tbd)
            } else {
                dateUtility.formatGMTDate(
                    Datetime(gameTime.timeMillis),
                    DisplayFormat.HOURS_MINUTES
                ).asParameterized()
            }
        }
}