package com.theathletic.debugtools.billingconfig

import com.theathletic.R
import com.theathletic.debugtools.billingconfig.models.BillingConfigSpinner
import com.theathletic.debugtools.billingconfig.models.BillingConfigToggle
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.AthleticMvpListFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

class BillingConfigFragment : AthleticMvpListFragment<
    BillingConfigContract.BillingConfigViewState,
    BillingConfigViewModel>() {

    override fun getLayoutForModel(model: UiModel): Int {
        return when (model) {
            is BillingConfigSpinner -> R.layout.list_item_billing_config_spinner
            is BillingConfigToggle -> R.layout.list_item_billing_config_toggle
            else -> throw IllegalArgumentException("$model not supported")
        }
    }

    override fun setupViewModel() = getViewModel<BillingConfigViewModel>()
}