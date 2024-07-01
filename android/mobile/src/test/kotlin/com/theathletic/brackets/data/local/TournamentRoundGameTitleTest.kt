package com.theathletic.brackets.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.theathletic.test.FixedTimeProvider
import com.theathletic.type.GameStatusCode
import java.util.Calendar
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TournamentRoundGameTitleTest {
    private val defaultGame = defaultTournamentRoundGame
    private lateinit var tournamentRoundGameTitleFormatter: TournamentRoundGameTitleFormatter
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var timeProvider: FixedTimeProvider

    @Before
    fun setUp() {
        timeProvider = FixedTimeProvider()
        tournamentRoundGameTitleFormatter = TournamentRoundGameTitleFormatterImpl(context, timeProvider)
    }

    @Test
    fun `equals Unscheduled when game is placeholder`() {
        val game = defaultGame.copy(
            isPlaceholder = true,
        )
        assertEquals("Upcoming", tournamentRoundGameTitleFormatter.format(game))
    }

    @Test
    fun `contains month and date followed be venue name when phase is post game`() {
        val november28th = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.NOVEMBER)
            set(Calendar.DAY_OF_MONTH, 28)
        }.timeInMillis
        val game = defaultGame.copy(
            scheduledAt = november28th,
            venueName = "Stadium Name",
            phase = TournamentRoundGame.Phase.PostGame,
        )
        assertEquals("Final, Nov 28, Stadium Name", tournamentRoundGameTitleFormatter.format(game))
    }

    @Test
    fun `contains match time followed by venue name when phase in game while status is in progress`() {
        val game = defaultGame.copy(
            status = GameStatusCode.in_progress,
            matchTimeDisplay = "23'",
            venueName = "Stadium Name",
            phase = TournamentRoundGame.Phase.InGame,
        )
        assertEquals("23' Stadium Name", tournamentRoundGameTitleFormatter.format(game))
    }

    @Test
    fun `contains status description followed by venue name when phase is in game and status is not in progress`() {
        val game = defaultGame.copy(
            status = GameStatusCode.final,
            venueName = "Stadium Name",
            phase = TournamentRoundGame.Phase.InGame,
        )
        assertEquals("Final Stadium Name", tournamentRoundGameTitleFormatter.format(game))
    }

    @Test
    fun `contains time followed by venue name when phase is pre game and it is scheduled for today`() {
        val todayAt233AM = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 2)
            set(Calendar.MINUTE, 33)
        }
        val game = defaultGame.copy(
            scheduledAt = todayAt233AM.timeInMillis,
            venueName = "Stadium Name",
            phase = TournamentRoundGame.Phase.PreGame,
        )
        assertEquals("2:33AM Stadium Name", tournamentRoundGameTitleFormatter.format(game))
    }

    @Test
    fun `contains weekday followed by time followed by venue name when phase is pre game and it is scheduled for within a week`() {
        val monday5Dec2022 = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2022)
            set(Calendar.MONTH, 11)
            set(Calendar.DAY_OF_MONTH, 5)
        }.timeInMillis
        val tuesdayAt141AM = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2022)
            set(Calendar.MONTH, 11)
            set(Calendar.DAY_OF_MONTH, 6)
            set(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 41)
        }.timeInMillis
        val game = defaultGame.copy(
            scheduledAt = tuesdayAt141AM,
            venueName = "Stadium Name",
            phase = TournamentRoundGame.Phase.PreGame,
        )
        timeProvider.currentTimeMs = monday5Dec2022
        assertEquals("Tue, 1:41AM Stadium Name", tournamentRoundGameTitleFormatter.format(game))
    }

    @Test
    fun `contains weekday followed by date followed by venue name when phase is pre game and it is scheduled for after a week`() {
        val wednesday5Oct2022 = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2022)
            set(Calendar.MONTH, 9)
            set(Calendar.DAY_OF_MONTH, 5)
        }.timeInMillis
        val saturday5Nov2022 = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2022)
            set(Calendar.MONTH, 10)
            set(Calendar.DAY_OF_MONTH, 5)
        }.timeInMillis
        val game = defaultGame.copy(
            scheduledAt = saturday5Nov2022,
            venueName = "Stadium Name",
            phase = TournamentRoundGame.Phase.PreGame,
        )
        timeProvider.currentTimeMs = wednesday5Oct2022
        assertEquals("Sat, Nov 5, Stadium Name", tournamentRoundGameTitleFormatter.format(game))
    }
}