package com.theathletic.gifts.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.gifts.data.remote.GiftsApi
import io.reactivex.Maybe
import retrofit2.Response

class GiftsRepository @AutoKoin constructor(
    private val giftsApi: GiftsApi,
    private val debugPreferences: DebugPreferences
) {

    suspend fun getGifts() = giftsApi.getGifts()

    @Suppress("LongParameterList")
    fun purchaseGiftAsEmail(
        planId: Long,
        buyerEmail: String,
        buyerName: String,
        recipientEmail: String,
        recipientName: String,
        giftDeliveryDate: String? = null,
        addressName: String? = null,
        addressLine1: String? = null,
        addressLine2: String? = null,
        addressCity: String? = null,
        addressState: String? = null,
        addressZip: String? = null,
        addressCountryCode: String? = null,
        shirtSize: String? = null,
        promotion: String? = null,
        giftMessage: String? = null,
        deliveryMethod: String = "email",
        googleReceiptToken: String
    ): Maybe<Response<GiftPurchaseResponse>> {
        return if (debugPreferences.enableDebugBillingTools) {
            if (debugPreferences.isGiftsResponseSuccessful) {
                Maybe.just(Response.success(GiftPurchaseResponse(true)))
            } else {
                Maybe.error(Throwable("Debug billing error"))
            }
        } else {
            giftsApi.purchaseGiftAsEmail(
                planId, buyerEmail, buyerName, recipientEmail,
                recipientName, giftDeliveryDate, addressName, addressLine1, addressLine2,
                addressCity, addressState, addressZip, addressCountryCode, shirtSize,
                promotion, giftMessage, deliveryMethod, googleReceiptToken
            )
        }
    }

    @Suppress("LongParameterList")
    fun purchaseGiftAsPrint(
        planId: Long,
        buyerEmail: String,
        buyerName: String,
        recipientName: String,
        addressName: String? = null,
        addressLine1: String? = null,
        addressLine2: String? = null,
        addressCity: String? = null,
        addressState: String? = null,
        addressZip: String? = null,
        addressCountryCode: String? = null,
        shirtSize: String? = null,
        promotion: String? = null,
        giftMessage: String? = null,
        deliveryMethod: String = "print",
        googleReceiptToken: String
    ): Maybe<Response<GiftPurchaseResponse>> {
        return if (debugPreferences.enableDebugBillingTools) {
            if (debugPreferences.isGiftsResponseSuccessful) {
                Maybe.just(Response.success(GiftPurchaseResponse(true)))
            } else {
                Maybe.error(Throwable("Debug billing error"))
            }
        } else {
            giftsApi.purchaseGiftAsPrint(
                planId, buyerEmail, buyerName, recipientName,
                addressName, addressLine1, addressLine2, addressCity, addressState,
                addressZip, addressCountryCode, shirtSize, promotion, giftMessage,
                deliveryMethod, googleReceiptToken
            )
        }
    }
}