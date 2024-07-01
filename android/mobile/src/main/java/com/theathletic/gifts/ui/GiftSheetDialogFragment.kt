package com.theathletic.gifts.ui

import android.animation.LayoutTransition
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.theathletic.AthleticApplication
import com.theathletic.BR
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.databinding.FragmentGiftBinding
import com.theathletic.databinding.FragmentGiftSectionChooseGiftItemBinding
import com.theathletic.databinding.FragmentGiftSectionTShirtSizeItemBinding
import com.theathletic.event.DataChangeEvent
import com.theathletic.event.SnackbarEvent
import com.theathletic.event.SnackbarEventRes
import com.theathletic.event.ToastEvent
import com.theathletic.extension.extGetDimensionPixelSize
import com.theathletic.extension.extGetStyledText
import com.theathletic.extension.viewModelProvider
import com.theathletic.gifts.data.GiftPlan
import com.theathletic.gifts.data.GiftShirt
import com.theathletic.gifts.data.GiftsDataHolder
import com.theathletic.user.UserManager
import java.text.NumberFormat
import java.util.Calendar
import java.util.Currency
import java.util.Date
import kotlin.math.roundToInt

class GiftSheetDialogFragment : BottomSheetDialogFragment(), GiftSheetDialogView {

    private lateinit var binding: FragmentGiftBinding
    private lateinit var viewModel: GiftSheetDialogViewModel

    companion object {
        val PEEK_HEIGHT = (AthleticApplication.getContext().resources.displayMetrics.heightPixels * 0.66f).roundToInt()
        fun newInstance() = GiftSheetDialogFragment()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)

        bottomSheetDialog.setOnShowListener({ sheetDialog ->
            val dialog = (sheetDialog as BottomSheetDialog)
            dialog.findViewById<View>(R.id.design_bottom_sheet)?.let {
                val behavior = BottomSheetBehavior.from(it)

                // Disable swipe to dismiss
                behavior.isHideable = false
                behavior.peekHeight = PEEK_HEIGHT

                // Disable outside click
                dialog.findViewById<View>(R.id.touch_outside)?.setOnClickListener(null)
            }
        })

