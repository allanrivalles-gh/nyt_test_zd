package com.theathletic.slidestories.ui.slidecomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.slidestories.R
import com.theathletic.slidestories.ui.SlideStoriesFixtures.slideImage
import com.theathletic.slidestories.ui.SlideStoriesFixtures.slideQuoteLarge
import com.theathletic.slidestories.ui.SlideStoriesFixtures.slideQuoteSmall
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthColor.Companion.Gray200
import com.theathletic.themes.AthColor.Companion.Gray600
import com.theathletic.themes.AthColor.Companion.Gray700
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.SimpleShrinkToFitText

@Composable
fun SlidePlaceholder(
    slideTop: @Composable BoxScope.() -> Unit,
    slideBottom: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(0.6f)) {
                slideTop()
            }
            Box(modifier = Modifier.weight(0.4f)) {
                slideBottom()
            }
        }
    }
}

@Composable
fun QuoteSlide(
    quote: String,
    attributor: String,
    attributorRole: String? = null,
    slideCardBlocks: SlideCardBlocks
) {
    SlidePlaceholder(
        slideTop = {
            Quote(
                quote = quote,
                attributor = attributor,
                attributorRole = attributorRole
            )
        },
        slideBottom = {
            SlideCard(
                takeawayMessage = slideCardBlocks.takeawayMessage,
                byline = slideCardBlocks.byline,
                readMore = slideCardBlocks.readMore
            )
        }
    )
}

@Composable
fun ImageSlide(
    urlImage: String,
    credit: String?,
    slideCardBlocks: SlideCardBlocks
) {
    val imageLoadingState = remember { mutableStateOf(SlidesStates.LOADING) }

    SlidePlaceholder(
        slideTop = {
            Box {
                RemoteImageAsync(
                    url = urlImage,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onLoading = { imageLoadingState.value = SlidesStates.LOADING },
                    onSuccess = { imageLoadingState.value = SlidesStates.COMPLETED },
                    onError = { imageLoadingState.value = SlidesStates.ERROR }
                )
                credit?.let {
                    ImageCredit(credit)
                }

                when (imageLoadingState.value) {
                    SlidesStates.LOADING -> SlideLoadingState()
                    SlidesStates.ERROR -> SlideErrorState()
                    else -> {}
                }
            }
        },
        slideBottom = {
            SlideCard(
                takeawayMessage = slideCardBlocks.takeawayMessage,
                byline = slideCardBlocks.byline,
                readMore = slideCardBlocks.readMore
            )
        }
    )
}

@Composable
private fun BoxScope.ImageCredit(credit: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AthColor.Gray100.copy(alpha = 0f),
                        AthColor.Gray100.copy(alpha = 0.8f)
                    )
                )
            )
            .align(Alignment.BottomCenter)
    ) {
        Text(
            text = credit,
            color = Gray600,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Right,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 8.dp)
        )
    }
}

@Composable
fun Quote(
    quote: String,
    attributor: String,
    attributorRole: String?
) {
    var attributorFontSize by remember { mutableStateOf(18.sp) }
    val quoteStyle = if (quote.count() > 100) {
        AthTextStyle.TiemposHeadline.Regular.Medium
    } else {
        AthTextStyle.TiemposHeadline.Regular.ExtraExtraLarge
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Gray700)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.slide_quote_starting_comma),
                style = quoteStyle,
                maxLines = 1,
                color = Gray200
            )

            Spacer(modifier = Modifier.width(4.dp))

            Column {
                Text(
                    text = quote.plus(stringResource(id = R.string.slide_quote_ending_comma)),
                    style = quoteStyle,
                    color = Gray200,
                    textAlign = TextAlign.Left
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    SimpleShrinkToFitText(
                        text = if (attributorRole == null) attributor else attributor.plus(", "),
                        style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge.copy(
                            color = Gray200,
                            fontSize = attributorFontSize
                        ),
                        onFontSizeChanged = { newFontSize -> attributorFontSize = newFontSize }
                    )

                    attributorRole?.let {
                        SimpleShrinkToFitText(
                            text = attributorRole,
                            style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge.copy(
                                color = Gray200,
                                fontSize = attributorFontSize
                            ),
                            onFontSizeChanged = { newFontSize -> attributorFontSize = newFontSize }
                        )
                    }
                }
            }
        }
    }
}

@DayNightPreview
@Composable
fun QuoteSlide_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        QuoteSlide(
            slideQuoteSmall.quote,
            slideQuoteSmall.attributor,
            slideQuoteSmall.attributorRole,
            slideQuoteSmall.slideCardBlocks
        )
    }
}

@DayNightPreview
@Composable
fun QuoteSlideLong_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        QuoteSlide(
            slideQuoteLarge.quote,
            slideQuoteLarge.attributor,
            slideQuoteLarge.attributorRole,
            slideQuoteLarge.slideCardBlocks
        )
    }
}

@DayNightPreview
@Composable
fun ImageSlide_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        ImageSlide(
            slideImage.imageUrl,
            slideImage.credit,
            slideImage.slideCardBlocks
        )
    }
}