package com.theathletic.debugtools.userinfo.ui

import com.theathletic.activity.SingleFragmentActivity

class DebugUserInfoActivity : SingleFragmentActivity() {

    override fun getFragment() = DebugUserInfoFragment.newInstance()
}