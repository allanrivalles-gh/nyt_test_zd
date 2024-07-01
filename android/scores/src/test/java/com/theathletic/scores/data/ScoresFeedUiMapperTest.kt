package com.theathletic.scores.data

import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.scores.ui.gamecells.GameCellModel
import com.theathletic.scores.ui.gamecells.GameCellModel.GameInfo.DateTimeStatus
import com.theathletic.scores.ui.gamecells.GameCellModel.GameInfo.Default
import com.theathletic.scores.ui.gamecells.GameCellModel.GameInfo.Live
import com.theathletic.scores.ui.gamecells.GameCellModel.GameInfo.Status
import com.theathletic.scores.ui.gamecells.GameCellModel.TeamDetails.PreGame
import com.theathletic.utility.LocaleUtility
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test

class ScoresFeedUiMapperTest {

    private lateinit var mapper: ScoresFeedUiMapper

    @Mock
    lateinit var mockDateUtility: DateUtility

    @Mock
    lateinit var mockLocaleUtility: LocaleUtility

    @BeforeTest
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        whenever(
            mockDateUtility.formatGMTDate(Datetime(TEST_GAME_TIMESTAMP), DisplayFormat.WEEKDAY_MONTH_DATE_ABBREVIATED)
        ).thenReturn(TEST_GAME_FORMATTED_DATE)

        whenever(
            mockDateUtility.formatGMTDate(Datetime(TEST_GAME_TIMESTAMP), DisplayFormat.HOURS_MINUTES)
        ).thenReturn(TEST_GAME_FORMATTED_TIME)

