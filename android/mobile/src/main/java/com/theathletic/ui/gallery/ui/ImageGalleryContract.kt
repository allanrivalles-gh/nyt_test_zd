package com.theathletic.ui.gallery.ui

import com.theathletic.presenter.Interactor

interface ImageGalleryContract {

    interface Presenter :
        Interactor,
        ImageGalleryViewInteractor {
        fun onNewPageSelected(position: Int)
    }

    data class ViewState(
        val imageList: List<String>,
        val pageIndicator: String
    ) : com.theathletic.ui.ViewState
}