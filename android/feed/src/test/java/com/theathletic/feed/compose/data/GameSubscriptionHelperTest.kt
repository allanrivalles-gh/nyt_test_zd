package com.theathletic.feed.compose.data

import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeProvider
import com.theathletic.scores.data.local.GameState
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test

private const val GAME_SCHEDULED_AT = 10000000L

class GameSubscriptionHelperTest {

    private lateinit var helper: GameSubscriptionHelper
    @Mock lateinit var timeProvider: TimeProvider

    @BeforeTest
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        helper = GameSubscriptionHelper(timeProvider)
    }

    @Test
    fun `return true when game is currently live`() {
        val isLive = helper.isGameLiveOrAboutToStart(GameState.LIVE, Datetime(GAME_SCHEDULED_AT))
        assertThat(isLive).isEqualTo(true)
    }

    @Test
    fun `return false when game is finished`() {
        val isLive = helper.isGameLiveOrAboutToStart(GameState.FINAL, Datetime(GAME_SCHEDULED_AT))
        assertThat(isLive).isEqualTo(false)
    }

    @Test
    fun `return true when current time is within 15 minutes of the start of the game`() {
        val currentTime = GAME_SCHEDULED_AT - 1000 * 60 * 10
        whenever(timeProvider.currentTimeMs).thenReturn(currentTime)
        val isLive = helper.isGameLiveOrAboutToStart(GameState.UPCOMING, Datetime(GAME_SCHEDULED_AT))
        assertThat(isLive).isEqualTo(true)
    }

    @Test
    fun `return true when current time is within 30 minutes after the start of the game and game is not live`() {
        val currentTime = GAME_SCHEDULED_AT + 1000 * 60 * 24
        whenever(timeProvider.currentTimeMs).thenReturn(currentTime)
        val isLive = helper.isGameLiveOrAboutToStart(GameState.UPCOMING, Datetime(GAME_SCHEDULED_AT))
        assertThat(isLive).isEqualTo(true)
    }

    @Test
    fun `return false when current time is earlier than 15 mins before the start of the game`() {
        val currentTime = GAME_SCHEDULED_AT - 1000 * 60 * 30
        whenever(timeProvider.currentTimeMs).thenReturn(currentTime)
        val isLive = helper.isGameLiveOrAboutToStart(GameState.UPCOMING, Datetime(GAME_SCHEDULED_AT))
        assertThat(isLive).isEqualTo(false)
    }

    @Test
    fun `return false when current time is after 30 minutes after the start of the game and game is not live`() {
        val currentTime = GAME_SCHEDULED_AT + 1000 * 60 * 32
        whenever(timeProvider.currentTimeMs).thenReturn(currentTime)
        val isLive = helper.isGameLiveOrAboutToStart(GameState.UPCOMING, Datetime(GAME_SCHEDULED_AT))
        assertThat(isLive).isEqualTo(false)
    }
}