package com.theathletic.gifts.ui

import android.os.Bundle
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LifecycleObserver
import com.theathletic.R
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.extension.ObservableString
import com.theathletic.extension.extGetString
import com.theathletic.gifts.data.GiftsDataHolder
import com.theathletic.utility.datetime.DateUtilityImpl
import com.theathletic.viewmodel.BaseViewModel

class GiftPurchaseSuccessViewModel() : BaseViewModel(), LifecycleObserver {
    var recipientFullname = ObservableString()
    var recipientEmail = ObservableString()
    var giftPlanDescription = ObservableString()
    var deliveryDescription = ObservableString()

    val shirtSectionVisible = ObservableBoolean(false)
    var shirtFullname = ObservableString()
    var shirtAddress1 = ObservableString()
    var shirtAddress2 = ObservableString()
    var shirtAddressStateCountryZip = ObservableString()
    var shirtSize = ObservableString()

    private var giftFormData: GiftsDataHolder? = null

    constructor(extras: Bundle?) : this() {
        // handle intent extras
        handleExtras(extras)
        updateWithGiftFormData()
    }

    private fun updateWithGiftFormData() {
        giftFormData?.let { giftFormData ->
            recipientFullname.set(giftFormData.recipientName)
            recipientEmail.set(giftFormData.recipientEmail)
            giftPlanDescription.set(giftFormData.displayableGiftPlanNameAndPrice)
            deliveryDescription.set(createDeliveryDescription(giftFormData))

            shirtSectionVisible.set(giftFormData.shirtIncludedWithPlan)
            shirtFullname.set(giftFormData.addressName)
            shirtAddress1.set(giftFormData.address1)
            shirtAddress2.set(giftFormData.address2)
            shirtAddressStateCountryZip.set(R.string.gifts_success_state_city_zip.extGetString(giftFormData.addressState ?: "", giftFormData.addressCity ?: "", giftFormData.addressZIP ?: ""))
            giftFormData.selectedShirtSize?.let { shirtSize.set(R.string.gifts_success_size_label.extGetString(it)) }
        }
    }

    private fun createDeliveryDescription(giftFormData: GiftsDataHolder): String {
        return if (giftFormData.purchaseAsEmail) {
            R.string.gifts_success_delivery_option_info.extGetString(R.string.gifts_success_delivery_option_email.extGetString(), giftFormData.deliveryDate?.time?.let { DateUtilityImpl.formatGMTDate(it, DisplayFormat.WEEKDAY_LONG_MONTH_LONG_DATE_LONG_YEAR) } ?: "")
        } else {
            R.string.gifts_success_delivery_option_info.extGetString(R.string.gifts_success_delivery_option_print.extGetString(), giftFormData.senderEmail)
        }
    }

    private fun handleExtras(arguments: Bundle?) {
        if (arguments != null && arguments.containsKey(GiftPurchaseSuccessDialogFragment.EXTRA_GIFT_FORM_DATA)) {
            arguments.getString(GiftPurchaseSuccessDialogFragment.EXTRA_GIFT_FORM_DATA, "")?.let {
                giftFormData = GiftsDataHolder.fromJson(it)
            }
        }
    }
}