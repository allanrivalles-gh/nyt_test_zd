package com.theathletic.ui.list

import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.lruCache
import androidx.core.view.doOnNextLayout
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theathletic.R
import com.theathletic.ads.ui.AdWrapperUiModel
import com.theathletic.ads.ui.databinding.ListItemAdWrapperBinding
import com.theathletic.analytics.impressions.ImpressionVisibilityListener
import com.theathletic.analytics.impressions.ViewVisibilityTracker
import com.theathletic.databinding.ListItemFeedCarouselBinding
import com.theathletic.databinding.ListItemFeedFourHeroCarouselBinding
import com.theathletic.databinding.ListItemFeedHeroCarouselBinding
import com.theathletic.databinding.ListItemFeedHeroCarouselV2Binding
import com.theathletic.databinding.ListItemFeedLiveBlogCarouselBinding
import com.theathletic.databinding.ListItemFeedMostPopularCarouselBinding
import com.theathletic.databinding.ListItemFeedRecommendedPodcastsGridBinding
import com.theathletic.databinding.ListItemFeedScoresCarouselBinding
import com.theathletic.databinding.ListItemFeedSideBySideCarouselBinding
import com.theathletic.databinding.ListItemFeedSideBySideLeftItemsBinding
import com.theathletic.databinding.ListItemFeedThreeFourContentCarouselBinding
import com.theathletic.databinding.ListItemFeedTopperModuleV2Binding
import com.theathletic.databinding.ListItemMissingBinding
import com.theathletic.feed.ui.FeedContract
import com.theathletic.feed.ui.FeedGameComposeCarousel
import com.theathletic.feed.ui.FeedHeroComposeCarousel
import com.theathletic.feed.ui.models.BasicSectionHeader
import com.theathletic.feed.ui.models.FeedAnnouncement
import com.theathletic.feed.ui.models.FeedAuthorHeader
import com.theathletic.feed.ui.models.FeedCarouselModel
import com.theathletic.feed.ui.models.FeedCuratedCarouselItem
import com.theathletic.feed.ui.models.FeedCuratedGroupedItem
import com.theathletic.feed.ui.models.FeedCuratedTopperHero
import com.theathletic.feed.ui.models.FeedEndOfFeed
import com.theathletic.feed.ui.models.FeedFourItemHeroCarousel
import com.theathletic.feed.ui.models.FeedHeadlineListItem
import com.theathletic.feed.ui.models.FeedHeroCarousel
import com.theathletic.feed.ui.models.FeedHeroItem
import com.theathletic.feed.ui.models.FeedHeroTabletItem
import com.theathletic.feed.ui.models.FeedInsiderItem
import com.theathletic.feed.ui.models.FeedLeftImageItem
import com.theathletic.feed.ui.models.FeedLiveBlogCarousel
import com.theathletic.feed.ui.models.FeedLoadingMore
import com.theathletic.feed.ui.models.FeedMostPopularArticle
import com.theathletic.feed.ui.models.FeedMostPopularCarousel
import com.theathletic.feed.ui.models.FeedPodcastEpisodeGrouped
import com.theathletic.feed.ui.models.FeedScoresCarousel
import com.theathletic.feed.ui.models.FeedScoresCarouselItem
import com.theathletic.feed.ui.models.FeedSeeAllButton
import com.theathletic.feed.ui.models.FeedSideBySideCarousel
import com.theathletic.feed.ui.models.FeedSideBySideItem
import com.theathletic.feed.ui.models.FeedSideBySideLeftItemCarousel
import com.theathletic.feed.ui.models.FeedSingleHeadlineItem
import com.theathletic.feed.ui.models.FeedSpotlightModel
import com.theathletic.feed.ui.models.FeedThreeFourContentCarousel
import com.theathletic.feed.ui.models.FeedTopperGroupedItem
import com.theathletic.feed.ui.models.FeedTopperModule
import com.theathletic.feed.ui.models.LiveBlogCarouselItem
import com.theathletic.feed.ui.models.LiveRoomUiModel
import com.theathletic.feed.ui.models.RecommendedPodcastSeriesGridItem
import com.theathletic.feed.ui.models.RecommendedPodcastsGrid
import com.theathletic.feed.ui.models.SectionHeaderWithDescription
import com.theathletic.frontpage.ui.trendingtopics.TrendingTopicGridItem
import com.theathletic.liveblog.ui.FeedLiveBlogCarousel
import com.theathletic.presenter.Interactor
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.CarouselUiModel
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.UiModel

