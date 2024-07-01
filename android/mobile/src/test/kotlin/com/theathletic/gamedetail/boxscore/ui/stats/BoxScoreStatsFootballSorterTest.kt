package com.theathletic.gamedetail.boxscore.ui.stats

import com.theathletic.gamedetail.boxscore.ui.BoxScoreStatistics
import com.theathletic.gamedetail.boxscore.ui.football.stats.BoxScoreStatsFootballSorter
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.gamedetail.data.local.StatisticCategory
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.test.AutoCloseKoinTest
import org.mockito.MockitoAnnotations

class BoxScoreStatsFootballSorterTest : AutoCloseKoinTest() {

    private lateinit var sorter: BoxScoreStatsFootballSorter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        sorter = BoxScoreStatsFootballSorter()
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

        assertEquals(
            3,
            sortedList?.size,
            "The size of list for Player Stats is incorrect"
        )

        // Check stats for the first category
        checkCategoryAndPlayerOrder(
            stats = sortedList!!.first(),
            expectedCategory = StatisticCategory.RUSHING,
            expectedPlayerOrder = listOf("Player 2", "Player 1", "Player 3"),
            expectedColumns = listOf("CAR", "YDS", "AVG", "TD", "LNG")
        )

        // Check stats for the first category
        checkCategoryAndPlayerOrder(
            stats = sortedList[1],
            expectedCategory = StatisticCategory.FUMBLES,
            expectedPlayerOrder = listOf("Player 2", "Player 3", "Player 1"),
            expectedColumns = listOf("FUM", "LOST", "REC")
        )

