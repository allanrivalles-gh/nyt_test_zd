package com.theathletic.liveblog.ui

import android.view.View
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.FillHeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.config.AppConfig
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.DayNightMode
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.HtmlTextView
import com.theathletic.ui.widgets.InfiniteListHandler
import com.theathletic.ui.widgets.RemoteImage
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.TweetView

// The code native Live Blog code will be removed in ATH-26081.
@Deprecated("We won't be supporting native Live Blogs we are using a WebView version now.")
class LiveBlogUi {
    data class LiveBlog(
        val id: String,
        val title: String,
        val publishedAt: ResourceString,
        val authorId: Long,
        val authorName: String,
        val imageUrl: String,
        val description: String,
        val permalink: String,
        val linkForEmbed: String,
        val posts: List<LiveBlogPost>,
        val tweets: List<Tweet>,
        val hasNextPage: Boolean,
        val sponsorPresentedBy: SponsorImage?,
        val sponsorBanner: SponsorImage?,
        val isLive: Boolean,
    )

    interface LiveBlogPost {
        val id: String
    }

    data class LiveBlogPostBasic(
        override val id: String,
        val title: String,
        val description: String,
        val imageUrl: String,
        val publishedAt: ResourceString,
        val authorId: Long,
        val authorName: String,
        val authorDescription: String,
        val avatarUrl: String,
        val relatedArticles: List<RelatedArticle>,
        val tweets: List<Tweet>
    ) : LiveBlogPost

    data class LiveBlogPostBanner(
        override val id: String,
        val sponsorBanner: SponsorImage
    ) : LiveBlogPost

    data class LiveBlogPostSponsored(
        override val id: String,
        val articleId: String,
        val title: String,
        val excerpt: String,
        val imageUrl: String,
        val updatedAt: ResourceString,
        val sponsorPresentedBy: SponsorImage?
    ) : LiveBlogPost

    data class LiveBlogDropzone(
        override val id: String,
        val dropzoneId: String,
        val type: String,
        val ad: View?
    ) : LiveBlogPost

    data class RelatedArticle(
        val id: Long,
        val title: String,
        val imageUrl: String,
        val authorName: String,
        val commentCount: String,
        val showComments: Boolean
    )

    data class Tweet(
        val url: String,
        val htmlContent: String?
    )

    data class SponsorImage(
        val imageUriLight: String,
        val imageUriDark: String,
        val label: String? = null,
    )

    interface Interactor {
        fun onUrlClick(url: String)
        fun onShareClick()
        fun onBackClick()
        fun onTextStyleClick()
        fun onRelatedArticleClick(id: Long, liveBlogPostId: String)
        fun onSponsoredArticleClick(id: String)
        fun onFabClick()
        fun onBlogAuthorClick(authorId: Long, liveBlogId: String)
        fun onPostAuthorClick(authorId: Long, liveBlogPostId: String)
        fun onInitialPostScroll()

        fun trackLiveBlogContent(liveBlogId: String)
        fun trackLiveBlogPostContent(liveBlogPostId: String)
        fun loadMorePosts()
    }

    interface BottomSheetInteractor {
        fun onTextSizeChange(textSize: ContentTextSize)
        fun onDayNightToggle(dayNightMode: DayNightMode)

        fun trackTextSizeChange()
    }
}

