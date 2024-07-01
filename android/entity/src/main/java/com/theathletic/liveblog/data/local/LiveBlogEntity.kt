package com.theathletic.liveblog.data.local

import com.squareup.moshi.JsonClass
import com.theathletic.datetime.Datetime
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.AthleticEntity

@JsonClass(generateAdapter = true)
data class LiveBlogEntity(
    override val id: String,
    val title: String = "",
    val description: String = "",
    val isLive: Boolean = false,
    val permalink: String = "",
    val contentUrl: String = "",
    val imageUrl: String? = null,
    val lastActivityAt: Datetime = Datetime(0),
    val authorName: String = "",
    val primaryLeague: NativeLiveBlogPrimaryLeague? = null,
    val tags: List<NativeLiveBlogTags> = listOf(),
    val posts: List<LiveBlogPostEntity> = listOf(),
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
    val tweetUrls: List<String> = listOf()
) : AthleticEntity {
    override val type = AthleticEntity.Type.LIVE_BLOG
}

@JsonClass(generateAdapter = true)
data class LiveBlogPostEntity(
    override val id: String,
    val title: String = "",
    val body: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val articles: List<ArticleEntity> = emptyList(),
    val relatedArticleId: String? = null,
    val relatedArticleTitle: String? = null,
    val imageUrl: String? = null,
    val occurredAt: Datetime = Datetime(0),
    val avatarUrl: String = "",
    val authorDescription: String = "",
    val lastActivityAt: Datetime = Datetime(0),
    val tweetUrls: List<String> = listOf()
) : AthleticEntity {
    override val type = AthleticEntity.Type.LIVE_BLOG_POST
}

data class NativeLiveBlog(
    val id: String,
    val title: String = "",
    val description: String = "",
    val gameId: String? = null,
    val isLive: Boolean = false,
    val permalink: String = "",
    val contentUrl: String = "",
    val imageUrl: String? = null,
    val publishedAt: Datetime = Datetime(0),
    val lastActivityAt: Datetime = Datetime(0),
    val authorName: String = "",
    val authorId: String = "",
    val primaryLeague: NativeLiveBlogPrimaryLeague? = null,
    val tags: List<NativeLiveBlogTags> = listOf(),
    val posts: List<NativeLiveBlogPost> = listOf(),
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
    val tweetUrls: List<String> = listOf(),
    val sponsorPresentedBy: NativeLiveBlogSponsorImage? = null,
    val sponsorBanner: NativeLiveBlogSponsorImage? = null,
    val adTargets: NativeLiveBlogAdTargets,
    val adUnitPath: String? = null,
    val adTargeting: Map<String, String?>? = null
)

data class NativeLiveBlogAuthor(
    val id: String,
    val name: String,
    val description: String?,
    val avatarUri: String?
)

interface NativeLiveBlogPost {
    val id: String
}

data class NativeLiveBlogPostBasic(
    override val id: String,
    val title: String = "",
    val body: String = "",
    val author: NativeLiveBlogAuthor? = null,
    val articles: List<ArticleEntity> = emptyList(),
    val relatedArticleId: String? = null,
    val relatedArticleTitle: String? = null,
    val imageUrl: String? = null,
    val occurredAt: Datetime = Datetime(0),
    val publishedAt: Datetime = Datetime(0),
    val updatedAt: Datetime = Datetime(0),
    val tweetUrls: List<String> = listOf()
) : NativeLiveBlogPost

data class NativeLiveBlogPostBanner(
    override val id: String,
    val bannerImage: NativeLiveBlogSponsorImage
) : NativeLiveBlogPost

data class NativeLiveBlogPostSponsored(
    override val id: String,
    val article: ArticleEntity,
    val publishedAt: Datetime = Datetime(0),
    val sponsorPresentedBy: NativeLiveBlogSponsorImage? = null,
) : NativeLiveBlogPost

data class NativeLiveBlogSponsorImage(
    val imageUriLight: String = "",
    val imageUriDark: String = "",
    val label: String? = ""
)

data class NativeLiveBlogDropzone(
    override val id: String,
    val dropzoneId: String,
    val type: String
) : NativeLiveBlogPost

@JsonClass(generateAdapter = true)
data class NativeLiveBlogPrimaryLeague(
    val shortname: String = "",
    val sportType: String = ""
)

@JsonClass(generateAdapter = true)
data class NativeLiveBlogTags(
    val id: String = "",
    val type: String = "",
    val name: String = "",
    val shortname: String = ""
)

@JsonClass(generateAdapter = true)
data class NativeLiveBlogAdTargets(
    val gameTags: List<NativeLiveBlogTags>,
    val newsTopicTags: List<NativeLiveBlogTags>,
    val leagueTags: List<NativeLiveBlogTags>,
    val teamTags: List<NativeLiveBlogTags>
)