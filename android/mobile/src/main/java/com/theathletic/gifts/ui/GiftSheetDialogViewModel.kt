package com.theathletic.gifts.ui

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.mlykotom.valifi.ValiFiForm
import com.mlykotom.valifi.fields.ValiFieldEmail
import com.mlykotom.valifi.fields.ValiFieldText
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.billing.BillingManager
import com.theathletic.billing.BillingSku
import com.theathletic.billing.BillingState
import com.theathletic.billing.GeneralBillingFailure
import com.theathletic.billing.PurchaseCancelled
import com.theathletic.billing.PurchaseConsumed
import com.theathletic.billing.PurchaseFailure
import com.theathletic.billing.PurchasePending
import com.theathletic.billing.PurchaseSuccess
import com.theathletic.billing.SetupComplete
import com.theathletic.billing.data.local.BillingPurchase
import com.theathletic.billing.data.local.BillingPurchaseState
import com.theathletic.event.DataChangeEvent
import com.theathletic.event.SnackbarEventRes
import com.theathletic.event.ToastEvent
import com.theathletic.extension.ObservableString
import com.theathletic.extension.extGetString
import com.theathletic.extension.extLogError
import com.theathletic.extension.mapRestRequest
import com.theathletic.gifts.data.GiftPlan
import com.theathletic.gifts.data.GiftPromotion
import com.theathletic.gifts.data.GiftShirt
import com.theathletic.gifts.data.GiftsDataHolder
import com.theathletic.gifts.data.GiftsRepository
import com.theathletic.gifts.data.GiftsResponse
import com.theathletic.repository.safeApiRequest
import com.theathletic.user.IUserManager
import com.theathletic.utility.IPreferences
import com.theathletic.utility.Preferences
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.utility.datetime.DateUtilityImpl
import com.theathletic.viewmodel.BaseViewModel
import com.theathletic.widget.StatefulLayout
import com.theathletic.widget.ValiFieldAddress
import com.theathletic.widget.ValiFieldAddressCountry
import com.theathletic.widget.ValiFieldGiftsDeliveryDate
import com.theathletic.widget.ValiFieldName
import io.reactivex.disposables.Disposable
import java.text.NumberFormat
import java.util.Calendar
import java.util.Currency
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class GiftSheetDialogViewModel : BaseViewModel(), LifecycleObserver, KoinComponent {
    val state = ObservableInt(StatefulLayout.PROGRESS)
    val failedToSendPaymentToBackend = ObservableBoolean(false)
    var textChooseGiftHeadline = ObservableString()
    var textPromotion = ObservableString()
    var plans: ArrayList<GiftPlan> = arrayListOf()
    var shirtSizes: ArrayList<GiftShirt> = arrayListOf()
    val purchaseAsEmail = ObservableBoolean(true)
    val shirtIncludedWithPlan = ObservableBoolean(false)
    val valiSelectedPlan: ValiFieldText = ValiFieldText()
    val valiSelectedShirtSize: ValiFieldText = ValiFieldText()
    val valiAddressCountryCode: ValiFieldAddressCountry = ValiFieldAddressCountry()
    val valiRecipientName: ValiFieldName = ValiFieldName()
    val valiRecipientEmail: ValiFieldEmail = ValiFieldEmail()
    val valiDeliveryDate: ValiFieldGiftsDeliveryDate = ValiFieldGiftsDeliveryDate()
    val valiSenderName: ValiFieldName = ValiFieldName()
    val valiSenderEmail: ValiFieldEmail = ValiFieldEmail()
    val valiAddressName: ValiFieldAddress = ValiFieldAddress()
    val valiAddress1: ValiFieldAddress = ValiFieldAddress()
    val valiAddress2: ValiFieldText = ValiFieldText()
    val valiAddressCity: ValiFieldAddress = ValiFieldAddress()
    val valiAddressState: ValiFieldAddress = ValiFieldAddress()
    val valiAddressZIP: ValiFieldAddress = ValiFieldAddress()
    val valiMessage: ValiFieldText = ValiFieldText()
    private val valiRecipientDeliveryEmailForm = ValiFiForm(valiRecipientName, valiRecipientEmail)
    private val valiRecipientDeliveryPrintForm = ValiFiForm(valiRecipientName)
    private val valiSenderInfoForm = ValiFiForm(valiSenderName, valiSenderEmail)
    private val valiAddressForm = ValiFiForm(valiAddressName, valiAddress1, valiAddress2, valiAddressCity, valiAddressState, valiAddressZIP, valiAddressCountryCode)
    private var lastPurchaseNotValidated: BillingPurchase? = null
    private var promotion: GiftPromotion? = null
        set(value) {
            field = value
            updateShirtGiftUi()
        }
    private var giftsDisposable: Disposable? = null
    private val giftsRepository by inject<GiftsRepository>()
    private val analytics by inject<Analytics>()
    private val billingManager by inject<BillingManager>()
    private val userManager by inject<IUserManager>()
    private val preferences by inject<IPreferences>()
    private val dispatcherProvider by inject<DispatcherProvider>()

    override fun onCleared() {
        Timber.d("[GiftSheetDialogViewModel] On cleared")
        giftsDisposable?.dispose()
        billingManager.onDestroy()
        super.onCleared()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        analytics.track(Event.Gift.ViewedGiftDialog())
    }

    /**
     * This is the init method. The first one that should be called from the Fragment.
     * The logic for loading Gifts data should be following:
     * 1) Setup [BillingManager]
     * 2) Call [getGifts] to download data from the API
     * 3) Process the data from the API
     * 4) Setup default values that we will use in the UI
     * 5) Fetch the billing plans based on the API data
     * 6) Process the billing data
     * 7) Show content and send [DataChangeEvent]
     */
    fun setupBillingManager() {
        billingManager.onCreate()
        billingManager.billingState.collectIn(viewModelScope) { handleBillingStates(it) }
    }

    fun selectDefaultPlan() {
        valiSelectedPlan.set(plans.firstOrNull { it.popular }?.googleProductId ?: plans.firstOrNull()?.googleProductId)
        updateShirtGiftUi()
    }

    fun selectPlanByProductId(googleProductId: String) {
        valiSelectedPlan.set(googleProductId)
        updateShirtGiftUi()
    }

    fun selectShirtSizeByValue(shirtSize: String) {
        valiSelectedShirtSize.set(shirtSize)
    }

    fun selectAddressCountryByTitle(countryCode: String) {
        valiAddressCountryCode.set(countryCode)
    }

    fun switchDeliveryMethodToEmailMethod(emailMethod: Boolean) {
        purchaseAsEmail.set(emailMethod)
    }

    fun setDeliveryDate(year: Int, month: Int, day: Int) {
        valiDeliveryDate.set(Calendar.getInstance().apply { set(year, month, day) })
    }

    fun payForGift() {
        val selectedPlan = plans.firstOrNull { it.googleProductId == valiSelectedPlan.get() }
        val skuDetails = selectedPlan?.skuDetails
        checkFormsValidity()

        if (areFormsValid() && selectedPlan != null && skuDetails != null) {
            billingManager.startPurchaseFlow(BillingSku(skuDetails))

            preferences.giftsPendingPaymentDataJson = createGiftsDataHolderEntity().toJson()

            analytics.track(
                Event.Profile.Click(
                    element = "gift",
                    object_type = "gift_card",
                    object_id = selectedPlan.name
                )
            )

            analytics.track(Event.Gift.CheckoutPress(object_id = selectedPlan.googleProductId))
        }
    }

    fun retryLastPurchaseApiValidation() {
        // Tt this should not be null, closing dialog is the last option here
        Preferences.giftsPendingPaymentDataJson?.let { json ->
            lastPurchaseNotValidated?.let {
                sendGiftPurchased(it, GiftsDataHolder.fromJson(json))
            } ?: sendEvent(ShowDialogAndCloseGiftsSheetEvent(R.string.gifts_error_billing_validation.extGetString()))
        } ?: sendEvent(ShowDialogAndCloseGiftsSheetEvent(R.string.gifts_error_billing_validation.extGetString()))
    }

    private fun updateShirtGiftUi() {
        Timber.d("updateShirtGiftUi: ${shirtIncludedWithPlan.get()}")
        shirtIncludedWithPlan.set(promotion != null && plans.firstOrNull { it.googleProductId == valiSelectedPlan.get() }?.hasShirt ?: false)
    }

    private fun getSelectedGiftPlan(googleProductId: String): GiftPlan? {
        return plans.firstOrNull { it.googleProductId == googleProductId }
    }

    private fun giftPlanPriceWithDiscount(giftPlan: GiftPlan?): String {
        return giftPlan?.let {
            val skuDetails = giftPlan.skuDetails ?: return ""
            val actualPrice = skuDetails.priceAmountMicros / 1_000_000f
            val discount = it.originalPrice - actualPrice

            if (skuDetails.priceCurrencyCode in listOf("USD", "GBP", "CAD") && discount > 0) {
                try {
                    val formatter = NumberFormat.getCurrencyInstance()
                    formatter.currency = Currency.getInstance(skuDetails.priceCurrencyCode)
                    R.string.gifts_success_price_with_discount.extGetString(
                        formatter.format(actualPrice),
                        formatter.format(discount)
                    )
                } catch (e: IllegalArgumentException) {
                    skuDetails.price.toString()
                }
            } else {
                skuDetails.price.toString()
            }
        } ?: ""
    }

    private fun giftPlanReadableName(giftPlan: GiftPlan?): String {
        return giftPlan?.name ?: ""
    }

    private fun displayGiftPlanNameAndPrice(): String {
        val giftPlan = getSelectedGiftPlan(valiSelectedPlan.get())
        return R.string.gifts_success_plan_name_with_price.extGetString(giftPlanReadableName(giftPlan), giftPlanPriceWithDiscount(giftPlan))
    }

    private fun handleBillingStates(billingState: BillingState?) {
        when (billingState) {
            is SetupComplete -> {
                getGifts()
                handlePendingGifts()
            }
            is PurchaseSuccess -> {
                preferences.giftsPendingPaymentDataJson?.let { json ->
                    sendGiftPurchased(billingState.purchase, GiftsDataHolder.fromJson(json))
                }
            }
            is PurchasePending -> {
                state.set(StatefulLayout.PROGRESS)
                sendEvent(ShowDialogAndCloseGiftsSheetEvent(R.string.gifts_payment_pending_processing.extGetString()))
            }
            is PurchaseCancelled -> {
                preferences.giftsPendingPaymentDataJson = null
                updateGiftsState()
            }
            is PurchaseFailure -> {
                sendEvent(SnackbarEventRes(R.string.gifts_error_billing_purchase))
                updateGiftsState()
            }
            is GeneralBillingFailure -> {
                sendEvent(SnackbarEventRes(R.string.gifts_error_billing_purchase))
                updateGiftsState()
            }
            is PurchaseConsumed -> logPurchase(billingState.purchase)
            else -> {}
        }
    }

    private fun getGifts() = viewModelScope.launch {
        safeApiRequest(dispatcherProvider.io) {
            giftsRepository.getGifts()
        }.onSuccess { response ->
            val gifts = response.body()
            if (gifts != null) {
                processGiftsDataResponse(gifts)
                setupDefaultValues()
                setupSkuDetails()
            } else {
                state.set(StatefulLayout.EMPTY)
            }
        }.onError {
            it.extLogError()
            state.set(StatefulLayout.EMPTY)
        }
    }

    private fun setupSkuDetails() = viewModelScope.launch {
        billingManager.getInAppSkus(plans.map { it.googleProductId }).forEach { sku ->
            plans.firstOrNull { it.googleProductId == sku.sku }?.skuDetails = sku.skuDetails
        }
        plans.removeAll { it.skuDetails == null }
        plans.sortBy { it.index }

        updateGiftsState()
        sendEvent(DataChangeEvent())
    }

    private fun handlePendingGifts() = viewModelScope.launch {
        billingManager.getInAppPurchases().purchases?.forEach { purchase ->
            if (purchase.purchaseState == BillingPurchaseState.Purchased) {
                handleBillingStates(PurchaseSuccess(purchase))
            }
        }
    }

    private fun updateGiftsState() {
        if (plans.isEmpty())
            state.set(StatefulLayout.EMPTY)
        else
            state.set(StatefulLayout.CONTENT)
    }

    private fun processGiftsDataResponse(giftsResponse: GiftsResponse) {
        promotion = giftsResponse.promotion
        plans.addAll(giftsResponse.plans)
        shirtSizes.addAll(giftsResponse.shirtSizes ?: emptyList())

        textChooseGiftHeadline.set(giftsResponse.headline)
        textPromotion.set(promotion?.text)

        valiDeliveryDate.set(Calendar.getInstance())
    }

    private fun setupDefaultValues() {
        if (!userManager.isAnonymous) {
            valiSenderName.set(userManager.getCurrentUser()?.getUserFullName())
            valiSenderEmail.set(userManager.getCurrentUser()?.email)
        }

        selectDefaultPlan()
        selectDefaultShirtSize()
    }

    private fun selectDefaultShirtSize() {
        valiSelectedShirtSize.set(shirtSizes.firstOrNull()?.value ?: "")
    }

    private fun checkFormsValidity() {
        valiRecipientDeliveryEmailForm.validate()
        valiRecipientDeliveryPrintForm.validate()
        valiSenderInfoForm.validate()
        valiAddressForm.validate()

        when {
            purchaseAsEmail.get() && !valiRecipientDeliveryEmailForm.isValid ->
                sendEvent(ToastEvent(R.string.gifts_error_check_required_fields_recipient_delivery.extGetString()))
            !purchaseAsEmail.get() && !valiRecipientDeliveryPrintForm.isValid ->
                sendEvent(ToastEvent(R.string.gifts_error_check_required_fields_recipient_delivery.extGetString()))
            !valiSenderInfoForm.isValid ->
                sendEvent(ToastEvent(R.string.gifts_error_check_required_fields_your_info.extGetString()))
            !valiAddressForm.isValid && shirtIncludedWithPlan.get() ->
                sendEvent(ToastEvent(R.string.gifts_error_check_required_fields_address.extGetString()))
        }
    }

    private fun areFormsValid(): Boolean {
        return when {
            purchaseAsEmail.get() && !valiRecipientDeliveryEmailForm.isValid -> false
            !purchaseAsEmail.get() && !valiRecipientDeliveryPrintForm.isValid -> false
            !valiSenderInfoForm.isValid -> false
            !valiAddressForm.isValid && shirtIncludedWithPlan.get() -> false
            else -> true
        }
    }

    private fun sendGiftPurchased(purchase: BillingPurchase, giftFormData: GiftsDataHolder) {
        if (giftsDisposable?.isDisposed == false)
            return

        state.set(StatefulLayout.PROGRESS)
        when {
            giftFormData.purchaseAsEmail -> sendGiftPurchasedAsEmail(purchase, giftFormData)
            else -> sendGiftPurchasedAsPrint(purchase, giftFormData)
        }
    }

    private fun getAddressInfoWhenShirtIncluded(addressInfo: ValiFieldText): String? {
        return if (shirtIncludedWithPlan.get()) addressInfo.get() else null
    }

    private fun sendGiftPurchasedAsEmail(purchase: BillingPurchase, giftFormData: GiftsDataHolder) {
        Timber.d("[GiftSheetDialogViewModel] sendGiftPurchasedAsEmail()")
        giftsDisposable = giftsRepository.purchaseGiftAsEmail(
            planId = giftFormData.selectedPlanBackendId,
            buyerEmail = giftFormData.senderEmail,
            buyerName = giftFormData.senderName,
            recipientEmail = giftFormData.recipientEmail ?: "", // it was validated, should not be null
            recipientName = giftFormData.recipientName,
            giftDeliveryDate = DateUtilityImpl.formatGiftsCalendar(giftFormData.deliveryDate),
            addressName = giftFormData.addressName,
            addressLine1 = giftFormData.address1,
            addressLine2 = giftFormData.address2,
            addressCity = giftFormData.addressCity,
            addressState = giftFormData.addressState,
            addressZip = giftFormData.addressZIP,
            addressCountryCode = giftFormData.addressCountryCode,
            shirtSize = giftFormData.selectedShirtSize,
            promotion = giftFormData.promotionName,
            giftMessage = giftFormData.message,
            googleReceiptToken = purchase.purchaseToken
        ).mapRestRequest().subscribe(
            {
                failedToSendPaymentToBackend.set(false)

                analytics.track(
                    Event.Gift.CheckoutPurchase(
                        object_id = giftFormData.selectedPlan,
                        deliveryMethod = "email"
                    )
                )
                viewModelScope.launch {
                    analytics.track(billingManager.getPurchaseAnalytics(purchase = purchase, isSubSku = false))
                }
                billingManager.consumePurchase(purchase)
            },
            {
                handlePurchaseApiFailure(it, purchase)
            }
        )
    }

    private fun sendGiftPurchasedAsPrint(purchase: BillingPurchase, giftFormData: GiftsDataHolder) {
        Timber.d("[GiftSheetDialogViewModel] sendGiftPurchasedAsPrint()")
        giftsDisposable = giftsRepository.purchaseGiftAsPrint(
            planId = giftFormData.selectedPlanBackendId,
            buyerEmail = giftFormData.senderEmail,
            buyerName = giftFormData.senderName,
            recipientName = giftFormData.recipientName,
            addressName = giftFormData.addressName,
            addressLine1 = giftFormData.address1,
            addressLine2 = giftFormData.address2,
            addressCity = giftFormData.addressCity,
            addressState = giftFormData.addressState,
            addressZip = giftFormData.addressZIP,
            addressCountryCode = giftFormData.addressCountryCode,
            shirtSize = giftFormData.selectedShirtSize,
            promotion = giftFormData.promotionName,
            giftMessage = giftFormData.message,
            googleReceiptToken = purchase.purchaseToken
        ).mapRestRequest().subscribe(
            {
                failedToSendPaymentToBackend.set(false)

                analytics.track(
                    Event.Gift.CheckoutPurchase(
                        object_id = giftFormData.selectedPlan,
                        deliveryMethod = "print"
                    )
                )
                viewModelScope.launch {
                    analytics.track(billingManager.getPurchaseAnalytics(purchase = purchase, isSubSku = false))
                }
                billingManager.consumePurchase(purchase)
            },
            {
                handlePurchaseApiFailure(it, purchase)
            }
        )
    }

    private fun handlePurchaseApiFailure(throwable: Throwable, purchase: BillingPurchase) {
        throwable.extLogError()
        lastPurchaseNotValidated = purchase
        failedToSendPaymentToBackend.set(true)
        state.set(StatefulLayout.CONTENT)
    }

    private fun logPurchase(purchase: BillingPurchase) = viewModelScope.launch {
        billingManager.registerGiftPurchase(purchase)

        val data = preferences.giftsPendingPaymentDataJson?.let {
            GiftsDataHolder.fromJson(it)
        } ?: createGiftsDataHolderEntity()

        preferences.giftsPendingPaymentDataJson = null
        sendEvent(GiftsPurchaseSuccessfulEvent(data))
    }

    private fun createGiftsDataHolderEntity() = GiftsDataHolder(
        textChooseGiftHeadline = textChooseGiftHeadline.get(),
        promotionName = promotion?.name,
        purchaseAsEmail = purchaseAsEmail.get(),
        shirtIncludedWithPlan = shirtIncludedWithPlan.get(),
        selectedPlan = valiSelectedPlan.get(),
        selectedPlanBackendId = getSelectedGiftPlan(valiSelectedPlan.get())?.id ?: 0,
        selectedShirtSize = getAddressInfoWhenShirtIncluded(valiSelectedShirtSize),
        addressCountryCode = valiAddressCountryCode.get(),
        recipientName = valiRecipientName.get(),
        recipientEmail = valiRecipientEmail.get(),
        deliveryDate = valiDeliveryDate.get(),
        senderName = valiSenderName.get(),
        senderEmail = valiSenderEmail.get(),
        addressName = valiAddressName.get(),
        address1 = getAddressInfoWhenShirtIncluded(valiAddress1),
        address2 = getAddressInfoWhenShirtIncluded(valiAddress2),
        addressCity = getAddressInfoWhenShirtIncluded(valiAddressCity),
        addressState = getAddressInfoWhenShirtIncluded(valiAddressState),
        addressZIP = getAddressInfoWhenShirtIncluded(valiAddressZIP),
        message = valiMessage.get(),
        displayableGiftPlanNameAndPrice = displayGiftPlanNameAndPrice()
    )
}