@Composable
fun LiveBlogScreen(
    isLoading: Boolean,
    liveBlog: LiveBlogUi.LiveBlog,
    stagedPostsCount: Int,
    initialPostIndex: Int,
    contentTextSize: ContentTextSize,
    listState: LazyListState,
    interactor: LiveBlogUi.Interactor,
    showToolbar: Boolean = true,
) {
    val tweetMap = remember { mutableMapOf<String, WebView>() }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AthTheme.colors.dark200)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AthTheme.colors.dark600,
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AthTheme.colors.dark200)
        ) {
            LiveBlogContentAndPosts(
                liveBlog = liveBlog,
                stagedPostsCount = stagedPostsCount,
                initialPostIndex = initialPostIndex,
                contentTextSize = contentTextSize,
                listState = listState,
                interactor = interactor,
                showToolbar = showToolbar,
                tweetMap = tweetMap
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun BoxScope.LiveBlogContentAndPosts(
    liveBlog: LiveBlogUi.LiveBlog,
    stagedPostsCount: Int,
    initialPostIndex: Int,
    contentTextSize: ContentTextSize,
    listState: LazyListState,
    interactor: LiveBlogUi.Interactor,
    showToolbar: Boolean = true,
    tweetMap: MutableMap<String, WebView>
) {
    LaunchedEffect(initialPostIndex) {
        if (initialPostIndex >= 0 && liveBlog.posts.isNotEmpty()) {
            listState.animateScrollToItem(initialPostIndex)
            interactor.onInitialPostScroll()
        }
    }
    Column {
        if (showToolbar) {
            LiveBlogToolbar(
                onBackClick = interactor::onBackClick,
                onTextStyleClick = interactor::onTextStyleClick,
                onShareClick = interactor::onShareClick
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState
        ) {
            liveBlog.sponsorBanner?.let {
                item(key = "sponsor-banner") {
                    LiveBlogSponsorBanner(
                        sponsorImage = it,
                        height = if (AppConfig.isTablet) 100.dp else 60.dp
                    )
                }
            }
            item(key = liveBlog.id) {
                LiveBlogContent(
                    liveBlog = liveBlog,
                    contentTextSize = contentTextSize,
                    tweetMap = tweetMap,
                    interactor = interactor
                )
            }
            itemsIndexed(
                items = liveBlog.posts,
                key = { _, post -> post.id }
            ) { index, post ->
                var showDivider = true
                if (index > 0) {
                    val previousPost = liveBlog.posts[index - 1]
                    showDivider = previousPost !is LiveBlogUi.LiveBlogDropzone
                }
                LiveBlogPost(
                    post = post,
                    contentTextSize = contentTextSize,
                    tweetMap = tweetMap,
                    interactor = interactor,
                    showDivider = showDivider
                )
            }
        }
        InfiniteListHandler(
            listState = listState,
            onLoadMore = interactor::loadMorePosts
        )
    }
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.TopCenter),
        visible = stagedPostsCount > 0,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        NewPostsFab(
            stagedPostsCount = stagedPostsCount,
            onClick = interactor::onFabClick
        )
    }
}

@Composable
fun LiveBlogPost(
    post: LiveBlogUi.LiveBlogPost,
    contentTextSize: ContentTextSize,
    tweetMap: MutableMap<String, WebView>,
    interactor: LiveBlogUi.Interactor,
    showDivider: Boolean = true
) {
    if (showDivider && post !is LiveBlogUi.LiveBlogDropzone) {
        Divider(
            color = AthTheme.colors.dark100,
            thickness = 6.dp
        )
    }
    when (post) {
        is LiveBlogUi.LiveBlogPostBasic -> {
            LiveBlogPostBasic(
                post = post,
                contentTextSize = contentTextSize,
                tweetMap = tweetMap,
                interactor = interactor
            )
        }
        is LiveBlogUi.LiveBlogPostBanner -> {
            LiveBlogSponsorBanner(
                sponsorImage = post.sponsorBanner,
                height = if (AppConfig.isTablet) 140.dp else 100.dp
            )
        }
        is LiveBlogUi.LiveBlogPostSponsored -> {
            LiveBlogPostSponsored(
                post = post,
                interactor = interactor
            )
        }
    }
}

@Composable
fun LiveBlogContent(
    liveBlog: LiveBlogUi.LiveBlog,
    contentTextSize: ContentTextSize,
    tweetMap: MutableMap<String, WebView>,
    interactor: LiveBlogUi.Interactor
) {
    with(liveBlog) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = spacedBy(16.dp)
        ) {
            LiveBlogContentHeader(
                publishedAt = publishedAt,
                isLive = liveBlog.isLive
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Headline.SemiBold.Medium,
            )
            Byline(
                authorName = authorName,
                authorNameColor = AthTheme.colors.dark500,
                onAuthorClick = { interactor.onBlogAuthorClick(authorId, id) }
            )
            sponsorPresentedBy?.let {
                LiveBlogSponsorPresentedByRow(sponsor = it, 16.dp)
            }
            RemoteImage(
                url = imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.77f)
                    .padding(top = 8.dp),
                contentScale = ContentScale.FillWidth
            )
            HtmlTextView(
                htmlText = description,
                contentTextSize = contentTextSize,
                onUrlClicked = interactor::onUrlClick
            )
        }
        if (tweets.isNotEmpty()) {
            Column(
                verticalArrangement = spacedBy(4.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                tweets.forEachIndexed { index, tweet ->
                    TweetView(
                        tweetHtml = tweet.htmlContent,
                        tweetKey = "${tweet.url}:$id:$index",
                        tweetMap = tweetMap,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveBlogContentHeader(
    publishedAt: ResourceString,
    isLive: Boolean
) {
    Row(
        modifier = Modifier.padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLive) {
            Box(
                Modifier
                    .background(color = AthTheme.colors.red, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    stringResource(id = R.string.feed_live).uppercase(),
                    color = AthColor.Gray800,
                    style = AthTextStyle.Calibre.Utility.Medium.Small
                )
            }
        }
        val leftPadding = if (isLive) 12.dp else 0.dp
        Text(
            text = publishedAt.asString(),
            modifier = Modifier.padding(start = leftPadding),
            color = AthTheme.colors.dark700,
            style = AthTextStyle.Calibre.Utility.Regular.Small
        )
    }
}

@Composable
fun LiveBlogSponsorPresentedByRow(
    sponsor: LiveBlogUi.SponsorImage,
    logoHeight: Dp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        sponsor.label?.let {
            Text(
                text = sponsor.label,
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        RemoteImage(
            url = if (MaterialTheme.colors.isLight) sponsor.imageUriLight else sponsor.imageUriDark,
            contentScale = FillHeight,
            modifier = Modifier
                .height(logoHeight)
                .wrapContentWidth()
        )
    }
}

@Composable
fun Byline(
    avatarUrl: String? = null,
    authorDescription: String = "",
    authorName: String,
    authorNameColor: Color,
    onAuthorClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .clickable { onAuthorClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (avatarUrl?.isNotEmpty() == true) {
            RemoteImage(
                url = avatarUrl,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp),
                circular = true,
                contentScale = ContentScale.Crop
            )
        }
        Text(
            modifier = Modifier.padding(end = 4.dp),
            text = authorName,
            color = authorNameColor,
            style = AthTextStyle.Calibre.Utility.Medium.Small
        )
        if (authorDescription.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(end = 4.dp),
                text = stringResource(id = R.string.global_bullet),
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Regular.Small
            )
            Text(
                text = authorDescription,
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Regular.Small
            )
        }
    }
}

@Composable
fun LiveBlogToolbar(
    onBackClick: () -> Unit,
    onTextStyleClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit),
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(AthTheme.colors.dark200)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        IconButton(
            onClick = onBackClick,
            content = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    tint = AthTheme.colors.dark800,
                    contentDescription = null
                )
            }
        )
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onTextStyleClick != null) {
                IconButton(
                    onClick = { onTextStyleClick() },
                    content = {
                        ResourceIcon(
                            resourceId = R.drawable.ic_text_style,
                            tint = AthTheme.colors.dark800
                        )
                    }
                )
            }
            IconButton(
                onClick = onShareClick,
                content = {
                    Icon(
                        imageVector = Icons.Default.Share,
                        tint = AthTheme.colors.dark800,
                        contentDescription = null,
                    )
                }
            )
        }
    }
}

@Composable
private fun NewPostsFab(
    stagedPostsCount: Int,
    onClick: () -> Unit
) {
    val resources = LocalContext.current.resources
    var postsCount by remember { mutableStateOf(stagedPostsCount) }
    if (stagedPostsCount > 0) {
        postsCount = stagedPostsCount
    }

    FloatingActionButton(
        modifier = Modifier
            .padding(top = 80.dp)
            .height(36.dp),
        onClick = onClick,
        backgroundColor = AthTheme.colors.red,
    ) {
        Row(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResourceIcon(resourceId = R.drawable.ic_arrow_up_light)
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = resources.getQuantityString(R.plurals.plural_new_update, postsCount, postsCount)
            )
        }
    }
}