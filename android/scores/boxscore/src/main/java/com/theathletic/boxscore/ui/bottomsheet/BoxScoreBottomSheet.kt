package com.theathletic.boxscore.ui.bottomsheet

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.R
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.widgets.ResourceIcon

enum class BoxScoreMenuOption(
    @DrawableRes val resourceId: Int,
    @StringRes val label: Int
) {
    ARTICLE_UNSAVE(R.drawable.ic_bookmark_selected, R.string.fragment_feed_unsave),
    ARTICLE_SAVE(R.drawable.ic_bookmark, R.string.fragment_feed_save),
    ARTICLE_UNREAD(R.drawable.ic_close, R.string.feed_mark_unread),
    ARTICLE_READ(R.drawable.ic_check, R.string.feed_mark_read),
    SHARE(R.drawable.ic_share, R.string.feed_article_action_share),
    PODCAST_SERIES_DETAILS(R.drawable.ic_alert_info, R.string.podcast_series_details),
    PODCAST_FOLLOW_SERIES(R.drawable.ic_add, R.string.podcast_follow_series),
    PODCAST_UNFOLLOW_SERIES(R.drawable.ic_close, R.string.podcast_unfollow_series),
    PODCAST_DOWNLOAD(R.drawable.ic_podcast_download_v2, R.string.podcast_download_episode),
    PODCAST_REMOVE_DOWNLOAD(R.drawable.ic_trash, R.string.podcast_remove_episode),
    PODCAST_CANCEL_DOWNLOAD(R.drawable.ic_close, R.string.podcast_cancel_episode_download)
}

@Composable
fun BoxScoreModalSheet(
    options: List<BoxScoreMenuOption>,
    selectedOption: (BoxScoreMenuOption) -> Unit
) {
    options.forEach { menuOption ->
        Row(
            modifier = Modifier
                .background(AthTheme.colors.dark300)
                .fillMaxWidth()
                .height(60.dp)
                .clickable { selectedOption(menuOption) }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResourceIcon(
                resourceId = menuOption.resourceId,
                tint = AthTheme.colors.dark800,
                modifier = Modifier
                    .padding(start = 14.dp)
                    .size(24.dp)
            )

            Text(
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                text = stringResource(id = menuOption.label),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}