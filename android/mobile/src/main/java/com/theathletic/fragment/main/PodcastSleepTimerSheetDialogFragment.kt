package com.theathletic.fragment.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theathletic.BR
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.databinding.FragmentPodcastSleepTimerBinding
import com.theathletic.ui.main.PodcastSleepTimerView
import com.theathletic.viewmodel.main.PodcastBigPlayerViewModel
import com.theathletic.viewmodel.main.PodcastSleepTimerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PodcastSleepTimerSheetDialogFragment : BottomSheetDialogFragment(), PodcastSleepTimerView {
    private val viewModel by viewModel<PodcastSleepTimerViewModel> { parametersOf(arguments) }
    private lateinit var binding: FragmentPodcastSleepTimerBinding

    companion object {
        const val EXTRA_SLEEP_TIMER_ACTIVATED = "sleep_timer_activated"

        fun newInstance(sleepTimerRunning: Boolean) = PodcastSleepTimerSheetDialogFragment().apply {
            arguments = Bundle().apply { putBoolean(EXTRA_SLEEP_TIMER_ACTIVATED, sleepTimerRunning) }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)

        bottomSheetDialog.setOnShowListener { dialog ->
            (dialog as BottomSheetDialog).findViewById<View>(R.id.design_bottom_sheet)?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.peekHeight = binding.root.height
            }
        }

        return bottomSheetDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_Athletic_BigPlayerSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPodcastSleepTimerBinding.inflate(layoutInflater)
        binding.setVariable(BR.view, this)
        binding.setVariable(BR.viewModel, viewModel)
        return binding.root
    }

    override fun showToast(stringRes: Int) {
        (activity as? BaseActivity)?.showToast(stringRes)
    }

    override fun showToast(message: String) {
        (activity as? BaseActivity)?.showToast(message)
    }

    override fun showSnackbar(stringRes: Int) {
        (activity as? BaseActivity)?.showSnackbar(stringRes)
    }

    override fun showSnackbar(message: String) {
        (activity as? BaseActivity)?.showSnackbar(message)
    }

    override fun viewLifecycleOwnerProducer() = viewLifecycleOwner

    override fun onCloseClick() {
        dismiss()
    }

    override fun onSleepDelayClick(chosenDelay: PodcastBigPlayerViewModel.SleepTimerOptions) {
        viewModel.onSleepDelayClick(chosenDelay.value)
        dismiss()
    }

    override fun onTurnTimerOffClick() {
        viewModel.onTurnTimerOffClick()
        dismiss()
    }

    override fun onSleepAfterEpisodeClick() {
        viewModel.onSleepAfterEpisodeClick()
        dismiss()
    }
}