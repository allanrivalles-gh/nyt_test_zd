package com.theathletic.debugtools.logs.ui

import com.theathletic.activity.SingleFragmentActivity

class AnalyticsLogActivity : SingleFragmentActivity() {

    override fun getFragment() = AnalyticsLogFragment.newInstance()
}