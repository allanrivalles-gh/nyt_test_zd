package com.theathletic.profile.ui

import androidx.annotation.StringRes

data class ManageAccountViewState(
    val uiModel: ManageAccountUiModel = ManageAccountUiModel(),
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean
        get() = uiModel.userInformation == null
}

sealed interface ManageAccountEvent {
    object ShowGooglePlaySubscription : ManageAccountEvent
    object ShowManageSubscriptionDialog : ManageAccountEvent
    data class ShowMessage(@StringRes val messageId: Int) : ManageAccountEvent
}