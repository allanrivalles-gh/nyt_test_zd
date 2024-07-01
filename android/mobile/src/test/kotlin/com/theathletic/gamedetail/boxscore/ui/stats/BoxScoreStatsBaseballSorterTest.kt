package com.theathletic.gamedetail.boxscore.ui.stats

import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatistics
import com.theathletic.gamedetail.boxscore.ui.baseball.stats.BoxScoreStatsBaseballSorter
import com.theathletic.gamedetail.boxscore.ui.baseball.stats.STAT_AT_BAT
import com.theathletic.gamedetail.boxscore.ui.baseball.stats.STAT_INNINGS_PITCHED
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.local.StatisticCategory
import kotlin.test.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class BoxScoreStatsBaseballSorterTest {

    private lateinit var sorter: BoxScoreStatsBaseballSorter

    private var closeable: AutoCloseable? = null

    companion object {
        const val INDENT = "    "
    }

    @Before
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)

        sorter = BoxScoreStatsBaseballSorter()
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

        // Check stats for the first category - hitters/batters
        checkCategoryAndPlayerOrder(
            stats = sortedList!!.first(),
            expectedSize = 10,
            expectedCategory = StatisticCategory.BATTING,
            expectedPlayerOrder = listOf(
                "Player 1",
                "Player 2",
                "Player 3",
                INDENT + "Player 7",
                INDENT + "Player 8",
                INDENT + "Player 9",
                "Player 4",
                "Player 5",
                INDENT + "Player 2",
                INDENT + "Player 6"
            ),
            expectedPlayerPositions = listOf(
                "C",
                "CF -PH",
                "DH",
                "RF",
                "SS",
                "3B",
                "1B -RF",
                "LF",
                "2B",
                "RF -1B -2B"
            ),
            expectedColumns = listOf("AB")
        )

        // Check stats for the second category - hitters/batters
        checkCategoryAndPlayerOrder(
            stats = sortedList[1],
            expectedSize = 4,
            expectedCategory = StatisticCategory.PITCHING,
            expectedPlayerOrder = listOf(
                "Player 10",
                "Player 11",
                "Player 12",
                "Player 13"
            ),
            expectedPlayerPositions = listOf(
                "(W, 2-1)",
                "(L, 1-1)",
                null,
                "(S, 3)"
            ),
            expectedColumns = listOf("IP")
        )
    }

    @SuppressWarnings("LongParameterList")
    private fun checkCategoryAndPlayerOrder(
        stats: BoxScoreStatistics,
        expectedSize: Int,
        expectedCategory: StatisticCategory,
        expectedPlayerOrder: List<String>,
        expectedPlayerPositions: List<String?>,
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
        expectedPlayerPositions.forEachIndexed { index, positions ->
            assertEquals(
                positions,
                stats.playerStats[index].playerPosition,
                "Player at index $index has incorrect positions or outcome assigned"
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
                    id = "1",
                    playerName = "Player 1",
                    PlayerPosition.CATCHER,
                    playerOrder = 1,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "2",
                    playerName = "Player 2",
                    PlayerPosition.CENTER_FIELD,
                    playerOrder = 2,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "3",
                    playerName = "Player 3",
                    PlayerPosition.DESIGNATED_HITTER,
                    playerOrder = 3,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "4",
                    playerName = "Player 4",
                    PlayerPosition.FIRST_BASE,
                    playerOrder = 4,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "5",
                    playerName = "Player 5",
                    PlayerPosition.LEFT_FIELD,
                    playerOrder = 5,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "6",
                    playerName = "Player 2",
                    PlayerPosition.PINCH_HITTER,
                    playerOrder = 2,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "7",
                    playerName = "Player 4",
                    PlayerPosition.RIGHT_FIELD,
                    playerOrder = 4,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "8",
                    playerName = "Player 2",
                    PlayerPosition.SECOND_BASE,
                    playerOrder = 5,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "9",
                    playerName = "Player 6",
                    PlayerPosition.RIGHT_FIELD,
                    playerOrder = 5,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "10",
                    playerName = "Player 6",
                    PlayerPosition.FIRST_BASE,
                    playerOrder = 5,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "11",
                    playerName = "Player 7",
                    PlayerPosition.RIGHT_FIELD,
                    playerOrder = 3,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "12",
                    playerName = "Player 8",
                    PlayerPosition.SHORTSTOP,
                    playerOrder = 3,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "13",
                    playerName = "Player 9",
                    PlayerPosition.THIRD_BASE,
                    playerOrder = 3,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "14",
                    playerName = "Player 6",
                    PlayerPosition.SECOND_BASE,
                    playerOrder = 5,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.IntegerStatistic(
                            id = "838373788hohk",
                            label = STAT_AT_BAT,
                            type = STAT_AT_BAT,
                            category = StatisticCategory.BATTING,
                            headerLabel = "AB",
                            intValue = 15,
                            lessIsBest = false,
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "20",
                    playerName = "Player 10",
                    PlayerPosition.PITCHER,
                    playerOrder = 0,
                    outcome = "W, 2-1",
                    stats = listOf(
                        GameDetailLocalModel.DecimalStatistic(
                            id = "838373788hohk",
                            label = STAT_INNINGS_PITCHED,
                            type = STAT_INNINGS_PITCHED,
                            category = StatisticCategory.PITCHING,
                            headerLabel = "IP",
                            decimalValue = 6.0,
                            lessIsBest = false,
                            stringValue = "6.0",
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "21",
                    playerName = "Player 11",
                    PlayerPosition.PITCHER,
                    playerOrder = 0,
                    outcome = "L, 1-1",
                    stats = listOf(
                        GameDetailLocalModel.DecimalStatistic(
                            id = "838373788hohk",
                            label = STAT_INNINGS_PITCHED,
                            type = STAT_INNINGS_PITCHED,
                            category = StatisticCategory.PITCHING,
                            headerLabel = "IP",
                            decimalValue = 5.0,
                            lessIsBest = false,
                            stringValue = "5.0",
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "22",
                    playerName = "Player 12",
                    PlayerPosition.PITCHER,
                    playerOrder = 0,
                    outcome = null,
                    stats = listOf(
                        GameDetailLocalModel.DecimalStatistic(
                            id = "838373788hohk",
                            label = STAT_INNINGS_PITCHED,
                            type = STAT_INNINGS_PITCHED,
                            category = StatisticCategory.PITCHING,
                            headerLabel = "IP",
                            decimalValue = 5.0,
                            lessIsBest = false,
                            stringValue = "5.0",
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
            add(
                createAPlayer(
                    id = "23",
                    playerName = "Player 13",
                    PlayerPosition.PITCHER,
                    playerOrder = 0,
                    outcome = "S, 3",
                    stats = listOf(
                        GameDetailLocalModel.DecimalStatistic(
                            id = "838373788hohk",
                            label = STAT_INNINGS_PITCHED,
                            type = STAT_INNINGS_PITCHED,
                            category = StatisticCategory.PITCHING,
                            headerLabel = "IP",
                            decimalValue = 5.0,
                            lessIsBest = false,
                            stringValue = "5.0",
                            isChildStat = false,
                            longHeaderLabel = "AB"
                        )
                    )
                )
            )
        }
    }

    @SuppressWarnings("LongParameterList")
    private fun createAPlayer(
        id: String,
        playerName: String,
        position: PlayerPosition,
        playerOrder: Int,
        outcome: String?,
        stats: List<GameDetailLocalModel.Statistic>
    ) =
        GameDetailLocalModel.Player(
            id = id,
            displayName = playerName,
            jerseyNumber = null,
            place = 0,
            position = position,
            regularPosition = position,
            statistics = stats,
            starter = true,
            playerOrder = playerOrder,
            outcome = outcome
        )
}