package com.theathletic.savedstories.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.formatter.TimeAgoShortDateFormatter
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.savedstories.ui.models.SavedStoriesEmptyItem
import com.theathletic.savedstories.ui.models.SavedStoryListItem
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.ui.list.ListVerticalPadding

class SavedStoriesTransformer @AutoKoin constructor(
    private val timeAgoShortDateFormatter: TimeAgoShortDateFormatter
) : Transformer<SavedStoriesMvpState, SavedStoriesContract.SavedStoriesViewState> {

    override fun transform(data: SavedStoriesMvpState):
        SavedStoriesContract.SavedStoriesViewState {
        return SavedStoriesContract.SavedStoriesViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            uiModels = when {
                data.loadingState == LoadingState.INITIAL_LOADING -> emptyList()
                data.savedStories.isEmpty() -> listOf(SavedStoriesEmptyItem)
                else -> listOf(ListVerticalPadding(R.dimen.global_spacing_8)) + data.savedStories.map { it.toUiModel() }
            },
            refreshable = true,
            isDeleteAllEnabled = data.savedStories.isNotEmpty()
        )
    }

    fun ArticleEntity.toUiModel() = SavedStoryListItem(
        id = articleId,
        title = articleTitle.orEmpty(),
        imageUrl = articleHeaderImg.orEmpty(),
        dateString = timeAgoShortDateFormatter.format(articlePublishDate)
    )
}