package com.theathletic.subscriptionplans

import com.theathletic.R
import com.theathletic.presenter.Interactor
import com.theathletic.ui.ViewState
import com.theathletic.ui.binding.ParameterizedString

interface SubscriptionPlansContract {

    interface Presenter : Interactor {
        fun onAnnualPlanClick()
        fun onMonthlyPlanClick()
        fun onContinueClick()
        fun onCloseClick()
        fun onTermsOfServiceClick()
        fun onPrivacyPolicyClick()
    }

    data class SubscriptionPlansViewState(
        val state: Int,
        val isAnnualPlanSelected: Boolean = true,
        val annualPlanSpecial: ParameterizedString? = null,
        val strikethroughPrice: String? = null,
        val annualPlanPrice: ParameterizedString? = null,
        val annualPlanNoteRes: Int = R.string.paywall_annual_billing,
        val monthlyPlanPrice: ParameterizedString? = null,
        val subscribeButtonRes: Int = R.string.article_paywall_start_trial
    ) : ViewState
}