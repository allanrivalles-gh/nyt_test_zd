package com.theathletic.feed.compose.ui.items.insiders

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon

data class InsiderUiModel(
    val imageUrl: String,
    val author: String,
    val authorRole: String,
    val excerpt: String,
    val lastUpdated: String,
    val commentCount: String
)

@Composable
fun Insider(uiModel: InsiderUiModel) {
    Box(
        modifier = Modifier
            .width(226.dp)
            .height(444.dp)
            .background(AthTheme.colors.dark300)
    ) {
        RemoteImageAsync(
            url = uiModel.imageUrl,
            error = R.drawable.ic_feed_placeholder_offline_large,
            modifier = Modifier.fillMaxWidth()
        )
        Content(uiModel, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun Content(uiModel: InsiderUiModel, modifier: Modifier) {
    Column(
        modifier = modifier.then(
            Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
    ) {
        Text(
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Slab.Bold.Medium,
            text = uiModel.author,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            color = AthTheme.colors.dark600,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            text = uiModel.authorRole,
            modifier = Modifier
                .padding(top = 8.dp)
        )

        Divider(
            color = AthColor.Gray200,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .height(1.dp)
                .fillMaxWidth()
        )

        Text(
            text = uiModel.excerpt,
            style = AthTextStyle.TiemposBody.Regular.Medium,
            color = AthTheme.colors.dark800,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .height(64.dp)
        )

        Footer(uiModel)
    }
}

@Composable
fun Footer(uiModel: InsiderUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            text = uiModel.lastUpdated,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Bottom)
        )

        Spacer(modifier = Modifier.weight(1f))

        ResourceIcon(
            resourceId = R.drawable.ic_feed_news_comment,
            tint = AthTheme.colors.dark800,
            modifier = Modifier
                .align(Alignment.Bottom)
                .padding(start = 8.dp)
        )

        Text(
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            text = uiModel.commentCount,
            modifier = Modifier
                .align(Alignment.Bottom)
                .padding(start = 6.dp)
        )
    }
}

@DayNightPreview
@Composable
private fun InsidersItemPreview(
    @PreviewParameter(InsiderPreviewParamProvider::class)
    uiModel: InsiderUiModel
) {
    return AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        Insider(uiModel)
    }
}

private class InsiderPreviewParamProvider : PreviewParameterProvider<InsiderUiModel> {

    override val values: Sequence<InsiderUiModel> = sequenceOf(
        getUiModel(),
        getUiModel(
            author = "Adam Crafto",
            authorRole = "Senior NBA Insider",
            excerpt = "Short to test alignment."
        )
    )

    fun getUiModel(
        imageUrl: String = "",
        author: String = "Marcus Thompson II",
        authorRole: String = "Correspondent, Wolverhampton Wolves",
        excerpt: String = "Malcolm Jenkins: NFL won't get it right until it " +
            "specifically addresses deflated footballs.",
        lastUpdated: String = "2d ago",
        commentCount: String = "125"
    ) = InsiderUiModel(
        imageUrl = imageUrl,
        author = author,
        authorRole = authorRole,
        excerpt = excerpt,
        lastUpdated = lastUpdated,
        commentCount = commentCount
    )
}