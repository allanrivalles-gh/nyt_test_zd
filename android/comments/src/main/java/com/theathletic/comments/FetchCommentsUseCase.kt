package com.theathletic.comments

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.comments.data.CommentsRepository
import com.theathletic.comments.game.TeamThreadsRepository
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.entity.user.SortType

class FetchCommentsUseCase @AutoKoin constructor(
    private val commentsRepository: CommentsRepository,
    private val teamThreadsRepository: TeamThreadsRepository
) {
    suspend operator fun invoke(
        sourceId: String,
        sortType: SortType,
        sourceType: CommentsSourceType
    ): Result<Unit> {
        return try {
            fetchComments(sourceId = sourceId, sourceType = sourceType, sortType = sortType)
            Result.success(Unit)
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }

    private suspend fun fetchComments(
        sourceId: String,
        sourceType: CommentsSourceType,
        sortType: SortType
    ) {
        val key = buildKey(sourceType, sourceId)
        when (sourceType) {
            CommentsSourceType.ARTICLE,
            CommentsSourceType.HEADLINE -> commentsRepository.fetchArticleComments(sourceId, key, sortType.value)

            CommentsSourceType.DISCUSSION -> commentsRepository.fetchDiscussionComments(sourceId, key, sortType.value)
            CommentsSourceType.QANDA -> commentsRepository.fetchQandaComments(sourceId, key, sortType.value)
            CommentsSourceType.PODCAST_EPISODE -> commentsRepository.fetchPodcastEpisodeComments(sourceId, key, sortType.value)
            CommentsSourceType.GAME -> commentsRepository.fetchGameComments(gameId = sourceId, key = key, sortBy = sortType.value)
            CommentsSourceType.TEAM_SPECIFIC_THREAD -> fetchTeamSpecificComments(sourceId, key, sortType)
        }
    }

    private suspend fun fetchTeamSpecificComments(sourceId: String, key: String, sortType: SortType) {
        val teamThreads = teamThreadsRepository.fetchTeamThreads(sourceId)
            ?: throw Exception("Unable to load specific team comments")

        commentsRepository.fetchGameComments(sourceId, teamThreads.teamId, key, sortType.value)
    }

    private fun buildKey(sourceType: CommentsSourceType, entityId: String): String {
        val type = when (sourceType) {
            CommentsSourceType.HEADLINE -> CommentsSourceType.ARTICLE
            CommentsSourceType.TEAM_SPECIFIC_THREAD -> CommentsSourceType.GAME
            else -> sourceType
        }
        return "$type-$entityId"
    }
}