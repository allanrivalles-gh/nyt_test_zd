package com.theathletic.onboarding.paywall.ui

import android.content.Context
import android.content.Intent
import com.theathletic.R
import com.theathletic.activity.SingleFragmentActivity

class OnboardingPaywallActivity : SingleFragmentActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, OnboardingPaywallActivity::class.java)
    }

    override fun getFragment() = OnboardingPaywallFragment.newInstance()

    override fun getStatusBarColor() = R.color.ath_grey_65
}