        // Check stats for the second category
        checkCategoryAndPlayerOrder(
            stats = sortedList[2],
            expectedCategory = StatisticCategory.KICK_RETURNS,
            expectedPlayerOrder = listOf("Player 3", "Player 1", "Player 2"),
            expectedColumns = listOf("NO", "YDS", "AVG", "LNG", "TD")
        )
    }

    private fun checkCategoryAndPlayerOrder(
        stats: BoxScoreStatistics,
        expectedCategory: StatisticCategory,
        expectedPlayerOrder: List<String>,
        expectedColumns: List<String>
    ) {
        assertEquals(
            3,
            stats.playerStats.size,
            "Item size not correct"
        )
        assertEquals(
            expectedCategory,
            stats.category,
            "Item category not Fumbles"
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
        val players = mutableListOf<GameDetailLocalModel.Player>()

        players.add(
            createAPlayer(
                1,
                listOf(
                    GameDetailLocalModel.IntegerStatistic(
                        id = "83enhxjhdoih",
                        label = BoxScoreStatsFootballSorter.STAT_FUMBLES,
                        type = BoxScoreStatsFootballSorter.STAT_FUMBLES,
                        category = StatisticCategory.FUMBLES,
                        headerLabel = "FUM",
                        intValue = 3,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "FUM"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "jslkdjklsjlkjs",
                        label = BoxScoreStatsFootballSorter.STAT_LOST_FUMBLES,
                        type = BoxScoreStatsFootballSorter.STAT_LOST_FUMBLES,
                        category = StatisticCategory.FUMBLES,
                        headerLabel = "LOST",
                        intValue = 1,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "LOST"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "89ye9uhohdh",
                        label = BoxScoreStatsFootballSorter.STAT_OPP_REC,
                        type = BoxScoreStatsFootballSorter.STAT_OPP_REC,
                        category = StatisticCategory.FUMBLES,
                        headerLabel = "REC",
                        intValue = 1,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "REC"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "93890390",
                        label = BoxScoreStatsFootballSorter.STAT_NUMBER,
                        type = BoxScoreStatsFootballSorter.STAT_NUMBER,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "NO",
                        intValue = 2,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "NO"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "jcoiku30udfjdk",
                        label = BoxScoreStatsFootballSorter.STAT_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_YARDS,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "YDS",
                        intValue = 160,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "YDS"
                    ),
                    GameDetailLocalModel.DecimalStatistic(
                        id = "lkwdhi90ud30jd",
                        label = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "AVG",
                        decimalValue = 73.3,
                        lessIsBest = false,
                        stringValue = "73.3",
                        isChildStat = false,
                        longHeaderLabel = "AVG"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "lkdjcio3u0dj",
                        label = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        type = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "LNG",
                        intValue = 56,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "LNG"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "el;ce;kc;l",
                        label = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        type = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "TD",
                        intValue = 0,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "TD"
                    ),
                    GameDetailLocalModel.DecimalStatistic(
                        id = "ksdmkjsdkjl",
                        label = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "AVG",
                        decimalValue = 12.5,
                        lessIsBest = false,
                        stringValue = "12.5",
                        isChildStat = false,
                        longHeaderLabel = "AVG"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "ksdkh9833d",
                        label = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        type = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "TD",
                        intValue = 1,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "TD"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "kdkldlklekd",
                        label = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        type = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "LNG",
                        intValue = 56,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "LNG"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "893dokhdoi",
                        label = BoxScoreStatsFootballSorter.STAT_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_YARDS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "YDS",
                        intValue = 160,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "YDS"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "893dokhdoi",
                        label = BoxScoreStatsFootballSorter.STAT_ATTEMPTS,
                        type = BoxScoreStatsFootballSorter.STAT_ATTEMPTS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "CAR",
                        intValue = 2,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "CAR"
                    )
                )
            )
        )
        players.add(
            createAPlayer(
                2,
                listOf(
                    GameDetailLocalModel.IntegerStatistic(
                        id = "sldj0e3dujpoidjpo",
                        label = BoxScoreStatsFootballSorter.STAT_FUMBLES,
                        type = BoxScoreStatsFootballSorter.STAT_FUMBLES,
                        category = StatisticCategory.FUMBLES,
                        headerLabel = "FUM",
                        intValue = 5,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "FUM"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "dkdjh093djndo",
                        label = BoxScoreStatsFootballSorter.STAT_LOST_FUMBLES,
                        type = BoxScoreStatsFootballSorter.STAT_LOST_FUMBLES,
                        category = StatisticCategory.FUMBLES,
                        headerLabel = "LOST",
                        intValue = 2,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "LOST"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "jdokh9id3hd",
                        label = BoxScoreStatsFootballSorter.STAT_OPP_REC,
                        type = BoxScoreStatsFootballSorter.STAT_OPP_REC,
                        category = StatisticCategory.FUMBLES,
                        headerLabel = "REC",
                        intValue = 0,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "REC"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "ldldllko-0-3k",
                        label = BoxScoreStatsFootballSorter.STAT_NUMBER,
                        type = BoxScoreStatsFootballSorter.STAT_NUMBER,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "NO",
                        intValue = 3,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "NO"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "lskjdl;;lc;lklkc;",
                        label = BoxScoreStatsFootballSorter.STAT_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_YARDS,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "YDS",
                        intValue = 130,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "YDS"
                    ),
                    GameDetailLocalModel.DecimalStatistic(
                        id = "ldooekdk",
                        label = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "AVG",
                        decimalValue = 42.5,
                        lessIsBest = false,
                        stringValue = "42.5",
                        isChildStat = false,
                        longHeaderLabel = "AVG"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "jdkljsklds",
                        label = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        type = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "LNG",
                        intValue = 45,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "LNG"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "983837333",
                        label = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        type = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "TD",
                        intValue = 0,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "TD"
                    ),
                    GameDetailLocalModel.DecimalStatistic(
                        id = "ksdmkjsdkjl",
                        label = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "AVG",
                        decimalValue = 29.0,
                        lessIsBest = false,
                        stringValue = "29.0",
                        isChildStat = false,
                        longHeaderLabel = "AVG"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "ksdkh9833d",
                        label = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        type = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "TD",
                        intValue = 2,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "TD"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "kdkldlklekd",
                        label = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        type = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "LNG",
                        intValue = 23,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "LNG"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "893dokhdoi",
                        label = BoxScoreStatsFootballSorter.STAT_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_YARDS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "YDS",
                        intValue = 160,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "YDS"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "893dokhdoi",
                        label = BoxScoreStatsFootballSorter.STAT_ATTEMPTS,
                        type = BoxScoreStatsFootballSorter.STAT_ATTEMPTS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "CAR",
                        intValue = 4,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "CAR"
                    )
                )
            )
        )
        players.add(
            createAPlayer(
                3,
                listOf(
                    GameDetailLocalModel.IntegerStatistic(
                        id = "lksjdkljskldj",
                        label = BoxScoreStatsFootballSorter.STAT_FUMBLES,
                        type = BoxScoreStatsFootballSorter.STAT_FUMBLES,
                        category = StatisticCategory.FUMBLES,
                        headerLabel = "FUM",
                        intValue = 3,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "FUM"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "938e903i",
                        label = BoxScoreStatsFootballSorter.STAT_LOST_FUMBLES,
                        type = BoxScoreStatsFootballSorter.STAT_LOST_FUMBLES,
                        category = StatisticCategory.FUMBLES,
                        headerLabel = "LOST",
                        intValue = 2,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "LOST"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "l;dj3u09dupo3d",
                        label = BoxScoreStatsFootballSorter.STAT_OPP_REC,
                        type = BoxScoreStatsFootballSorter.STAT_OPP_REC,
                        category = StatisticCategory.FUMBLES,
                        headerLabel = "REC",
                        intValue = 0,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "REC"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "9383798kodji0",
                        label = BoxScoreStatsFootballSorter.STAT_NUMBER,
                        type = BoxScoreStatsFootballSorter.STAT_NUMBER,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "NO",
                        intValue = 3,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "NO"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "pdj093dijddop",
                        label = BoxScoreStatsFootballSorter.STAT_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_YARDS,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "YDS",
                        intValue = 160,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "YDS"
                    ),
                    GameDetailLocalModel.DecimalStatistic(
                        id = "djjdl;sjd;lj",
                        label = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "AVG",
                        decimalValue = 33.5,
                        lessIsBest = false,
                        stringValue = "33.5",
                        isChildStat = false,
                        longHeaderLabel = "AVG"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "3iofhip0cj",
                        label = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        type = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "LNG",
                        intValue = 87,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "LNG"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "cnoehfnmd",
                        label = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        type = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        category = StatisticCategory.KICK_RETURNS,
                        headerLabel = "TD",
                        intValue = 0,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "TD"
                    ),
                    GameDetailLocalModel.DecimalStatistic(
                        id = "ksdmkjsdkjl",
                        label = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_AVG_YARDS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "AVG",
                        decimalValue = 22.5,
                        lessIsBest = false,
                        stringValue = "22.5",
                        isChildStat = false,
                        longHeaderLabel = "AVG"

                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "ksdkh9833d",
                        label = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        type = BoxScoreStatsFootballSorter.STAT_TOUCHDOWNS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "TD",
                        intValue = 1,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "TD"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "kdkldlklekd",
                        label = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        type = BoxScoreStatsFootballSorter.STAT_LONGEST,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "LNG",
                        intValue = 53,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "LNG"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "893dokhdoi",
                        label = BoxScoreStatsFootballSorter.STAT_YARDS,
                        type = BoxScoreStatsFootballSorter.STAT_YARDS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "YDS",
                        intValue = 110,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "YDS"
                    ),
                    GameDetailLocalModel.IntegerStatistic(
                        id = "893dokhdoi",
                        label = BoxScoreStatsFootballSorter.STAT_ATTEMPTS,
                        type = BoxScoreStatsFootballSorter.STAT_ATTEMPTS,
                        category = StatisticCategory.RUSHING,
                        headerLabel = "CAR",
                        intValue = 3,
                        lessIsBest = false,
                        isChildStat = false,
                        longHeaderLabel = "CAR"
                    )
                )
            )
        )
        return players
    }

    private fun createAPlayer(number: Int, stats: List<GameDetailLocalModel.Statistic>) =
        GameDetailLocalModel.Player(
            id = "player#$number",
            displayName = "Player $number",
            jerseyNumber = "$number",
            place = 0,
            position = PlayerPosition.QUARTERBACK,
            regularPosition = PlayerPosition.QUARTERBACK,
            statistics = stats,
            starter = false,
            playerOrder = 0,
            outcome = null
        )
}