        mapper = ScoresFeedUiMapper(mockDateUtility, mockLocaleUtility)
    }

    @Test
    fun `pregame game data with title and Discussion CTA and US locale is mapped correctly to the UI data`() {
        whenever(mockLocaleUtility.isUnitedStatesOrCanada()).thenReturn(true)

        val uiData = mapper.mapToDayFeedUi(createPregameWithHeaderText(), 0)

        val gameCell = uiData[0].games[0]
        assertThat(gameCell.title).isEqualTo("Conference Finals - Game 3, Panthers lead series 3-0")
        assertThat(gameCell.firstTeam.name).isEqualTo("Team 1")
        assertThat(gameCell.firstTeam.ranking).isEqualTo("") // empty string
        assertThat(gameCell.firstTeam.teamDetails).isEqualTo(PreGame("41-16"))
        assertThat(gameCell.secondTeam.name).isEqualTo("Team 2")
        assertThat(gameCell.secondTeam.ranking).isEqualTo("") // empty string
        assertThat(gameCell.secondTeam.teamDetails).isEqualTo(PreGame("9th in EPL"))
        assertThat(gameCell.infoWidget).isEqualTo(
            GameCellModel.InfoWidget.LabelWidget(
                listOf(
                    DateTimeStatus(TEST_GAME_FORMATTED_DATE),
                    DateTimeStatus(TEST_GAME_FORMATTED_TIME),
                    Default("FOX SPORTS"),
                    Default("BOS -10.5")
                )
            )
        )
        assertThat(gameCell.discussionLinkText).isEqualTo("Join the Discussion")
    }

    @Test
    fun `pregame game, game day data with rankings and US locale is mapped correctly to the UI data`() {
        whenever(mockLocaleUtility.isUnitedStatesOrCanada()).thenReturn(true)

        val uiData = mapper.mapToDayFeedUi(createPregameGameDayWithRankings(), 0)

        val gameCell = uiData[0].games[0]
        assertThat(gameCell.title).isEqualTo("")
        assertThat(gameCell.firstTeam.name).isEqualTo("Team 1")
        assertThat(gameCell.firstTeam.ranking).isEqualTo("5")
        assertThat(gameCell.firstTeam.teamDetails).isEqualTo(PreGame("41-16"))
        assertThat(gameCell.secondTeam.name).isEqualTo("Team 2")
        assertThat(gameCell.secondTeam.ranking).isEqualTo("3")
        assertThat(gameCell.secondTeam.teamDetails).isEqualTo(PreGame("9th in EPL"))
        assertThat(gameCell.infoWidget).isEqualTo(
            GameCellModel.InfoWidget.LabelWidget(
                listOf(DateTimeStatus(TEST_GAME_FORMATTED_TIME), Default("FOX SPORTS"), Default("BOS -10.5"))
            )
        )
    }

    @Test
    fun `pregame game, game day data and non-US locale is mapped correctly to the Odds UI data`() {
        whenever(mockLocaleUtility.isUnitedStatesOrCanada()).thenReturn(false)

        val uiData = mapper.mapToDayFeedUi(createPregameGameDayWithRankings(), 0)

        val gameCell = uiData[0].games[0]
        assertThat(gameCell.infoWidget).isEqualTo(
            GameCellModel.InfoWidget.LabelWidget(
                listOf(DateTimeStatus(TEST_GAME_FORMATTED_TIME), Default("FOX SPORTS"), Default("BOS +1/4"))
            )
        )
    }

    @Test
    fun `generic live game data is mapped correctly to the Ui data`() {
        whenever(mockLocaleUtility.isUnitedStatesOrCanada()).thenReturn(true)

        val uiData = mapper.mapToDayFeedUi(createStandardLiveGame(), 0)
        val gameCell = uiData[0].games[0]
        assertThat(gameCell.title).isEqualTo("")
        assertThat(gameCell.firstTeam.name).isEqualTo("Team 1")
        assertThat(gameCell.firstTeam.ranking).isEqualTo("")
        assertThat(gameCell.firstTeam.teamDetails).isEqualTo(
            GameCellModel.TeamDetails.InAndPostGame(
                score = "34",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        )
        assertThat(gameCell.secondTeam.name).isEqualTo("Team 2")
        assertThat(gameCell.secondTeam.ranking).isEqualTo("")
        assertThat(gameCell.secondTeam.teamDetails).isEqualTo(
            GameCellModel.TeamDetails.InAndPostGame(
                score = "56",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        )
        assertThat(gameCell.infoWidget).isEqualTo(
            GameCellModel.InfoWidget.LabelWidget(listOf(Live("Q3 3:47"), Default("FOX SPORTS")))
        )
    }

    @Test
    fun `live baseball game data is mapped correctly to the Ui data`() {
        whenever(mockLocaleUtility.isUnitedStatesOrCanada()).thenReturn(true)

        val uiData = mapper.mapToDayFeedUi(createLiveBaseballGame(), 0)
        val gameCell = uiData[0].games[0]
        assertThat(gameCell.title).isEqualTo("")
        assertThat(gameCell.firstTeam.name).isEqualTo("Team 1")
        assertThat(gameCell.firstTeam.ranking).isEqualTo("")
        assertThat(gameCell.firstTeam.teamDetails).isEqualTo(
            GameCellModel.TeamDetails.InAndPostGame(
                score = "2",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        )
        assertThat(gameCell.secondTeam.name).isEqualTo("Team 2")
        assertThat(gameCell.secondTeam.ranking).isEqualTo("")
        assertThat(gameCell.secondTeam.teamDetails).isEqualTo(
            GameCellModel.TeamDetails.InAndPostGame(
                score = "1",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        )
        assertThat(gameCell.infoWidget).isEqualTo(
            GameCellModel.InfoWidget.BaseballWidget(
                infos = listOf(Live("BOT 2"), Status("1 OUT"), Default("FOX SPORTS")),
                occupiedBases = listOf(1, 3)
            )
        )
    }

    @Test
    fun `generic final game data is mapped correctly to the Ui data`() {
        whenever(mockLocaleUtility.isUnitedStatesOrCanada()).thenReturn(true)

        val uiData = mapper.mapToDayFeedUi(createStandardPostGame(), 0)
        val gameCell = uiData[0].games[0]
        assertThat(gameCell.title).isEqualTo("")
        assertThat(gameCell.firstTeam.name).isEqualTo("Team 1")
        assertThat(gameCell.firstTeam.ranking).isEqualTo("")
        assertThat(gameCell.firstTeam.isDimmed).isTrue()
        assertThat(gameCell.firstTeam.teamDetails).isEqualTo(
            GameCellModel.TeamDetails.InAndPostGame(
                score = "56",
                penaltyGoals = null,
                icon = null,
                isWinner = false
            )
        )
        assertThat(gameCell.secondTeam.name).isEqualTo("Team 2")
        assertThat(gameCell.secondTeam.ranking).isEqualTo("")
        assertThat(gameCell.secondTeam.isDimmed).isFalse()
        assertThat(gameCell.secondTeam.teamDetails).isEqualTo(
            GameCellModel.TeamDetails.InAndPostGame(
                score = "78",
                penaltyGoals = null,
                icon = null,
                isWinner = true
            )
        )
        assertThat(gameCell.infoWidget).isEqualTo(
            GameCellModel.InfoWidget.LabelWidget(listOf(Status("Final"), Default(TEST_GAME_FORMATTED_DATE)))
        )
    }

    @Test
    fun `pregame game data with a TBD Team is mapped correctly to the UI data`() {
        whenever(mockLocaleUtility.isUnitedStatesOrCanada()).thenReturn(true)

        val uiData = mapper.mapToDayFeedUi(createPregameTBDGameDay(), 0)

        val gameCell = uiData[0].games[0]
        assertThat(gameCell.title).isEqualTo("")
        assertThat(gameCell.firstTeam.name).isEqualTo("Team 1")
        assertThat(gameCell.firstTeam.ranking).isEqualTo("")
        assertThat(gameCell.firstTeam.teamDetails).isEqualTo(PreGame("41-16"))
        assertThat(gameCell.firstTeam.isDimmed).isFalse()
        assertThat(gameCell.secondTeam.name).isEqualTo("TBD")
        assertThat(gameCell.secondTeam.ranking).isEqualTo("")
        assertThat(gameCell.secondTeam.teamDetails).isEqualTo(PreGame(""))
        assertThat(gameCell.secondTeam.isDimmed).isTrue()
        assertThat(gameCell.infoWidget).isEqualTo(
            GameCellModel.InfoWidget.LabelWidget(
                listOf(
                    DateTimeStatus(TEST_GAME_FORMATTED_DATE),
                    DateTimeStatus(TEST_GAME_FORMATTED_TIME),
                    Default("FOX SPORTS"),
                )
            )
        )
    }

    @Test
    fun `canceled pregame game is mapped correctly to the UI data`() {
        whenever(mockLocaleUtility.isUnitedStatesOrCanada()).thenReturn(true)

        val uiData = mapper.mapToDayFeedUi(createPregameWithHeaderText(isCancelled = true), 0)

        val gameCell = uiData[0].games[0]
        assertThat(gameCell.title).isEqualTo("Conference Finals - Game 3, Panthers lead series 3-0")
        assertThat(gameCell.firstTeam.name).isEqualTo("Team 1")
        assertThat(gameCell.firstTeam.teamDetails).isEqualTo(PreGame("41-16"))
        assertThat(gameCell.firstTeam.isDimmed).isTrue()
        assertThat(gameCell.secondTeam.name).isEqualTo("Team 2")
        assertThat(gameCell.secondTeam.teamDetails).isEqualTo(PreGame("9th in EPL"))
        assertThat(gameCell.secondTeam.isDimmed).isTrue()
        assertThat(gameCell.infoWidget).isEqualTo(
            GameCellModel.InfoWidget.LabelWidget(listOf(Status("Canceled")))
        )
    }

    @Test
    fun `final game data with a red card icon and penalty goals is mapped correctly to the Ui data`() {
        whenever(mockLocaleUtility.isUnitedStatesOrCanada()).thenReturn(true)

        val uiData = mapper.mapToDayFeedUi(createStandardPostGame(hasRedCardIconAndPenaltyGoals = true), 0)
        val gameCell = uiData[0].games[0]
        assertThat(gameCell.title).isEqualTo("")
        assertThat(gameCell.firstTeam.name).isEqualTo("Team 1")
        assertThat(gameCell.firstTeam.ranking).isEqualTo("")
        assertThat(gameCell.firstTeam.isDimmed).isTrue()
        assertThat(gameCell.firstTeam.teamDetails).isEqualTo(
            GameCellModel.TeamDetails.InAndPostGame(
                score = "56",
                penaltyGoals = "6",
                icon = GameCellModel.EventIcon.RED_CARD,
                isWinner = false
            )
        )
        assertThat(gameCell.secondTeam.name).isEqualTo("Team 2")
        assertThat(gameCell.secondTeam.ranking).isEqualTo("")
        assertThat(gameCell.secondTeam.isDimmed).isFalse()
        assertThat(gameCell.secondTeam.teamDetails).isEqualTo(
            GameCellModel.TeamDetails.InAndPostGame(
                score = "78",
                penaltyGoals = "8",
                icon = null,
                isWinner = true
            )
        )
        assertThat(gameCell.infoWidget).isEqualTo(
            GameCellModel.InfoWidget.LabelWidget(listOf(Status("Final"), Default(TEST_GAME_FORMATTED_DATE)))
        )
    }

    @Test
    fun `groups for following and leagues are mapped correctly to the Ui data`() {
        whenever(mockLocaleUtility.isUnitedStatesOrCanada()).thenReturn(true)

        val uiData = mapper.mapToDayFeedUi(createGroups(), 0)

        assertThat(uiData.size).isEqualTo(5)
        assertThat(uiData[0].header.title).isEqualTo("NHL")
        assertThat(uiData[0].header.subTitle).isNull()
        assertThat(uiData[1].header.title).isEqualTo("NBA")
        assertThat(uiData[1].header.subTitle).isNull()
        assertThat(uiData[2].header.title).isEqualTo("NFL")
        assertThat(uiData[2].header.subTitle).isEqualTo("8 games today")
        assertThat(uiData[3].header.title).isEqualTo("Premier League")
        assertThat(uiData[3].header.subTitle).isNull()
        assertThat(uiData[4].header.title).isEqualTo("Following")
        assertThat(uiData[4].header.subTitle).isEqualTo("4 games today")
    }
}