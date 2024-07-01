package com.theathletic.hub.team.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.hub.team.data.local.TeamHubRosterLocalModel
import com.theathletic.hub.ui.SortablePlayerValuesTableUi
import org.junit.Before
import org.junit.Test

class TeamHubRosterGrouperTest {

    lateinit var grouper: TeamHubRosterGrouper

    @Before
    fun setup() {
        grouper = TeamHubRosterGrouper()
    }

    @Test
    fun `new ordering correct when resorting the height column and current order is None`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.Height,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.None
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByHeightInDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting the height column and current order is Descending`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.Height,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Descending
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByHeightInDescendingOrder.reversed())
    }

    @Test
    fun `new ordering correct when resorting the height column and current order is Ascending`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.Height,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Ascending
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByHeightInDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting the weight column and current order is None`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.Weight,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.None
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByWeightInDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting the weight column and current order is Descending`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.Weight,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Descending
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByWeightInDescendingOrder.reversed())
    }

    @Test
    fun `new ordering correct when resorting the weight column and current order is Ascending`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.Weight,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Ascending
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByWeightInDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting the date of birth column and current order is None`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.DateOfBirth,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.None
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByDOBInDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting the date of birth column and current order is Descending`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.DateOfBirth,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Descending
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByDOBInDescendingOrder.reversed())
    }

    @Test
    fun `new ordering correct when resorting the date of birth column and current order is Ascending`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.DateOfBirth,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Ascending
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByDOBInDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting the position column and current order is None`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.Position,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.None
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByPositionInDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting the position column and current order is Descending`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.Position,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Descending
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByPositionInAscendingOrder)
    }

    @Test
    fun `new ordering correct when resorting the position column and current order is Ascending`() {
        val reordered = resort(
            sortType = TeamHubRosterState.SortType.Position,
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Ascending
        )

        assertThat(reordered.first().roster.map { it.id.toInt() })
            .isEqualTo(playersOrderByPositionInDescendingOrder)
    }

    private fun resort(
        sortType: TeamHubRosterState.SortType,
        currentOrder: SortablePlayerValuesTableUi.ColumnOrder
    ) = grouper.resortColumn(
        createUnsortedRosterData(),
        categoryType = TeamHubRosterState.CategoryType.NoCategories,
        sortType = sortType,
        currentOrder = currentOrder
    )

    private fun createUnsortedRosterData(): List<TeamHubRosterState.Category> {
        return listOf(
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.NoCategories,
                sortType = TeamHubRosterState.SortType.Default,
                order = SortablePlayerValuesTableUi.ColumnOrder.None,
                roster = createPlayerRoster()
            )
        )
    }

    @Suppress("LongMethod")
    private fun createPlayerRoster(): List<TeamHubRosterLocalModel.PlayerDetails> {
        return listOf(
            TeamHubRosterLocalModel.PlayerDetails(
                id = "1",
                displayName = "Player 1",
                jerseyNumber = 1,
                headshots = emptyList(),
                position = PlayerPosition.CENTER,
                height = 200,
                weight = 100,
                dateOfBirth = "1993-12-21"
            ),
            TeamHubRosterLocalModel.PlayerDetails(
                id = "2",
                displayName = "Player 2",
                jerseyNumber = 2,
                headshots = emptyList(),
                position = PlayerPosition.POINT_GUARD,
                height = 210,
                weight = 90,
                dateOfBirth = "1989-02-29"
            ),
            TeamHubRosterLocalModel.PlayerDetails(
                id = "3",
                displayName = "Player 3",
                jerseyNumber = 3,
                headshots = emptyList(),
                position = PlayerPosition.SMALL_FORWARD,
                height = 189,
                weight = 75,
                dateOfBirth = "1994-08-01"
            ),
            TeamHubRosterLocalModel.PlayerDetails(
                id = "4",
                displayName = "Player 4",
                jerseyNumber = 4,
                headshots = emptyList(),
                position = PlayerPosition.POINT_GUARD,
                height = 211,
                weight = 85,
                dateOfBirth = "1993-11-20"
            ),
            TeamHubRosterLocalModel.PlayerDetails(
                id = "5",
                displayName = "Player 5",
                jerseyNumber = 5,
                headshots = emptyList(),
                position = PlayerPosition.POINT_GUARD,
                height = 220,
                weight = 80,
                dateOfBirth = "1990-02-13"
            ),
            TeamHubRosterLocalModel.PlayerDetails(
                id = "6",
                displayName = "Player 6",
                jerseyNumber = 6,
                headshots = emptyList(),
                position = PlayerPosition.CENTER,
                height = 180,
                weight = 105,
                dateOfBirth = "1990-12-21"
            ),
            TeamHubRosterLocalModel.PlayerDetails(
                id = "7",
                displayName = "Player 7",
                jerseyNumber = 7,
                headshots = emptyList(),
                position = PlayerPosition.SMALL_FORWARD,
                height = 185,
                weight = 99,
                dateOfBirth = "2000-12-21"
            ),
            TeamHubRosterLocalModel.PlayerDetails(
                id = "8",
                displayName = "Player 8",
                jerseyNumber = 8,
                headshots = emptyList(),
                position = PlayerPosition.CENTER,
                height = 205,
                weight = 78,
                dateOfBirth = "1995-11-21"
            ),
        )
    }
}

private val playersOrderByHeightInDescendingOrder = listOf(5, 4, 2, 8, 1, 3, 7, 6)

private val playersOrderByWeightInDescendingOrder = listOf(6, 1, 7, 2, 4, 5, 8, 3)

private val playersOrderByDOBInDescendingOrder = listOf(7, 8, 3, 1, 4, 6, 5, 2)

private val playersOrderByPositionInDescendingOrder = listOf(1, 6, 8, 3, 7, 2, 4, 5)

private val playersOrderByPositionInAscendingOrder = listOf(2, 4, 5, 3, 7, 1, 6, 8)