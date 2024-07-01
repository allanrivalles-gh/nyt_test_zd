package com.theathletic.scores.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource
import kotlinx.coroutines.flow.firstOrNull

class ScheduleLocalDataSource @AutoKoin(Scope.SINGLE) constructor() : InMemoryLocalDataSource<String, Schedule>() {

    suspend fun updateScheduleFeedGroup(
        key: String,
        groupId: String,
        sections: List<Schedule.Section>,
        filterId: String?
    ) {
        observeItem(key).firstOrNull()?.let { schedule ->
            update(
                key = key,
                data = schedule.update(groupId, sections, filterId)
            )
        }
    }

    suspend fun updateGame(
        key: String,
        groupId: String,
        game: Schedule.Game
    ) {
        observeItem(key).firstOrNull()?.let { schedule ->
            schedule.groups.find { it.navItem.id == groupId }?.let { group ->
                update(
                    key = key,
                    data = schedule.update(
                        groupId,
                        group.updateSections(game),
                        null
                    )
                )
            }
        }
    }

    private fun Schedule.update(
        groupId: String,
        sections: List<Schedule.Section>,
        filterId: String?
    ): Schedule {
        val updatedGroups = groups.map { group ->
            if (group.navItem.id == groupId) {
                group.copy(
                    sections = sections,
                    navItem = group.navItem.copy(filterSelected = filterId ?: group.navItem.filterSelected)
                )
            } else {
                group
            }
        }
        return copy(groups = updatedGroups)
    }

    private fun Schedule.Group.updateSections(
        game: Schedule.Game
    ): List<Schedule.Section> {
        return sections.map { section ->
            if (section.games.find { it.gameId == game.gameId } != null) {
                section.updateGame(game)
            } else {
                section
            }
        }
    }
}

private fun Schedule.Section.updateGame(updatedGame: Schedule.Game) =
    copy(
        games = games.map { game ->
            if (game.gameId == updatedGame.gameId) updatedGame else game
        }
    )