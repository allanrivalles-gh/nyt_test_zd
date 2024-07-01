package com.theathletic.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2
import com.theathletic.databinding.FragmentImageGalleryBinding
import com.theathletic.fragment.AthleticMvpBindingFragment
import com.theathletic.ui.gallery.data.local.ImageGalleryModel
import com.theathletic.ui.gallery.ui.ImageGalleryAdapter
import com.theathletic.ui.gallery.ui.ImageGalleryContract
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class ImageGalleryFragment : AthleticMvpBindingFragment<
    ImageGalleryViewModel,
    FragmentImageGalleryBinding,
    ImageGalleryContract.ViewState
    >() {

    companion object {
        private const val EXTRA_IMAGES_LIST = "extra_images_list"
        private const val EXTRA_IMAGE_INDEX = "extra_image_index"

        fun newInstance(
            imageList: ArrayList<String>?,
            index: Int
        ): ImageGalleryFragment {
            return ImageGalleryFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(EXTRA_IMAGES_LIST, imageList)
                    putInt(EXTRA_IMAGE_INDEX, index)
                }
            }
        }
    }

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ImageGalleryAdapter

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentImageGalleryBinding {
        val binding = FragmentImageGalleryBinding.inflate(inflater)
        viewPager = binding.pagerImagesGallery
        adapter = ImageGalleryAdapter(context)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                presenter.onNewPageSelected(position)
            }
        })

        return binding
    }

    override fun setupViewModel() = getViewModel<ImageGalleryViewModel> {
        parametersOf(getParameters(), navigator)
    }

    private fun getParameters() = ImageGalleryModel(
        arguments?.getStringArrayList(EXTRA_IMAGES_LIST) ?: emptyList<String>(),
        arguments?.getInt(EXTRA_IMAGE_INDEX, 0) ?: 0
    )

    override fun renderState(viewState: ImageGalleryContract.ViewState) {
        adapter.setImages(viewState.imageList)
    }
}