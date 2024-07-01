package com.theathletic.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.billing.BillingManager
import com.theathletic.billing.BillingProducts
import com.theathletic.billing.BillingState
import com.theathletic.billing.GeneralBillingFailure
import com.theathletic.billing.PurchaseFailure
import com.theathletic.billing.PurchasePending
import com.theathletic.billing.PurchaseSuccess
import com.theathletic.billing.SetupComplete
import com.theathletic.extension.extLogError
import com.theathletic.profile.GetCurrentUserUseCase
import com.theathletic.profile.R
import com.theathletic.profile.SaveUserUseCase
import com.theathletic.ui.updateState
import com.theathletic.utility.BillingPreferences
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageAccountViewModel @AutoKoin constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val billingManager: BillingManager,
    private val billingPreferences: BillingPreferences,
    private val analytics: IAnalytics,
) : ViewModel() {

    private val _viewState: MutableStateFlow<ManageAccountViewState> = MutableStateFlow(ManageAccountViewState())
    val viewState = _viewState.asStateFlow()

    private val _viewEvents = MutableSharedFlow<ManageAccountEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    init {
        loadData()
        setupBillingManager()
    }

    override fun onCleared() {
        billingManager.onDestroy()
    }

    fun loadData() {
        getCurrentUserUseCase()
            .onSuccess { result ->
                _viewState.updateState {
                    copy(
                        uiModel = uiModel.copy(
                            userInformation = result.toUserInformation()
                        ),
                        isLoading = false
                    )
                }
            }
            .onFailure {
                _viewState.updateState { copy(isLoading = false) }
            }
    }

    fun saveUserChanges() {
        viewModelScope.launch {
            val uiModel = _viewState.value.uiModel
            uiModel.userInformation?.let { userInformation ->
                _viewState.updateState { copy(isLoading = true) }
                saveUserUseCase(userInformation.currentCustomer)
                    .onSuccess { loadData() }
                    .onFailure { handleSaveError(it) }
            }
        }
    }

    fun updateFirstName(newFirstName: String) {
        val newCustomer = _viewState.value.uiModel.userInformation?.currentCustomer?.copy(
            firstName = newFirstName
        )
        updateCurrentCustomer(newCustomer)
    }

    fun updateLastName(newLastName: String) {
        val newCustomer = _viewState.value.uiModel.userInformation?.currentCustomer?.copy(
            lastName = newLastName
        )
        updateCurrentCustomer(newCustomer)
    }

    fun updateEmail(newEmail: String) {
        val newCustomer = _viewState.value.uiModel.userInformation?.currentCustomer?.copy(
            email = newEmail
        )
        updateCurrentCustomer(newCustomer)
    }

    fun onManageAccountsClicked() {
        viewModelScope.launch {
            if (billingPreferences.subscriptionData != null) {
                // User is subscribed on Google Play
                _viewEvents.emit(ManageAccountEvent.ShowGooglePlaySubscription)
            } else if (_viewState.value.uiModel.userInformation?.isUserSubscribed == true) {
                // Subscribed, but not through Google Play
                _viewEvents.emit(ManageAccountEvent.ShowManageSubscriptionDialog)
            }
        }
    }

    fun onSwitchToAnnualPlanClicked() {
        viewModelScope.launch {
            billingManager.getSubSkus(
                listOf(BillingProducts.IAB_PRODUCT_ANNUAL.planId)
            ).firstOrNull()?.let { sku ->
                billingManager.startSubscriptionChangeFlow(sku)
            }
        }
    }

    private fun handleSaveError(error: Throwable) {
        error.extLogError()
        _viewState.updateState { copy(isLoading = false) }
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
            is PurchaseFailure -> _viewEvents.emit(ManageAccountEvent.ShowMessage(R.string.global_billing_error_internal))
            is GeneralBillingFailure -> _viewEvents.emit(ManageAccountEvent.ShowMessage(R.string.global_error))
            else -> { /* Do nothing */ }
        }
    }

    private suspend fun onBillingManagerSetup() {
        val lastPurchaseSku = getLastPurchaseSku() ?: return
        viewModelScope.launch {
            val sku = billingManager.getSubSkus(listOf(lastPurchaseSku)).firstOrNull()
            _viewState.updateState {
                copy(
                    uiModel = uiModel.copy(
                        isMonthlySubscriber = sku?.isMonthlySubscription ?: false
                    )
                )
            }
        }
    }

    private suspend fun getLastPurchaseSku(): String? {
        return billingManager.getSubscriptionPurchases().purchases?.lastOrNull()?.skus?.lastOrNull()
    }

    private suspend fun handleSuccessfulPurchase(billingState: PurchaseSuccess) {
        billingManager.registerSubPurchaseIfNeeded(billingState.purchase, "profile")
        analytics.track(billingManager.getPurchaseAnalytics(purchase = billingState.purchase, isSubSku = true))
        _viewState.updateState {
            copy(
                uiModel = uiModel.copy(
                    isMonthlySubscriber = false
                )
            )
        }
    }

    private suspend fun handlePendingPurchase() {
        _viewEvents.emit(ManageAccountEvent.ShowMessage((R.string.gifts_payment_pending_processing)))
        _viewState.updateState {
            copy(
                uiModel = uiModel.copy(
                    isMonthlySubscriber = false
                )
            )
        }
    }

    private fun updateCurrentCustomer(newCustomer: Customer?) {
        if (newCustomer == null) return
        _viewState.updateState {
            copy(
                uiModel = uiModel.copy(
                    userInformation = uiModel.userInformation?.copy(
                        currentCustomer = newCustomer
                    )
                )
            )
        }
    }
}