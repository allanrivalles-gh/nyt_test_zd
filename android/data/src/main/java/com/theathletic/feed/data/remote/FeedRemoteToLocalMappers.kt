package com.theathletic.feed.data.remote

import com.theathletic.FeedQuery
import com.theathletic.article.data.remote.toInsiderEntity
import com.theathletic.datetime.Datetime
import com.theathletic.entity.article.TrendingTopicsEntity
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.main.FeedItem
import com.theathletic.entity.main.FeedItemAction
import com.theathletic.entity.main.FeedItemEntryType
import com.theathletic.entity.main.FeedItemStyle
import com.theathletic.entity.main.FeedItemType
import com.theathletic.entity.main.FeedResponse
import com.theathletic.entity.remote.toEntity
import com.theathletic.feed.data.local.AnnouncementEntity
import com.theathletic.fragment.Announcement
import com.theathletic.fragment.Consumable
import com.theathletic.fragment.FeedPodcast
import com.theathletic.fragment.LiveBlog
import com.theathletic.fragment.NewsHeadline
import com.theathletic.fragment.PodcastEpisode
import com.theathletic.fragment.Topic
import com.theathletic.headline.data.local.HeadlineEntity
import com.theathletic.liveblog.data.local.LiveBlogEntity
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.local.PodcastSeriesEntity
import com.theathletic.rooms.remote.toEntity
import com.theathletic.type.LayoutType
import com.theathletic.utility.safeLet
import timber.log.Timber

