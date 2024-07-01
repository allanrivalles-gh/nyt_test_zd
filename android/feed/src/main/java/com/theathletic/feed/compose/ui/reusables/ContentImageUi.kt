package com.theathletic.feed.compose.ui.reusables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.feed.compose.data.PostType
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon
import timber.log.Timber

/**
 * Image used in feed content, with logic for applying color filter to already-read content, selecting
 * a local image plus overlay for is-read indicator, as well as any other shared treatments
 */
@Composable
internal fun ContentImage(
    image: Image,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    overlay: @Composable (modifier: Modifier) -> Unit = {},
    isRead: Boolean = false
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        val colorFilter = if (isRead) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
        when (image) {
            is Image.RemoteImage -> RemoteImageAsync(
                url = image.url,
                error = image.error,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
                colorFilter = colorFilter
            )

            is Image.ResourceImage -> Image(
                painter = painterResource(id = image.id),
                modifier = Modifier.fillMaxSize(),
                contentDescription = null,
                contentScale = contentScale,
                colorFilter = colorFilter,
            )
        }

        Box(
            modifier = Modifier
                .background(AthColor.Gray100.copy(alpha = 0.1f))
                .matchParentSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            overlay(modifier)
        }
    }
}

@Composable
fun ReadIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .then(modifier)
            .background(
                color = AthColor.Gray800,
                shape = RoundedCornerShape(size = 40.dp),
            )
            .wrapContentSize()
            .height(IntrinsicSize.Max)
            .padding(start = 2.dp, end = 8.dp),
    ) {
        ResourceIcon(
            resourceId = R.drawable.ic_feed_check,
            tint = AthColor.Gray400,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterVertically),
        )

        Text(
            color = AthColor.Gray400,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            text = stringResource(R.string.fragment_feed_item_read),
            modifier = Modifier
                .align(Alignment.CenterVertically),
        )
    }
}

@Composable
fun SmallReadIndicator(modifier: Modifier = Modifier) {
    ResourceIcon(
        resourceId = R.drawable.ic_feed_read_indicator_small,
        modifier = modifier,
    )
}

@Composable
fun Date(date: String, modifier: Modifier = Modifier) {
    // NOTE: Colors are on top of an image, so they don't change based on dark/light mode
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                spotColor = AthColor.Gray600,
                ambientColor = AthColor.Gray600
            )
            .height(18.dp)
            .background(color = AthColor.Gray800, shape = RoundedCornerShape(size = 40.dp))
            .padding(start = 8.dp, top = 2.dp, end = 8.dp, bottom = 4.dp)
    ) {
        Text(
            color = AthColor.Gray400,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            text = date
        )
    }
}

@Preview
@Composable
private fun ReadIndicatorPreview() {
    Column {
        ReadIndicator()
        SmallReadIndicator()
    }
}

@Preview(widthDp = 300, heightDp = 225)
@Composable
private fun ContentImagePreview() {
    ContentImage(
        image = Image.ResourceImage(id = R.drawable.img_feed_q_and_a_live),
    )
}

@Preview(widthDp = 300, heightDp = 225)
@Composable
private fun ContentImagePreviewRead() {
    ContentImage(
        image = Image.ResourceImage(id = R.drawable.img_feed_q_and_a_live),
        isRead = true,
        overlay = { SmallReadIndicator(Modifier.padding(10.dp)) }
    )
}

@Preview(widthDp = 300, heightDp = 225)
@Composable
private fun ContentImagePreviewDatePlusRead() {
    val isRead = true
    ContentImage(
        image = Image.ResourceImage(id = R.drawable.img_feed_q_and_a_live),
        isRead = isRead,
        overlay = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 10.dp, end = 10.dp)
            ) {
                Date(date = "5 Jun")
                if (isRead) {
                    SmallReadIndicator(modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    )
}

@Preview(widthDp = 300, heightDp = 225)
@Composable
private fun ContentImagePreviewDateUnread() {
    val isRead = false
    ContentImage(
        image = Image.ResourceImage(id = R.drawable.img_feed_q_and_a_live),
        isRead = isRead,
        overlay = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 10.dp, end = 10.dp)
            ) {
                Date(date = "5 Jun")
                if (isRead) {
                    SmallReadIndicator(modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    )
}

sealed interface Image {
    data class RemoteImage(val url: String, @DrawableRes val error: Int? = null) : Image
    data class ResourceImage(@DrawableRes val id: Int) : Image
}

@Composable
internal fun getImage(postType: PostType, imageUrl: String?): Image {
    return when (postType) {
        PostType.Q_AND_A_LIVE -> Image.ResourceImage(id = R.drawable.img_feed_q_and_a_live)
        PostType.Q_AND_A_UPCOMING -> Image.ResourceImage(id = R.drawable.img_feed_q_and_a_upcoming)
        PostType.Q_AND_A_RECAP -> Image.ResourceImage(id = R.drawable.img_feed_q_and_a_recap)
        PostType.DISCUSSION -> Image.ResourceImage(id = R.drawable.img_feed_discussion)
        else -> {
            Timber.w("Missing image URL for article")
            Image.RemoteImage(url = imageUrl ?: "", error = R.drawable.ic_feed_placeholder_offline_large)
        }
    }
}