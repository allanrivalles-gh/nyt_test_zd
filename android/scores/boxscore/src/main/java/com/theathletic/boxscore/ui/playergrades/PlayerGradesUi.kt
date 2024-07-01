package com.theathletic.boxscore.ui.playergrades

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.BoxScoreHeaderTitle
import com.theathletic.boxscore.ui.modules.PlayerGradeModule
import com.theathletic.boxscore.ui.playergrades.PlayerGradePreviewData.playerGradeLockedGradedModule
import com.theathletic.boxscore.ui.playergrades.PlayerGradePreviewData.playerGradeLockedUngradedModule
import com.theathletic.boxscore.ui.playergrades.PlayerGradePreviewData.playerGradeModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.widgets.buttons.TwoItemToggleButton
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

sealed class BoxScorePlayerGrades {
    data class PlayerGrades(
        val teams: Teams,
        val isLocked: Boolean,
        val firstTeamPlayerGrades: List<FeedModuleV2>,
        val secondTeamPlayerGrades: List<FeedModuleV2>,
    )

    data class Teams(
        val firstTeamName: String,
        val secondTeamName: String,
    )
}

@Composable
fun PlayerGrades(
    firstTeamName: String,
    secondTeamName: String,
    firstTeamPlayerGrades: List<FeedModuleV2>,
    secondTeamPlayerGrades: List<FeedModuleV2>,
    isLocked: Boolean,
    showFirstTeam: Boolean
) {
    var isFirstTeamSelected by remember { mutableStateOf(showFirstTeam) }
    val interactor = LocalFeedInteractor.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {
        BoxScoreHeaderTitle(R.string.box_score_player_grade_title)
        TwoItemToggleButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            itemOneLabel = StringWrapper(firstTeamName),
            itemTwoLabel = StringWrapper(secondTeamName),
            isFirstItemSelected = isFirstTeamSelected,
            onTwoItemToggleSelected = { itemOneSelected ->
                if (itemOneSelected != isFirstTeamSelected) {
                    isFirstTeamSelected = !isFirstTeamSelected
                    interactor.send(PlayerGradeModule.Interaction.OnTeamToggled(isFirstTeamSelected))
                }
            }
        )
        val playerGrades = if (isFirstTeamSelected) {
            firstTeamPlayerGrades
        } else {
            secondTeamPlayerGrades
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (isLocked) {
                LockedPlayerGrades(playerGrades)
            } else {
                UnlockedPlayerGrades(playerGrades, isFirstTeamSelected)
            }
        }
        PlayerGradesFooter(isLocked, isFirstTeamSelected)
    }
}

@Composable
fun LockedPlayerGrades(playerGrades: List<FeedModuleV2>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
    ) {
        playerGrades.forEachIndexed { index, player ->
            player.Render()
        }
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun UnlockedPlayerGrades(players: List<FeedModuleV2>, isFirstTeamSelected: Boolean) {
    val firstTeamState = rememberLazyListState()
    val secondTeamState = rememberLazyListState()
    val state = if (isFirstTeamSelected) firstTeamState else secondTeamState

    LazyRow(
        state = state,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Absolute.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        flingBehavior = rememberSnapperFlingBehavior(lazyListState = state)
    ) {
        itemsIndexed(players) { index, item ->
            item.Render()
        }
    }
}

@Composable
internal fun PlayerGradesFooter(
    isLocked: Boolean,
    isFirstTeamSelected: Boolean
) {
    val interactor = LocalFeedInteractor.current
    if (!isLocked) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = AthTheme.colors.dark300
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .clickable {
                interactor.send(PlayerGradeModule.Interaction.OnPlayerGradesClick(isLocked))
            }
            .padding(vertical = 16.dp),

        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val title = if (isLocked) {
            R.string.player_grade_footer_title_view_player_grades
        } else {
            R.string.player_grade_footer_title_grade_players
        }
        Text(
            text = stringResource(id = title),
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = AthTheme.colors.dark500,
        )
    }
}

@Preview
@Composable
private fun PlayerGrades_Preview() {
    playerGradeModule.Render()
}

@Preview
@Composable
private fun PlayerGrades_Lockedgraded_Preview() {
    playerGradeLockedGradedModule.Render()
}

@Preview
@Composable
private fun PlayerGrades_LockedUngraded_Preview() {
    playerGradeLockedUngradedModule.Render()
}

@Preview(device = Devices.PIXEL)
@Composable
private fun PlayerGrades__SmallDevice_Preview() {
    playerGradeModule.Render()
}

@Preview
@Composable
private fun PlayerGrades_PreviewLight() {
    AthleticTheme(lightMode = true) {
        playerGradeModule.Render()
    }
}