@Suppress("LongParameterList")
class AthleticFeedAdapter(
    lifecycleOwner: LifecycleOwner,
    interactor: Interactor,
    private val displayPreferences: DisplayPreferences,
    private val onPostBindAtPositionListener: OnPostBindAtPositionListener? = null,
    private val impressionTracker: ViewVisibilityTracker? = null,
    private val impressionListener: ImpressionVisibilityListener? = null
) : BindingDiffAdapter(lifecycleOwner, interactor) {

    companion object {
        val TOPICS_PILL_CAROUSEL_TYPE = R.layout.grid_item_trending_topic
        val FEED_INSIDERS_TYPE = R.layout.carousel_item_feed_insider
        val FEED_LIVE_ROOM_CAROUSEL_TYPE = R.layout.carousel_item_live_audio_room_wrapper
        val FEED_SPOTLIGHT_TYPE = R.layout.list_item_feed_spotlight_v2
    }

    private val scrollPositionCache = lruCache<String, Int>(10)
    private val listStateCache = lruCache<String, LazyListState>(10)

    @Suppress("LongMethod")
    override fun getLayoutForModel(model: UiModel) = model.layoutId()

    override fun viewTypeOverride(model: UiModel): Int? {
        if (model !is CarouselUiModel) return null

        val uiModel = model.carouselItemModels.firstOrNull() ?: return null

        return when (uiModel) {
            // Feed Carousel Types
            is FeedSpotlightModel -> FEED_SPOTLIGHT_TYPE
            is TrendingTopicGridItem -> TOPICS_PILL_CAROUSEL_TYPE
            is FeedInsiderItem -> FEED_INSIDERS_TYPE
            is LiveRoomUiModel -> FEED_LIVE_ROOM_CAROUSEL_TYPE
            else -> null
        }
    }

    override fun layoutForViewType(viewType: Int) = when (viewType) {
        // Feed Carousel Types
        FEED_INSIDERS_TYPE,
        FEED_SPOTLIGHT_TYPE,
        TOPICS_PILL_CAROUSEL_TYPE,
        FEED_LIVE_ROOM_CAROUSEL_TYPE -> R.layout.list_item_feed_carousel
        else -> viewType
    }

    @Suppress("LongMethod")
    override fun onPostBind(
        uiModel: UiModel,
        holder: DataBindingViewHolder<ViewDataBinding>
    ) {
        if (holder.binding is ListItemMissingBinding) return

        when (uiModel) {
            is FeedCarouselModel -> setupFeedCarouselAdapter(
                (holder.binding as ListItemFeedCarouselBinding).carousel,
                uiModel
            )
            is FeedMostPopularCarousel -> setupFeedCarouselAdapter(
                (holder.binding as ListItemFeedMostPopularCarouselBinding).recyclerView,
                uiModel
            )
            is FeedScoresCarousel -> {
                (holder.binding as? ListItemFeedScoresCarouselBinding)?.composeView?.setContent {
                    val listState = listStateCache.getOrPut(uiModel.stableId) {
                        LazyListState(firstVisibleItemIndex = uiModel.firstVisibleIndex.toInt())
                    }
                    AthleticTheme(lightMode = displayPreferences.shouldDisplayDayMode(LocalContext.current)) {
                        FeedGameComposeCarousel(
                            uiModels = uiModel.carouselItemModels,
                            interactor = view as FeedContract.Presenter,
                            listState = listState
                        )
                    }
                }
            }
            is FeedLiveBlogCarousel -> createLiveBlogCarouselView(holder, uiModel)
            is FeedHeroCarousel -> {
                (holder.binding as ListItemFeedHeroCarouselV2Binding).composeView.setContent {
                    AthleticTheme(lightMode = displayPreferences.shouldDisplayDayMode(LocalContext.current)) {
                        FeedHeroComposeCarousel(
                            uiModels = uiModel.carouselItemModels,
                            interactor = view as FeedContract.Presenter
                        )
                    }
                }
            }
            is FeedSideBySideCarousel -> setupFeedCarouselAdapter(
                (holder.binding as ListItemFeedSideBySideCarouselBinding).recyclerView,
                uiModel
            )
            is RecommendedPodcastsGrid -> setupFeedCarouselAdapter(
                (holder.binding as ListItemFeedRecommendedPodcastsGridBinding).recyclerView,
                uiModel
            )
            is FeedTopperModule -> setupFeedCarouselAdapter(
                (holder.binding as ListItemFeedTopperModuleV2Binding).headlines, uiModel.headlines
            )
            is FeedThreeFourContentCarousel -> setupFeedCarouselAdapter(
                (holder.binding as ListItemFeedThreeFourContentCarouselBinding).recyclerView,
                uiModel
            )
            is FeedFourItemHeroCarousel -> setupFeedCarouselAdapter(
                (holder.binding as ListItemFeedFourHeroCarouselBinding).recyclerView,
                uiModel
            )
            is FeedSideBySideLeftItemCarousel -> setupFeedCarouselAdapter(
                (holder.binding as ListItemFeedSideBySideLeftItemsBinding).recyclerView,
                uiModel
            )
            is AdWrapperUiModel -> {
                val adHolder = holder.binding as? ListItemAdWrapperBinding ?: return
                uiModel.adView?.let {
                    it.setLightMode(displayPreferences.shouldDisplayDayMode(adHolder.root.context))
                    adHolder.adPlaceholder.removeAllViews()
                    if (it.view.parent != null) {
                        (it.view.parent as? ViewGroup)?.removeAllViews()
                    }
                    adHolder.adPlaceholder.addView(it.view)
                    it.resume()
                }
            }
        }

        uiModel.trackImpressions(
            holder.binding.root,
            impressionTracker,
            impressionListener
        )

        onPostBindAtPositionListener?.onPostBindAtPosition(holder.adapterPosition, itemCount)
    }

    override fun onViewRecycled(holder: DataBindingViewHolder<ViewDataBinding>) {
        super.onViewRecycled(holder)
        impressionTracker?.unregisterView(holder.binding.root)

        (holder.binding as? ListItemFeedHeroCarouselBinding)?.let { binding ->
            val id = binding.data?.stableId ?: return@let
            val scrollPos = (binding.recyclerView.layoutManager as? GridLayoutManager)
                ?.findFirstCompletelyVisibleItemPosition()
            scrollPositionCache.put(id, scrollPos)
        }
    }

    private fun setupFeedCarouselAdapter(
        recyclerView: RecyclerView,
        carouselModel: CarouselUiModel
    ) {
        recyclerView.bindData(carouselModel.carouselItemModels) {
            AthleticFeedCarouselAdapter(
                lifecycleOwner,
                view, impressionTracker,
                impressionListener
            )
        }
    }

    private fun createLiveBlogCarouselView(
        holder: DataBindingViewHolder<ViewDataBinding>,
        uiModel: FeedLiveBlogCarousel
    ) {
        (holder.binding as? ListItemFeedLiveBlogCarouselBinding)?.composeView?.setContent {
            val items = uiModel.carouselItemModels.filterIsInstance<LiveBlogCarouselItem>()
            FeedLiveBlogCarousel(items, view as FeedContract.Presenter)
        }
    }

    interface OnPostBindAtPositionListener {
        fun onPostBindAtPosition(indexBound: Int, listSize: Int)
        fun onLoadMore()
    }
}

