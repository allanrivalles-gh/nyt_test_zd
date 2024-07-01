package com.theathletic.news

import com.squareup.moshi.JsonClass
import com.theathletic.datetime.Datetime
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.type.NewsImportance
import java.io.Serializable

data class News(
    var allowComments: Boolean = true,
    var lockedComments: Boolean = false,
    var commentsCount: Int = 0,
    var content: List<NewsContent>? = null,
    var lede: String? = null,
    var headline: String,
    var following: Boolean,
    var permalink: String,
    var id: String,
    val images: List<NewsImage>,
    var importance: NewsImportance = NewsImportance.standard,
    var smartBrevity: String? = null,
    var status: String,
    var type: String,
    var createdAt: Datetime,
    var updatedAt: Datetime,
    var user: User,
    var bylineHtml: String?
)

interface NewsContent {
    var createdAt: String
    var id: String
    var status: String
    var type: String
    var updatedAt: String
    var user: User
}

data class UserImpl(
    override var id: String,
    override var fullName: String
) : User

data class ArticleAuthor(
    var author: User,
    var displayOrder: Int
)

data class NewsInsight(
    override var createdAt: String,
    override var id: String,
    override var status: String,
    override var type: String,
    override var updatedAt: String,
    override var user: User,
    var audioUrl: String? = null,
    var images: List<NewsImage>? = null,
    var text: String? = null
) : NewsContent

data class NewsBackgroundReading(
    override var createdAt: String,
    override var id: String,
    override var status: String,
    override var type: String,
    override var updatedAt: String,
    override var user: User,
    var text: String?,
    var article: ArticleEntity = ArticleEntity()
) : NewsContent

data class NewsDevelopment(
    override var createdAt: String,
    override var id: String,
    override var status: String,
    override var type: String,
    override var updatedAt: String,
    override var user: User,
    var text: String? = null,
    var tweets: List<String> = ArrayList()
) : NewsContent

data class NewsRelatedArticle(
    override var createdAt: String,
    override var id: String,
    override var status: String,
    override var type: String,
    override var updatedAt: String,
    override var user: User,
    var article: ArticleEntity = ArticleEntity()
) : NewsContent

data class NewsRelatedDiscussion(
    override var createdAt: String,
    override var id: String,
    override var status: String,
    override var type: String,
    override var updatedAt: String,
    override var user: User,
    var discussion: ArticleEntity = ArticleEntity()
) : NewsContent

data class NewsRelatedPodcastEpisode(
    override var createdAt: String,
    override var id: String,
    override var status: String,
    override var type: String,
    override var updatedAt: String,
    override var user: User,
    var podcastEpisode: PodcastEpisodeItem = PodcastEpisodeItem()
) : NewsContent

data class NewsComment(
    var authorId: String,
    var authorName: String,
    var authorUserLevel: Int,
    var avatarUrl: String? = null,
    var comment: String,
    var commentedAt: String,
    var id: String,
    var isFlagged: Boolean = false,
    var isPinned: Boolean = false,
    var likesCount: Int = 0,
    var parentId: String,
    var replies: MutableList<NewsComment>? = null,
    var totalReplies: Int = 0
)

@JsonClass(generateAdapter = true)
data class Staff(
    override var id: String,
    override var fullName: String,
    var avatarUrl: String? = null,
    var description: String? = null,
    var fullDescription: String? = null,
    var leagueId: String? = null,
    var leagueAvatarUri: String? = null,
    var role: String? = null,
    var teamId: String? = null,
    var teamAvatarUri: String? = null,
    var bio: String? = null,
    var firstName: String? = null,
    var lastName: String? = null
) : User, Serializable

@JsonClass(generateAdapter = true)
data class NewsImage(
    var imageHeight: Int? = null,
    var imageWidth: Int? = null,
    var imageUrl: String,
    var thumbnailHeight: Int? = null,
    var thumbnailWidth: Int? = null,
    var thumbnailUrl: String? = null
) : Serializable

interface User {
    var id: String
    var fullName: String
}

enum class Region {
    UK,
    US
}