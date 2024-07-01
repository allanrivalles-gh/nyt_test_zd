package com.theathletic.feed.compose.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.theathletic.feed.R
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.widgets.ModalBottomSheetMenuItem

sealed interface FeedDetailsMenuOption {
    data class Save(val articleId: Long, val isSaved: Boolean) : FeedDetailsMenuOption
    data class MarkRead(val articleId: Long, val isRead: Boolean) : FeedDetailsMenuOption
    data class Share(val permalink: String) : FeedDetailsMenuOption
}

private val FeedDetailsMenuOption.icon: Int
    @DrawableRes
    get() = when (this) {
        is FeedDetailsMenuOption.Save -> if (isSaved) R.drawable.ic_bookmark_selected else R.drawable.ic_bookmark
        is FeedDetailsMenuOption.MarkRead -> if (isRead) R.drawable.ic_x else R.drawable.ic_check
        is FeedDetailsMenuOption.Share -> R.drawable.ic_share
    }

private val FeedDetailsMenuOption.label: Int
    @StringRes
    get() = when (this) {
        is FeedDetailsMenuOption.Save -> if (isSaved) R.string.fragment_feed_unsave else R.string.fragment_feed_save
        is FeedDetailsMenuOption.MarkRead -> if (isRead) R.string.feed_mark_unread else R.string.feed_mark_read
        is FeedDetailsMenuOption.Share -> R.string.feed_article_action_share
    }

@Composable
fun FeedDetailsMenuModalSheet(
    options: List<FeedDetailsMenuOption>,
    onOptionSelected: (FeedDetailsMenuOption) -> Unit
) {
    options.forEach { option ->
        ModalBottomSheetMenuItem(
            icon = option.icon,
            label = option.label,
            modifier = Modifier
                .clickable { onOptionSelected(option) }
        )
    }
}

@DayNightPreview
@Composable
private fun FeedDetailsMenuModalSheetPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        FeedDetailsMenuModalSheet(
            options = listOf(
                FeedDetailsMenuOption.Share("https://theathletic.com/4675110/2023/07/08/bruins-prospects-fabian-lysell-mason-lohrei/")
            ),
            onOptionSelected = {}
        )
    }
}