private class AthleticFeedCarouselAdapter(
    lifecycleOwner: LifecycleOwner,
    interactor: Interactor,
    private val impressionTracker: ViewVisibilityTracker? = null,
    private val impressionListener: ImpressionVisibilityListener? = null
) : CarouselBindingAdapter(lifecycleOwner, interactor) {

    override fun getLayoutForModel(model: UiModel) = model.carouselLayoutId()

    override fun onPostBind(uiModel: UiModel, holder: DataBindingViewHolder<ViewDataBinding>) {
        super.onPostBind(uiModel, holder)
        uiModel.trackImpressions(
            holder.binding.root,
            impressionTracker,
            impressionListener
        )

        when (uiModel) {
            is FeedMostPopularArticle -> setMostPopularItemWidth(uiModel, holder)
        }
    }

    override fun onViewRecycled(holder: DataBindingViewHolder<ViewDataBinding>) {
        super.onViewRecycled(holder)
        impressionTracker?.unregisterView(holder.binding.root)
    }

    private fun setMostPopularItemWidth(
        model: FeedMostPopularArticle,
        holder: DataBindingViewHolder<ViewDataBinding>
    ) {
        if (model.isTablet) return
        with(holder.itemView) {
            doOnNextLayout {
                if (!model.isInLastColumn) {
                    val margin = resources.getDimensionPixelSize(R.dimen.feed_most_popular_margin)
                    layoutParams.width = width - margin
                } else {
                    layoutParams.width = RecyclerView.LayoutParams.MATCH_PARENT
                }
            }
        }
    }
}

fun UiModel.trackImpressions(
    view: View,
    impressionTracker: ViewVisibilityTracker? = null,
    impressionListener: ImpressionVisibilityListener? = null
) {
    val payload = impressionPayload ?: return

    impressionTracker?.registerView(view) { visiblePct ->
        impressionListener?.onViewVisibilityChanged(
            payload,
            visiblePct
        )
            ?: throw IllegalStateException("ImpressionTracker provided but no ImpressionVisibilityListener")
    }
}

private inline fun <K, V> LruCache<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    val cacheHit = get(key)
    return if (cacheHit == null) {
        val cacheAdd = defaultValue()
        put(key, cacheAdd)
        cacheAdd
    } else {
        cacheHit
    }
}

