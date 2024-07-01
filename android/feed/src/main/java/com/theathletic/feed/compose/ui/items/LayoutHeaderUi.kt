package com.theathletic.feed.compose.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon

data class LayoutHeaderUiModel(
    val title: String,
    val icon: String,
    val deepLink: String = "",
    val actionText: String = ""
) {
    fun showIcon(): Boolean = icon.isNotEmpty()
    fun showTitle(): Boolean = title.isNotEmpty()
    fun showDeepLinkAction(): Boolean = deepLink.isNotEmpty() && actionText.isNotEmpty()

    fun showHeader(): Boolean = showIcon() || showTitle() || showDeepLinkAction()
}

@Composable
fun LayoutHeader(
    uiModel: LayoutHeaderUiModel,
    modifier: Modifier = Modifier,
    onActionClick: (String) -> Unit = {}
) {
    if (uiModel.showHeader().not()) {
        return
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .background(color = AthTheme.colors.dark200)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f, fill = false)
        ) {
            if (uiModel.showIcon()) {
                HeaderIcon(uiModel.icon)
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (uiModel.showTitle()) {
                HeaderText(uiModel.title)
            }
        }
        if (uiModel.showDeepLinkAction()) {
            DeepLinkAction(uiModel.actionText, onClick = { onActionClick(uiModel.deepLink) })
        }
    }
}

@Composable
private fun HeaderIcon(icon: String) {
    Box(
        modifier = Modifier
            .size(24.dp)
    ) {
        RemoteImageAsync(
            url = icon,
            placeholder = R.drawable.ic_team_logo_placeholder,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
private fun HeaderText(text: String) {
    Text(
        color = AthTheme.colors.dark700,
        style = AthTextStyle.Slab.Bold.Small,
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun DeepLinkAction(actionText: String, onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(
            onClick = onClick,
            interactionSource = MutableInteractionSource(),
            indication = null
        )
    ) {
        Text(
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            text = actionText
        )
        Spacer(modifier = Modifier.width(2.dp))
        ResourceIcon(
            resourceId = R.drawable.ic_arrow_right,
            tint = AthTheme.colors.dark500,
            modifier = Modifier
                .height(16.dp)
                .align(alignment = Alignment.Bottom)
        )
    }
}

private data class LayoutHeaderPreviewParams(
    val lightMode: Boolean = false,
    val title: String = "",
    val icon: String = "",
    val deepLink: String = "",
    val actionText: String = "See all"
)

private class LayoutHeaderPreviewParamsProvider : PreviewParameterProvider<LayoutHeaderPreviewParams> {
    override val values: Sequence<LayoutHeaderPreviewParams> = sequenceOf(
        LayoutHeaderPreviewParams(
            title = "Today's Must Read",
            icon = "https://upload.wikimedia.org/wikipedia/commons/5/50/The_Athletic_app_icon.png",
            deepLink = "https://seealllink"
        ),
        LayoutHeaderPreviewParams(
            title = "For You",
            icon = "",
            deepLink = "https://seemorelink",
            actionText = "See more"
        ),
        LayoutHeaderPreviewParams(
            title = "NHL Offseason Has More Going On Than You Can Fit In One Header",
            icon = "https://upload.wikimedia.org/wikipedia/commons/5/50/The_Athletic_app_icon.png",
            deepLink = "https://seealllink"
        ),
        LayoutHeaderPreviewParams(
            lightMode = true,
            title = "Latest Updates"
        )
    )
}

@Preview
@Composable
private fun LayoutHeaderPreview(
    @PreviewParameter(LayoutHeaderPreviewParamsProvider::class) params: LayoutHeaderPreviewParams
) {
    AthleticTheme(lightMode = params.lightMode) {
        LayoutHeader(
            LayoutHeaderUiModel(
                title = params.title,
                icon = params.icon,
                deepLink = params.deepLink,
                actionText = params.actionText
            )
        )
    }
}