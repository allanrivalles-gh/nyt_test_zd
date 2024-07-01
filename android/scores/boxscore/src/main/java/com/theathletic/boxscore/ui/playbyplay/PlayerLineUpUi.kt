package com.theathletic.boxscore.ui.playbyplay

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.boxscore.ui.BoxScoreFooterDivider
import com.theathletic.boxscore.ui.BoxScoreHeaderTitle
import com.theathletic.boxscore.ui.PlayerLineUpPreviewData
import com.theathletic.boxscore.ui.modules.PlayerLineUpModule
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.buttons.TwoItemToggleButton

@Suppress("LongMethod")
@Composable
fun PlayerLineUp(
    firstTeamLabel: ResourceString,
    secondTeamLabel: ResourceString,
    firstTeamFormationUrl: String?,
    secondTeamFormationUrl: String?,
    firstTeamLineup: Map<ResourceString, List<PlayerLineUpModule.PlayerLineUp>>,
    secondTeamLineup: Map<ResourceString, List<PlayerLineUpModule.PlayerLineUp>>,
    interactor: FeedInteractor
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        var isFirstTeamSelected by remember { mutableStateOf(true) }

        BoxScoreHeaderTitle(R.string.box_score_player_line_up_title_live_final)

        TwoItemToggleButton(
            itemOneLabel = firstTeamLabel,
            itemTwoLabel = secondTeamLabel,
            isFirstItemSelected = isFirstTeamSelected,
            onTwoItemToggleSelected = { isFirstTeamSelected = !isFirstTeamSelected },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        )

        ShowFormationImage(isFirstTeamSelected, firstTeamFormationUrl, secondTeamFormationUrl)

        val currentLineup = if (isFirstTeamSelected) {
            firstTeamLineup
        } else {
            secondTeamLineup
        }

        if (currentLineup.isEmpty()) {
            EmptyLineup()
        } else {
            currentLineup.entries.forEach {
                Text(
                    text = it.key.asString(),
                    style = AthTextStyle.Calibre.Utility.Medium.Large,
                    color = AthTheme.colors.dark800,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 14.dp)
                )

                it.value.forEachIndexed { index, player ->

                    if (player is PlayerLineUpModule.PlayerLineUp.Player) {
                        PlayerRow(
                            id = player.id,
                            jerseyNumber = player.jerseyNumber,
                            playerName = player.name,
                            position = player.position,
                            isPreGame = player.isPreGame,
                            showExpandIcon = player.showExpandIcon,
                            eventIcons = player.eventIcons,
                            playerStats = player.playerStats,
                            isExpanded = player.isExpanded,
                            substitution = player.substitution,
                            substitutionTime = player.substitutionTime,
                            modifier = Modifier.clickable {
                                interactor.send(PlayerLineUpModule.Interaction.OnLineUpExpandClick(playerId = player.id))
                            }
                        )
                    } else if (player is PlayerLineUpModule.PlayerLineUp.Manager) {
                        ManagerRow(name = player.name)
                    }
                }
            }
        }

        BoxScoreFooterDivider(false)
    }
}

@Composable
private fun EmptyLineup() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(238.dp)
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .background(color = AthTheme.colors.dark300),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.box_score_player_line_no_lineup),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
            color = AthTheme.colors.dark800,
            modifier = Modifier.padding(top = 34.dp, bottom = 8.dp)
        )

        Text(
            text = stringResource(id = R.string.box_score_player_line_no_lineup_message),
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            color = AthTheme.colors.dark800,
        )
    }
}

@Composable
private fun ShowFormationImage(
    isFirstTeamSelected: Boolean,
    firstTeamFormationUrl: String?,
    secondTeamFormationUrl: String?
) {
    if (firstTeamFormationUrl == null && secondTeamFormationUrl == null) return
    val formationImageUrl = if (isFirstTeamSelected) firstTeamFormationUrl else secondTeamFormationUrl

    if (formationImageUrl == null) {
        Box {
            ResourceIcon(
                resourceId = R.drawable.soccer_field_placeholder,
                modifier = Modifier
                    .aspectRatio(1.45f)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            )
            Text(
                text = stringResource(id = R.string.box_score_player_line_no_formation_image),
                style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
                color = AthColor.Gray800,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        RemoteImageAsync(
            url = formationImageUrl,
            modifier = Modifier
                .aspectRatio(1.45f)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            alignment = Alignment.CenterStart,
            contentScale = ContentScale.FillBounds,
            placeholder = R.drawable.soccer_field_placeholder,
            error = R.drawable.soccer_field_placeholder,
        )
    }
}

@Composable
private fun ManagerRow(
    name: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = name,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                color = AthTheme.colors.dark800,
                modifier = Modifier.padding(vertical = 14.dp)
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = AthTheme.colors.dark300
        )
    }
}

