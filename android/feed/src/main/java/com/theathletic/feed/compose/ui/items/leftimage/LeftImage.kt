package com.theathletic.feed.compose.ui.items.leftimage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.feed.compose.ui.reusables.ArticleTitle
import com.theathletic.feed.compose.ui.reusables.ContentImage
import com.theathletic.feed.compose.ui.reusables.FeedItemFooter
import com.theathletic.feed.compose.ui.reusables.FeedItemFooterUiModel
import com.theathletic.feed.compose.ui.reusables.Image
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme

@Composable
fun LeftImage(
    image: @Composable (modifier: Modifier) -> Unit,
    header: @Composable () -> Unit,
    footer: @Composable (modifier: Modifier) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(AthTheme.colors.dark200)
    ) {
        image(
            Modifier
                .height(90.dp)
                .padding(end = 16.dp)
        )

        Column(modifier = Modifier.weight(1f, fill = true)) {
            header()

            footer(
                Modifier
                    .align(Alignment.Start)
            )
        }
    }
}

private data class LeftImageBlueprintPreviewParams(
    val lightMode: Boolean = false,
    val image: @Composable (modifier: Modifier) -> Unit,
    val title: @Composable () -> Unit,
    val footer: @Composable (modifier: Modifier) -> Unit
)

private class LeftImageBlueprintPreviewParamsProvider :
    PreviewParameterProvider<LeftImageBlueprintPreviewParams> {
    override val values: Sequence<LeftImageBlueprintPreviewParams> = sequenceOf(
        LeftImageBlueprintPreviewParams(
            lightMode = false,
            image = { modifier -> PreviewImage(modifier) },
            title = { PreviewTitle() },
            footer = { modifier -> PreviewFooter(modifier) }
        ),
        LeftImageBlueprintPreviewParams(
            lightMode = true,
            image = { modifier -> PreviewImage(modifier) },
            title = { PreviewTitle() },
            footer = { modifier -> PreviewFooter(modifier) }
        )
    )

    @Composable
    private fun PreviewImage(modifier: Modifier) {
        ContentImage(
            image = Image.RemoteImage(url = "", error = R.drawable.ic_feed_placeholder_offline_large),
            isRead = false,
            modifier = modifier
        )
    }

    @Composable
    private fun PreviewTitle() {
        ArticleTitle(
            text = "Top 10 mock draft with The Athletic NFL Staff",
            style = AthTextStyle.TiemposHeadline.Regular.ExtraExtraSmall,
            isRead = false
        )
    }

    @Composable
    private fun PreviewFooter(modifier: Modifier) {
        val uiModel = FeedItemFooterUiModel(
            isBookmarked = false,
            byline = "Marc Mazzoni and Jonathan Stewart",
            commentCount = "100"
        )
        FeedItemFooter(uiModel, modifier)
    }
}

@Preview
@Composable
private fun LeftImageBlueprintPreview(
    @PreviewParameter(LeftImageBlueprintPreviewParamsProvider::class)
    params: LeftImageBlueprintPreviewParams
) {
    return AthleticTheme(lightMode = params.lightMode) {
        LeftImage(params.image, params.title, params.footer)
    }
}