        return bottomSheetDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModelProvider { GiftSheetDialogViewModel() }
        viewModel.observeEvent(this, ToastEvent::class.java, Observer { showToast(it.message) })
        viewModel.observeEvent(this, ShowDialogAndCloseGiftsSheetEvent::class.java, Observer { showDialogAndDismissSheet(it.message) })
        viewModel.observeEvent(this, GiftsPurchaseSuccessfulEvent::class.java, Observer { onGiftsPurchaseSuccessful(it.giftFormData) })
        viewModel.observeEvent(
            this, SnackbarEventRes::class.java,
            Observer { showSnackbar(it.msgResId) }
        )
        lifecycle.addObserver(viewModel)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_Athletic_GiftSheetDialog)

        viewModel.setupBillingManager()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGiftBinding.inflate(layoutInflater)
        binding.setVariable(BR.view, this)
        binding.setVariable(BR.viewModel, viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // This fix the progress state height of the bottomSheetDialog.
        // We need to set minimum height manually to peekHeight minus the toolbar height.
        binding.scrollview.minimumHeight = PEEK_HEIGHT - R.dimen.gifts_toolbar_height.extGetDimensionPixelSize()

        viewModel.observeEvent(viewLifecycleOwner, SnackbarEvent::class.java, Observer { showSnackbar(it.message) })
        viewModel.observeEvent(viewLifecycleOwner, DataChangeEvent::class.java, Observer { onDataChangeEvent() })
    }

    override fun showToast(stringRes: Int) {
        (activity as? BaseActivity)?.showToast(stringRes)
    }

    override fun showToast(message: String) {
        (activity as? BaseActivity)?.showToast(message)
    }

    override fun showSnackbar(stringRes: Int) {
        showSnackbar(getString(stringRes))
    }

    override fun showSnackbar(message: String) {
        Snackbar.make(binding.fragmentGiftCoordinatorContainer, message, Snackbar.LENGTH_LONG).show()
    }

    override fun viewLifecycleOwnerProducer() = viewLifecycleOwner

    override fun onPlanSelected(googleProductId: String) {
        if (binding.contentContainer.layoutTransition.isChangingLayout)
            return

        viewModel.selectPlanByProductId(googleProductId)
    }

    override fun onDeliveryMethodSelected(isEmailMethod: Boolean) {
        if (binding.contentContainer.layoutTransition.isChangingLayout)
            return

        viewModel.switchDeliveryMethodToEmailMethod(isEmailMethod)
    }

    override fun onShirtSelected(shirtSize: String) {
        viewModel.selectShirtSizeByValue(shirtSize)
    }

    override fun onAddressCountrySelected(countryCode: String) {
        viewModel.selectAddressCountryByTitle(countryCode)
    }

    override fun onEditDeliveryDateClick() {
        context?.let { context ->
            val calendar = viewModel.valiDeliveryDate.get()
            val dpd = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    viewModel.setDeliveryDate(year, monthOfYear, dayOfMonth)
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )

            dpd.datePicker.minDate = Date().time + DateUtils.HOUR_IN_MILLIS
            dpd.show()
        }
    }

    override fun onEditSenderNameClick() {
        enableEditingAndRequestFocus(binding.sectionConfirmInfo.nameTextInput, binding.sectionConfirmInfo.nameEditText, binding.sectionConfirmInfo.nameContainer)
    }

    override fun onEditSenderEmailClick() {
        enableEditingAndRequestFocus(binding.sectionConfirmInfo.emailTextInput, binding.sectionConfirmInfo.emailEditText, binding.sectionConfirmInfo.emailContainer)
    }

    override fun onPayClick() {
        viewModel.payForGift()
    }

    override fun onCloseClick() {
        dismiss()
    }

    private fun onDataChangeEvent() {
        inflatePlanItems()
        inflateShirtItems()
        setupCheckYourInfoFieldVisibility()

        // Enable Changing transition, to animate item height changes.
        // We cannot do it inside onViewCreated because the sheet would get expanded immediately.
        binding.contentContainer.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun showDialogAndDismissSheet(message: String) {
        context?.let {
            val dialog = AlertDialog.Builder(it, R.style.Theme_Athletic_Dialog)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.gifts_payment_pending_processing_confirmation) { _, _ -> dismiss() }
                .create()
            dialog.show()
        } ?: dismiss()
    }

    private fun setupCheckYourInfoFieldVisibility() {
        if (UserManager.isAnonymous) {
            binding.sectionConfirmInfo.nameTextInput.visibility = View.VISIBLE
            binding.sectionConfirmInfo.emailTextInput.visibility = View.VISIBLE
            binding.sectionConfirmInfo.nameContainer.visibility = View.GONE
            binding.sectionConfirmInfo.emailContainer.visibility = View.GONE
        } else {
            binding.sectionConfirmInfo.nameTextInput.visibility = View.GONE
            binding.sectionConfirmInfo.emailTextInput.visibility = View.GONE
            binding.sectionConfirmInfo.nameContainer.visibility = View.VISIBLE
            binding.sectionConfirmInfo.emailContainer.visibility = View.VISIBLE
        }
    }

    private fun inflatePlanItems() {
        fun inflatePlanView(view: GiftSheetDialogView, planItem: GiftPlan) = FragmentGiftSectionChooseGiftItemBinding.inflate(layoutInflater).apply {
            setVariable(BR.view, view)
            setVariable(BR.title, planItem.name)
            setVariable(BR.planId, planItem.googleProductId)
            setVariable(BR.isPopular, planItem.popular)
            setVariable(BR.selectedPlan, viewModel.valiSelectedPlan)

            textPrice.text = formatPlanPriceString(planItem)

            root.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, R.dimen.global_spacing_16.extGetDimensionPixelSize(), 0, 0)
            }
        }.root

        binding.sectionChooseGift.plansContainer.removeAllViews()
        viewModel.plans.forEach {
            binding.sectionChooseGift.plansContainer.addView(inflatePlanView(this, it))
        }
    }

    private fun inflateShirtItems() {
        binding.sectionTShirt.shirtFlexbox.apply {
            val columnCount = 3
            val spacing = R.dimen.global_spacing_20.extGetDimensionPixelSize()
            val itemWidth = ((resources.displayMetrics.widthPixels - spacing) / columnCount) - spacing
            val itemHeight = R.dimen.global_spacing_64.extGetDimensionPixelSize()

            fun inflateShirtView(view: GiftSheetDialogView, planItem: GiftShirt) = FragmentGiftSectionTShirtSizeItemBinding.inflate(layoutInflater).apply {
                setVariable(BR.view, view)
                setVariable(BR.size, planItem.title)
                setVariable(BR.value, planItem.value)
                setVariable(BR.selectedShirtSize, viewModel.valiSelectedShirtSize)

                root.layoutParams = FlexboxLayout.LayoutParams(itemWidth, itemHeight).apply {
                    setMargins(0, 0, spacing, R.dimen.global_spacing_16.extGetDimensionPixelSize())
                }
            }.root

            removeAllViews()

            viewModel.shirtSizes.forEach {
                addView(inflateShirtView(this@GiftSheetDialogFragment, it))
            }
        }
    }

    private fun onGiftsPurchaseSuccessful(giftFormData: GiftsDataHolder) {
        activity?.supportFragmentManager?.let { fragmentManager ->
            GiftPurchaseSuccessDialogFragment.newInstance(giftFormData).show(fragmentManager, "gift_bottom_bar_success")
            dismiss()
        }
    }

    private fun formatPlanPriceString(planItem: GiftPlan): SpannableString {
        val skuDetails = planItem.skuDetails ?: return SpannableString("")
        val actualPrice = skuDetails.priceAmountMicros / 1_000_000f
        val discount = planItem.originalPrice - actualPrice

        return if (skuDetails.priceCurrencyCode in listOf("USD", "GBP", "CAD") && discount > 0) {
            try {
                val formatter = NumberFormat.getCurrencyInstance()
                formatter.currency = Currency.getInstance(skuDetails.priceCurrencyCode)
                R.string.gifts_plan_description.extGetStyledText(
                    formatter.format(planItem.originalPrice),
                    formatter.format(actualPrice),
                    formatter.format(discount)
                )
            } catch (e: IllegalArgumentException) {
                SpannableString(SpannableStringBuilder(skuDetails.price.toString()))
            }
        } else {
            SpannableString(SpannableStringBuilder(skuDetails.price.toString()))
        }
    }

    private fun enableEditingAndRequestFocus(
        textInputLayout: TextInputLayout,
        editText: EditText,
        oldContainer: View
    ) {
        textInputLayout.visibility = View.VISIBLE
        oldContainer.visibility = View.GONE
        editText.requestFocus()
        editText.setSelection(editText.text.length)

        val imm = activity?.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(editText, 0)
    }
}