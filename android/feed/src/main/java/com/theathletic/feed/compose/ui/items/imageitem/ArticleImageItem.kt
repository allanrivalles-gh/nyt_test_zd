package com.theathletic.feed.compose.ui.items.imageitem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.ui.components.ArticlePreviewProvider
import com.theathletic.feed.compose.ui.components.articlePreviewData
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.feed.compose.ui.reusables.FeedItemFooter
import com.theathletic.feed.compose.ui.reusables.FeedItemFooterUiModel
import com.theathletic.feed.compose.ui.reusables.getImage
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

@Composable
fun ArticleTopImage(uiModel: ArticleUiModel, itemInteractor: ItemInteractor) {
    val image = uiModel.getImage()

    TopImageItem(
        image = { ItemImage(image = image, isRead = uiModel.isRead, modifier = Modifier.carouselAspect()) },
        title = { TopImageItemTitle(title = uiModel.title) },
        footer = { ArticleFooter(uiModel) },
        modifier = Modifier.interactive(uiModel, itemInteractor)
    )
}

@Composable
internal fun ArticleRightImageForYou(uiModel: ArticleUiModel, itemInteractor: ItemInteractor) {
    ArticleRightImage(
        uiModel = uiModel,
        maxLines = 4,
        itemInteractor = itemInteractor,
        modifier = Modifier.forYouAspect()
    )
}

@Composable
internal fun ArticleRightImageHero(uiModel: ArticleUiModel, itemInteractor: ItemInteractor) {
    ArticleRightImage(
        uiModel = uiModel,
        maxLines = 3,
        itemInteractor = itemInteractor,
        modifier = Modifier.heroAspect()
    )
}

@Composable
fun ArticleRightImage(
    uiModel: ArticleUiModel,
    maxLines: Int = 3,
    itemInteractor: ItemInteractor,
    modifier: Modifier
) {
    val image = uiModel.getImage()

    RightImageItem(
        title = { HorizontalImageItemTitle(uiModel.title, uiModel.isRead, maxLines = maxLines) },
        footer = { ArticleFooter(uiModel) },
        image = { ItemImage(image = image, isRead = uiModel.isRead, modifier = modifier) },
        modifier = Modifier.interactive(uiModel, itemInteractor)
    )
}

@Composable
internal fun ArticleLeftImageHero(uiModel: ArticleUiModel, itemInteractor: ItemInteractor) {
    ArticleLeftImage(
        uiModel = uiModel,
        maxLines = 3,
        itemInteractor = itemInteractor,
        modifier = Modifier.heroAspect()
    )
}

@Composable
fun ArticleLeftImage(
    uiModel: ArticleUiModel,
    maxLines: Int = 3,
    itemInteractor: ItemInteractor,
    modifier: Modifier
) {
    val image = uiModel.getImage()

    LeftImageItem(
        title = { HorizontalImageItemTitle(uiModel.title, uiModel.isRead, maxLines = maxLines) },
        footer = { ArticleFooter(uiModel) },
        image = { ItemImage(image = image, isRead = uiModel.isRead, modifier = modifier) },
        modifier = Modifier.interactive(uiModel, itemInteractor)
    )
}

@Composable
fun ArticleUiModel.getImage() = getImage(postType = postType, imageUrl = imageUrl)

@Composable
fun ArticleFooter(uiModel: ArticleUiModel) {
    val footerUiModel = FeedItemFooterUiModel(
        isBookmarked = uiModel.isBookmarked,
        byline = uiModel.byline,
        commentCount = uiModel.commentCount
    )
    FeedItemFooter(data = footerUiModel)
}

@DayNightPreview
@Composable
private fun ArticleTopImagePreview(@PreviewParameter(ArticlePreviewProvider::class) article: ArticleUiModel) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        ArticleTopImage(article, ItemInteractor())
    }
}

@DayNightPreview
@Composable
private fun ArticleRightImageHeroPreview(@PreviewParameter(ArticlePreviewProvider::class) article: ArticleUiModel) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        ArticleRightImageHero(article, ItemInteractor())
    }
}

@DayNightPreview
@Composable
private fun ArticleRightImageForYouPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        ArticleRightImageForYou(articlePreviewData(postType = PostType.DISCUSSION), ItemInteractor())
    }
}