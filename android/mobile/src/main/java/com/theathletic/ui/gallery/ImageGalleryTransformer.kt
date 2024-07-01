package com.theathletic.ui.gallery

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.ui.Transformer
import com.theathletic.ui.gallery.ui.ImageGalleryContract

class ImageGalleryTransformer @AutoKoin constructor() :
    Transformer<ImageGalleryState, ImageGalleryContract.ViewState> {

    override fun transform(data: ImageGalleryState): ImageGalleryContract.ViewState {

        val pageIndicator = "${data.currentPageIndex + 1}/${data.imageUrlList.size}"

        return ImageGalleryContract.ViewState(
            imageList = data.imageUrlList,
            pageIndicator = pageIndicator
        )
    }
}