fun FeedQuery.Data.toLocalModel(
    feedId: String,
    page: Int
): FeedResponse {
    return FeedResponse(
        feedId = feedId,
        feed = feed.layouts.mapIndexedNotNull { pageIndex, feedItem ->
            try {
                feedItem?.toLocalModel(
                    feedId = feedId,
                    adUnitPath = feed.ad_unit_path,
                    pageIndex = pageIndex,
                    page = page,
                    hasNextPage = feed.pageInfo.hasNextPage
                )
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }.toMutableList()
    )
}

@Suppress("LongMethod", "ComplexMethod")
private fun FeedQuery.Layout.toLocalModel(
    feedId: String,
    adUnitPath: String?,
    pageIndex: Int,
    page: Int,
    hasNextPage: Boolean
): FeedItem? {

    asBasicGroupConsumableLayout?.let { layout ->
        val feedStyle = type.toFeedItemStyle() ?: return null

        val curatedFields = CuratedFields()
        val entities = layout.contents.flatMap { it.fragments.consumable.toEntities(curatedFields) }
        val structuredEntities = layout.contents.map { it.fragments.consumable.toStructuredEntities(curatedFields) }
        if (entities.isEmpty() && structuredEntities.isEmpty()) return null

        val action = safeLet(
            layout.feedAction?.raw_string,
            layout.feedAction?.app_linked_string
        ) { text, deeplink ->
            FeedItemAction(text, deeplink)
        }

        return feedItem(
            feedId = feedId,
            adUnitPath = adUnitPath,
            id = layout.id,
            style = feedStyle,
            position = pageIndex.toLong(),
            page = page,
            entities = entities,
            structuredEntities = structuredEntities,
            title = layout.title?.app_text,
            titleImageUrl = layout.tag?.image_url,
            description = layout.description?.app_text,
            action = action,
            container = container_type,
            hasNextPage = hasNextPage,
            curatedTitles = curatedFields.titles,
            curatedDescriptions = curatedFields.descriptions,
            curatedImageUrls = curatedFields.imageUrls,
            curatedDisplayOrder = curatedFields.displayOrder
        )
    }

    asSingleConsumableLayout?.let { layout ->
        val feedStyle = type.toFeedItemStyle() ?: return null
        val curatedFields = CuratedFields()

        val entities = layout.content.fragments.consumable.toEntities(curatedFields)
        return feedItem(
            feedId = feedId,
            adUnitPath = adUnitPath,
            id = layout.id,
            style = feedStyle,
            position = pageIndex.toLong(),
            page = page,
            entities = entities,
            title = layout.title?.app_text,
            hasNextPage = hasNextPage,
            container = container_type,
            curatedTitles = curatedFields.titles,
            curatedDescriptions = curatedFields.descriptions,
            curatedImageUrls = curatedFields.imageUrls,
            curatedDisplayOrder = curatedFields.displayOrder
        )
    }
    asDropzonePlaceholderLayout?.let { layout ->
        val feedStyle = type.toFeedItemStyle() ?: return null
        return feedItem(
            feedId = feedId,
            adUnitPath = adUnitPath,
            id = layout.dropzone_id,
            style = feedStyle,
            position = pageIndex.toLong(),
            page = page,
            hasNextPage = hasNextPage,
            container = container_type,
            entities = emptyList()
        )
    }

    return null
}

fun LayoutType.toFeedItemStyle() = when (this) {
    LayoutType.four_content,
    LayoutType.three_content,
    LayoutType.highlight_three_content -> FeedItemStyle.FEED_THREE_FOUR_CONTENT
    LayoutType.most_popular_articles -> FeedItemStyle.FRONTPAGE_MOST_POPULAR_ARTICLES
    LayoutType.insiders -> FeedItemStyle.FRONTPAGE_INSIDERS_CAROUSEL
    LayoutType.single_headline -> FeedItemStyle.HEADLINE
    LayoutType.headlines_list -> FeedItemStyle.HEADLINE_LIST
    LayoutType.recommended_podcasts -> FeedItemStyle.CAROUSEL_RECOMMENDED_PODCASTS
    LayoutType.announcement -> FeedItemStyle.IPM_ANNOUNCEMENT
    LayoutType.podcast_episodes_list -> FeedItemStyle.PODCAST_EPISODE
    LayoutType.single_content -> FeedItemStyle.ARTICLE
    LayoutType.topic -> FeedItemStyle.CAROUSEL_TOPICS
    LayoutType.one_hero_curation -> FeedItemStyle.ONE_HERO
    LayoutType.two_hero_curation -> FeedItemStyle.TWO_HERO
    LayoutType.three_hero_curation -> FeedItemStyle.THREE_HERO
    LayoutType.four_hero_curation -> FeedItemStyle.FOUR_HERO
    LayoutType.five_hero_curation -> FeedItemStyle.FIVE_HERO
    LayoutType.six_hero_curation -> FeedItemStyle.SIX_HERO
    LayoutType.seven_plus_hero_curation -> FeedItemStyle.SEVEN_PLUS_HERO
    LayoutType.one_content -> FeedItemStyle.TOPPER
    LayoutType.scores -> FeedItemStyle.CAROUSEL_SCORES
    LayoutType.live_blogs -> FeedItemStyle.CAROUSEL_LIVE_BLOGS
    LayoutType.four_gallery_curation,
    LayoutType.five_gallery_curation -> FeedItemStyle.FOUR_FIVE_GALLERY
    LayoutType.six_plus_gallery_curation -> FeedItemStyle.SIX_PLUS_GALLERY
    LayoutType.spotlight -> FeedItemStyle.SPOTLIGHT
    LayoutType.spotlight_carousel -> FeedItemStyle.SPOTLIGHT
    LayoutType.curated_content_list -> FeedItemStyle.HEADLINE_LIST
    LayoutType.one_content_curated -> FeedItemStyle.TOPPER
    LayoutType.three_content_curated -> FeedItemStyle.FEED_THREE_FOUR_CONTENT
    LayoutType.four_content_curated -> FeedItemStyle.FEED_THREE_FOUR_CONTENT
    LayoutType.more_for_you -> FeedItemStyle.FEED_THREE_FOUR_CONTENT
    LayoutType.live_room -> FeedItemStyle.LIVE_ROOM
    LayoutType.dropzone -> FeedItemStyle.DROPZONE
    else -> null
}

fun PodcastEpisode.toEntity(): PodcastEpisodeEntity {
    return PodcastEpisodeEntity(
        id = id,
        seriesId = podcast_id,
        seriesTitle = series_title.orEmpty(),
        title = title,
        description = description,
        duration = duration.toLong(),
        mp3Url = mp3_uri,
        permalinkUrl = permalink,
        imageUrl = image_uri.orEmpty(),
        publishedAt = Datetime(published_at)
    )
}

private fun LiveBlog.toEntity() = LiveBlogEntity(
    id = id,
    title = title,
    isLive = liveStatus == "live",
    permalink = permalink,
    imageUrl = images.firstOrNull()?.image_uri,
    lastActivityAt = Datetime(lastActivityAt)
)

fun Topic.toEntity(): TrendingTopicsEntity {
    return TrendingTopicsEntity(
        id = id,
        articleCount = articles_count?.toString().orEmpty(),
        name = title,
        imageUrl = image_url
    )
}

private fun FeedPodcast.toEntity(): PodcastSeriesEntity {
    return PodcastSeriesEntity(
        id = id,
        title = title,
        imageUrl = image_url.orEmpty()
    )
}

private fun Announcement.toEntity(): AnnouncementEntity {
    return AnnouncementEntity(
        id = id,
        title = title,
        subtitle = excerpt,
        ctaText = cta_text.orEmpty(),
        deeplinkUrl = deeplink_url.orEmpty(),
        imageUrl = image_url.orEmpty()
    )
}

fun NewsHeadline.toEntity(): HeadlineEntity {
    return HeadlineEntity(
        id = id,
        imageUrls = images.map { it.fragments.newsImage.image_uri },
        headline = headline,
        byline = byline_linkable?.raw_string.orEmpty(),
        commentsCount = comment_count,
        createdAt = Datetime(created_at),
        updatedAt = Datetime(last_activity_at),
        commentsDisabled = disable_comments
    )
}

fun Consumable.toStructuredEntities(
    curatedFields: CuratedFields
): List<AthleticEntity> {
    val fragments = consumable?.fragments ?: return emptyList()

    safeLet(
        fragments.insider?.staff_author?.fragments?.staff,
        fragments.insider?.post?.fragments?.article
    ) { staff, article ->
        return listOf(article.toEntity(FeedItemEntryType.ARTICLE), staff.toInsiderEntity())
    }

    fragments.spotlight?.let { spotlight ->
        spotlight.article?.fragments?.article?.let { article ->
            val entities = mutableListOf<AthleticEntity>(
                article.toEntity(FeedItemEntryType.ARTICLE, spotlight.spotlight_scheduled_at).also {
                    curatedFields.titles[it.entityId] = title
                    curatedFields.descriptions[it.entityId] = description
                }
            )
            entities.addAll(
                article.authors.sortedBy { it.display_order }
                    .mapNotNull { it.author.fragments.user.asStaff?.fragments?.staff?.toInsiderEntity() }
            )
            return entities
        }
    }

    return emptyList()
}

@Suppress("ReturnCount")
fun Consumable.toEntities(
    curatedFields: CuratedFields
): List<AthleticEntity> {
    val fragments = consumable?.fragments ?: return emptyList()

    fragments.feedArticleLite?.let {
        return listOf(
            it.toEntity().also { entity ->
                curatedFields.titles[entity.entityId] = title
                curatedFields.descriptions[entity.entityId] = description
            }
        )
    }

    fragments.discussion?.let {
        return listOf(
            it.toEntity().also { entity ->
                curatedFields.titles[entity.entityId] = title
                curatedFields.descriptions[entity.entityId] = description
                curatedFields.imageUrls[entity.entityId] = it.image_uri
            }
        )
    }

    fragments.qanda?.let {
        return listOf(
            it.toEntity().also { entity ->
                curatedFields.titles[entity.entityId] = title
                curatedFields.descriptions[entity.entityId] = description
                curatedFields.imageUrls[entity.entityId] = it.image_uri
            }
        )
    }

    fragments.newsHeadline?.let {
        return listOf(
            it.toEntity().also { entity ->
                curatedFields.titles[entity.entityId] = title
            }
        )
    }

    fragments.announcement?.let {
        return listOf(
            it.toEntity()
        )
    }

    fragments.feedPodcast?.let {
        return listOf(
            it.toEntity()
        )
    }

    fragments.topic?.let {
        return listOf(
            it.toEntity()
        )
    }

    fragments.podcastEpisode?.let {
        return listOf(
            it.toEntity().also { entity ->
                curatedFields.titles[entity.entityId] = title
                curatedFields.descriptions[entity.entityId] = description
            }
        )
    }

    fragments.feedGame?.let {
        return listOfNotNull(
            it.toEntity().also { entity ->
                curatedFields.displayOrder[entity.entityId] = it.index.toShort()
            }
        )
    }

    fragments.liveBlog?.let {
        return listOf(
            it.toEntity().also { entity ->
                curatedFields.titles[entity.entityId] = title
                curatedFields.descriptions[entity.entityId] = description
            }
        )
    }

    fragments.liveRoomFragment?.let {
        return listOf(
            it.toEntity()
        )
    }

    return emptyList()
}

@Suppress("LongParameterList")
private fun feedItem(
    feedId: String,
    adUnitPath: String?,
    id: String,
    style: FeedItemStyle,
    position: Long,
    page: Int,
    entities: List<AthleticEntity>,
    structuredEntities: List<List<AthleticEntity>> = emptyList(),
    title: String? = null,
    titleImageUrl: String? = null,
    description: String? = null,
    action: FeedItemAction? = null,
    hasNextPage: Boolean = true,
    container: String? = null,
    curatedTitles: MutableMap<AthleticEntity.Id, String?> = mutableMapOf(),
    curatedDescriptions: MutableMap<AthleticEntity.Id, String?> = mutableMapOf(),
    curatedImageUrls: MutableMap<AthleticEntity.Id, String?> = mutableMapOf(),
    curatedDisplayOrder: MutableMap<AthleticEntity.Id, Short?> = mutableMapOf()
): FeedItem {
    return FeedItem().also {
        it.feedId = feedId
        it.adUnitPath = adUnitPath
        it.id = id
        it.pageIndex = position
        it.page = page
        it.itemType = FeedItemType.ROW
        it.style = style
        it.title = title.orEmpty()
        it.titleImageUrl = titleImageUrl.orEmpty()
        it.description = description.orEmpty()
        it.entities = entities
        it.compoundEntities = structuredEntities
        it.action = action
        it.hasNextPage = hasNextPage
        it.container = container
        it.entityCuratedTitles = curatedTitles
        it.entityCuratedDescriptions = curatedDescriptions
        it.entityCuratedImageUrls = curatedImageUrls
        it.entityCuratedDisplayOrder = curatedDisplayOrder
    }
}

data class CuratedFields(
    val titles: MutableMap<AthleticEntity.Id, String?> = mutableMapOf(),
    val descriptions: MutableMap<AthleticEntity.Id, String?> = mutableMapOf(),
    val imageUrls: MutableMap<AthleticEntity.Id, String?> = mutableMapOf(),
    val displayOrder: MutableMap<AthleticEntity.Id, Short?> = mutableMapOf()
)