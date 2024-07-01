package com.theathletic.feed.compose

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.device.IsTabletProvider
import com.theathletic.feed.compose.data.A1Layout
import com.theathletic.feed.compose.data.Article
import com.theathletic.feed.compose.data.DropzoneLayout
import com.theathletic.feed.compose.data.FeaturedGameLayout
import com.theathletic.feed.compose.data.FeedRepository
import com.theathletic.feed.compose.data.FeedRequest
import com.theathletic.feed.compose.data.HeadlineLayout
import com.theathletic.feed.compose.data.HeroCarouselLayout
import com.theathletic.feed.compose.data.HeroListLayout
import com.theathletic.feed.compose.data.Layout
import com.theathletic.feed.compose.data.ListLayout
import com.theathletic.feed.compose.data.MostPopularLayout
import com.theathletic.feed.compose.data.MyPodcastLayout
import com.theathletic.feed.compose.data.ScoresCarouselLayout
import com.theathletic.feed.compose.data.TopperHeroLayout
import com.theathletic.repository.user.IUserDataRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import timber.log.Timber

internal class ObserveFeedUseCase @AutoKoin constructor(
    private val feedRepository: FeedRepository,
    private val userDataRepository: IUserDataRepository,
    private val isTabletProvider: IsTabletProvider
) {

    operator fun invoke(feedRequest: FeedRequest) = combine(
        feedRepository.observeFeed(feedRequest),
        userDataRepository.userDataFlow
    ) { feed, _ ->
        val layouts = feed.layouts.mergeForYouLayouts()
            .parseTopperLayouts()
            .updateMetadata(userDataRepository)

        feed.copy(layouts = layouts)
    }
        .catch { Timber.e(it) }

    private fun List<Layout>.updateMetadata(userDataRepository: IUserDataRepository): List<Layout> = map { layout ->
        val updatedLayout = layout.updateMetadata(userDataRepository)

        when (updatedLayout.type) {
            Layout.Type.ONE_CONTENT_CURATED -> updatedLayout
            Layout.Type.TWO_CONTENT_CURATED -> HeroListLayout(updatedLayout)

            Layout.Type.TOPPER -> TopperHeroLayout(updatedLayout)
            Layout.Type.A1 -> A1Layout(updatedLayout)

            Layout.Type.FOR_YOU,
            Layout.Type.THREE_HERO_CURATION,
            Layout.Type.HIGHLIGHT_THREE_CONTENT -> ListLayout(updatedLayout)

            Layout.Type.FOUR_HERO_CURATION,
            Layout.Type.FIVE_HERO_CURATION,
            Layout.Type.SIX_HERO_CURATION,
            Layout.Type.SEVEN_PLUS_HERO_CURATION -> HeroCarouselLayout(updatedLayout)

            Layout.Type.FOUR_CONTENT_CURATED -> ListLayout(updatedLayout)
            Layout.Type.HEADLINE -> HeadlineLayout(updatedLayout.copy(items = updatedLayout.items.take(8)))
            Layout.Type.MOST_POPULAR -> MostPopularLayout(updatedLayout)
            Layout.Type.MY_PODCASTS -> MyPodcastLayout(updatedLayout)

            Layout.Type.FEATURE_GAME -> FeaturedGameLayout(updatedLayout)
            Layout.Type.SCORES -> ScoresCarouselLayout(updatedLayout)
            Layout.Type.DROPZONE -> DropzoneLayout(updatedLayout)
        }
    }

    private fun Layout.updateMetadata(userData: IUserDataRepository): Layout {
        val items = items.map { item ->
            when (item) {
                is Article -> item.copy(
                    isRead = userData.isItemRead(item.id.toLong()),
                    isBookmarked = userData.isItemBookmarked(item.id.toLong())
                )
                else -> item
            }
        }

        return copy(items = items)
    }

    private fun List<Layout>.mergeForYouLayouts(): List<Layout> {
        if (size < 2) return this
        val mergedLayouts = mutableListOf<Layout>()

        var previous = get(0)
        for (nextIndex in 1..lastIndex) {
            val next = get(nextIndex)

            if (previous.isForYou && next.isForYou) {
                previous = previous.copy(items = previous.items + next.items)
            } else {
                mergedLayouts.add(previous)
                previous = next
            }
            if (nextIndex == lastIndex) mergedLayouts.add(previous)
        }
        return mergedLayouts
    }

    private fun List<Layout>.parseTopperLayouts(): List<Layout> {
        if (size < 2 || isTabletProvider.isTablet) return this
        val parsedLayouts = mutableListOf<Layout>()

        var index = 0
        while (index in indices) {
            val nextIndex = if (isIndexInBound(index + 1)) index + 1 else index

            val previous = get(index++)
            val next = get(nextIndex)

            val layout = if (previous.isOneContent && next.isFourContent) {
                index++
                previous.copy(type = Layout.Type.TOPPER, items = previous.items + next.items)
            } else {
                previous
            }
            parsedLayouts.add(layout)
        }
        return parsedLayouts
    }

    private fun List<Layout>.isIndexInBound(index: Int) = index in 0..lastIndex
    private val Layout.isForYou: Boolean get() = type == Layout.Type.FOR_YOU
    private val Layout.isOneContent: Boolean get() = type == Layout.Type.ONE_CONTENT_CURATED
    private val Layout.isFourContent: Boolean get() = type == Layout.Type.FOUR_CONTENT_CURATED
}