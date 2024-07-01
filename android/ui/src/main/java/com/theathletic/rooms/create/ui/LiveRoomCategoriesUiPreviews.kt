package com.theathletic.rooms.create.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ResourceString.StringWrapper

@Preview
@Composable
fun LiveRoomCategoriesScreen_Preview() {
    LiveRoomCategoriesScreen(
        uiModel = CategoriesPreviewData.UiModel,
        interactor = CategoriesPreviewData.Interactor,
    )
}

@Preview
@Composable
fun LiveRoomCategoriesScreen_PreviewLight() {
    AthleticTheme(lightMode = true) {
        LiveRoomCategoriesScreen(
            uiModel = CategoriesPreviewData.UiModel,
            interactor = CategoriesPreviewData.Interactor,
        )
    }
}

private object CategoriesPreviewData {
    val UiModel = LiveRoomCategoriesUi(
        categories = listOf(
            LiveRoomCategoriesUi.Category(
                slug = "q_n_a",
                title = StringWrapper("Q&A"),
                isSelected = false,
            ),
            LiveRoomCategoriesUi.Category(
                slug = "game_recap",
                title = StringWrapper("Game Recap"),
                isSelected = false,
            ),
            LiveRoomCategoriesUi.Category(
                slug = "live_podcast",
                title = StringWrapper("Live Podcast"),
                isSelected = true,
            ),
        ),
    )

    val Interactor = object : LiveRoomCategoriesUi.Interactor {
        override fun onCategoryClicked(value: String) {
        }

        override fun onCloseClicked() {
        }
    }
}