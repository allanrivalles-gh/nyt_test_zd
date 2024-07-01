package com.theathletic.feed.compose

import com.theathletic.datetime.Datetime
import com.theathletic.feed.compose.data.Article
import com.theathletic.feed.compose.data.Author
import com.theathletic.feed.compose.data.Feed
import com.theathletic.feed.compose.data.FeedFilter
import com.theathletic.feed.compose.data.FeedRequest
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.data.Headline
import com.theathletic.feed.compose.data.Layout
import com.theathletic.feed.compose.data.LiveBlog
import com.theathletic.feed.compose.data.POST_ID_ARTICLE
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.data.layout
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.links.deep.Deeplink

internal fun feedFixture(
    id: String = "feedId",
    layouts: List<Layout> = emptyList(),
    pageInfo: Feed.PageInfo = Feed.PageInfo(currentPage = 0, hasNextPage = false),
) = Feed(
    id = id,
    layouts = layouts,
    pageInfo = pageInfo,
)

internal fun layoutFixture(
    id: String = "layoutId",
    title: String = "Layout Title",
    icon: String = "icon",
    action: String = "See All",
    deepLink: String = "deepLink",
    type: Layout.Type = Layout.Type.ONE_CONTENT_CURATED,
    index: Int = 0,
    items: List<Layout.Item> = emptyList(),
) = layout(id, title, icon, action, deepLink, type, index, items)

internal fun articleFixture(
    id: String = "123",
    title: String = "Article Title",
    excerpt: String = "Article Excerpt",
    image: String = "",
    author: Author = Author("First name", "Last Name"),
    commentCount: Int = 0,
    isRead: Boolean = false,
    isSaved: Boolean = false,
    startedAt: Datetime = Datetime(0),
    endedAt: Datetime = Datetime(0),
    postTypeId: String = POST_ID_ARTICLE
) = Article(
    id = id,
    title = title,
    excerpt = excerpt,
    image = image,
    author = author,
    commentCount = commentCount,
    isBookmarked = isSaved,
    isRead = isRead,
    startedAt = startedAt,
    endedAt = endedAt,
    permalink = "",
    postTypeId = postTypeId,
    publishedAt = Datetime(0)
)

internal fun headlineFixture(
    id: String = "headlineId",
    title: String = "Headline Title",
    image: String = "",
    permalink: String = ""
) = Headline(
    id = id,
    title = title,
    image = image,
    permalink = permalink
)

internal fun liveBlogFixture(
    id: String = "liveBlogId",
    title: String = "Live Blog Title",
    description: String = "Live Blog Descriptiopn",
    image: String = "",
    isLive: Boolean = false
) = LiveBlog(
    id = id,
    title = title,
    description = description,
    image = image,
    isLive = isLive,
    permalink = "",
    lastActivity = Datetime(0),
)

internal fun feedRequestFixture(
    feedType: FeedType = FeedType.DISCOVER,
    feedId: Int? = null,
    feedUrl: String? = null,
    filter: FeedFilter? = null,
    locale: String? = null
) = FeedRequest(
    feedType = feedType,
    feedId = feedId,
    feedUrl = feedUrl,
    filter = filter,
    locale = locale,
)

fun articleUiModelFixture(
    permalink: String = "https://staging2.theathletic.com/4905643/2023/09/27/robert-saleh-zach-wilson-trevor-siemian-jets/",
    id: String = "4905643",
    title: String = "Jets’ Robert Saleh: ‘We all acknowledge’ Zach Wilson has to play better",
    excerpt: String = "In three games, Wilson threw four interceptions to just two touchdowns.",
    byline: String = "The Athletic Staff",
    commentCount: String = "2",
    isBookmarked: Boolean = false,
    isRead: Boolean = false,
    imageUrl: String = "https://cdn.theathletic.com/app/uploads/2023/09/27125012/GettyImages-1427279509-1024x683.jpg",
) = ArticleUiModel(
    permalink = permalink,
    id = id,
    title = title,
    excerpt = excerpt,
    byline = byline,
    commentCount = commentCount,
    isBookmarked = isBookmarked,
    isRead = isRead,
    imageUrl = imageUrl,
    postType = PostType.ARTICLE,
    analyticsData = analyticsPreviewData()
)

fun discussionUiModelFixture(
    id: String = "4905376",
    title: String = "Submit a question for our Twins playoff mailbag",
    permalink: String = "https://staging2.theathletic.com/4905376/2023/09/27/twins-playoff-mailbag/",
    excerpt: String = "Got a question about the Twins as they finish out the season and get set for their wild-card matchup? Submit it below and Aaron Gleeman could answer it in his mailbag on Friday.",
    imageUrl: String = "https://theathletic.com/app/themes/athletic/assets/img/article-discussions-feed.png",
    byline: String = "Aaron Gleeman",
    commentCount: String = "42",
) = ArticleUiModel(
    id = id,
    title = title,
    permalink = permalink,
    excerpt = excerpt,
    imageUrl = imageUrl,
    byline = byline,
    commentCount = commentCount,
    isBookmarked = false,
    isRead = false,
    postType = PostType.DISCUSSION,
    analyticsData = analyticsPreviewData()
)

fun qandaUiModelFixture(
    id: String = "4889381",
    title: String = "Fantasy Premier League Q&A: FPL GW6 advice from Ben Dinnery",
    permalink: String = "https://staging2.theathletic.com/4889381/2023/09/22/fantasy-premier-league-qa-fpl-gw6-advice-from-ben-dinnery/",
    excerpt: String = "On the eve of Gameweek 6, send your questions to Ben to get all the best FPL tips for this weekend…",
    imageUrl: String = "https://theathletic.com/app/themes/athletic/assets/img/article-live-qa-feed.png",
    byline: String = "Ben Dinnery",
    commentCount: String = "115",
    postType: PostType = PostType.Q_AND_A_LIVE
): ArticleUiModel {
    return ArticleUiModel(
        id = id,
        title = title,
        permalink = permalink,
        excerpt = excerpt,
        imageUrl = imageUrl,
        byline = byline,
        commentCount = commentCount,
        isBookmarked = false,
        isRead = false,
        postType = postType,
        analyticsData = analyticsPreviewData()
    )
}

fun itemFixture(
    id: String = "itemId",
    permalink: String? = null
) = object : LayoutUiModel.Item {
    override val id: String = id
    override val permalink: String? = permalink
    override val analyticsData: AnalyticsData? = null
    override fun deepLink() = Deeplink("")
}