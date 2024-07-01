package com.theathletic.slidestories.ui

import com.theathletic.slidestories.ui.slidecomponents.SlideCardBlocks

data class SlideStoriesUiModel(
    val id: String,
    val slides: List<Slide>
) {
    interface Slide {
        val id: String
        val slideDuration: Long
        val slideCardBlocks: SlideCardBlocks
    }

    // todo: Just a test slide to allow for pager and support items to be created
    //  This will be replaced with proper slide types in a future PR
    data class TestSlide(
        override val id: String,
        override val slideDuration: Long = DEFAULT_DELAY_BETWEEN_SLIDES,
        override val slideCardBlocks: SlideCardBlocks,
        val title: String,
    ) : Slide

    data class QuoteSlide(
        override val id: String,
        override val slideDuration: Long = DEFAULT_DELAY_BETWEEN_SLIDES,
        override val slideCardBlocks: SlideCardBlocks,
        val quote: String,
        val attributor: String,
        val attributorRole: String? = null,
    ) : Slide

    data class ImageSlide(
        override val id: String,
        override val slideDuration: Long = DEFAULT_DELAY_BETWEEN_SLIDES,
        override val slideCardBlocks: SlideCardBlocks,
        val imageUrl: String,
        val credit: String?
    ) : Slide

    interface Card {
        val id: String
    }

    data class TakeawaySmallMessage(
        override val id: String,
        val text: String
    ) : Card

    data class TakeawayMessage(
        override val id: String,
        val index: String,
        val text: String
    ) : Card

    data class ReadMore(
        override val id: String,
        val title: String,
        val description: String,
        val imageUrl: String,
        val permalink: String,
        val onClick: (String) -> Unit
    ) : Card

    data class Byline(
        override val id: String,
        val byline: String,
        val reportingFrom: String?,
        val authorImageUrls: List<String>,
        val isIntroByline: Boolean
    ) : Card
}