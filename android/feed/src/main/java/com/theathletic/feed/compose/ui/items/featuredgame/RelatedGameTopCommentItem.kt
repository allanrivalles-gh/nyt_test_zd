package com.theathletic.feed.compose.ui.items.featuredgame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.feed.compose.ui.components.TopComment
import com.theathletic.feed.compose.ui.components.TopCommentUiModel
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.themes.AthTheme

@Composable
fun RelatedGameTopCommentItem(
    uiModel: TopCommentUiModel,
    itemInteractor: ItemInteractor
) {
    Column(
        modifier = Modifier
            .interactive(uiModel, itemInteractor)
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {
        Divider(
            color = AthTheme.colors.dark300,
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 16.dp)
        )
        TopComment(
            uiModel = uiModel,
            itemInteractor = itemInteractor
        )
    }
}