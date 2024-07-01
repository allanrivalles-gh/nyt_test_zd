package com.theathletic.profile.legacy.account.ui

import android.app.Activity
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.viewModelScope
import com.mlykotom.valifi.ValiFiForm
import com.mlykotom.valifi.fields.ValiFieldEmail
import com.theathletic.R
import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.auth.data.AuthenticationRepository
import com.theathletic.billing.BillingManager
import com.theathletic.billing.BillingProducts
import com.theathletic.billing.BillingState
import com.theathletic.billing.GeneralBillingFailure
import com.theathletic.billing.PurchaseFailure
import com.theathletic.billing.PurchasePending
import com.theathletic.billing.PurchaseSuccess
import com.theathletic.billing.SetupComplete
import com.theathletic.event.SnackbarEventRes
import com.theathletic.extension.extGetString
import com.theathletic.extension.extLogError
import com.theathletic.extension.mapRestRequest
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.profile.DeleteAccountUseCase
import com.theathletic.profile.ShowManagePrivacySettingsUseCase
import com.theathletic.ui.LegacyAthleticViewModel
import com.theathletic.user.IUserManager
import com.theathletic.utility.Event
import com.theathletic.utility.Preferences
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.widget.StatefulLayout
import com.theathletic.widget.ValiFieldName
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class ManageAccountViewModel @AutoKoin constructor(
    @Assisted private val navigator: ScreenNavigator,
    private val userManager: IUserManager,
    private val billingManager: BillingManager,
    private val analytics: IAnalytics,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val showManagePrivacySettingsUseCase: ShowManagePrivacySettingsUseCase
) : LegacyAthleticViewModel() {
    val state =
        ObservableInt(StatefulLayout.PROGRESS)
    val currentFirstName: ValiFieldName = ValiFieldName()
    val currentLastName: ValiFieldName = ValiFieldName()
    val currentEmail: ValiFieldEmail = ValiFieldEmail()
    val valuesForm = ValiFiForm(currentFirstName, currentLastName, currentEmail)
    var headerImageUrl = ObservableField<String>("")
    var headerName = ObservableField<String>("")
    var isFbLinked = ObservableBoolean(false)
    val isAccountInfoEditable = ObservableBoolean(false)
    val isAnonymousAccount = ObservableBoolean(false)
    var valuesChanged = ObservableBoolean(false)
    val isMonthlySubscriber = ObservableBoolean(false)
    val showManagePrivacySettings = ObservableBoolean(false)
    private var editUserDisposable: Disposable? = null
    private val originalCustomer = userManager.getCurrentUser()
    private val authenticationRepository by inject<AuthenticationRepository>()

    val manageAccountsVisible
        get() = Preferences.subscriptionData != null || userManager.isUserSubscribed()

    private val propertyChangeCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            if (currentFirstName.get() != originalCustomer?.firstName ||
                currentLastName.get() != originalCustomer?.lastName ||
                currentEmail.get() != originalCustomer?.email
            ) {
                valuesChanged.set(true)
            } else {
                valuesChanged.set(false)
            }
        }
    }

    init {
        viewModelScope.launch {
            val result = showManagePrivacySettingsUseCase()
            showManagePrivacySettings.set(result)
        }
        loadData()

        currentFirstName.addOnPropertyChangedCallback(propertyChangeCallback)
        currentLastName.addOnPropertyChangedCallback(propertyChangeCallback)
        currentEmail.addOnPropertyChangedCallback(propertyChangeCallback)

        setupBillingManager()
    }

    override fun onCleared() {
        super.onCleared()

        editUserDisposable?.dispose()

        currentFirstName.removeOnPropertyChangedCallback(propertyChangeCallback)
        currentLastName.removeOnPropertyChangedCallback(propertyChangeCallback)
        currentEmail.removeOnPropertyChangedCallback(propertyChangeCallback)

        currentFirstName.destroy()
        currentLastName.destroy()
        currentEmail.destroy()

        billingManager.onDestroy()
    }

    fun loadData() {
        if (originalCustomer == null) {
            state.set(StatefulLayout.EMPTY)
            return
        }

        headerImageUrl.set(originalCustomer.avatarUrl)
        currentFirstName.set(originalCustomer.firstName)
        currentLastName.set(originalCustomer.lastName)
        currentEmail.set(originalCustomer.email)

        if (userManager.isAnonymous) {
            headerName.set(R.string.profile_create_account.extGetString())
        } else {
            headerName.set(originalCustomer.getUserNickName())
        }

        isFbLinked.set(userManager.isFbLinked)
        isAnonymousAccount.set(userManager.isAnonymous)
        isAccountInfoEditable.set(!userManager.isFbLinked && !userManager.isAnonymous)
        valuesChanged.set(false)

        state.set(StatefulLayout.CONTENT)
    }

    fun saveUserChanges() {
        if (editUserDisposable?.isDisposed == false)
            return

        state.set(StatefulLayout.PROGRESS)

        editUserDisposable = authenticationRepository.editUser(
            userManager.getCurrentUserId(),
            currentFirstName.get(),
            currentLastName.get(),
            currentEmail.get()
        )
            .mapRestRequest()
            .subscribe(
                {
                    originalCustomer?.firstName = currentFirstName.get()
                    originalCustomer?.lastName = currentLastName.get()
                    originalCustomer?.email = currentEmail.get()
                    userManager.saveCurrentUser(
                        originalCustomer
                    )
                    loadData()
                },
                {
                    it.extLogError()
                    // sendEvent(SnackbarEvent(R.string.global_error.extGetString()))
                    state.set(StatefulLayout.CONTENT)
                }
            )
    }

    fun onManageAccountsClicked() {
        if (Preferences.subscriptionData != null) { // User is subscribed on Google Play
            sendEvent(ShowGooglePlaySubscription)
        } else if (userManager.isUserSubscribed()) { // Subscribed, but not through Google Play
            sendEvent(ShowManageSubscriptionDialog)
        }
    }

    fun onDeleteAccountClicked() {
        if (!userManager.isAnonymous) {
            sendEvent(ShowDeleteAccountDialog)
        }
    }

    fun onDeleteAccountContinueClicked() {
        sendEvent(ShowDeleteAccountConfirmationDialog)
    }

    fun onDeleteAccountConfirmationClicked() {
        viewModelScope.launch {
            deleteAccountUseCase.invoke()
                .onSuccess { sendEvent(ShowDeleteAccountSuccessDialog) }
                .onFailure { sendEvent(SnackbarEventRes(R.string.global_error)) }
        }
    }

    fun onDeleteAccountSuccessDialogDismissed() {
        userManager.logOut()
        navigator.startAuthenticationActivity(false)
        navigator.finishAffinity()
    }

    fun onSwitchToAnnualPlanClicked(activity: Activity) {
        viewModelScope.launch {
            val sku = billingManager.getSubSkus(
                listOf(BillingProducts.IAB_PRODUCT_ANNUAL.planId)
            ).firstOrNull() ?: return@launch
            billingManager.startSubscriptionChangeFlow(sku)
        }
    }

    fun onManagePrivacySettingsClicked() {
        navigator.startConsentWebView(true)
    }

    private fun setupBillingManager() {
        billingManager.onCreate()
        billingManager.billingState.collectIn(viewModelScope) { handleBillingStates(it) }
    }

    private suspend fun handleBillingStates(billingState: BillingState?) {
        when (billingState) {
            is SetupComplete -> onBillingManagerSetup()
            is PurchaseSuccess -> handleSuccessfulPurchase(billingState)
            is PurchasePending -> handlePendingPurchase()
            is PurchaseFailure -> sendEvent(SnackbarEventRes(R.string.global_billing_error_internal))
            is GeneralBillingFailure -> sendEvent(SnackbarEventRes(R.string.global_error))
            else -> { /* Do nothing */ }
        }
    }

    private suspend fun onBillingManagerSetup() {
        val lastPurchaseSku = getLastPurchaseSku() ?: return
        viewModelScope.launch {
            val sku = billingManager.getSubSkus(listOf(lastPurchaseSku)).firstOrNull()
            isMonthlySubscriber.set(sku?.isMonthlySubscription ?: false)
        }
    }

    private suspend fun getLastPurchaseSku(): String? {
        return billingManager.getSubscriptionPurchases().purchases?.lastOrNull()?.skus?.lastOrNull()
    }

    private suspend fun handleSuccessfulPurchase(billingState: PurchaseSuccess) {
        billingManager.registerSubPurchaseIfNeeded(billingState.purchase, "profile")
        analytics.track(billingManager.getPurchaseAnalytics(purchase = billingState.purchase, isSubSku = true))
        isMonthlySubscriber.set(false)
    }

    private fun handlePendingPurchase() {
        sendEvent(SnackbarEventRes(R.string.gifts_payment_pending_processing))
        isMonthlySubscriber.set(false)
    }
}

object ShowGooglePlaySubscription : Event()
object ShowManageSubscriptionDialog : Event()
object ShowDeleteAccountDialog : Event()
object ShowDeleteAccountConfirmationDialog : Event()
object ShowDeleteAccountSuccessDialog : Event()