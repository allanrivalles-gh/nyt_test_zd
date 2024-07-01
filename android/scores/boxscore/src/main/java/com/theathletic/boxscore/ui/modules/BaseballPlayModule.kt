package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.playbyplay.IndentedBaseballPitchPlay
import com.theathletic.boxscore.ui.playbyplay.IndentedStandardPlay
import com.theathletic.boxscore.ui.playbyplay.TeamScores
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.feed.ui.FeedModule
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme

data class BaseballPlayModule(
    val id: String,
    val description: String,
    val awayTeamAlias: String? = null,
    val homeTeamAlias: String? = null,
    val awayTeamScore: String? = null,
    val homeTeamScore: String? = null,
    val showScores: Boolean = false,
    val isExpanded: Boolean = false,
    val subPlays: List<SubPlay>
) : FeedModule {

    interface SubPlay

    data class PitchPlay(
        val title: String,
        val description: String?,
        val pitchNumber: Int,
        val pitchOutcomeType: BaseballPitchOutcomeType,
        val occupiedBases: List<Int>,
        val hitZone: Int?,
        val pitchZone: Int?
    ) : SubPlay

    data class StandardSubPlay(
        val description: String
    ) : SubPlay

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        BaseballPlay(
            id = id,
            description = description,
            awayTeamAlias = awayTeamAlias,
            homeTeamAlias = homeTeamAlias,
            awayTeamScore = awayTeamScore,
            homeTeamScore = homeTeamScore,
            showScores = showScores,
            isExpanded = isExpanded,
            subPlays = subPlays,
            interactor = interactor
        )
    }

    interface Interaction {
        data class OnPlayExpandClick(
            val id: String
        ) : FeedInteraction
    }
}

enum class BaseballPitchOutcomeType {
    BALL,
    DEAD_BALL,
    HIT,
    STRIKE,
    UNKNOWN;

    val color: Color
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            BALL -> AthTheme.colors.green
            DEAD_BALL -> AthTheme.colors.dark500
            HIT -> AthTheme.colors.blue
            STRIKE -> AthTheme.colors.red
            UNKNOWN -> AthTheme.colors.dark100
        }
}

@Composable
private fun BaseballPlay(
    id: String,
    description: String,
    awayTeamAlias: String? = null,
    homeTeamAlias: String? = null,
    awayTeamScore: String? = null,
    homeTeamScore: String? = null,
    showScores: Boolean = false,
    isExpanded: Boolean,
    subPlays: List<BaseballPlayModule.SubPlay>,
    interactor: FeedInteractor
) {
    Column(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp
            )
    ) {
        TopLevelPlay(
            id = id,
            description = description,
            awayTeamAlias = awayTeamAlias,
            homeTeamAlias = homeTeamAlias,
            awayTeamScore = awayTeamScore,
            homeTeamScore = homeTeamScore,
            showScores = showScores,
            isExpanded = isExpanded,
            subPlays = subPlays,
            interactor = interactor
        )
        if (isExpanded && subPlays.isNotEmpty()) {
            subPlays.forEach { play ->
                when (play) {
                    is BaseballPlayModule.PitchPlay ->
                        IndentedBaseballPitchPlay(
                            title = play.title,
                            description = play.description,
                            pitchNumber = play.pitchNumber,
                            pitchOutcomeColor = play.pitchOutcomeType.color,
                            occupiedBases = play.occupiedBases,
                            hitZone = play.hitZone,
                            pitchZone = play.pitchZone
                        )
                    is BaseballPlayModule.StandardSubPlay ->
                        IndentedStandardPlay(
                            description = play.description,
                        )
                    else -> {}
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp
        )
    }
}

@Composable
private fun TopLevelPlay(
    id: String,
    description: String,
    awayTeamAlias: String? = null,
    homeTeamAlias: String? = null,
    awayTeamScore: String? = null,
    homeTeamScore: String? = null,
    showScores: Boolean = false,
    isExpanded: Boolean,
    subPlays: List<BaseballPlayModule.SubPlay>,
    interactor: FeedInteractor
) {
    val rowModifier = if (subPlays.isNotEmpty()) {
        Modifier.clickable {
            interactor.send(BaseballPlayModule.Interaction.OnPlayExpandClick(id))
        }
    } else {
        Modifier
    }
    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(24.dp)
        ) {
            if (subPlays.isNotEmpty()) {
                Icon(
                    if (isExpanded) {
                        Icons.Default.ExpandLess
                    } else {
                        Icons.Default.ExpandMore
                    },
                    contentDescription = null,
                    tint = AthTheme.colors.dark800,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Text(
            text = description,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(1f)
        )
        if (showScores) {
            TeamScores(
                firstTeamAlias = awayTeamAlias.orEmpty(),
                secondTeamAlias = homeTeamAlias.orEmpty(),
                firstTeamScore = awayTeamScore.orEmpty(),
                secondTeamScore = homeTeamScore.orEmpty(),
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .padding(start = 6.dp)
                    .align(Alignment.Top)
            )
        }
    }
}

@Preview
@Composable
fun BaseballPlayModulePreview() {
    BaseballPlayModule(
        id = "uniqueId",
        description = "D. Smith reached on infield single to first, Nimmo scored, McNeil to second.",
        awayTeamAlias = "NYM",
        homeTeamAlias = "PIT",
        awayTeamScore = "2",
        homeTeamScore = "6",
        isExpanded = false,
        showScores = true,
        subPlays = BaseballPlayModulePreviewData.subPlays
    ).Render()
}

@Preview
@Composable
fun BaseballPlayModulePreview_Expanded() {
    BaseballPlayModule(
        id = "uniqueId",
        description = "D. Smith reached on infield single to first, Nimmo scored, McNeil to second.",
        awayTeamAlias = "NYM",
        homeTeamAlias = "PIT",
        awayTeamScore = "2",
        homeTeamScore = "6",
        isExpanded = true,
        showScores = true,
        subPlays = BaseballPlayModulePreviewData.subPlays
    ).Render()
}

@Preview
@Composable
fun BaseballPlayModulePreview_DoesNotExpand() {
    BaseballPlayModule(
        id = "uniqueId",
        description = "D. Smith reached on infield single to first, Nimmo scored, McNeil to second.",
        awayTeamAlias = null,
        homeTeamAlias = null,
        awayTeamScore = null,
        homeTeamScore = null,
        isExpanded = false,
        showScores = false,
        subPlays = emptyList()
    ).Render()
}

@Preview
@Composable
fun BaseballPlayModulePreview_Light() {
    AthleticTheme(lightMode = true) {
        BaseballPlayModule(
            id = "uniqueId",
            description = "D. Smith reached on infield single to first, Nimmo scored, McNeil to second.",
            awayTeamAlias = null,
            homeTeamAlias = null,
            awayTeamScore = null,
            homeTeamScore = null,
            isExpanded = true,
            showScores = false,
            subPlays = BaseballPlayModulePreviewData.subPlays
        ).Render()
    }
}