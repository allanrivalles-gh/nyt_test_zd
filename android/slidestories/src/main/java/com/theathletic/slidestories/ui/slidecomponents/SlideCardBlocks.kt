package com.theathletic.slidestories.ui.slidecomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.slidestories.ui.SlideStoriesFixtures
import com.theathletic.slidestories.ui.SlideStoriesUiModel
import com.theathletic.themes.AthColor.Companion.Gray200
import com.theathletic.themes.AthColor.Companion.Gray400
import com.theathletic.themes.AthColor.Companion.Gray700
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.preview.DevicePreviewSmallAndLarge
import com.theathletic.ui.widgets.SimpleShrinkToFitText

data class SlideCardBlocks(
    val byline: SlideStoriesUiModel.Byline? = null,
    val takeawayMessage: SlideStoriesUiModel.TakeawayMessage,
    val readMore: SlideStoriesUiModel.ReadMore? = null
)

@Composable
fun SlideCard(
    byline: SlideStoriesUiModel.Byline? = null,
    takeawayMessage: SlideStoriesUiModel.TakeawayMessage?,
    readMore: SlideStoriesUiModel.ReadMore? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Gray200)
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .defaultMinSize(minHeight = 300.dp)
            .fillMaxHeight()
    ) {
        if (byline != null && byline.isIntroByline) {
            Byline(
                byline = byline.byline,
                authorImageUrls = byline.authorImageUrls,
                reportingFrom = byline.reportingFrom,
                showSmallIcons = false,
                showSmallFont = false,
                isIntro = byline.isIntroByline
            )
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            if (takeawayMessage != null) {
                TakeAwayMessage(
                    index = takeawayMessage.index,
                    text = takeawayMessage.text
                )
            }

            Column {
                if (byline != null && byline.isIntroByline.not()) {
                    Byline(
                        byline = byline.byline,
                        authorImageUrls = byline.authorImageUrls,
                        reportingFrom = byline.reportingFrom,
                        showSmallIcons = true,
                        showSmallFont = false,
                        isIntro = byline.isIntroByline
                    )
                }

                if (readMore != null) {
                    ReadMore(
                        title = readMore.title,
                        description = readMore.description,
                        imageUrl = readMore.imageUrl,
                        permalink = readMore.permalink,
                        onClick = readMore.onClick
                    )
                }
            }
        }
    }
}

@Composable
private fun Byline(
    byline: String,
    authorImageUrls: List<String>,
    reportingFrom: String?,
    showSmallIcons: Boolean,
    showSmallFont: Boolean,
    isIntro: Boolean
) {
    Byline(
        byline,
        authorImageUrls,
        reportingFrom,
        showSmallIcons,
        showSmallFont
    )

    if (isIntro) {
        Divider(
            color = Gray400,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    } else {
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun TakeAwayMessage(index: String, text: String) {
    Row(modifier = Modifier.padding(bottom = 18.dp)) {
        Text(
            text = index,
            style = AthTextStyle.Calibre.Headline.SemiBold.Medium,
            maxLines = 1,
            color = Gray700
        )

        Spacer(modifier = Modifier.width(18.dp))

        SimpleShrinkToFitText(
            text = text,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraExtraLarge.copy(color = Gray700),
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@DayNightPreview
@DevicePreviewSmallAndLarge
@Composable
fun IntroCard_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        SlideCard(
            byline = SlideStoriesFixtures.byLineIntro,
            takeawayMessage = SlideStoriesFixtures.takeawayMessage,
            readMore = null
        )
    }
}

@Preview
@Composable
fun TakeAwayMessagesCard_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        SlideCard(
            takeawayMessage = SlideStoriesFixtures.takeawayMessage
        )
    }
}

@Preview
@Composable
fun TakeAwayMessageCard_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        SlideCard(takeawayMessage = SlideStoriesFixtures.takeawayMessage.copy(index = "99."))
    }
}

@Preview
@Composable
fun TakeAwayMessageReadMoreCard_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        SlideCard(
            takeawayMessage = SlideStoriesFixtures.takeawayMessage,
            readMore = SlideStoriesFixtures.readMore
        )
    }
}

@Preview
@Composable
fun TakeAwayMessageFooterBylineCard_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        SlideCard(
            takeawayMessage = SlideStoriesFixtures.takeawayMessage,
            byline = SlideStoriesFixtures.byLineFooter
        )
    }
}

@Preview
@Composable
fun TakeAwayMessageFooterBylineReadMoreCard_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        SlideCard(
            takeawayMessage = SlideStoriesFixtures.takeawayMessage,
            byline = SlideStoriesFixtures.byLineFooter,
            readMore = SlideStoriesFixtures.readMore
        )
    }
}

@Preview
@Composable
fun TakeAwayMessagesFooterBylineCard_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        SlideCard(
            takeawayMessage = SlideStoriesFixtures.takeawayMessage,
            byline = SlideStoriesFixtures.byLineFooter
        )
    }
}