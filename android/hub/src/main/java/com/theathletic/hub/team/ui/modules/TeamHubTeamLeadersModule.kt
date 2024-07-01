package com.theathletic.hub.team.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.StatTitle
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.Headshot

data class TeamHubTeamLeadersModule(
    val id: String,
    val leaderGroups: List<Group>
) : FeedModuleV2 {
    override val moduleId: String = "TeamHubTeamLeadersModule:$id"

    data class Group(
        val label: String,
        val players: List<Player>
    )

    data class Player(
        val name: String,
        val position: String,
        val headShots: SizedImages,
        val teamColor: Color,
        val teamLogos: SizedImages,
        val stats: List<PlayerStatistic>,
        val showDivider: Boolean
    )

    data class PlayerStatistic(
        val label: String,
        val value: String
    )

    @Composable
    override fun Render() {
        TeamLeaders(
            leaderGroups = leaderGroups
        )
    }
}

@Composable
private fun TeamLeaders(leaderGroups: List<TeamHubTeamLeadersModule.Group>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {
        TeamStatsTableHeading(R.string.box_score_team_leaders_title)
        leaderGroups.forEach { group -> TeamLeaderGroup(group = group) }
    }
}

@Composable
fun TeamStatsTableHeading(headingResId: Int) {
    Text(
        text = stringResource(headingResId),
        color = AthTheme.colors.dark700,
        style = AthTextStyle.Slab.Bold.Small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 7.dp, bottom = 10.dp),
    )
}

@Composable
private fun TeamLeaderGroup(group: TeamHubTeamLeadersModule.Group) {
    Column() {
        StatTitle(title = group.label.uppercase())
        group.players.forEach { player -> TeamLeaderPlayer(player) }
    }
}

@Composable
private fun TeamLeaderPlayer(player: TeamHubTeamLeadersModule.Player) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .padding(horizontal = 16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Headshot(
                headshotsUrls = player.headShots,
                teamUrls = player.teamLogos,
                teamColor = player.teamColor,
                preferredSize = 40.dp,
                modifier = Modifier
                    .size(40.dp, 40.dp)
            )
            Text(
                text = player.name,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark700,
                modifier = Modifier.padding(start = 8.dp),
            )

            Text(
                text = player.position,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500,
                modifier = Modifier.padding(start = 8.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                player.stats.forEach {
                    PlayerStatsColumn(
                        label = it.label,
                        stat = it.value
                    )
                }
            }
        }
        if (player.showDivider) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                color = AthTheme.colors.dark300
            )
        }
    }
}

@Composable
private fun PlayerStatsColumn(label: String, stat: String) {
    Column(
        modifier = Modifier
            .width(140.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stat,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            textAlign = TextAlign.Center,
            color = AthTheme.colors.dark700,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 2.dp)
        )

        Text(
            text = label,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .padding(top = 4.dp)
        )
    }
}

@Preview
@Composable
private fun TeamHubTeamLeadersModulePreview() {
    TeamHubTeamLeadersModule(
        id = "uniqueId",
        leaderGroups = TeamHubStatsPreviewData.createLeaderGroups()
    ).Render()
}