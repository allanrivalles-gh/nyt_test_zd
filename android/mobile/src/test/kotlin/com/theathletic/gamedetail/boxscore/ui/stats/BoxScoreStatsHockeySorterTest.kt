package com.theathletic.gamedetail.boxscore.ui.stats

import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatistics
import com.theathletic.gamedetail.boxscore.ui.basketball.stats.BoxScoreStatsBasketballSorter
import com.theathletic.gamedetail.boxscore.ui.hockey.stats.BoxScoreStatsHockeySorter
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.local.StatisticCategory
import kotlin.test.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class BoxScoreStatsHockeySorterTest {

    private lateinit var sorter: BoxScoreStatsHockeySorter

    private var closeable: AutoCloseable? = null

    @Before
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)

        sorter = BoxScoreStatsHockeySorter()
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
            expectedSize = 2,
            expectedCategory = StatisticCategory.GOALIES,
            expectedPlayerOrder = listOf("Player 4", "Player 6"),
            expectedColumns = listOf("SA")
        )

        checkCategoryAndPlayerOrder(
            stats = sortedList[1],
            expectedSize = 8,
            expectedCategory = StatisticCategory.SKATERS,
            expectedPlayerOrder = listOf(
                "Player 1", "Player 10", "Player 2", "Player 3", "Player 5", "Player 7", "Player 8", "Player 9",
            ),
            expectedColumns = listOf("G")
        )
    }

    private fun checkCategoryAndPlayerOrder(
        stats: BoxScoreStatistics,
        expectedSize: Int,
        expectedCategory: StatisticCategory,
        expectedPlayerOrder: List<String>,
        expectedColumns: List<String>
    ) {
        assertEquals(
            expectedSize,
            stats.playerStats.size,
            "Item size not correct"
        )
        assertEquals(
            expectedCategory,
            stats.category,
            "Item category not as expected"
        )
        expectedPlayerOrder.forEachIndexed { index, player ->
            assertEquals(
                player,
                stats.playerStats[index].playerName,
                "Player at index $index not correct, maybe out of order"
            )
        }
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
                    PlayerPosition.DEFENSE,
                    true,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "83enhxjhdoih",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.STARTERS,
                            headerLabel = "G",
                            intValue = 20,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "G"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    2,
                    PlayerPosition.FORWARD,
                    true,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "whdjkhwdhkj",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.STARTERS,
                            headerLabel = "G",
                            intValue = 20,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "G"
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
                            headerLabel = "G",
                            intValue = 22,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "G"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    4,
                    PlayerPosition.GOALIE,
                    true,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "x';lk;ljx",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.STARTERS,
                            headerLabel = "SA",
                            intValue = 23,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "SA"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    5,
                    PlayerPosition.LEFT_WING,
                    true,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "klvjhldmklznjxkl",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.STARTERS,
                            headerLabel = "G",
                            intValue = 17,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "G"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    6,
                    PlayerPosition.GOALIE,
                    false,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "83enhxjhdoih",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.BENCH,
                            headerLabel = "SA",
                            intValue = 16,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "SA"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    7,
                    PlayerPosition.RIGHT_WING,
                    false,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "whdjkhwdhkj",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.BENCH,
                            headerLabel = "G",
                            intValue = 18,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "G"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    8,
                    PlayerPosition.DEFENSE,
                    false,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.BENCH,
                            headerLabel = "G",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "G"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    9,
                    PlayerPosition.FORWARD,
                    false,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "x';lk;ljx",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.BENCH,
                            headerLabel = "G",
                            intValue = 18,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "G"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    10,
                    PlayerPosition.DEFENSE,
                    false,
                    listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "klvjhldmklznjxkl",
                            label = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            type = BoxScoreStatsBasketballSorter.STAT_MINUTES,
                            category = StatisticCategory.BENCH,
                            headerLabel = "G",
                            intValue = 20,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "G"
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