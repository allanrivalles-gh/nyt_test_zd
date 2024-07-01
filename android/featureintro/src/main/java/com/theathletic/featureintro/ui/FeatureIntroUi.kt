package com.theathletic.featureintro.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.buttons.PrimaryButtonLarge

data class FeatureIntroUiModel(
    val pageCount: Int,
    val pages: List<Page>,
) {
    data class Page(
        @StringRes val title: Int,
        @StringRes val description: Int,
        @DrawableRes val image: Int,
        @StringRes val buttonLabel: Int,
    )
}

@Composable
fun FeatureIntroScreen(viewModel: FeatureIntroViewModel) {
    val viewState by viewModel.viewState.collectAsState()

    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        FeatureIntro(
            uiModel = viewState.uiModel,
            currentPage = viewState.currentPage,
            onPageChanged = { page -> viewModel.onPageChanged(page) },
            onNextAction = { viewModel.onNextAction() },
            onClose = { viewModel.onClose() }
        )
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)
@Composable
fun FeatureIntro(
    uiModel: FeatureIntroUiModel,
    currentPage: Int,
    onPageChanged: (page: Int) -> Unit = {},
    onNextAction: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AthTheme.colors.dark200)
            .semantics { testTagsAsResourceId = true }
    ) {
        val pagerState = rememberPagerState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 12.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CloseButton(onClick = { onClose() })
            NewTag()
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (uiModel.pages.isNotEmpty()) {
            HorizontalPager(
                count = uiModel.pageCount,
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { page ->
                Column(Modifier.padding(horizontal = 24.dp)) {
                    ImageWithBackground(uiModel.pages[page].image)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                WhatIsNew(uiModel.pages[currentPage])
                Spacer(modifier = Modifier.size(24.dp))
                NextButton(buttonLabel = uiModel.pages[currentPage].buttonLabel, onNext = onNextAction)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiModel.pageCount > 1) {
                HorizontalPagerIndicator(
                    activeColor = AthTheme.colors.dark700,
                    pagerState = pagerState,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        LaunchedEffect(pagerState.currentPage) {
            onPageChanged(pagerState.currentPage)
        }

        LaunchedEffect(currentPage) {
            pagerState.animateScrollToPage(currentPage)
        }
    }
}

@Composable
private fun CloseButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(shape = CircleShape)
            .testTag(FeatureIntroTag.CLOSE_BUTTON)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = AthTheme.colors.dark800
        )
    }
}

@Composable
private fun NewTag() {
    Text(
        text = stringResource(id = R.string.new_tag),
        style = AthTextStyle.Calibre.Utility.Medium.Small,
        color = AthColor.Gray700,
        modifier = Modifier
            .background(
                AthTheme.colors.red,
                shape = RoundedCornerShape(CornerSize(20.dp))
            )
            .padding(vertical = 6.dp, horizontal = 10.dp)
    )
}

@Composable
private fun ImageWithBackground(@DrawableRes drawable: Int) {
    Image(
        painter = painterResource(id = drawable),
        contentDescription = null,
        modifier = Modifier
            .aspectRatio(0.78f)
            .fillMaxSize()
            .background(AthTheme.colors.dark200)
    )
}

@Composable
private fun WhatIsNew(news: FeatureIntroUiModel.Page) {
    Column {
        Text(
            text = stringResource(id = news.title),
            style = AthTextStyle.Slab.Bold.Medium,
            color = AthTheme.colors.dark700
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(news.description),
            style = AthTextStyle.TiemposBody.Regular.Medium,
            color = AthTheme.colors.dark500,
        )
    }
}

@Composable
private fun NextButton(buttonLabel: Int, onNext: () -> Unit) {
    PrimaryButtonLarge(
        onClick = { onNext() },
        text = stringResource(id = buttonLabel),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun FeatureIntroPreview() {
    AthleticTheme(lightMode = false) {
        FeatureIntro(featureIntroPreviewState, currentPage = 0)
    }
}

@Preview
@Composable
private fun FeatureIntroDarkPreview() {
    AthleticTheme(lightMode = true) {
        FeatureIntro(featureIntroPreviewState, currentPage = 0)
    }
}

private val featureIntroPreviewState = FeatureIntroUiModel(
    pageCount = 1,
    pages = listOf(
        FeatureIntroUiModel.Page(
            title = R.string.gifts_success_button_give_another,
            description = R.string.gifts_success_title_thank_you,
            image = R.drawable.bg_onboarding_subscribe,
            buttonLabel = R.string.global_next,
        )
    )
)

object FeatureIntroTag {
    const val CLOSE_BUTTON = "CloseButton"
}