package com.theathletic.feed.compose.ui.items.featuredgame

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.theathletic.entity.main.Sport
import com.theathletic.extension.toStringOrShortDash
import com.theathletic.feed.R
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.components.TopCommentUiModel
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.preview.DevicePreviewSmallAndLarge
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.SimpleShrinkToFitText
import com.theathletic.ui.widgets.TeamCurtain

private val teamCurtainWidth = 56.dp

@Composable
internal fun FeaturedGameItem(
    model: FeaturedGameUiModel,
    itemInteractor: ItemInteractor
) {
    val curtainLength = if (model.navLinks.isEmpty()) 100.dp else 124.dp
    Column(
        modifier = Modifier
            .defaultMinSize(minHeight = curtainLength)
            .background(AthTheme.colors.dark200)
            .interactive(model, itemInteractor)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
            ) {
                model.firstTeam.colors?.let { teamColor ->
                    TeamCurtain(
                        teamColor = teamColor,
                        height = curtainLength,
                        width = teamCurtainWidth,
                        orientation = TeamCurtain.Orientation.LEFT,
                        modifier = Modifier.weight(1f)
                    )
                }
                model.secondTeam.colors?.let { teamColor ->
                    TeamCurtain(
                        teamColor = teamColor,
                        height = curtainLength,
                        width = teamCurtainWidth,
                        orientation = TeamCurtain.Orientation.RIGHT,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Column {
                GameHeader(model = model)
                if (model.navLinks.isNotEmpty()) {
                    GameNavigationLinks(
                        model.navLinks,
                        onLinkClick = { link, linkType -> itemInteractor.onNavLinkClick(model, link, linkType) }
                    )
                }
                Spacer(
                    modifier = Modifier.height(
                        if (model.relatedContent == null) 12.dp else 10.dp
                    )
                )
            }
        }
        RelatedContent(model, itemInteractor)
    }
}

@Composable
private fun RelatedContent(uiModel: FeaturedGameUiModel, itemInteractor: ItemInteractor) {
    when (val model = uiModel.relatedContent) {
        is ArticleUiModel -> RelatedGameArticleItem(
            uiModel = model,
            itemInteractor = itemInteractor
        )
        is TopCommentUiModel -> RelatedGameTopCommentItem(
            uiModel = model,
            itemInteractor = itemInteractor
        )
        else -> { /* Not supported, render nothing */ }
    }
}

@Composable
private fun GameHeader(model: FeaturedGameUiModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (model.title.isEmpty().not()) {
            Text(
                text = model.title.uppercase(),
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                color = AthTheme.colors.dark500,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        when (model.gameStatus.state) {
            FeaturedGameUiModel.GameState.PREGAME ->
                PregameHeader(
                    firstTeam = model.firstTeam,
                    secondTeam = model.secondTeam,
                    gameStatus = model.gameStatus
                )
            FeaturedGameUiModel.GameState.LIVE_GAME ->
                LiveGameHeader(
                    firstTeam = model.firstTeam,
                    secondTeam = model.secondTeam,
                    gameStatus = model.gameStatus,
                    sport = model.sport
                )
            FeaturedGameUiModel.GameState.POSTGAME ->
                PostGameStatus(
                    firstTeam = model.firstTeam,
                    secondTeam = model.secondTeam,
                    gameStatus = model.gameStatus
                )
        }
    }
}

@Composable
private fun PregameHeader(
    firstTeam: FeaturedGameUiModel.Team,
    secondTeam: FeaturedGameUiModel.Team,
    gameStatus: FeaturedGameUiModel.GameStatus
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        PreGameTeam(
            team = firstTeam,
            isFirstTeam = true,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        GameStatus(
            majorLabel = gameStatus.gameTime.orEmpty(),
            topMinorLabel = gameStatus.gameDate,
            modifier = Modifier.align(Alignment.Center),
            aggregateLabel = gameStatus.aggregate?.asString()
        )
        PreGameTeam(
            team = secondTeam,
            isFirstTeam = false,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun LiveGameHeader(
    firstTeam: FeaturedGameUiModel.Team,
    secondTeam: FeaturedGameUiModel.Team,
    gameStatus: FeaturedGameUiModel.GameStatus,
    sport: Sport
) {
    var scoreFontSize by remember { mutableStateOf(AthTextStyle.Calibre.Headline.Regular.ExtraLarge.fontSize) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LiveGameTeam(
            team = firstTeam,
            isFirstTeam = true,
            sport = sport
        )
        Score(
            score = firstTeam.score.toStringOrShortDash(),
            fontSize = scoreFontSize,
            onFontSizeChanged = { newFontSize -> scoreFontSize = newFontSize },
            modifier = Modifier.weight(0.3f)
        )
        LiveGameHeader(
            gameStatus,
            sport,
            modifier = Modifier.weight(0.3f)
        )
        Score(
            score = secondTeam.score.toStringOrShortDash(),
            fontSize = scoreFontSize,
            onFontSizeChanged = { newFontSize -> scoreFontSize = newFontSize },
            modifier = Modifier.weight(0.3f)
        )
        LiveGameTeam(
            team = secondTeam,
            isFirstTeam = false,
            sport = sport
        )
    }
}

@Composable
private fun PostGameStatus(
    firstTeam: FeaturedGameUiModel.Team,
    secondTeam: FeaturedGameUiModel.Team,
    gameStatus: FeaturedGameUiModel.GameStatus,
) {
    var scoreFontSize by remember { mutableStateOf(AthTextStyle.Calibre.Headline.Regular.ExtraLarge.fontSize) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PostGameTeam(team = firstTeam)
        Score(
            score = firstTeam.score.toStringOrShortDash(),
            fontSize = scoreFontSize,
            onFontSizeChanged = { newFontSize -> scoreFontSize = newFontSize },
            modifier = Modifier.weight(0.3f)
        )
        GameStatus(
            majorLabel = gameStatus.period?.uppercase(),
            bottomMinorLabel = gameStatus.gameDate,
            aggregateLabel = gameStatus.aggregate?.asString(),
            modifier = Modifier.weight(0.3f),
        )
        Score(
            score = secondTeam.score.toStringOrShortDash(),
            fontSize = scoreFontSize,
            onFontSizeChanged = { newFontSize -> scoreFontSize = newFontSize },
            modifier = Modifier.weight(0.3f)
        )
        PostGameTeam(team = secondTeam)
    }
}

@Composable
private fun PreGameTeam(
    team: FeaturedGameUiModel.Team,
    isFirstTeam: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isFirstTeam) GameTeamLogo(team = team, hideTeamAlias = true)

        if (team.winLossRecord != null) {
            TeamDetailsWithWinLoss(team, isFirstTeam)
        } else {
            TeamDetails(team)
        }

        if (isFirstTeam.not()) GameTeamLogo(team = team, hideTeamAlias = true)
    }
}

@Composable
private fun TeamDetails(team: FeaturedGameUiModel.Team) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TeamAlias(team.alias)
        team.currentRecord?.let {
            Text(
                text = it,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                color = AthTheme.colors.dark500
            )
        }
    }
}

@Composable
private fun TeamDetailsWithWinLoss(
    team: FeaturedGameUiModel.Team,
    isFirstTeam: Boolean
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        horizontalAlignment = if (isFirstTeam) Alignment.Start else Alignment.End
    ) {
        TeamAlias(team.alias)
        team.winLossRecord?.let { record -> WinLoseRecord(record, team.isWinLossReversed) }
    }
}

