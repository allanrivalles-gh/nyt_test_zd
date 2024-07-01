package com.theathletic.profile.addfollowing

import android.content.Context
import android.content.Intent
import com.theathletic.activity.SingleFragmentActivity

class AddFollowingActivity : SingleFragmentActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, AddFollowingActivity::class.java)
    }

    override fun getFragment() = AddFollowingFragment.newInstance()
}