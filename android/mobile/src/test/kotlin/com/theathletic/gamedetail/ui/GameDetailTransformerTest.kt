package com.theathletic.gamedetail.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.theathletic.entity.main.Sport
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.gamedetail.data.local.CoverageDataType
import com.theathletic.gamedetail.data.local.GameSummaryLocalModel
import com.theathletic.gamedetail.data.local.gameSummaryLocalModelFixture
import com.theathletic.gamedetail.playergrades.ui.SportsWithPlayerGrades
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.ui.asString
import com.theathletic.user.IUserManager
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GameDetailTransformerTest : TestCase() {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val gameSummaryTeamRenderer = GameSummaryTeamRenderer(SupportedLeagues())

    private val gameSummaryGameStatusRenderer = mockk<GameSummaryGameStatusRenderer>(relaxed = true)
    private val gameSummaryGameInfoRenderer = mockk<GameSummaryGameInfoRenderer>(relaxed = true)
    private val sportsWithPlayerGrades = mockk<SportsWithPlayerGrades>()
    private val featureSwitches = mockk<FeatureSwitches>()
    private val userManager = mockk<IUserManager>()

    private lateinit var transformer: GameDetailTransformer

    @Before
    fun setup() {
        every { featureSwitches.isFeatureEnabled(FeatureSwitch.BOX_SCORE_DISCUSS_TAB_ENABLED) } returns true
        every { featureSwitches.isFeatureEnabled(FeatureSwitch.TEAM_SPECIFIC_COMMENTS) } returns true
        every { userManager.isUserLoggedIn() } returns true
        every { userManager.isUserSubscribed() } returns true

        transformer = GameDetailTransformer(
            gameSummaryTeamRenderer,
            gameSummaryGameStatusRenderer,
            gameSummaryGameInfoRenderer,
            sportsWithPlayerGrades,
            featureSwitches,
            userManager
        )
    }

    @Test
    fun `should build first team at second team string for content-descriptor title when game title is null`() {
        every { sportsWithPlayerGrades.isSupported(Sport.BASEBALL) } returns true

        val transformed = transformer.transform(
            buildGameDetailComposeState(gameTitle = null, firstTeamAlias = "LAA", secondTeamAlias = "TB")
        )

        val discussTabModule: DiscussTabModule = transformed.tabModules.first { module -> module is DiscussTabModule } as DiscussTabModule
        assertNotNull(discussTabModule)
        assertEquals("LAA @ TB", discussTabModule.title?.asString(context, ""))
    }

    @Test
    fun `should use game title for content-descriptor title when game title is not null`() {
        every { sportsWithPlayerGrades.isSupported(Sport.BASEBALL) } returns true

        val transformed = transformer.transform(
            buildGameDetailComposeState(gameTitle = "World Series - Game 2", firstTeamAlias = "AZ", secondTeamAlias = "TEX")
        )

        val discussTabModule: DiscussTabModule = transformed.tabModules.first { module -> module is DiscussTabModule } as DiscussTabModule
        assertNotNull(discussTabModule)
        assertEquals("World Series - Game 2", discussTabModule.title?.asString(context, ""))
    }

    private fun buildGameDetailComposeState(
        gameTitle: String?,
        firstTeamAlias: String,
        secondTeamAlias: String
    ): GameDetailComposeState {
        val firstTeam = mockBaseballGameSummaryTeam(firstTeamAlias)
        val secondTeam = mockBaseballGameSummaryTeam(secondTeamAlias)

        return GameDetailComposeState(
            gameSummary = gameSummaryLocalModelFixture(firstTeam, secondTeam).copy(
                gameTitle = gameTitle,
                sport = Sport.BASEBALL,
                coverage = listOf(CoverageDataType.COMMENTS_NAVIGATION)
            )
        )
    }
    private fun mockBaseballGameSummaryTeam(alias: String): GameSummaryLocalModel.BaseballGameSummaryTeam {
        val team = mockk<GameSummaryLocalModel.BaseballGameSummaryTeam>(relaxed = true)
        every { team.alias } returns alias
        return team
    }
}