@Suppress("LongMethod")
fun UiModel.layoutId() = when (this) {
    // Core
    ListRoot -> R.layout.list_root
    ListLoadingItem -> R.layout.list_loading
    DefaultEmptyUiModel -> R.layout.list_item_empty_default
    is ListVerticalPadding -> R.layout.list_padding_vertical
    is BasicRowItem.Text -> R.layout.list_item_basic_row
    is BasicRowItem.LeftDrawableUri -> R.layout.list_item_basic_row_uri_drawable

    // Feed
    is FeedAnnouncement -> R.layout.list_item_feed_announcement
    is FeedCuratedTopperHero -> R.layout.list_item_feed_topper_hero_v2
    is FeedCuratedGroupedItem -> R.layout.list_item_feed_curated_group_item_v2
    is FeedTopperGroupedItem -> R.layout.list_item_feed_topper_grouped_item
    is BasicSectionHeader -> R.layout.list_item_basic_section_header
    is FeedPodcastEpisodeGrouped -> R.layout.list_item_feed_podcast_episode_v2
    is FeedSingleHeadlineItem -> R.layout.list_item_feed_single_headline
    is FeedHeadlineListItem -> R.layout.list_item_headline_list_item_v2
    is FeedMostPopularCarousel -> R.layout.list_item_feed_most_popular_carousel
    is FeedHeroCarousel -> R.layout.list_item_feed_hero_carousel_v2
    is FeedSideBySideCarousel -> R.layout.list_item_feed_side_by_side_carousel
    is FeedScoresCarousel -> R.layout.list_item_feed_scores_carousel
    is FeedLiveBlogCarousel -> R.layout.list_item_feed_live_blog_carousel
    is SectionHeaderWithDescription -> R.layout.list_item_section_header_with_description
    is FeedSpotlightModel -> R.layout.list_item_feed_spotlight
    is FeedLoadingMore -> R.layout.list_item_feed_loading_more
    is RecommendedPodcastsGrid -> R.layout.list_item_feed_recommended_podcasts_grid
    is FeedSeeAllButton -> R.layout.list_item_feed_see_all
    is FeedLeftImageItem -> R.layout.list_item_feed_left_image
    is FeedHeroItem -> R.layout.list_item_feed_hero_item_v2
    is FeedAuthorHeader -> R.layout.fragment_author_detail_header
    is LiveRoomUiModel -> R.layout.layout_live_audio_room
    is FeedEndOfFeed -> R.layout.list_item_end_of_feed
    is FeedTopperModule -> R.layout.list_item_feed_topper_module_v2
    is FeedThreeFourContentCarousel -> R.layout.list_item_feed_three_four_content_carousel
    is FeedHeroTabletItem -> R.layout.list_item_feed_hero_tablet_item_v2
    is FeedFourItemHeroCarousel -> R.layout.list_item_feed_four_hero_carousel
    is FeedSideBySideLeftItemCarousel -> R.layout.list_item_feed_side_by_side_left_items
    is FeedMostPopularArticle -> R.layout.list_item_feed_most_popular_article_v2

    // Utility
    is FeedItemDivider -> R.layout.list_item_feed_divider
    is FeedItemVerticalPadding -> R.layout.list_item_feed_padding_vertical

    is AdWrapperUiModel -> com.theathletic.ads.R.layout.list_item_ad_wrapper

    else -> R.layout.list_item_missing
}

@Suppress("LongMethod")
fun UiModel.carouselLayoutId() = when (this) {
    // Feed carousel items
    is TrendingTopicGridItem -> R.layout.grid_item_trending_topic
    is FeedInsiderItem -> R.layout.carousel_item_feed_insider
    // todo (Mark): Remove after refactoring done plus rename below to remove GQL
    is FeedScoresCarouselItem -> R.layout.list_item_feed_scores
    is FeedMostPopularArticle -> R.layout.list_item_feed_most_popular_article_v2
    is FeedLeftImageItem -> R.layout.list_item_feed_left_image_v2
    is FeedSideBySideItem -> R.layout.list_item_feed_hero_side_by_side_item_v2
    is RecommendedPodcastSeriesGridItem -> R.layout.list_item_recommended_podcast_row_item
    is LiveRoomUiModel -> R.layout.carousel_item_live_audio_room_wrapper
    is FeedHeadlineListItem -> R.layout.list_item_headline_list_item_v2
    is BasicSectionHeader -> R.layout.list_item_basic_section_header
    is ListVerticalPadding -> R.layout.list_padding_vertical
    is FeedItemVerticalPadding -> R.layout.list_item_feed_padding_vertical
    is FeedHeroItem -> R.layout.list_item_feed_hero_item_v2
    is FeedCuratedGroupedItem -> R.layout.list_item_feed_curated_group_item_v2
    is FeedSpotlightModel -> R.layout.list_item_feed_spotlight_v2
    is FeedCuratedCarouselItem -> R.layout.list_item_feed_curated_carousel_item

    else -> throw IllegalArgumentException("Does not support ${this::class}")
}