package com.theathletic.boxscore.analytics

import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.gamedetail.data.local.GameStatus

interface NewsroomAnalytics {
    fun trackArticleContentClick(gameStatus: GameStatus, postId: String, gameId: String)
    fun trackPodcastEpisodeClick(gameStatus: GameStatus, podcastEpisodeId: String, gameId: String)
    fun trackPodcastEpisodeMenuClick(gameStatus: GameStatus, podcastEpisodeId: String, gameId: String)

    fun trackPodcastEpisodePlay(gameStatus: GameStatus, podcastEpisodeId: String, gameId: String)

    fun trackPodcastFollowUnfollow(gameStatus: GameStatus, podcastEpisodeId: String, gameId: String, isFollow: Boolean)
    fun trackPodcastDownload(gameStatus: GameStatus, podcastEpisodeId: String, gameId: String)
}

private const val PREGAME_GAME = "pregame_box_score_game"
private const val INGAME_GAME = "ingame_box_score_game"
private const val POSTGAME_GAME = "postgame_box_score_game"

class NewsroomAnalyticsHandler @AutoKoin constructor(
    private val analytics: IAnalytics
) : NewsroomAnalytics {

    override fun trackArticleContentClick(
        gameStatus: GameStatus,
        postId: String,
        gameId: String
    ) {
        analytics.track(
            Event.Newsroom.Click(
                view = gameStatus.toViewStatus(),
                object_type = "post_id",
                object_id = postId,
                game_id = gameId
            )
        )
    }

    override fun trackPodcastEpisodeClick(
        gameStatus: GameStatus,
        podcastEpisodeId: String,
        gameId: String
    ) {
        analytics.track(
            Event.Newsroom.Click(
                view = gameStatus.toViewStatus(),
                object_type = "podcast_episode_id",
                object_id = podcastEpisodeId,
                game_id = gameId
            )
        )
    }

    override fun trackPodcastEpisodeMenuClick(
        gameStatus: GameStatus,
        podcastEpisodeId: String,
        gameId: String
    ) {
        analytics.track(
            Event.Newsroom.Click(
                view = gameStatus.toViewStatus(),
                object_type = "podcast_episode_id",
                object_id = podcastEpisodeId,
                game_id = gameId
            )
        )
    }

    override fun trackPodcastEpisodePlay(
        gameStatus: GameStatus,
        podcastEpisodeId: String,
        gameId: String
    ) {
        analytics.track(
            Event.Newsroom.Play(
                view = gameStatus.toViewStatus(),
                object_type = "podcast_episode",
                podcast_episode_id = podcastEpisodeId,
                game_id = gameId
            )
        )
    }

    override fun trackPodcastFollowUnfollow(
        gameStatus: GameStatus,
        podcastEpisodeId: String,
        gameId: String,
        isFollow: Boolean
    ) {
        val view = gameStatus.toViewStatus()
        if (isFollow) {
            analytics.track(
                Event.Newsroom.Add(
                    view = view,
                    object_type = "podcast_episode_id",
                    object_id = podcastEpisodeId,
                    game_id = gameId
                )
            )
        } else {
            analytics.track(
                Event.Newsroom.Remove(
                    view = view,
                    object_type = "podcast_episode_id",
                    object_id = podcastEpisodeId,
                    game_id = gameId
                )
            )
        }
    }

    override fun trackPodcastDownload(gameStatus: GameStatus, podcastEpisodeId: String, gameId: String) {
        analytics.track(
            Event.Newsroom.Download(
                view = gameStatus.toViewStatus(),
                object_type = "podcast_episode_id",
                object_id = podcastEpisodeId,
                game_id = gameId
            )
        )
    }

    private fun GameStatus.toViewStatus() =
        when (this) {
            GameStatus.IN_PROGRESS -> INGAME_GAME
            GameStatus.FINAL -> POSTGAME_GAME
            else -> PREGAME_GAME
        }
}