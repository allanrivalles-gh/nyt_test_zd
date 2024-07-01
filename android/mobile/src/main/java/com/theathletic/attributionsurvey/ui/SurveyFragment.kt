package com.theathletic.attributionsurvey.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import com.theathletic.R
import com.theathletic.databinding.FragmentAttributionSurveyBinding
import com.theathletic.fragment.AthleticMvpBindingFragment
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.BindingDiffAdapter
import com.theathletic.ui.observe
import com.theathletic.utility.Event
import java.lang.IllegalStateException
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class SurveyFragment : AthleticMvpBindingFragment<
    SurveyViewModel,
    FragmentAttributionSurveyBinding,
    SurveyContract.SurveyViewState
    >() {
    private lateinit var adapter: SurveyListAdapter

    override fun setupViewModel() = getViewModel<SurveyViewModel> { parametersOf(navigator) }

    override fun renderState(viewState: SurveyContract.SurveyViewState) {
        adapter.submitList(viewState.listModels)
    }

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentAttributionSurveyBinding {
        val binding = FragmentAttributionSurveyBinding.inflate(inflater)
        adapter = SurveyListAdapter(viewLifecycleOwner, presenter)
        binding.recyclerView.adapter = adapter
        presenter.observe<SurveyContract.Event>(this) { processEvent(it) }
        return binding
    }

    private fun processEvent(event: Event) {
        when (event) {
            is SurveyContract.Event.FinishEvent -> finishActivity()
        }
    }

    private fun finishActivity() {
        activity?.let { activity ->
            activity.setResult(RESULT_OK, Intent())
            activity.finish()
        }
    }

    inner class SurveyListAdapter(
        lifecycleOwner: LifecycleOwner,
        view: SurveyContract.SurveyInteractor
    ) : BindingDiffAdapter(lifecycleOwner, view) {
        override fun getLayoutForModel(model: UiModel): Int {
            return when (model) {
                is SurveyEntryUiModel -> R.layout.list_item_attribution_survey_option
                is SurveyHeaderUiModel -> R.layout.list_item_attribution_survey_header
                else -> throw IllegalStateException("Missing layout for SurveyList")
            }
        }
    }
}