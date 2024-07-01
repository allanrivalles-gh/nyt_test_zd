package com.theathletic.subscriptionplans

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.billing.BillingSku
import com.theathletic.ui.Transformer
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.utility.safeLet
import com.theathletic.widget.StatefulLayout

class SubscriptionPlansTransformer @AutoKoin constructor() :
    Transformer<SubscriptionPlansState, SubscriptionPlansContract.SubscriptionPlansViewState> {

    override fun transform(
        data: SubscriptionPlansState
    ): SubscriptionPlansContract.SubscriptionPlansViewState {
        return when {
            data.isSetupComplete -> createBillingState(data)
            else -> createLoadingState()
        }
    }

    private fun createLoadingState() =
        SubscriptionPlansContract.SubscriptionPlansViewState(state = StatefulLayout.PROGRESS)

    private fun createErrorState() =
        SubscriptionPlansContract.SubscriptionPlansViewState(state = StatefulLayout.EMPTY)

    private fun createBillingState(
        data: SubscriptionPlansState
    ): SubscriptionPlansContract.SubscriptionPlansViewState {
        if (data.isSpecialOffer) {
            return data.annualPlan?.let { createSpecialOffer(it) } ?: createErrorState()
        }

        return safeLet(data.annualPlan, data.monthlyPlan) { annualPlan, monthlyPlan ->
            createNonSpecialOffer(
                data.isTrialEligible,
                data.isAnnualPlanSelected,
                annualPlan,
                monthlyPlan
            )
        } ?: createErrorState()
    }

    private fun createNonSpecialOffer(
        isTrialEligible: Boolean,
        isAnnualPlanSelected: Boolean,
        annualPlan: BillingSku,
        monthlyPlan: BillingSku
    ): SubscriptionPlansContract.SubscriptionPlansViewState {
        return if (isTrialEligible) {
            createFreeTrialOffer(
                isAnnualPlanSelected,
                annualPlan,
                monthlyPlan
            )
        } else {
            createNoTrialOffer(
                isAnnualPlanSelected,
                annualPlan,
                monthlyPlan
            )
        }
    }

    private fun createFreeTrialOffer(
        isAnnualPlanSelected: Boolean,
        annualPlan: BillingSku,
        monthlyPlan: BillingSku
    ) = SubscriptionPlansContract.SubscriptionPlansViewState(
        state = StatefulLayout.CONTENT,
        isAnnualPlanSelected = isAnnualPlanSelected,
        annualPlanSpecial = ParameterizedString(R.string.plans_annual_free_period),
        strikethroughPrice = monthlyPlan.monthlyPrice,
        annualPlanPrice = ParameterizedString(R.string.plans_monthly_price, annualPlan.monthlyPrice),
        annualPlanNoteRes = R.string.paywall_annual_billing,
        monthlyPlanPrice = ParameterizedString(R.string.plans_monthly_price, monthlyPlan.monthlyPrice),
        subscribeButtonRes = if (isAnnualPlanSelected) {
            R.string.article_paywall_start_trial
        } else {
            R.string.paywall_article_preview_subscribe_button
        }
    )

    private fun createNoTrialOffer(
        isAnnualPlanSelected: Boolean,
        annualPlan: BillingSku,
        monthlyPlan: BillingSku
    ) = SubscriptionPlansContract.SubscriptionPlansViewState(
        state = StatefulLayout.CONTENT,
        isAnnualPlanSelected = isAnnualPlanSelected,
        annualPlanSpecial = ParameterizedString(""),
        strikethroughPrice = monthlyPlan.monthlyPrice,
        annualPlanPrice = ParameterizedString(R.string.plans_monthly_price, annualPlan.monthlyPrice),
        annualPlanNoteRes = R.string.paywall_annual_billing_no_trial,
        monthlyPlanPrice = ParameterizedString(R.string.plans_monthly_price, monthlyPlan.monthlyPrice),
        subscribeButtonRes = R.string.paywall_article_preview_subscribe_button
    )

    private fun createSpecialOffer(
        sku: BillingSku
    ) = SubscriptionPlansContract.SubscriptionPlansViewState(
        state = StatefulLayout.CONTENT,
        isAnnualPlanSelected = true,
        annualPlanSpecial = ParameterizedString(R.string.plans_annual_save_xx, sku.percentOff),
        strikethroughPrice = sku.price,
        annualPlanPrice = ParameterizedString(R.string.plans_annual_price, sku.introPrice)
    )
}