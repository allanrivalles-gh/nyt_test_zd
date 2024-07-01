package com.theathletic.debugtools.billingconfig.models

import com.theathletic.ui.UiModel

data class BillingConfigSpinner(
    val id: Long,
    val selectedIndex: Int,
    val options: List<String>
) : UiModel {
    override val stableId = id.toString()

    interface Interactor {
        fun onSpinnerClicked(newSelection: Int)
    }
}