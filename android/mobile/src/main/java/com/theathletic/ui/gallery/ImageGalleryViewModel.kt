package com.theathletic.ui.gallery

import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.ui.gallery.data.local.ImageGalleryModel
import com.theathletic.ui.gallery.ui.ImageGalleryContract

class ImageGalleryViewModel @AutoKoin constructor(
    @Assisted val igModel: ImageGalleryModel,
    @Assisted val navigator: ScreenNavigator,
    transformer: ImageGalleryTransformer
) : AthleticViewModel<ImageGalleryState, ImageGalleryContract.ViewState>(),
    Transformer<ImageGalleryState, ImageGalleryContract.ViewState> by transformer,
    ImageGalleryContract.Presenter {

    override val initialState by lazy {
        ImageGalleryState(
            imageUrlList = igModel.imageUrlList,
            currentPageIndex = igModel.initialSelectedIndex
        )
    }

    override fun onClose() {
        navigator.finishActivity()
    }

    override fun onNewPageSelected(position: Int) {
        updateState { copy(currentPageIndex = position) }
    }
}

data class ImageGalleryState(
    val imageUrlList: List<String>,
    val currentPageIndex: Int
) : DataState