@Composable
private fun TeamAlias(alias: String) {
    Text(
        text = alias,
        style = AthTextStyle.Calibre.Headline.Medium.Small,
        color = AthTheme.colors.dark700
    )
}

@Composable
private fun LiveGameTeam(
    team: FeaturedGameUiModel.Team,
    isFirstTeam: Boolean,
    sport: Sport,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isFirstTeam) GameTeamLogo(team = team)

        LiveGameTeamIndicator(team, sport)

        if (isFirstTeam.not()) GameTeamLogo(team = team)
    }
}

@Composable
private fun PostGameTeam(team: FeaturedGameUiModel.Team) {
    GameTeamLogo(team = team)
}

private val teamLogoSize = 48.dp
private val teamLogoSizeSmall = 32.dp

@Composable
private fun GameTeamLogo(
    team: FeaturedGameUiModel.Team,
    hideTeamAlias: Boolean = false
) {
    Box {
        if (team.logoUrl == null) {
            Column {
                ResourceIcon(
                    resourceId = R.drawable.ic_team_logo_placeholder,
                    modifier = Modifier
                        .size(if (hideTeamAlias) teamLogoSize else teamLogoSizeSmall)
                )
                if (hideTeamAlias.not()) {
                    Text(
                        text = team.alias,
                        style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
                        color = AthTheme.colors.dark700
                    )
                }
            }
        } else {
            RemoteImageAsync(
                url = team.logoUrl,
                modifier = Modifier
                    .size(teamLogoSize)
                    .padding(end = 4.dp),
                error = com.theathletic.ui.R.drawable.ic_team_logo_placeholder,
            )
        }
    }
}

