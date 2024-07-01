package com.theathletic.liveblog.data.remote

import com.theathletic.datetime.Datetime
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.fragment.LiveBlogAuthor
import com.theathletic.fragment.LiveBlogDropzone
import com.theathletic.fragment.LiveBlogFragment
import com.theathletic.fragment.LiveBlogLinksFragment
import com.theathletic.fragment.LiveBlogPostArticle
import com.theathletic.fragment.LiveBlogPostFragment
import com.theathletic.fragment.LiveBlogPostInlineBanner
import com.theathletic.fragment.LiveBlogPostSponsored
import com.theathletic.fragment.LiveBlogSponsorPresentedBy
import com.theathletic.liveblog.data.local.LiveBlogLinks
import com.theathletic.liveblog.data.local.NativeLiveBlog
import com.theathletic.liveblog.data.local.NativeLiveBlogAdTargets
import com.theathletic.liveblog.data.local.NativeLiveBlogAuthor
import com.theathletic.liveblog.data.local.NativeLiveBlogDropzone
import com.theathletic.liveblog.data.local.NativeLiveBlogPost
import com.theathletic.liveblog.data.local.NativeLiveBlogPostBanner
import com.theathletic.liveblog.data.local.NativeLiveBlogPostBasic
import com.theathletic.liveblog.data.local.NativeLiveBlogPostSponsored
import com.theathletic.liveblog.data.local.NativeLiveBlogPrimaryLeague
import com.theathletic.liveblog.data.local.NativeLiveBlogSponsorImage
import com.theathletic.liveblog.data.local.NativeLiveBlogTags
import com.theathletic.type.LiveStatus

fun LiveBlogFragment.toLocal(): NativeLiveBlog {
    return NativeLiveBlog(
        id = id,
        title = title,
        description = description.orEmpty(),
        gameId = game_id,
        isLive = liveStatus == LiveStatus.live,
        permalink = permalink,
        contentUrl = permalinkForEmbed,
        publishedAt = publishedAt?.let { Datetime(it) } ?: Datetime(0),
        lastActivityAt = Datetime(lastActivityAt),
        imageUrl = images.firstOrNull()?.image_uri,
        authorName = byline_linkable?.raw_string.orEmpty(),
        authorId = byline_authors.firstOrNull()?.id.orEmpty(),
        primaryLeague = primaryLeague?.toLocal(),
        tags = tags.map { it.toLocal() },
        posts = posts.items.mapNotNull { it?.toLocal() },
        currentPage = posts.pageInfo.currentPage,
        hasNextPage = posts.pageInfo.hasNextPage,
        tweetUrls = tweets,
        sponsorPresentedBy = sponsor?.presented_by?.fragments?.liveBlogSponsorPresentedBy?.toLocal(),
        sponsorBanner = sponsor?.toBanner(),
        adTargets = tags.toAdTargets(),
        adUnitPath = ad_unit_path,
        adTargeting = ad_targeting_params?.toAdTargeting()
    )
}

private fun LiveBlogSponsorPresentedBy.toLocal() = NativeLiveBlogSponsorImage(
    imageUriLight = image.image_uri,
    imageUriDark = image.dark_image_uri ?: image.image_uri,
    label = label
)

private fun LiveBlogAuthor.toLocal() = NativeLiveBlogAuthor(
    id = id,
    name = name,
    description = description,
    avatarUri = avatar_uri
)

private fun LiveBlogFragment.Sponsor.toBanner(): NativeLiveBlogSponsorImage? {
    cobranded_header?.mobile_image?.let {
        return NativeLiveBlogSponsorImage(
            imageUriLight = it.image_uri,
            imageUriDark = it.dark_image_uri ?: it.image_uri
        )
    }
    return null
}

