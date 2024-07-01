package com.theathletic.liveblog.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme

@Composable
@Preview(backgroundColor = 0xFF000000, showBackground = true, name = "Live Blog")
private fun LiveBlogScreen_Preview() {
    LiveBlogScreen(
        isLoading = false,
        liveBlog = LiveBlogPreviewData.LiveBlog,
        stagedPostsCount = LiveBlogPreviewData.liveBlogPostsCount,
        initialPostIndex = -1,
        contentTextSize = LiveBlogPreviewData.contentTextSize,
        listState = LiveBlogPreviewData.listState,
        interactor = LiveBlogPreviewData.Interactor
    )
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, name = "Live Blog (Light Mode)")
private fun LiveBlogScreen_LightPreview() {
    AthleticTheme(lightMode = true) {
        LiveBlogScreen(
            isLoading = false,
            liveBlog = LiveBlogPreviewData.LiveBlog,
            stagedPostsCount = LiveBlogPreviewData.liveBlogPostsCount,
            initialPostIndex = -1,
            contentTextSize = LiveBlogPreviewData.contentTextSize,
            listState = LiveBlogPreviewData.listState,
            interactor = LiveBlogPreviewData.Interactor,
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFF000000, showBackground = true, name = "Theme selection (Dark Mode)")
private fun LiveBlogTextStyleBottomSheet_Preview() {
    LiveBlogTextSettingsBottomSheet(
        dayNightMode = LiveBlogPreviewData.dayNightMode,
        displaySystemThemeButton = LiveBlogPreviewData.displaySystemThemeButton,
        contentTextSize = LiveBlogPreviewData.contentTextSize,
        interactor = LiveBlogPreviewData.BottomSheetInteractor
    )
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, name = "Theme selection (Light Mode)")
private fun LiveBlogTextStyleBottomSheet_LightPreview() {
    AthleticTheme(lightMode = true) {
        LiveBlogTextSettingsBottomSheet(
            dayNightMode = LiveBlogPreviewData.dayNightMode,
            displaySystemThemeButton = LiveBlogPreviewData.displaySystemThemeButton,
            contentTextSize = LiveBlogPreviewData.contentTextSize,
            interactor = LiveBlogPreviewData.BottomSheetInteractor
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFF000000, showBackground = true, name = "Basic Post")
private fun LiveBlogPost_Preview() {
    LiveBlogPostBasic(
        post = LiveBlogPreviewData.LiveBlogPostBasic,
        contentTextSize = LiveBlogPreviewData.contentTextSize,
        tweetMap = LiveBlogPreviewData.tweetMap,
        interactor = LiveBlogPreviewData.Interactor
    )
}

@Composable
@Preview(backgroundColor = 0xFF000000, showBackground = true, name = "Byline with Image")
private fun LiveBlogBylineWithImage_Preview() {
    Byline(
        authorName = "Marcus Thompson II",
        authorDescription = "Warriors",
        avatarUrl = "some_image_url",
        authorNameColor = AthTheme.colors.dark700
    ) { }
}

@Composable
@Preview(backgroundColor = 0xFF000000, showBackground = true, name = "Byline")
private fun LiveBlogByline_Preview() {
    Byline(
        authorName = "Marcus Thompson II",
        authorDescription = "Warriors",
        avatarUrl = null,
        authorNameColor = AthTheme.colors.dark700
    ) { }
}