@Composable
private fun LiveGameTeamIndicator(
    team: FeaturedGameUiModel.Team,
    sport: Sport
) {
    Box(modifier = Modifier.padding(horizontal = 4.dp)) {
        when (sport) {
            Sport.FOOTBALL -> ResourceIcon(
                modifier = Modifier
                    .size(12.dp)
                    .alpha(if (team.hasPossession) 1f else 0f),
                resourceId = R.drawable.americanfootball_possession,
                tint = AthTheme.colors.dark700
            )
            else -> {}
        }
    }
}

@Composable
private fun Score(
    score: String,
    fontSize: TextUnit,
    onFontSizeChanged: (newFontSize: TextUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    SimpleShrinkToFitText(
        text = score,
        style = AthTextStyle.Calibre.Headline.Regular.ExtraLarge.copy(
            color = AthTheme.colors.dark700,
            fontSize = fontSize
        ),
        maxLines = 1,
        textAlign = TextAlign.Center,
        onFontSizeChanged = onFontSizeChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .then(modifier)
    )
}

@Composable
private fun LiveGameHeader(
    status: FeaturedGameUiModel.GameStatus,
    sport: Sport,
    modifier: Modifier = Modifier
) {
    when (sport) {
        Sport.FOOTBALL -> {
            GameStatus(
                topMinorLabel = status.clock,
                majorLabel = status.period,
                modifier = modifier
            )
        }
        Sport.BASKETBALL -> {
            GameStatus(
                topMinorLabel = status.period,
                majorLabel = status.clock,
                modifier = modifier
            )
        }
        Sport.HOCKEY -> {
            GameStatus(
                topMinorLabel = status.period,
                majorLabel = status.clock,
                modifier = modifier
            )
        }
        Sport.SOCCER -> {
            GameStatus(
                majorLabel = status.clock,
                aggregateLabel = status.aggregate?.asString(),
                modifier = modifier
            )
        }
        else -> {}
    }
}

@Composable
private fun GameStatus(
    modifier: Modifier = Modifier,
    majorLabel: String? = null,
    topMinorLabel: String? = null,
    bottomMinorLabel: String? = null,
    aggregateLabel: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        topMinorLabel?.let { label ->
            Text(
                text = label,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark700
            )
        }
        majorLabel?.let { label ->
            Text(
                text = label,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark700
            )
        }
        bottomMinorLabel?.let { label ->
            Text(
                text = label,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark700
            )
        }
        aggregateLabel?.let { label ->
            Text(
                text = label,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark500
            )
        }
    }
}

@Composable
private fun WinLoseRecord(record: String, isReversed: Boolean) {
    var colors = listOf(
        AthTheme.colors.dark700,
        AthTheme.colors.dark600,
        AthTheme.colors.dark500,
        AthTheme.colors.dark500,
        AthTheme.colors.dark400,
    )
    if (isReversed) colors = colors.reversed()
    val lastFiveGames = if (isReversed) record.take(5).reversed() else record.take(5)

    Row {
        lastFiveGames.forEachIndexed { index, winLoss ->
            Text(
                text = winLoss.toString(),
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = colors[index]
            )
        }
    }
}

