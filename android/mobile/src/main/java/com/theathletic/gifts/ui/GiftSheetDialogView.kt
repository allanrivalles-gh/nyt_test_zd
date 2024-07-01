package com.theathletic.gifts.ui

import com.theathletic.ui.BaseView

interface GiftSheetDialogView : BaseView {
    fun onPlanSelected(googleProductId: String)
    fun onDeliveryMethodSelected(isEmailMethod: Boolean)
    fun onShirtSelected(shirtSize: String)
    fun onAddressCountrySelected(countryCode: String)
    fun onEditDeliveryDateClick()
    fun onEditSenderNameClick()
    fun onEditSenderEmailClick()
    fun onPayClick()
    fun onCloseClick()
}