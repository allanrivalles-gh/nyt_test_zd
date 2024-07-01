package com.theathletic.gifts.data.remote

import com.theathletic.gifts.data.GiftPurchaseResponse
import com.theathletic.gifts.data.GiftsResponse
import io.reactivex.Maybe
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface GiftsApi {
    // TT Gifts
    @GET("v5/gifts")
    fun getGiftsRx(): Maybe<Response<GiftsResponse>>

    @GET("v5/gifts")
    suspend fun getGifts(): Response<GiftsResponse>

    // TT Gift Purchased as Email
    @Suppress("LongParameterList")
    @FormUrlEncoded
    @POST("v5/purchase_gift")
    fun purchaseGiftAsEmail(
        @Field("plan_id") planId: Long,
        @Field("buyer_email") buyerEmail: String,
        @Field("buyer_name") buyerName: String,
        @Field("recipient_email") recipientEmail: String,
        @Field("recipient_name") recipientName: String,
        @Field("gift_delivery_date") giftDeliveryDate: String? = null,
        @Field("address_name") addressName: String? = null,
        @Field("address_line1") addressLine1: String? = null,
        @Field("address_line2") addressLine2: String? = null,
        @Field("address_city") addressCity: String? = null,
        @Field("address_state") addressState: String? = null,
        @Field("address_zip") addressZip: String? = null,
        @Field("address_country_code") addressCountryCode: String? = null,
        @Field("shirt_size") shirtSize: String? = null,
        @Field("promotion") promotion: String? = null,
        @Field("gift_message") giftMessage: String? = null,
        @Field("delivery_method") deliveryMethod: String = "email",
        @Field("google_receipt_token") googleReceiptToken: String
    ): Maybe<Response<GiftPurchaseResponse>>

    // TT Gift Purchased as Print
    @Suppress("LongParameterList")
    @FormUrlEncoded
    @POST("v5/purchase_gift")
    fun purchaseGiftAsPrint(
        @Field("plan_id") planId: Long,
        @Field("buyer_email") buyerEmail: String,
        @Field("buyer_name") buyerName: String,
        @Field("recipient_name") recipientName: String,
        @Field("address_name") addressName: String? = null,
        @Field("address_line1") addressLine1: String? = null,
        @Field("address_line2") addressLine2: String? = null,
        @Field("address_city") addressCity: String? = null,
        @Field("address_state") addressState: String? = null,
        @Field("address_zip") addressZip: String? = null,
        @Field("address_country_code") addressCountryCode: String? = null,
        @Field("shirt_size") shirtSize: String? = null,
        @Field("promotion") promotion: String? = null,
        @Field("gift_message") giftMessage: String? = null,
        @Field("delivery_method") deliveryMethod: String = "print",
        @Field("google_receipt_token") googleReceiptToken: String
    ): Maybe<Response<GiftPurchaseResponse>>
}