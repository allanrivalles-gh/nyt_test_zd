package com.theathletic.scores.data

import com.apollographql.apollo3.api.Error
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.League
import com.theathletic.repository.CoroutineRepository
import com.theathletic.scores.data.local.Schedule
import com.theathletic.scores.data.local.ScheduleLocalDataSource
import com.theathletic.scores.data.remote.ScheduleApi
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull

@Suppress("RedundantAsync")
class ScheduleRepository @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val scheduleApi: ScheduleApi,
    private val scheduleLocalDataSource: ScheduleLocalDataSource
) : CoroutineRepository {
    class ScheduleException(message: String) : Exception(message)

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    suspend fun fetchTeamSchedule(teamId: String) = repositoryScope.async {
        try {
            val response = scheduleApi.getTeamSchedule(teamId)
            if (response.hasErrors()) throw Exception(response.errors.toMessage())
            response.data?.scheduleFeed?.toDomain()?.let { domainModel ->
                scheduleLocalDataSource.update(teamId, domainModel)
            }
        } catch (error: Throwable) {
            throw ScheduleException("Error fetching Team Schedule for team Id: $teamId, message: ${error.message}")
        }
    }.await()

    suspend fun fetchLeagueSchedule(league: League) = repositoryScope.async {
        try {
            val response = scheduleApi.getLeagueSchedule(league)
            if (response.hasErrors()) throw Exception(response.errors.toMessage())
            response.data?.scheduleFeed?.toDomain()?.let { domainModel ->
                scheduleLocalDataSource.update(league.name, domainModel)
            }
        } catch (error: Throwable) {
            throw ScheduleException("Error fetching League Schedule for league: $league, message: ${error.message}")
        }
    }.await()

    fun getSchedule(key: String) = scheduleLocalDataSource.observeItem(key)

    suspend fun getScheduleFeedGroup(key: String, groupId: String, filterId: String?): Schedule.Group? {
        return scheduleLocalDataSource.observeItem(key).firstOrNull()?.let { scoresFeed ->
            scoresFeed.groups.find {
                it.navItem.id == groupId && it.navItem.filterSelected == filterId
            }
        }
    }

    suspend fun getGroupIdForIndex(key: String, index: Int) =
        scheduleLocalDataSource.observeItem(key).firstOrNull()?.let { scoresFeed ->
            scoresFeed.groups.getOrNull(index)?.navItem?.id
        }

    suspend fun fetchScheduleFeedGroup(
        entity: String,
        groupId: String,
        filterId: String?
    ) = repositoryScope.async {
        try {
            val response = scheduleApi.getScheduleFeedGroup(groupId, filterId)
            if (response.hasErrors()) throw Exception(response.errors.toMessage())
            response.data?.scoresFeedGroups?.toDomain()?.let { domainModel ->
                scheduleLocalDataSource.updateScheduleFeedGroup(entity, groupId, domainModel, filterId)
            }
        } catch (error: Throwable) {
            throw ScheduleException("Error fetching Schedule Group for entity: $entity, id: $groupId, message: ${error.message}")
        }
    }.await()

    suspend fun subscribeToScheduleUpdates(
        key: String,
        groupId: String,
        blockIds: List<String>,
    ) {
        try {
            scheduleApi.getScheduleUpdates(blockIds).collect { data ->
                scheduleLocalDataSource.updateGame(
                    key = key,
                    groupId = groupId,
                    game = data.scoresFeedUpdates.block.fragments.scoresFeedBlock.toDomain()
                )
            }
        } catch (error: Throwable) {
            throw ScheduleException(
                "Subscribing to schedule updates failed for ids: $blockIds with error: ${error.message}"
            )
        }
    }

    private fun List<Error>?.toMessage() =
        this?.joinToString(separator = "\n") { error -> error.message }.orEmpty()
}