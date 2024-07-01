package com.theathletic.feed.compose.data

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.theathletic.ui.asString
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScoresCarouselItemTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun `Team identifier returns alias if team is not null`() = composeTestRule.setContent {
        val teamAlias = "BOS"
        val team = mockk<ScoresCarouselItem.Team> {
            every { alias }.returns(teamAlias)
        }
        assertThat(team.identifier.asString()).isEqualTo(teamAlias)
    }

    @Test
    fun `Team identifier returns TBD if team is null`() = composeTestRule.setContent {
        val team: ScoresCarouselItem.Team? = null
        assertThat(team.identifier.asString()).isEqualTo("TBD")
    }

    @Test
    fun `Team displayScore returns null if no score provided`() {
        val team = mockk<ScoresCarouselItem.Team> {
            every { score }.returns(null)
        }
        assertThat(team.displayScore).isNull()
    }

    @Test
    fun `Team displayScore returns only score if penalty score is null`() {
        val teamScore = 12
        val team = mockk<ScoresCarouselItem.Team> {
            every { score }.returns(teamScore)
            every { penaltyScore }.returns(null)
        }
        assertThat(team.displayScore).isEqualTo("$teamScore")
    }

    @Test
    fun `Team displayScore returns score with penalty score in parentheses if both provided`() {
        val teamScore = 13
        val teamPenaltyScore = 2
        val team = mockk<ScoresCarouselItem.Team> {
            every { score }.returns(teamScore)
            every { penaltyScore }.returns(teamPenaltyScore)
        }
        assertThat(team.displayScore).isEqualTo("$teamScore ($teamPenaltyScore)")
    }

    @Test
    fun `Team isTextDimmed returns false if team is null`() {
        val team: ScoresCarouselItem.Team? = null
        assertThat(team.isTextDimmed).isFalse()
    }

    @Test
    fun `Team isTextDimmed returns false if team did not lose`() {
        val team = mockk<ScoresCarouselItem.Team> {
            every { lost }.returns(false)
        }
        assertThat(team.isTextDimmed).isFalse()
    }

    @Test
    fun `Team isTextDimmed returns true if team lost`() {
        val team = mockk<ScoresCarouselItem.Team> {
            every { lost }.returns(true)
        }
        assertThat(team.isTextDimmed).isTrue()
    }
}