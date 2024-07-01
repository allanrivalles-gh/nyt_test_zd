package com.theathletic.feed.compose.ui.items.featuredgame

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.feed.compose.data.PostType
import com.theathletic.feed.compose.ui.analyticsPreviewData
import com.theathletic.feed.compose.ui.interaction.ItemInteractor
import com.theathletic.feed.compose.ui.interaction.interactive
import com.theathletic.feed.compose.ui.items.imageitem.ArticleFooter
import com.theathletic.feed.compose.ui.items.imageitem.ItemImage
import com.theathletic.feed.compose.ui.items.imageitem.getImage
import com.theathletic.feed.compose.ui.items.imageitem.heroAspect
import com.theathletic.feed.compose.ui.reusables.ArticleTitle
import com.theathletic.feed.compose.ui.reusables.ArticleUiModel
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.preview.DevicePreviewSmallAndLarge

@Composable
fun RelatedGameArticleItem(
    uiModel: ArticleUiModel,
    itemInteractor: ItemInteractor
) {
    Column(
        modifier = Modifier
            .interactive(uiModel, itemInteractor)
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
            .interactive(uiModel, itemInteractor)
    ) {
        Divider(
            color = AthTheme.colors.dark300,
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 16.dp)
        )
        RelatedArticleLeftImageHero(uiModel)
    }
}

@Composable
fun RelatedArticleLeftImageHero(uiModel: ArticleUiModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .height(IntrinsicSize.Max)
            .padding(top = 16.dp, bottom = 20.dp)
            .padding(horizontal = 16.dp)
    ) {
        ItemImage(image = uiModel.getImage(), isRead = uiModel.isRead, modifier = Modifier.heroAspect())
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f, fill = true)
                .padding(start = 14.dp)
                .fillMaxHeight()
        ) {
            ArticleTitle(
                text = uiModel.title,
                maxLines = 3,
                style = AthTextStyle.TiemposHeadline.Regular.ExtraExtraSmall.copy(fontSize = 14.sp),
                isRead = uiModel.isRead
            )
            ArticleFooter(uiModel)
        }
    }
}

@DayNightPreview
@DevicePreviewSmallAndLarge
@Composable
private fun FeaturedGameArticleLayoutPreview(
    @PreviewParameter(FeaturedGameArticleLayoutPreviewProvider::class) gameArticle: ArticleUiModel
) {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        RelatedGameArticleItem(
            uiModel = gameArticle,
            itemInteractor = ItemInteractor()
        )
    }
}

internal class FeaturedGameArticleLayoutPreviewProvider : PreviewParameterProvider<ArticleUiModel> {
    override val values: Sequence<ArticleUiModel> = sequenceOf(
        relatedArticlePreviewData()
    )

    private fun relatedArticlePreviewData(): ArticleUiModel = ArticleUiModel(
        id = "articleId1",
        title = "Jets Week 8 storylines and prediction: Can Zach Wilson keep pace with the Ravens?",
        excerpt = "",
        imageUrl = "",
        byline = "The Athletic NFL Staff",
        commentCount = "",
        isBookmarked = false,
        isRead = false,
        postType = PostType.DISCUSSION,
        permalink = "",
        analyticsData = analyticsPreviewData()
    )
}