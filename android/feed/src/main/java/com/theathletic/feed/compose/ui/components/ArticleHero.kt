package com.theathletic.feed.compose.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.feed.compose.ui.reusables.FeedItemFooter
import com.theathletic.feed.compose.ui.reusables.FeedItemFooterUiModel
import com.theathletic.feed.compose.ui.reusables.getImage
import com.theathletic.themes.AthleticTheme

@Composable
internal fun ArticleHero(
    uiModel: ArticleUiModel,
    itemInteractor: ItemInteractor,
    modifier: Modifier
) {
    val image = getImage(postType = uiModel.postType, imageUrl = uiModel.imageUrl)

    Hero(
        image = { HeroImage(image = image, isRead = uiModel.isRead, modifier = modifier) },
        title = { HeroTitle(title = uiModel.title, uiModel.isRead) },
        footer = { HeroArticleFooter(uiModel = uiModel) },
        Modifier.interactive(uiModel, itemInteractor)
    )
}

@Composable
internal fun HeroArticleFooter(uiModel: ArticleUiModel) {
    FeedItemFooter(
        data = FeedItemFooterUiModel(uiModel.isBookmarked, uiModel.byline, uiModel.commentCount)
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ArticleHeroPreview(@PreviewParameter(ArticlePreviewProvider::class) article: ArticleUiModel) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        ArticleHero(
            uiModel = article,
            itemInteractor = ItemInteractor(),
            modifier = Modifier.heroAspect()
        )
    }
}

internal class ArticlePreviewProvider : PreviewParameterProvider<ArticleUiModel> {
    override val values: Sequence<ArticleUiModel> = sequenceOf(
        articlePreviewData(postType = PostType.Q_AND_A_LIVE, isRead = true),
        articlePreviewData(postType = PostType.Q_AND_A_UPCOMING, isBookmarked = true),
        articlePreviewData(postType = PostType.DISCUSSION),
    )
}

fun articlePreviewData(
    postType: PostType = PostType.ARTICLE,
    isBookmarked: Boolean = false,
    isRead: Boolean = false,
) = ArticleUiModel(
    id = "",
    title = "Ranking the winners, losers and snoozers of the 2023 MLB trade deadline",
    excerpt = "The trade deadline went pretty well for the Angels, Rangers, Cardinals and others. In New York? Well, not so much.",
    imageUrl = "",
    byline = "The Athletic Staff",
    commentCount = "35",
    isBookmarked = isBookmarked,
    isRead = isRead,
    permalink = "",
    postType = postType,
    analyticsData = analyticsPreviewData()
)