package com.theathletic.scores.ui.gamecells

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameCellUiKtTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun `display team ranking when there is a ranking value`() {
        composeTestRule.setContent {
            GameCell(
                gameId = "gameId",
                title = GameCellPreviewData.inGameCellWithRanking.title,
                showTitle = GameCellPreviewData.inGameCellWithRanking.showTitle,
                firstTeam = GameCellPreviewData.inGameCellWithRanking.firstTeam,
                secondTeam = GameCellPreviewData.inGameCell.secondTeam,
                infoWidget = GameCellPreviewData.inGameCell.infoWidget,
                discussionLinkText = GameCellPreviewData.inGameCellWithRanking.discussionLinkText,
                showDivider = true,
                showTeamRanking = true,
                onGameClicked = {},
                onDiscussionLinkClicked = {},
            )
        }

        val teamRanking = composeTestRule.onNodeWithTag(
            testTag = GameCellTags.GameCellTeamRanking,
            useUnmergedTree = true
        )

        teamRanking.assertExists()
        teamRanking.assertIsDisplayed()
        composeTestRule.onRoot().printToLog(GameCellTags.GameCellRow)
    }

    @Test
    fun `do not display team ranking when there is no team ranking value`() {
        composeTestRule.setContent {
            GameCell(
                gameId = "gameId",
                title = GameCellPreviewData.ctaGameCell.title,
                showTitle = GameCellPreviewData.ctaGameCell.showTitle,
                firstTeam = GameCellPreviewData.ctaGameCell.firstTeam,
                secondTeam = GameCellPreviewData.ctaGameCell.secondTeam,
                infoWidget = GameCellPreviewData.ctaGameCell.infoWidget,
                discussionLinkText = GameCellPreviewData.ctaGameCell.discussionLinkText,
                showDivider = true,
                showTeamRanking = false,
                onGameClicked = {},
                onDiscussionLinkClicked = {},
            )
        }

        composeTestRule.onNodeWithTag(testTag = GameCellTags.GameCellTeamRanking, useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun `invoke onGameClicked function with gameId on a game row click`() {
        var gameId = ""
        composeTestRule.setContent {
            GameCell(
                gameId = "gameId",
                title = GameCellPreviewData.preGameCell.title,
                showTitle = GameCellPreviewData.preGameCell.showTitle,
                firstTeam = GameCellPreviewData.preGameCell.firstTeam,
                secondTeam = GameCellPreviewData.preGameCell.secondTeam,
                infoWidget = GameCellPreviewData.preGameCell.infoWidget,
                discussionLinkText = GameCellPreviewData.preGameCell.discussionLinkText,
                showDivider = true,
                showTeamRanking = false,
                onGameClicked = { gameId = it },
                onDiscussionLinkClicked = {},
            )
        }

        composeTestRule.onNodeWithTag(GameCellTags.GameCellRow).performClick()
        assertThat(gameId).isEqualTo("gameId")
        composeTestRule.onRoot().printToLog(GameCellTags.GameCellRow)
    }
}