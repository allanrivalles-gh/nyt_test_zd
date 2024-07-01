package com.theathletic.debugtools.billingconfig.models

import androidx.annotation.StringRes
import com.theathletic.ui.UiModel

data class BillingConfigToggle(
    val id: Long,
    @StringRes val textRes: Int,
    val type: BillingConfigToggleType,
    val isActive: Boolean
) : UiModel {
    override val stableId get() = id.toString()

    interface Interactor {
        fun onToggleSelected(type: BillingConfigToggleType)
    }
}

enum class BillingConfigToggleType {
    IS_SUBSCRIBED,
    IS_TRIAL_ELIGIBLE,
    IS_GIFTS_RESPONSE_SUCCESS
}