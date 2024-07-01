package com.theathletic.gifts.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theathletic.AthleticApplication
import com.theathletic.BR
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.databinding.FragmentGiftPurchaseSuccessBinding
import com.theathletic.extension.extGetDimensionPixelSize
import com.theathletic.extension.viewModelProvider
import com.theathletic.gifts.data.GiftsDataHolder
import kotlin.math.roundToInt

class GiftPurchaseSuccessDialogFragment : BottomSheetDialogFragment(), GiftPurchaseSuccessView {
    private lateinit var binding: FragmentGiftPurchaseSuccessBinding
    private lateinit var viewModel: GiftPurchaseSuccessViewModel

    companion object {
        const val EXTRA_GIFT_FORM_DATA = "gift_form_data"

        val PEEK_HEIGHT = (AthleticApplication.getContext().resources.displayMetrics.heightPixels * 0.66f).roundToInt()
        fun newInstance(giftFormData: GiftsDataHolder): GiftPurchaseSuccessDialogFragment {
            val args = Bundle()
            args.putString(EXTRA_GIFT_FORM_DATA, giftFormData.toJson())
            return GiftPurchaseSuccessDialogFragment().apply { arguments = args }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)

        bottomSheetDialog.setOnShowListener { sheetDialog ->
            val dialog = (sheetDialog as BottomSheetDialog)
            dialog.findViewById<View>(R.id.design_bottom_sheet)?.let {
                val behavior = BottomSheetBehavior.from(it)

                // Disable swipe to dismiss
                behavior.isHideable = false
                behavior.peekHeight = PEEK_HEIGHT

                // Disable outside click
                dialog.findViewById<View>(R.id.touch_outside)?.setOnClickListener(null)
            }
        }

        return bottomSheetDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModelProvider { GiftPurchaseSuccessViewModel(arguments) }
        lifecycle.addObserver(viewModel)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_Athletic_GiftSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGiftPurchaseSuccessBinding.inflate(layoutInflater)
        binding.setVariable(BR.view, this)
        binding.setVariable(BR.viewModel, viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // This fix the progress state height of the bottomSheetDialog.
        // We need to set minimum height manually to peekHeight minus the toolbar height.
        binding.scrollview.minimumHeight = PEEK_HEIGHT - R.dimen.global_spacing_48.extGetDimensionPixelSize()
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

    override fun onGiveAnotherGiftClick() {
        activity?.supportFragmentManager?.let { fragmentManager ->
            GiftSheetDialogFragment.newInstance().show(fragmentManager, "gift_bottom_bar_sheet")
            dismiss()
        }
    }
}