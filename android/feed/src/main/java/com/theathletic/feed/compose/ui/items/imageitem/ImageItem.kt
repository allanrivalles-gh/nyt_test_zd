package com.theathletic.feed.compose.ui.items.imageitem

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.feed.compose.ui.reusables.ArticleTitle
import com.theathletic.feed.compose.ui.reusables.ContentImage
import com.theathletic.feed.compose.ui.reusables.Image
import com.theathletic.feed.compose.ui.reusables.SmallReadIndicator
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme

@Composable
fun RightImageItem(
    title: @Composable ColumnScope.() -> Unit,
    image: @Composable RowScope.() -> Unit,
    footer: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .background(AthTheme.colors.dark200)
            .height(IntrinsicSize.Max)
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f, fill = true)
                .padding(end = 24.dp)
                .fillMaxHeight()
        ) {
            title()
            footer()
        }
        image()
    }
}

@Composable
fun LeftImageItem(
    title: @Composable ColumnScope.() -> Unit,
    image: @Composable RowScope.() -> Unit,
    footer: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .background(AthTheme.colors.dark200)
            .height(IntrinsicSize.Max)
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        image()
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f, fill = true)
                .padding(start = 24.dp)
                .fillMaxHeight()
        ) {
            title()
            footer()
        }
    }
}

@Composable
fun TopImageItem(
    image: @Composable ColumnScope.() -> Unit,
    title: @Composable ColumnScope.() -> Unit,
    footer: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(AthTheme.colors.dark200)
            .width(IntrinsicSize.Min)
    ) {
        image()
        Spacer(Modifier.padding(top = 10.dp))
        title()
        Spacer(Modifier.padding(top = 10.dp))
        footer()
    }
}

@Composable
fun ItemImage(
    modifier: Modifier = Modifier,
    image: Image,
    isRead: Boolean = false
) {
    ContentImage(
        image = image,
        isRead = isRead,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        overlay = { if (isRead) SmallReadIndicator(Modifier.padding(6.dp)) }
    )
}

fun Modifier.heroAspect() = height(81.dp).aspectRatio(4f / 3)
fun Modifier.latestNewsAspect() = size(90.dp)
fun Modifier.forYouAspect() = height(100.dp).aspectRatio(4f / 3)
fun Modifier.carouselAspect() = width(160.dp).aspectRatio(3f / 2)

@Composable
internal fun TopImageItemTitle(title: String, isRead: Boolean = false) {
    ArticleTitle(
        text = title,
        isRead = isRead,
        style = AthTextStyle.TiemposBody.Medium.Small,
        minLines = 4,
        maxLines = 4
    )
}

@Composable
fun HorizontalImageItemTitle(title: String, isRead: Boolean = false, maxLines: Int = 3) {
    ArticleTitle(
        text = title,
        maxLines = maxLines,
        style = AthTextStyle.TiemposHeadline.Regular.ExtraExtraSmall,
        isRead = isRead
    )
}

@Preview
@Composable
private fun ImagePreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        ItemImage(image = Image.ResourceImage(R.drawable.img_feed_discussion), isRead = true)
    }
}