@Composable
private fun GameNavigationLinks(
    links: List<FeaturedGameUiModel.NavLink>,
    onLinkClick: (String, String) -> Unit
) {
    // Space links evenly. Row SpaceEvenly still didn't align properly
    val configuration = LocalConfiguration.current
    val linkWidth = when (links.size) {
        2 -> 130.dp // when 2 links they are spaced closer together
        else -> (configuration.screenWidthDp / links.size).dp
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        links.forEach { link ->
            Box(modifier = Modifier.width(linkWidth)) {
                Row(
                    modifier = Modifier
                        .clickable { onLinkClick(link.appLink, link.label.lowercase()) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = link.label,
                        style = AthTextStyle.Calibre.Utility.Medium.Large,
                        color = AthTheme.colors.dark700,
                        textAlign = TextAlign.Center,
                    )
                    ResourceIcon(
                        resourceId = R.drawable.ic_chalk_chevron_right,
                        tint = AthTheme.colors.dark500,
                        modifier = Modifier
                            .height(8.dp)
                            .padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@DayNightPreview
@DevicePreviewSmallAndLarge
@Composable
private fun FeaturedGameLayoutPreview(
    @PreviewParameter(FeaturedGameLayoutPreviewProvider::class) featuredGame: FeaturedGameUiModel
) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        FeaturedGameItem(
            model = featuredGame,
            itemInteractor = ItemInteractor()
        )
    }
}

internal class FeaturedGameLayoutPreviewProvider : PreviewParameterProvider<FeaturedGameUiModel> {
    override val values: Sequence<FeaturedGameUiModel> = sequenceOf(
        preGameNFLPreviewData(),
        preGameEPLPreviewData(),
        liveGameNFLPreviewData(),
        liveGameEPLPreviewData(),
        postGameNFLPreviewData(),
        postGameEPLPreviewData(),
    )

    private fun preGameNFLPreviewData(): FeaturedGameUiModel = FeaturedGameUiModel(
        id = "gameNFL",
        title = "NFL Week 8",
        firstTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "BAL",
            colors = "6A14D6",
            logoUrl = null,
            currentRecord = "(2-5)",
            winLossRecord = null
        ),
        secondTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "NYJ",
            colors = "0E823C",
            logoUrl = null,
            currentRecord = "(3-4)",
            winLossRecord = null
        ),
        gameStatus = FeaturedGameUiModel.GameStatus(
            state = FeaturedGameUiModel.GameState.PREGAME,
            gameDate = "Sun, Nov 9",
            gameTime = "8:10 PM",
        ),
        navLinks = listOf(
            FeaturedGameUiModel.NavLink("Game", "appGameLink"),
            FeaturedGameUiModel.NavLink("Discuss", "appDiscussLink"),
            FeaturedGameUiModel.NavLink("Grade", "appGradeLink"),
        ),
        sport = Sport.FOOTBALL,
        relatedContent = relatedArticlePreviewData(),
        analyticsData = analyticsPreviewData()
    )

    private fun preGameEPLPreviewData(): FeaturedGameUiModel = FeaturedGameUiModel(
        id = "gameEPL",
        title = "Premier League",
        firstTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "WHU",
            colors = "C4576B",
            logoUrl = null,
            currentRecord = null,
            winLossRecord = "WWLLLD",
            isWinLossReversed = false
        ),
        secondTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "CHE",
            colors = "1E6EDD",
            logoUrl = null,
            currentRecord = null,
            winLossRecord = "LLWWLD",
            isWinLossReversed = true
        ),
        gameStatus = FeaturedGameUiModel.GameStatus(
            state = FeaturedGameUiModel.GameState.PREGAME,
            gameDate = "Sun, Nov 9",
            gameTime = "8:10 PM",
            aggregate = "Agg. 2-1".asResourceString()
        ),
        navLinks = listOf(
            FeaturedGameUiModel.NavLink("Match", "appGameLink"),
            FeaturedGameUiModel.NavLink("Discuss", "appDiscussLink"),
            FeaturedGameUiModel.NavLink("Live Blog", "appLiveBlogLink"),
        ),
        sport = Sport.SOCCER,
        relatedContent = null,
        analyticsData = analyticsPreviewData()
    )

    private fun liveGameNFLPreviewData(): FeaturedGameUiModel = FeaturedGameUiModel(
        id = "gameNFL",
        title = "NFL Week 8",
        firstTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "BAL",
            colors = "6A14D6",
            logoUrl = null,
            score = "38",
            hasPossession = true
        ),
        secondTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "NYJ",
            colors = "0E823C",
            logoUrl = null,
            score = "199",
            hasPossession = false
        ),
        gameStatus = FeaturedGameUiModel.GameStatus(
            state = FeaturedGameUiModel.GameState.LIVE_GAME,
            clock = "14:55",
            period = "3RD"
        ),
        navLinks = listOf(
            FeaturedGameUiModel.NavLink("Game", "appGameLink"),
            FeaturedGameUiModel.NavLink("Discuss", "appDiscussLink"),
        ),
        sport = Sport.FOOTBALL,
        relatedContent = null,
        analyticsData = analyticsPreviewData()
    )

    private fun liveGameEPLPreviewData(): FeaturedGameUiModel = FeaturedGameUiModel(
        id = "gameEPL",
        title = "Premier League",
        firstTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "WHU",
            colors = "C4576B",
            logoUrl = null,
            score = "5"
        ),
        secondTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "CHE",
            colors = "1E6EDD",
            logoUrl = null,
            score = "3"
        ),
        gameStatus = FeaturedGameUiModel.GameStatus(
            state = FeaturedGameUiModel.GameState.LIVE_GAME,
            clock = "90' + 7'",
            aggregate = "Agg. 2-1".asResourceString()
        ),
        navLinks = listOf(
            FeaturedGameUiModel.NavLink("Match", "appGameLink"),
        ),
        sport = Sport.SOCCER,
        relatedContent = topCommentStaffPreviewData(),
        analyticsData = analyticsPreviewData()
    )

    private fun postGameNFLPreviewData(): FeaturedGameUiModel = FeaturedGameUiModel(
        id = "featuredGameId",
        title = "NFL Week 8",
        firstTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "BAL",
            colors = "6A14D6",
            logoUrl = null,
            score = "96",
            hasPossession = false
        ),
        secondTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "NYJ",
            colors = "0E823C",
            logoUrl = null,
            score = "138",
            hasPossession = false
        ),
        gameStatus = FeaturedGameUiModel.GameStatus(
            state = FeaturedGameUiModel.GameState.POSTGAME,
            gameDate = "Sun, Nov 9",
            period = "FINAL/OT"
        ),
        navLinks = emptyList(),
        sport = Sport.FOOTBALL,
        relatedContent = topCommentUserPreviewData(),
        analyticsData = analyticsPreviewData()
    )

    private fun postGameEPLPreviewData(): FeaturedGameUiModel = FeaturedGameUiModel(
        id = "featuredGameId",
        title = "Premier League",
        firstTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "WHU",
            colors = "C4576B",
            logoUrl = null,
            score = "5"
        ),
        secondTeam = FeaturedGameUiModel.Team(
            id = "teamId",
            alias = "CHE",
            colors = "1E6EDD",
            logoUrl = null,
            score = "3"
        ),
        gameStatus = FeaturedGameUiModel.GameStatus(
            state = FeaturedGameUiModel.GameState.POSTGAME,
            gameDate = "Sun, Nov 9",
            period = "FT",
            aggregate = "Agg. 2-1".asResourceString()
        ),
        navLinks = emptyList(),
        sport = Sport.SOCCER,
        relatedContent = relatedArticlePreviewData(),
        analyticsData = analyticsPreviewData()
    )

    private fun relatedArticlePreviewData() = ArticleUiModel(
        id = "articleId1",
        title = "Jets Week 8 storylines and prediction: Can Zach Wilson keep pace with the Ravens?",
        excerpt = "",
        imageUrl = "",
        byline = "The Athletic NFL Staff",
        commentCount = "",
        isBookmarked = false,
        isRead = false,
        postType = PostType.DISCUSSION,
        permalink = "",
        analyticsData = analyticsPreviewData()
    )

    private fun topCommentUserPreviewData() = TopCommentUiModel(
        id = "commentId",
        avatarUrl = null,
        author = "Elijah M.",
        commentedAt = "12m".asResourceString(),
        flairName = "NYJ",
        flairColor = "0E823C",
        isStaff = false,
        comment = "I think it will be close at times but the Jets should win. We’ve got players coming back and Cowboys are missing OL and CB. Hopefully Zach comes out and saves the day.",
        permalink = "permalink",
        analyticsData = analyticsPreviewData()
    )

    private fun topCommentStaffPreviewData() = TopCommentUiModel(
        id = "commentId",
        avatarUrl = null,
        author = "James L. Edwards III",
        commentedAt = "8m".asResourceString(),
        flairName = null,
        flairColor = null,
        isStaff = true,
        comment = "I think it will be close at times but the Jets should win. We’ve got players coming back and Cowboys are missing OL and CB. Hopefully Zach comes out and saves the day.",
        permalink = "permalink",
        analyticsData = analyticsPreviewData()
    )
}