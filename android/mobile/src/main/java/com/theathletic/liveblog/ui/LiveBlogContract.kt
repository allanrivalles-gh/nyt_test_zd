package com.theathletic.liveblog.ui

import com.theathletic.ads.ui.AdWrapperUiModel
import com.theathletic.presenter.Interactor
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.widgets.ModalBottomSheetType

interface LiveBlogContract {
    interface Presenter : Interactor, LiveBlogUi.Interactor, AdWrapperUiModel.Interactor {
        fun dismissBottomSheet()
    }

    data class ViewState(
        val liveBlog: LiveBlogUi.LiveBlog,
        val stagedPostsCount: Int = 0,
        val initialPostIndex: Int = -1,
        val currentBottomSheetModal: ModalSheetType.TextStyleBottomSheet? = null,
        val isLoading: Boolean = false,
        val contentTextSize: ContentTextSize = ContentTextSize.DEFAULT,
    ) : com.theathletic.ui.ViewState

    sealed class ModalSheetType : ModalBottomSheetType {
        data class TextStyleBottomSheet(
            val liveBlogId: String
        ) : ModalSheetType()
    }

    sealed class Event : com.theathletic.utility.Event() {
        object ScrollToFirstPost : Event()
    }
}