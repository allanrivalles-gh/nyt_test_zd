package com.theathletic.slidestories.ui

import com.theathletic.slidestories.ui.slidecomponents.SlideCardBlocks

/**
 * (TODO) Adil: Remove this once we have actual data wired
 */
object SlideStoriesTestSlides {
    val slides = listOf(
        SlideStoriesFixtures.slideImage,
        SlideStoriesFixtures.slideQuoteLarge,
        SlideStoriesFixtures.slideQuoteSmall,
        SlideStoriesUiModel.TestSlide(
            id = "Slide1",
            title = "Slide One",
            slideCardBlocks = SlideCardBlocks(takeawayMessage = SlideStoriesFixtures.takeawayMessage)
        ),
        SlideStoriesUiModel.TestSlide(
            id = "Slide2",
            title = "Slide Two",
            slideCardBlocks = SlideCardBlocks(takeawayMessage = SlideStoriesFixtures.takeawayMessage)
        ),
        SlideStoriesUiModel.TestSlide(
            id = "Slide3",
            title = "Slide Three",
            slideCardBlocks = SlideCardBlocks(takeawayMessage = SlideStoriesFixtures.takeawayMessage)
        ),
        SlideStoriesUiModel.TestSlide(
            id = "Slide4",
            slideDuration = 10000L,
            title = "Slide Four - 10 sec duration",
            slideCardBlocks = SlideCardBlocks(takeawayMessage = SlideStoriesFixtures.takeawayMessage)
        ),
        SlideStoriesUiModel.TestSlide(
            id = "Slide5",
            title = "Slide Five",
            slideCardBlocks = SlideCardBlocks(takeawayMessage = SlideStoriesFixtures.takeawayMessage)
        ),
        SlideStoriesUiModel.TestSlide(
            id = "Slide6",
            title = "Slide Six",
            slideCardBlocks = SlideCardBlocks(takeawayMessage = SlideStoriesFixtures.takeawayMessage)
        ),
    )
}