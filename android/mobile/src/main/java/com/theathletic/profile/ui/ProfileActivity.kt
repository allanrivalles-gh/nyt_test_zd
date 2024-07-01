package com.theathletic.profile.ui

import android.content.Context
import android.content.Intent
import com.theathletic.R
import com.theathletic.activity.SingleFragmentActivity

class ProfileActivity : SingleFragmentActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }

    override fun getFragment() = ProfileFragment.newInstance()

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun getStatusBarColor() = R.color.ath_grey_65
}