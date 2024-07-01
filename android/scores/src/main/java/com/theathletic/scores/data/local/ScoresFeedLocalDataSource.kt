package com.theathletic.scores.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull

class ScoresFeedLocalDataSource @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val updateFeedDayUseCase: UpdateScoresFeedDayUseCase
) : InMemoryLocalDataSource<String, ScoresFeedLocalModel>() {

    private val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    suspend fun updateScoresFeedDay(
        currentFeedIdentifier: String,
        dayGroupIdentifier: String,
        dayGroups: List<ScoresFeedGroup>
    ) {
        observeItem(currentFeedIdentifier).firstOrNull()?.let { scoresFeed ->
            update(
                key = currentFeedIdentifier,
                data = updateFeedDayUseCase(dayGroupIdentifier, dayGroups, scoresFeed)
            )
        }
    }

    suspend fun updateScoresFeedGameBlock(
        currentFeedIdentifier: String,
        dayGroupIdentifier: String,
        updatedBlock: ScoresFeedBlock
    ) {
        observeItem(currentFeedIdentifier).firstOrNull()?.let { scoresFeed ->
            scoresFeed.days.find { it.day == dayGroupIdentifier }?.let { feedDay ->
                update(
                    key = currentFeedIdentifier,
                    data = updateFeedDayUseCase(
                        dayGroupIdentifier,
                        feedDay.groups.mapUpdatedGroups(updatedBlock),
                        scoresFeed
                    )
                )
            }
        }
    }

    private fun List<ScoresFeedGroup>.mapUpdatedGroups(updatedBlock: ScoresFeedBlock) =
        mapNotNull { group ->
            if (group.blocks.find { it.id == updatedBlock.id } != null) {
                group.updateGroup(group.blocks.mapUpdatedBlock(updatedBlock))
            } else {
                group
            }
        }

    private fun ScoresFeedGroup.updateGroup(updatedBlocks: List<ScoresFeedBlock>) =
        when (this) {
            is ScoresFeedBaseGroup -> copy(blocks = updatedBlocks)
            is ScoresFeedFollowingGroup -> copy(blocks = updatedBlocks)
            is ScoresFeedLeagueGroup -> copy(blocks = updatedBlocks)
            else -> null
        }

    private fun List<ScoresFeedBlock>.mapUpdatedBlock(updatedBlock: ScoresFeedBlock) =
        map { block -> if (block.id == updatedBlock.id) updatedBlock else block }
}