package com.theathletic.scores.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.scores.ui.gamecells.GameCell
import com.theathletic.scores.ui.gamecells.GameCellModel
import com.theathletic.scores.ui.gamecells.SectionFooter
import com.theathletic.scores.ui.gamecells.SectionHeader
import com.theathletic.themes.AthTheme
import kotlin.math.min

private const val KEY_GAMECELL = "Key_GameCell"
private const val KEY_HEADER = "Key_Header"
private const val KEY_FOOTER = "Key_Footer"
private const val KEY_DIVIDER = "Key_Divider"

@OptIn(ExperimentalFoundationApi::class)
@SuppressWarnings("LongMethod")
@Composable
fun ScoresFeedList(
    listItems: List<ScoresFeedUI.FeedGroup>,
    onLeagueSectionClicked: (Long, Int) -> Unit,
    onAllGamesClicked: (Long, Int) -> Unit,
    onGameClicked: (String) -> Unit,
    onDiscussionLinkClicked: (String, Long?) -> Unit,
    onRefresh: () -> Unit,
    onViewVisibilityChanged: (ImpressionPayload, Float) -> Unit,
    collapsableHeader: @Composable () -> Unit,
    loadingDayFeed: Boolean
) {
    val listState = rememberLazyListState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = { onRefresh() }
    ) {
        LazyColumn(
            state = listState
        ) {
            item { collapsableHeader() }
            if (loadingDayFeed) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    FeedListPlaceholder()
                }
            } else {
                listItems.forEachIndexed { groupIndex, group ->
                    val groupKey = group.header.id
                    item(key = "$KEY_DIVIDER-$groupKey") {
                        Divider(
                            modifier = Modifier.height(4.dp),
                            color = AthTheme.colors.dark100
                        )
                    }
                    stickyHeader(key = "$KEY_HEADER-$groupKey") {
                        ScoresHeader(
                            group = group,
                            onLeagueSectionClicked = onLeagueSectionClicked
                        )
                    }
                    itemsIndexed(
                        group.games,
                        key = { _, game -> "$KEY_GAMECELL-$groupIndex:${game.gameId}" }
                    ) { index, game ->
                        GameCell(
                            gameId = game.gameId,
                            title = game.title,
                            showTitle = game.showTitle,
                            firstTeam = game.firstTeam,
                            secondTeam = game.secondTeam,
                            infoWidget = game.infoWidget,
                            discussionLinkText = game.discussionLinkText,
                            showDivider = group.games.lastIndex != index,
                            showTeamRanking = game.showTeamRanking,
                            onGameClicked = { gameId -> onGameClicked(gameId) },
                            onDiscussionLinkClicked = { onDiscussionLinkClicked(it, group.header.leagueId) }
                        )
                    }
                    if (group.footer?.leagueId != null) {
                        item(key = "$KEY_FOOTER-$groupKey") {
                            SectionFooter(
                                label = group.footer.label,
                                onClick = {
                                    onAllGamesClicked(
                                        group.footer.leagueId,
                                        group.footer.index
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        if (listItems.isNotEmpty()) {
            ScoresFeedImpressionTracker(
                listState = listState,
                listItems = listItems,
                onViewVisibilityChanged = onViewVisibilityChanged
            )
        }
    }
}

@Composable
fun ScoresHeader(
    group: ScoresFeedUI.FeedGroup,
    onLeagueSectionClicked: (Long, Int) -> Unit
) {
    SectionHeader(
        id = group.header.id,
        title = group.header.title,
        subTitle = group.header.subTitle,
        canNavigate = group.header.canNavigate,
        leagueId = group.header.leagueId,
        index = group.header.index,
        onClick = { leagueId, index ->
            onLeagueSectionClicked(
                leagueId,
                index
            )
        }
    )
}

@Composable
fun ScoresFeedImpressionTracker(
    listState: LazyListState,
    listItems: List<ScoresFeedUI.FeedGroup>,
    onViewVisibilityChanged: (ImpressionPayload, Float) -> Unit
) {
    val visibilityState = remember { mutableMapOf<Int, Float>() }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect {
                it.visibleItemsInfo.forEachIndexed { localIndex, composable ->
                    if (composable.key.toString().startsWith(KEY_GAMECELL).not()) return@forEachIndexed
                    listItems.toImpressionsGameCell(composable)?.let { gameCell ->
                        val visibility = when (localIndex) {
                            0 -> (composable.size + min(composable.offset, 0)).toFloat() / composable.size

                            listState.layoutInfo.visibleItemsInfo.indices.last -> {
                                val lastVisiblePx = listState.layoutInfo.viewportEndOffset - composable.offset
                                lastVisiblePx.toFloat() / composable.size
                            }

                            else -> 1.0f
                        }.coerceIn(0f, 1f)

                        if (visibilityState[composable.index] != visibility) {
                            visibilityState[composable.index] = visibility
                            onViewVisibilityChanged(gameCell.impressionPayload, visibility)
                        }
                    }
                }
            }
    }
}

private fun List<ScoresFeedUI.FeedGroup>.toImpressionsGameCell(composable: LazyListItemInfo): GameCellModel? {
    // Game cell key -> "Key_GameCell-'group_index':'game_id'
    val keys = composable.key.toString().removePrefix("$KEY_GAMECELL-").split(":")
    val groupIndex = keys[0].toIntOrNull() ?: return null
    if (groupIndex > lastIndex) return null
    return get(groupIndex).games.find { it.gameId == keys[1] }
}