@Composable
private fun PlayerRow(
    id: String,
    jerseyNumber: String,
    playerName: String,
    position: String,
    isPreGame: Boolean,
    isExpanded: Boolean,
    showExpandIcon: Boolean,
    eventIcons: List<PlayerLineUpModule.PlayerLineUp.EventIconType>,
    playerStats: List<PlayerLineUpModule.PlayerLineUp.Stats>,
    substitution: PlayerLineUpModule.PlayerLineUp.PlayerSubstitution,
    substitutionTime: String,
    modifier: Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(0.9f)
            ) {
                PlayerDetails(jerseyNumber, playerName, position)

                //  player icons
                RenderPlayerIcons(eventIcons = eventIcons)

                SubstitutionIcon(substitution, substitutionTime)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(0.1f),
                horizontalArrangement = Arrangement.End
            ) {
                if (showExpandIcon) {
                    Icon(
                        if (isExpanded) {
                            Icons.Default.ExpandLess
                        } else {
                            Icons.Default.ExpandMore
                        },
                        contentDescription = null,
                        tint = AthTheme.colors.dark800,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )
                }
            }
        }

        if (isExpanded) {
            playerStats.forEachIndexed { index, stats ->
                PlayerStatRow(label = stats.label, value = stats.value)
            }
        } else {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = AthTheme.colors.dark300
            )
        }
    }
}

@Composable
private fun SubstitutionIcon(
    substitution: PlayerLineUpModule.PlayerLineUp.PlayerSubstitution,
    substitutionTime: String
) {
    if (substitution != PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.NONE) {
        val (iconResId, size) = getSubstitutionIcon(substitution)
        ResourceIcon(
            resourceId = iconResId,
            modifier = Modifier
                .size(size)
                .padding(start = 6.dp)
        )

        Text(
            text = substitutionTime,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = AthTheme.colors.dark600,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
private fun PlayerDetails(jerseyNumber: String, playerName: String, position: String) {
    Text(
        text = jerseyNumber,
        style = AthTextStyle.Calibre.Utility.Regular.Large,
        color = AthTheme.colors.dark500,
        modifier = Modifier
            .width(30.dp)
    )

    Text(
        text = playerName,
        style = AthTextStyle.Calibre.Utility.Regular.Large,
        color = AthTheme.colors.dark800,
        modifier = Modifier.padding(vertical = 14.dp)
    )

    Text(
        text = position,
        style = AthTextStyle.Calibre.Utility.Regular.Large,
        color = AthTheme.colors.dark500,
        modifier = Modifier.padding(start = 4.dp)
    )
}

fun getSubstitutionIcon(substitution: PlayerLineUpModule.PlayerLineUp.PlayerSubstitution): Pair<Int, Dp> {
    return when (substitution) {
        PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.IN -> Pair(R.drawable.ic_substitution_on, 18.dp)
        PlayerLineUpModule.PlayerLineUp.PlayerSubstitution.OUT -> Pair(R.drawable.ic_substitution_off, 18.dp)
        else -> Pair(R.drawable.ic_substitution_new, 24.dp)
    }
}

@Composable
private fun RowScope.RenderPlayerIcons(eventIcons: List<PlayerLineUpModule.PlayerLineUp.EventIconType>) {
    Row(
        modifier = Modifier
            .padding(start = 4.dp)
            .weight(0.5f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        eventIcons.forEach { event ->
            if (event is PlayerLineUpModule.PlayerLineUp.BubbleIcon) {
                GoalBubbleIcon(
                    resId = event.icon,
                    value = StringWithParams(R.string.box_score_player_line_up_bubble_text, event.count),
                )
            } else if (event is PlayerLineUpModule.PlayerLineUp.SingleIcon) {
                PlayerIcon(
                    resId = event.icon,
                )
            }
        }
    }
}

@Composable
private fun PlayerIcon(@DrawableRes resId: Int) {
    ResourceIcon(
        resourceId = resId,
        modifier = Modifier
            .size(18.dp)
            .padding(horizontal = 2.dp)
    )
}

@Composable
private fun GoalBubbleIcon(
    @DrawableRes resId: Int,
    value: ResourceString,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.size(20.dp),
        contentAlignment = Alignment.Center
    ) {
        ResourceIcon(
            resourceId = resId,
            modifier = Modifier
                .size(18.dp)
        )
        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .clip(shape = RoundedCornerShape(6.dp))
                .background(color = AthTheme.colors.dark700)
                .align(Alignment.TopEnd)
                .then(modifier)
        ) {
            Text(
                text = value.asString(),
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall.copy(fontSize = 8.sp),
                color = AthTheme.colors.dark100,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
            )
        }
    }
}

@Composable
private fun PlayerStatRow(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark300)
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 46.dp,
                    end = 16.dp
                )
                .padding(vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = label,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                color = AthTheme.colors.dark500,
                modifier = Modifier.weight(0.8f),
            )

            Text(
                text = value,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark800,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.2f),
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 16.dp),
            color = AthTheme.colors.dark300
        )
    }
}

@Preview
@Composable
private fun PlayerLineUp_Preview() {
    PlayerLineUpPreviewData.playerLineUpMock.Render()
}

@Preview(device = Devices.PIXEL)
@Composable
private fun PlayerLineUp_PreviewSmallDevice() {
    PlayerLineUpPreviewData.playerLineUpMock.Render()
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun PlayerLineUp_PreviewLargeDevice() {
    PlayerLineUpPreviewData.playerLineUpMock.Render()
}

@Preview
@Composable
private fun PlayerLineUp_PreviewLight() {
    AthleticTheme(lightMode = true) {
        PlayerLineUpPreviewData.playerLineUpMock.Render()
    }
}