private fun LiveBlogFragment.Item.toLocal(): NativeLiveBlogPost? {
    this.fragments.liveBlogPostFragment?.let { return it.toLocal() }
    this.fragments.liveBlogPostInlineBanner?.let { return it.toLocal() }
    this.fragments.liveBlogPostSponsored?.let { return it.toLocal() }
    this.fragments.liveBlogDropzone?.let { return it.toLocal() }
    return null
}

fun LiveBlogPostInlineBanner.toLocal(): NativeLiveBlogPost {
    return NativeLiveBlogPostBanner(
        id = id,
        bannerImage = NativeLiveBlogSponsorImage(
            imageUriLight = mobile_image.image_uri,
            imageUriDark = mobile_image.dark_image_uri ?: mobile_image.image_uri
        )
    )
}

fun LiveBlogPostFragment.toLocal(): NativeLiveBlogPost {
    return NativeLiveBlogPostBasic(
        id = id,
        title = title,
        body = body,
        publishedAt = publishedAt?.let { Datetime(it) } ?: Datetime(0),
        updatedAt = Datetime(updatedAt),
        author = author.fragments.liveBlogAuthor?.toLocal(),
        imageUrl = images.firstOrNull()?.image_uri,
        articles = articles?.mapNotNull { it?.fragments?.liveBlogPostArticle?.toEntity() }.orEmpty(),
        tweetUrls = tweets
    )
}

fun LiveBlogPostSponsored.toLocal(): NativeLiveBlogPostSponsored {
    return NativeLiveBlogPostSponsored(
        id = id,
        article = article.fragments.liveBlogPostArticle.toEntity(),
        publishedAt = published_at?.let { Datetime(it) } ?: Datetime(0),
        sponsorPresentedBy = presented_by?.fragments?.liveBlogSponsorPresentedBy?.toLocal()
    )
}

fun LiveBlogDropzone.toLocal() = NativeLiveBlogDropzone(
    id = id,
    dropzoneId = dropzone_id,
    type = type
)

fun LiveBlogFragment.PrimaryLeague.toLocal() = NativeLiveBlogPrimaryLeague(
    shortname = this.shortname.orEmpty(),
    sportType = this.sport_type.orEmpty()
)

fun LiveBlogFragment.Tag.toLocal() = NativeLiveBlogTags(
    id = this.id,
    type = this.type.orEmpty(),
    name = this.name.orEmpty(),
    shortname = this.shortname
)

fun List<LiveBlogFragment.Tag>.toAdTargets(): NativeLiveBlogAdTargets {
    val gameTags = arrayListOf<NativeLiveBlogTags>()
    val leagueTags = arrayListOf<NativeLiveBlogTags>()
    val teamTags = arrayListOf<NativeLiveBlogTags>()
    val newsTopicTags = arrayListOf<NativeLiveBlogTags>()
    forEach { tag ->
        when (tag.type) {
            "game" -> gameTags.add(tag.toLocal())
            "league" -> leagueTags.add(tag.toLocal())
            "team" -> teamTags.add(tag.toLocal())
            "topic" -> newsTopicTags.add(tag.toLocal())
        }
    }
    return NativeLiveBlogAdTargets(
        newsTopicTags = newsTopicTags,
        gameTags = gameTags,
        leagueTags = leagueTags,
        teamTags = teamTags
    )
}

fun LiveBlogFragment.Ad_targeting_params.toAdTargeting(): Map<String, String?> = mapOf(
    "auth" to this.auth,
    "byline" to this.byline,
    "coll" to this.coll,
    "id" to this.id,
    "keywords" to this.keywords,
    "org" to this.org,
    "tags" to this.tags
)

fun LiveBlogLinksFragment.toLocal() = LiveBlogLinks(
    id = id,
    permalink = permalink,
    linkForEmbed = permalinkForEmbed,
)

private fun LiveBlogPostArticle.toEntity(): ArticleEntity {
    return ArticleEntity(
        articleId = id.toLong(),
        articleTitle = title,
        authorName = author.name,
        articleHeaderImg = image_uri,
        commentsCount = comment_count.toLong(),
        excerpt = excerpt
    )
}