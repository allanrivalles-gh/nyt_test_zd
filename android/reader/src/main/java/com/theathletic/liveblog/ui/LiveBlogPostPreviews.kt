package com.theathletic.liveblog.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.theathletic.themes.AthleticTheme

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, name = "Sponsored Post Light")
private fun SponsoredPost_LightPreview() {
    AthleticTheme(lightMode = true) {
        LiveBlogPostSponsored(
            post = LiveBlogPreviewData.LiveBlogPostSponsored,
            interactor = LiveBlogPreviewData.Interactor
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFF000000, showBackground = true, name = "Sponsored Post")
private fun SponsoredPost_Preview() {
    LiveBlogPostSponsored(
        post = LiveBlogPreviewData.LiveBlogPostSponsored,
        interactor = LiveBlogPreviewData.Interactor
    )
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, name = "Related Article Light")
private fun RelatedArticle_LightPreview() {
    AthleticTheme(lightMode = true) {
        RelatedArticle(
            relatedArticle = LiveBlogPreviewData.RelatedArticle,
            liveBlogPostId = "id",
            onRelatedArticleClick = emptyArticleClick
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFF000000, showBackground = true, name = "Related Article")
private fun RelatedArticle_Preview() {
    RelatedArticle(
        relatedArticle = LiveBlogPreviewData.RelatedArticle,
        liveBlogPostId = "id",
        onRelatedArticleClick = emptyArticleClick
    )
}

private val emptyArticleClick: (Long, String) -> Unit = { a, b -> }