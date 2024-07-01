package com.theathletic.feed.compose.ui.formatter

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.Chronos
import com.theathletic.datetime.Datetime
import com.theathletic.feed.compose.data.ScoresCarouselItem
import com.theathletic.scores.data.local.GameState
import com.theathletic.test.FixedTimeProvider
import com.theathletic.ui.asString
import com.theathletic.utility.datetime.DateUtilityImpl
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class ScoresCarouselItemFormatterTest {
    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var timeProvider: FixedTimeProvider
    private lateinit var formatter: ScoresCarouselItemFormatter

    @Before
    fun setUp() {
        timeProvider = FixedTimeProvider()
        formatter = ScoresCarouselItemFormatter(Chronos(timeProvider), DateUtilityImpl)
    }

    @Test
    fun `formatFirstStatusString returns display main when it is not null and status is in progress`() = composeTestRule.setContent {
        val itemStatusDisplay = mockk<ScoresCarouselItem.StatusDisplay> {
            every { main }.returns("BOT 1")
        }
        val item = mockk<ScoresCarouselItem.Game> {
            every { state }.returns(GameState.LIVE)
            every { statusDisplay }.returns(itemStatusDisplay)
        }
        val formatted = formatter.formatFirstStatusString(item).asString()
        assertThat(formatted).isEqualTo(itemStatusDisplay.main)
    }

    @Test
    fun `formatFirstStatusString returns LIVE when display main is null and status is in progress`() = composeTestRule.setContent {
        val itemStatusDisplay = mockk<ScoresCarouselItem.StatusDisplay> {
            every { main }.returns(null)
        }
        val item = mockk<ScoresCarouselItem.Game> {
            every { state }.returns(GameState.LIVE)
            every { statusDisplay }.returns(itemStatusDisplay)
        }
        val formatted = formatter.formatFirstStatusString(item).asString()
        assertThat(formatted).isEqualTo("LIVE")
    }

    @Test
    fun `formatFirstStatusString returns Today if scheduled for same day and status is not in progress`() = composeTestRule.setContent {
        val todayAt3am = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 3)
        }.timeInMillis
        val todayAt10am = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
        }.timeInMillis

        timeProvider.currentTimeMs = todayAt3am
        val item = mockk<ScoresCarouselItem.Game> {
            every { state }.returns(GameState.UPCOMING)
            every { scheduledAt }.returns(Datetime(todayAt10am))
        }
        val formatted = formatter.formatFirstStatusString(item).asString()
        assertThat(formatted).isEqualTo("Today")
    }

    @Test
    fun `formatFirstStatusString returns day of the week if less than one week away and status is not in progress`() = composeTestRule.setContent {
        val monday = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }.timeInMillis
        val friday = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        }.timeInMillis

        timeProvider.currentTimeMs = monday
        val item = mockk<ScoresCarouselItem.Game> {
            every { state }.returns(GameState.UPCOMING)
            every { scheduledAt }.returns(Datetime(friday))
        }
        val formatted = formatter.formatFirstStatusString(item).asString()
        assertThat(formatted).isEqualTo("Fri")
    }

    @Test
    fun `formatFirstStatusString returns month and date if more than one week away and status is not in progress`() = composeTestRule.setContent {
        val november1st = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.NOVEMBER)
            set(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
        val november8th = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.NOVEMBER)
            set(Calendar.DAY_OF_MONTH, 8)
        }.timeInMillis

        timeProvider.currentTimeMs = november1st
        val item = mockk<ScoresCarouselItem.Game> {
            every { state }.returns(GameState.UPCOMING)
            every { scheduledAt }.returns(Datetime(november8th))
        }
        val formatted = formatter.formatFirstStatusString(item).asString()
        assertThat(formatted).isEqualTo("Nov 8")
    }

    @Test
    fun `formatSecondStatusString returns display extra when status is in progress`() = composeTestRule.setContent {
        val itemStatusDisplay = mockk<ScoresCarouselItem.StatusDisplay> {
            every { extra }.returns("1 Out")
        }
        val item = mockk<ScoresCarouselItem.Game> {
            every { state }.returns(GameState.LIVE)
            every { statusDisplay }.returns(itemStatusDisplay)
        }
        val formatted = formatter.formatSecondStatusString(item).asString()
        assertThat(formatted).isEqualTo(itemStatusDisplay.extra)
    }

    @Test
    fun `formatSecondStatusString returns display main and extra when status is final`() = composeTestRule.setContent {
        val itemStatusDisplay = mockk<ScoresCarouselItem.StatusDisplay> {
            every { main }.returns("BOT 1")
            every { extra }.returns("1 Out")
        }
        val item = mockk<ScoresCarouselItem.Game> {
            every { state }.returns(GameState.FINAL)
            every { statusDisplay }.returns(itemStatusDisplay)
        }
        val formatted = formatter.formatSecondStatusString(item).asString()
        assertThat(formatted).isEqualTo("BOT 1 1 Out")
    }

    @Test
    fun `formatSecondStatusString returns TBD if status is scheduled and schedule time is tbd`() = composeTestRule.setContent {
        val item = mockk<ScoresCarouselItem.Game> {
            every { state }.returns(GameState.UPCOMING)
            every { timeTBD }.returns(true)
        }
        val formatted = formatter.formatSecondStatusString(item).asString()
        assertThat(formatted).isEqualTo("TBD")
    }

    @Test
    fun `formatSecondStatusString returns scheduled time if status is scheduled and schedule time is not tbd`() = composeTestRule.setContent {
        val eightFortyFiveAM = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 45)
        }.timeInMillis

        val item = mockk<ScoresCarouselItem.Game> {
            every { state }.returns(GameState.UPCOMING)
            every { timeTBD }.returns(false)
            every { scheduledAt }.returns(Datetime(eightFortyFiveAM))
        }
        val formatted = formatter.formatSecondStatusString(item).asString()
        assertThat(formatted).isEqualTo("8:45 AM")
    }
}