package com.theathletic.gifts.ui

import com.theathletic.gifts.data.GiftsDataHolder
import com.theathletic.ui.BaseView
import com.theathletic.utility.Event

interface GiftPurchaseSuccessView : BaseView {
    fun onCloseClick()
    fun onGiveAnotherGiftClick()
}

class GiftsPurchaseSuccessfulEvent(val giftFormData: GiftsDataHolder) : Event()
class ShowDialogAndCloseGiftsSheetEvent(val message: String) : Event()