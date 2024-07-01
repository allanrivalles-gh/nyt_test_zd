package com.theathletic.scores.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.repository.CoroutineRepository
import com.theathletic.scores.data.local.ScoresFeedLocalDataSource
import com.theathletic.scores.data.remote.ScoresFeedGraphqlApi
import com.theathletic.scores.data.remote.toLocalModel
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull

@Suppress("RedundantAsync")
class ScoresFeedRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val scoresFeedGraphqlApi: ScoresFeedGraphqlApi,
    private val scoresFeedLocalDataSource: ScoresFeedLocalDataSource,
) : CoroutineRepository {

    class ScoresFeedException(message: String) : Exception(message)

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    /*
     * Parameter 'currentFeedIdentifier' must be the local today date and be in the format "YYYY-MM-DD" eg 2023-03-22
     */
    suspend fun fetchScoresFeed(currentFeedIdentifier: String) = repositoryScope.async {
        try {
            val response = scoresFeedGraphqlApi.getScoresFeed()
            if (response.hasErrors()) throw ScoresFeedException("Error fetching the Scores Feed")
            response.data?.toLocalModel()?.let { localModel ->
                scoresFeedLocalDataSource.update(currentFeedIdentifier, localModel)
            }
        } catch (error: Throwable) {
            throw ScoresFeedException("Exception fetching the Scores Feed, message: ${error.message}")
        }
    }.await()

    /*
     * Parameter 'dayGroupIdentifier' to be in the format "YYYY-MM-DD" eg 2023-03-22
     * This should be exactly what is provided in the fetchScoresFeed query response
     * for that day group. 'currentFeedIdentifier' as described for fetchScoresFeed above.
     */
    suspend fun fetchScoresFeedForDay(
        currentFeedIdentifier: String,
        dayGroupIdentifier: String
    ) = repositoryScope.async {
        try {
            val response = scoresFeedGraphqlApi.getScoresFeedForDay(dayGroupIdentifier)
            if (response.hasErrors()) {
                throw ScoresFeedException("Error occurred fetching Scores Feed for day: $dayGroupIdentifier")
            }
            response.data?.toLocalModel()?.let { localModel ->
                scoresFeedLocalDataSource.updateScoresFeedDay(
                    currentFeedIdentifier = currentFeedIdentifier,
                    dayGroupIdentifier = dayGroupIdentifier,
                    dayGroups = localModel
                )
            }
        } catch (error: Throwable) {
            throw ScoresFeedException(
                "Exception received fetching Scores Feed for day: $dayGroupIdentifier, message: ${error.message}"
            )
        }
    }.await()

    fun getScoresFeed(currentFeedIdentifier: String) = scoresFeedLocalDataSource.observeItem(currentFeedIdentifier)

    suspend fun subscribeToScoresFeedUpdates(
        currentFeedIdentifier: String,
        dayGroupIdentifier: String,
        blockIds: List<String>
    ) {
        try {
            scoresFeedGraphqlApi.getScoreFeedUpdates(blockIds).collect { data ->
                val localModel = data.scoresFeedUpdates.block.fragments.scoresFeedBlock.toLocalModel
                scoresFeedLocalDataSource.updateScoresFeedGameBlock(
                    currentFeedIdentifier = currentFeedIdentifier,
                    dayGroupIdentifier = dayGroupIdentifier,
                    updatedBlock = localModel
                )
            }
        } catch (error: Throwable) {
            throw ScoresFeedException("Setting up Scores Feed subscription failed with error: ${error.message}")
        }
    }

    suspend fun isEmptyDayFeed(currentFeedIdentifier: String, dayGroupIdentifier: String): Boolean {
        return scoresFeedLocalDataSource.observeItem(currentFeedIdentifier).firstOrNull()?.let { scoresFeed ->
            scoresFeed.days.find { it.day == dayGroupIdentifier }?.groups?.isEmpty()
        } ?: true
    }
}