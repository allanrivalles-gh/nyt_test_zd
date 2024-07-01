package com.theathletic.liveblog.ui

import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.HtmlTextView
import com.theathletic.ui.widgets.RemoteImage
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.TweetView

@Composable
fun LiveBlogPostSponsored(
    post: LiveBlogUi.LiveBlogPostSponsored,
    interactor: LiveBlogUi.Interactor
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { interactor.onSponsoredArticleClick(post.articleId) }
            .padding(16.dp),
        verticalArrangement = spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = post.title,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Headline.SemiBold.Medium
        )
        RemoteImage(
            url = post.imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.33f),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = post.excerpt,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.LiveBlog.SponsoredPostExcerpt
        )
        post.sponsorPresentedBy?.let { LiveBlogSponsorPresentedByRow(it, 20.dp) }
    }
}

@Composable
fun LiveBlogPostBasic(
    post: LiveBlogUi.LiveBlogPostBasic,
    contentTextSize: ContentTextSize,
    tweetMap: MutableMap<String, WebView>,
    interactor: LiveBlogUi.Interactor
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        verticalArrangement = spacedBy(16.dp)
    ) {
        Header(
            title = post.title,
            publishedAt = post.publishedAt
        )
        if (post.imageUrl.isNotEmpty()) {
            RemoteImage(
                url = post.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            )
        }
        HtmlTextView(
            htmlText = post.description,
            contentTextSize = contentTextSize
        ) { url ->
            interactor.onUrlClick(url)
            interactor.trackLiveBlogPostContent(post.id)
        }
        Byline(
            avatarUrl = post.avatarUrl,
            authorName = post.authorName,
            authorNameColor = AthTheme.colors.dark700,
            authorDescription = post.authorDescription,
            onAuthorClick = { interactor.onPostAuthorClick(post.authorId, post.id) }
        )
    }
    Column {
        if (post.relatedArticles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
        }
        post.relatedArticles.forEach {
            RelatedArticle(
                relatedArticle = it,
                liveBlogPostId = post.id,
                onRelatedArticleClick = interactor::onRelatedArticleClick
            )
        }
    }
    if (post.tweets.isNotEmpty()) {
        Column(
            verticalArrangement = spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            post.tweets.forEachIndexed { index, tweet ->
                TweetView(
                    tweetHtml = tweet.htmlContent,
                    tweetKey = "${tweet.url}:${post.id}:$index",
                    tweetMap = tweetMap,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun Header(
    title: String,
    publishedAt: ResourceString
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        UpdatedAtLabel(publishedAt = publishedAt)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = title,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            fontSize = 24.sp
        )
    }
}

@Composable
private fun UpdatedAtLabel(publishedAt: ResourceString) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(end = 4.dp),
            text = stringResource(id = R.string.global_bullet),
            color = AthTheme.colors.red,
            style = AthTextStyle.Calibre.Utility.Medium.Small
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = publishedAt.asString(),
            color = AthTheme.colors.red,
            style = AthTextStyle.Calibre.Utility.Medium.Small
        )
    }
}

@Composable
fun RelatedArticle(
    relatedArticle: LiveBlogUi.RelatedArticle,
    liveBlogPostId: String,
    onRelatedArticleClick: (Long, String) -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onRelatedArticleClick(relatedArticle.id, liveBlogPostId) }
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RemoteImage(
                url = relatedArticle.imageUrl,
                modifier = Modifier
                    .size(82.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = relatedArticle.title,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = AthTheme.colors.dark700,
                    style = AthTextStyle.TiemposBody.Medium.Small
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = relatedArticle.authorName,
                        color = AthTheme.colors.dark500,
                        style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall
                    )
                    if (relatedArticle.showComments) {
                        ResourceIcon(
                            resourceId = R.drawable.ic_news_comment,
                            tint = AthTheme.colors.dark500,
                            modifier = Modifier
                                .padding(start = 10.dp, end = 4.dp, bottom = 2.dp)
                                .align(Alignment.Bottom)
                                .size(8.dp)
                        )
                        Text(
                            text = relatedArticle.commentCount,
                            color = AthTheme.colors.dark500,
                            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun LiveBlogSponsorBanner(
    sponsorImage: LiveBlogUi.SponsorImage,
    height: Dp
) {
    with(sponsorImage) {
        RemoteImage(
            url = if (MaterialTheme.colors.isLight) imageUriLight else imageUriDark,
            modifier = Modifier
                .height(height)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
}