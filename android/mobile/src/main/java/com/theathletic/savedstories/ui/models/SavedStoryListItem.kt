package com.theathletic.savedstories.ui.models

import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString

data class SavedStoryListItem(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val dateString: ParameterizedString
) : UiModel {
    override val stableId get() = id.toString()

    interface Interactor {
        fun onArticleClicked(id: Long)
        fun onArticleLongClicked(id: Long): Boolean
    }
}

object SavedStoriesEmptyItem : UiModel {
    override val stableId = SavedStoriesEmptyItem::javaClass.name
}