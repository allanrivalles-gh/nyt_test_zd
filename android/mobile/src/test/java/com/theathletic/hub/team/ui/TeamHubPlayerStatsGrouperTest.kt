package com.theathletic.hub.team.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.local.StatisticCategory
import com.theathletic.hub.team.data.local.TeamHubStatsLocalModel
import com.theathletic.hub.ui.SortablePlayerValuesTableUi
import org.junit.Before
import org.junit.Test

class TeamHubPlayerStatsGrouperTest {

    lateinit var grouper: TeamHubPlayerStatsGrouper

    @Before
    fun setup() {
        grouper = TeamHubPlayerStatsGrouper()
    }

    @Test
    fun `new ordering correct when resorting an Integer Statistic column and current order is None`() {
        val reordered = resort(
            sortColumn = "IntegerStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.None
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByIntegerValueDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting an Integer Statistic column and current order is Descending`() {
        val reordered = resort(
            sortColumn = "IntegerStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Descending
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByIntegerValueDescendingOrder.reversed())
    }

    @Test
    fun `new ordering correct when resorting an Integer Statistic column and current order is Ascending`() {
        val reordered = resort(
            sortColumn = "IntegerStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Ascending
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByIntegerValueDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting a Decimal Statistic column and current order is None`() {
        val reordered = resort(
            sortColumn = "DecimalStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.None
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByDecimalValueDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting a Decimal Statistic column and current order is Descending`() {
        val reordered = resort(
            sortColumn = "DecimalStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Descending
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByDecimalValueDescendingOrder.reversed())
    }

    @Test
    fun `new ordering correct when resorting a Decimal Statistic column and current order is Ascending`() {
        val reordered = resort(
            sortColumn = "DecimalStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Ascending
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByDecimalValueDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting a Percentage Statistic column and current order is None`() {
        val reordered = resort(
            sortColumn = "PercentageStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.None
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByPercentageValueDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting a Percentage Statistic column and current order is Descending`() {
        val reordered = resort(
            sortColumn = "PercentageStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Descending
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByPercentageValueDescendingOrder.reversed())
    }

    @Test
    fun `new ordering correct when resorting a Percentage Statistic column and current order is Ascending`() {
        val reordered = resort(
            sortColumn = "PercentageStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Ascending
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByPercentageValueDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting a String Statistic column and current order is None`() {
        val reordered = resort(
            sortColumn = "StringStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.None
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByStringValueDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting a String Statistic column and current order is Descending`() {
        val reordered = resort(
            sortColumn = "StringStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Descending
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByStringValueDescendingOrder.reversed())
    }

    @Test
    fun `new ordering correct when resorting a String Statistic column and current order is Ascending`() {
        val reordered = resort(
            sortColumn = "StringStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Ascending
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByStringValueDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting a Time Statistic column and current order is None`() {
        val reordered = resort(
            sortColumn = "TimeStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.None
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByTimeValueDescendingOrder)
    }

    @Test
    fun `new ordering correct when resorting a Time Statistic column and current order is Descending`() {
        val reordered = resort(
            sortColumn = "TimeStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Descending
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByTimeValueDescendingOrder.reversed())
    }

    @Test
    fun `new ordering correct when resorting a Time Statistic column and current order is Ascending`() {
        val reordered = resort(
            sortColumn = "TimeStatistic",
            currentOrder = SortablePlayerValuesTableUi.ColumnOrder.Ascending
        )

        assertThat(reordered.first().playerStats.map { it.id.toInt() })
            .isEqualTo(playersOrderByTimeValueDescendingOrder)
    }

    private fun resort(
        sortColumn: String,
        currentOrder: SortablePlayerValuesTableUi.ColumnOrder
    ) = grouper.resortColumn(
        createUnsortedPlayerStatsData(),
        categoryType = TeamHubStatsState.CategoryType.NoCategories,
        sortColumn = sortColumn,
        currentOrder = currentOrder
    )

    private fun createUnsortedPlayerStatsData(): List<TeamHubStatsState.Category> {
        return listOf(
            TeamHubStatsState.Category(
                type = TeamHubStatsState.CategoryType.NoCategories,
                currentSortColumn = "",
                order = SortablePlayerValuesTableUi.ColumnOrder.None,
                playerStats = createPlayers()
            )
        )
    }

    @SuppressWarnings("LongMethod")
    private fun createPlayers(): List<TeamHubStatsLocalModel.PlayerStats> {
        return listOf(
            TeamHubStatsLocalModel.PlayerStats(
                id = "1",
                displayName = "Player 1",
                headshots = emptyList(),
                jerseyNumber = 1,
                position = PlayerPosition.CENTER,
                stats = createPlayerStats(4, 1.6, 34.7, "R", 12, 34, 22)
            ),
            TeamHubStatsLocalModel.PlayerStats(
                id = "2",
                displayName = "Player 2",
                headshots = emptyList(),
                jerseyNumber = 2,
                position = PlayerPosition.CENTER,
                stats = createPlayerStats(12, 0.7, 88.7, "M", 455, 52, 22)
            ),
            TeamHubStatsLocalModel.PlayerStats(
                id = "3",
                displayName = "Player 3",
                headshots = emptyList(),
                jerseyNumber = 3,
                position = PlayerPosition.CENTER,
                stats = createPlayerStats(34, 25.3, 12.3, "12", 0, 36, 13)
            ),
            TeamHubStatsLocalModel.PlayerStats(
                id = "4",
                displayName = "Player 4",
                headshots = emptyList(),
                jerseyNumber = 4,
                position = PlayerPosition.CENTER,
                stats = createPlayerStats(21, 13.2, 34.2, "P", 10, 0, 0)
            ),
            TeamHubStatsLocalModel.PlayerStats(
                id = "5",
                displayName = "Player 5",
                headshots = emptyList(),
                jerseyNumber = 5,
                position = PlayerPosition.CENTER,
                stats = createPlayerStats(50, 8.3, 73.2, "B", 23, 47, 52)
            ),
            TeamHubStatsLocalModel.PlayerStats(
                id = "6",
                displayName = "Player 6",
                headshots = emptyList(),
                jerseyNumber = 6,
                position = PlayerPosition.CENTER,
                stats = createPlayerStats(18, 29.9, 55.1, "536", 1, 1, 10)
            ),
            TeamHubStatsLocalModel.PlayerStats(
                id = "7",
                displayName = "Player 7",
                headshots = emptyList(),
                jerseyNumber = 7,
                position = PlayerPosition.CENTER,
                stats = createPlayerStats(1, 10.0, 67.1, "F", 26, 7, 23)
            ),
            TeamHubStatsLocalModel.PlayerStats(
                id = "8",
                displayName = "Player 8",
                headshots = emptyList(),
                jerseyNumber = 8,
                position = PlayerPosition.CENTER,
                stats = createPlayerStats(45, 38.2, 12.9, "Athletic", 867, 34, 16)
            ),
        )
    }

    @SuppressWarnings("LongParameterList", "LongMethod")
    private fun createPlayerStats(
        intValue: Int,
        decValue: Double,
        pctValue: Double,
        stringValue: String,
        hoursValue: Int,
        minsValue: Int,
        secsValue: Int
    ): List<GameDetailLocalModel.Statistic> {
        return listOf<GameDetailLocalModel.Statistic>(
            GameDetailLocalModel.IntegerStatistic(
                id = "int stat",
                label = "",
                headerLabel = "",
                type = "IntegerStatistic",
                category = StatisticCategory.UNKNOWN,
                lessIsBest = false,
                isChildStat = false,
                intValue = intValue,
                longHeaderLabel = ""
            ),
            GameDetailLocalModel.DecimalStatistic(
                id = "dec stat",
                label = "",
                type = "DecimalStatistic",
                category = StatisticCategory.UNKNOWN,
                headerLabel = "",
                lessIsBest = false,
                isChildStat = false,
                decimalValue = decValue,
                stringValue = null,
                longHeaderLabel = ""
            ),
            GameDetailLocalModel.PercentageStatistic(
                id = "pct stat",
                label = "",
                type = "PercentageStatistic",
                category = StatisticCategory.UNKNOWN,
                headerLabel = "",
                lessIsBest = false,
                isChildStat = false,
                decimalValue = pctValue,
                stringValue = null,
                longHeaderLabel = ""
            ),
            GameDetailLocalModel.StringStatistic(
                id = "str stat",
                label = "",
                type = "StringStatistic",
                category = StatisticCategory.UNKNOWN,
                headerLabel = "",
                lessIsBest = false,
                isChildStat = false,
                value = stringValue,
                longHeaderLabel = ""
            ),
            GameDetailLocalModel.TimeStatistic(
                id = "time stat",
                label = "",
                type = "TimeStatistic",
                category = StatisticCategory.UNKNOWN,
                headerLabel = "",
                lessIsBest = false,
                isChildStat = false,
                hours = hoursValue,
                minutes = minsValue,
                seconds = secsValue,
                stringValue = null,
                longHeaderLabel = ""
            )
        )
    }

    private val playersOrderByIntegerValueDescendingOrder = listOf(5, 8, 3, 4, 6, 2, 1, 7)
    private val playersOrderByDecimalValueDescendingOrder = listOf(8, 6, 3, 4, 7, 5, 1, 2)
    private val playersOrderByPercentageValueDescendingOrder = listOf(2, 5, 7, 6, 1, 4, 8, 3)
    private val playersOrderByStringValueDescendingOrder = listOf(1, 4, 2, 7, 5, 8, 6, 3)
    private val playersOrderByTimeValueDescendingOrder = listOf(8, 2, 7, 5, 1, 4, 6, 3)
}