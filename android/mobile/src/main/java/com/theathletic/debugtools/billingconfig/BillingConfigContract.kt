package com.theathletic.debugtools.billingconfig

import com.theathletic.R
import com.theathletic.debugtools.billingconfig.models.BillingConfigSpinner
import com.theathletic.debugtools.billingconfig.models.BillingConfigToggle
import com.theathletic.presenter.Interactor
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.ui.list.ListViewState

interface BillingConfigContract {

    interface Presenter :
        Interactor,
        BillingConfigToggle.Interactor,
        BillingConfigSpinner.Interactor

    data class BillingConfigViewState(
        override val showSpinner: Boolean,
        override val uiModels: List<UiModel>,
        override val refreshable: Boolean = false,
        override val showToolbar: Boolean = true,
        override val showListUpdateNotification: Boolean = false,
        override val listUpdateLabel: ParameterizedString? = null,
        override val backgroundColorRes: Int = R.color.ath_grey_80
    ) : ListViewState
}