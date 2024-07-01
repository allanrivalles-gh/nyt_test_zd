package com.theathletic.gamedetail.boxscore.ui.stats

import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatistics
import com.theathletic.gamedetail.boxscore.ui.basketball.stats.BoxScoreStatsBasketballSorter
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.local.StatisticCategory
import kotlin.test.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class BoxScoreStatsBasketballSorterTest {

    private lateinit var sorter: BoxScoreStatsBasketballSorter

    private var closeable: AutoCloseable? = null

    @Before
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)

        sorter = BoxScoreStatsBasketballSorter()
    }

    @After
    fun tearDown() {
        closeable?.close()
    }

    @Test
    fun `sorter returns an empty list when line up is null`() {
        val sortedList = sorter.sort(null)

        assertEquals(true, sortedList.isNullOrEmpty(), "UiModels for a null game should be empty")
    }

    @Test
    fun `sorter returns correctly an ordered list of categories and players`() {
        val data = createLineUpAndStats()
        val sortedList = sorter.sort(data)

        // Check stats for the first category
        checkCategoryAndPlayerOrder(
            stats = sortedList!!.first(),
            expectedCategory = StatisticCategory.STARTERS,
            expectedPlayerOrder = listOf("Player 4", "Player 5", "Player 1", "Player 2", "Player 3"),
            expectedColumns = listOf("MIN")
        )

        checkCategoryAndPlayerOrder(
            stats = sortedList[1],
            expectedCategory = StatisticCategory.BENCH,
            expectedPlayerOrder = listOf("Player 10", "Player 7", "Player 9", "Player 6", "Player 8"),
            expectedColumns = listOf("MIN")
        )
    }

    private fun checkCategoryAndPlayerOrder(
        stats: BoxScoreStatistics,
        expectedCategory: StatisticCategory,
        expectedPlayerOrder: List<String>,
        expectedColumns: List<String>
    ) {
        assertEquals(
            5,
            stats.playerStats.size,
            "Item size not correct"
        )
        assertEquals(
            expectedCategory,
            stats.category,
            "Item category not as expected"
        )
        assertEquals(
            expectedPlayerOrder[0],
            stats.playerStats[0].playerName,
            "First player not correct, maybe out of order"
        )
        assertEquals(
            expectedPlayerOrder[1],
            stats.playerStats[1].playerName,
            "Second player not correct, maybe out of order"
        )
        assertEquals(
            expectedPlayerOrder[2],
            stats.playerStats[2].playerName,
            "Third player not correct, maybe out of order"
        )
        assertEquals(
            expectedPlayerOrder[3],
            stats.playerStats[3].playerName,
            "Fourth player not correct, maybe out of order"
        )
        assertEquals(
            expectedPlayerOrder[4],
            stats.playerStats[4].playerName,
            "Fifth player not correct, maybe out of order"
        )

        assertEquals(
            expectedColumns,
            stats.playerStats.first().stats.map { it.headerLabel },
            "Ordered columns not in the correct order"
        )
    }

    private fun createLineUpAndStats() = GameDetailLocalModel.LineUp(
        formation = null,
        formationImage = null,
        players = createPlayersList(),
        manager = null
    )

    @SuppressWarnings("LongMethod")
    private fun createPlayersList(): List<GameDetailLocalModel.Player> {
        return mutableListOf<GameDetailLocalModel.Player>().apply {

            add(
                createAPlayer(
                    1,
                    PlayerPosition.SMALL_FORWARD,
                    true,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "83enhxjhdoih",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.STARTERS,
                            headerLabel = "MIN",
                            intValue = 20,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "MIN"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    2,
                    PlayerPosition.POWER_FORWARD,
                    true,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "whdjkhwdhkj",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.STARTERS,
                            headerLabel = "MIN",
                            intValue = 20,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "MIN"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    3,
                    PlayerPosition.CENTER,
                    true,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.STARTERS,
                            headerLabel = "MIN",
                            intValue = 22,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "MIN"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    4,
                    PlayerPosition.POINT_GUARD,
                    true,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "x';lk;ljx",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.STARTERS,
                            headerLabel = "MIN",
                            intValue = 23,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "MIN"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    5,
                    PlayerPosition.SHOOTING_GUARD,
                    true,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "klvjhldmklznjxkl",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.STARTERS,
                            headerLabel = "MIN",
                            intValue = 17,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "MIN"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    6,
                    PlayerPosition.SMALL_FORWARD,
                    false,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "83enhxjhdoih",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.BENCH,
                            headerLabel = "MIN",
                            intValue = 16,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "MIN"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    7,
                    PlayerPosition.POWER_FORWARD,
                    false,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "whdjkhwdhkj",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.BENCH,
                            headerLabel = "MIN",
                            intValue = 18,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "MIN"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    8,
                    PlayerPosition.CENTER,
                    false,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.BENCH,
                            headerLabel = "MIN",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "MIN"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    9,
                    PlayerPosition.POINT_GUARD,
                    false,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "x';lk;ljx",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.BENCH,
                            headerLabel = "MIN",
                            intValue = 18,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "MIN"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    10,
                    PlayerPosition.SHOOTING_GUARD,
                    false,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "klvjhldmklznjxkl",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.BENCH,
                            headerLabel = "MIN",
                            intValue = 20,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "MIN"
                        )
                    )
                )
            )
        }
    }

    private fun createAPlayer(
        number: Int,
        position: PlayerPosition,
        starter: Boolean,
        stats: List<GameDetailLocalModel.Statistic>
    ) =
        GameDetailLocalModel.Player(
            id = "player#$number",
            displayName = "Player $number",
            jerseyNumber = "$number",
            place = 0,
            position = position,
            regularPosition = position,
            statistics = stats,
            starter = starter,
            playerOrder = 0,
            outcome = null
        )
}