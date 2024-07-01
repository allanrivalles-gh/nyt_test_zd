package com.theathletic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.theathletic.databinding.FragmentFullscreenPhotoBinding
import com.theathletic.extension.viewModelProvider
import com.theathletic.ui.FullscreenPhotoView
import com.theathletic.viewmodel.FullscreenPhotoViewModel

class FullscreenPhotoFragment : BaseBindingFragment<FullscreenPhotoViewModel, FragmentFullscreenPhotoBinding>(), FullscreenPhotoView {
    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentFullscreenPhotoBinding {
        return FragmentFullscreenPhotoBinding.inflate(inflater)
    }

    override fun setupViewModel(): FullscreenPhotoViewModel {
        val viewModel = viewModelProvider { FullscreenPhotoViewModel(getExtras()) }
        lifecycle.addObserver(viewModel)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.executePendingBindings()
    }
}