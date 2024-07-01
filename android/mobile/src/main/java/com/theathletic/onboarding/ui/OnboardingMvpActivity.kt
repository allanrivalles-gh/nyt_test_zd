package com.theathletic.onboarding.ui

import android.content.Context
import android.content.Intent
import com.theathletic.activity.SingleFragmentActivity

class OnboardingMvpActivity : SingleFragmentActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, OnboardingMvpActivity::class.java)
    }

    override fun getFragment() = OnboardingFragment.newInstance()
}