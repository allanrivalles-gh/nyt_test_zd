package com.theathletic.article.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.theathletic.databinding.FragmentArticleSettingsSheetBinding
import com.theathletic.fragment.AthleticBottomSheetBindingFragment
import com.theathletic.ui.ContentTextSize
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ArticleSettingsSheetFragment : AthleticBottomSheetBindingFragment<
    ArticleSettingsSheetViewModel,
    FragmentArticleSettingsSheetBinding,
    ArticleSettingsSheetContract.ViewState
    >() {

    companion object {
        fun newInstance() = ArticleSettingsSheetFragment()
    }

    override fun inflateBindingLayout(inflater: LayoutInflater) = FragmentArticleSettingsSheetBinding.inflate(inflater)

    override fun setupViewModel() = getViewModel<ArticleSettingsSheetViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textSizeSlider.addOnChangeListener { _, value, _ ->
            presenter.onTextSizeChange(parseSliderValue(value))
        }
    }

    override fun renderState(viewState: ArticleSettingsSheetContract.ViewState) {
        binding.textSizeSlider.value = viewState.textSizeValue.toFloat()
    }

    private fun parseSliderValue(value: Float) = when (value) {
        3f -> ContentTextSize.EXTRA_LARGE
        2f -> ContentTextSize.LARGE
        1f -> ContentTextSize.MEDIUM
        else -> ContentTextSize.DEFAULT
    }

    private fun ContentTextSize.toFloat() = when (this) {
        ContentTextSize.EXTRA_LARGE -> 3f
        ContentTextSize.LARGE -> 2f
        ContentTextSize.MEDIUM -> 1f
        else -> 0f
    }
}