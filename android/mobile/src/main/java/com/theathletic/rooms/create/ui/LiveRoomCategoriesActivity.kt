package com.theathletic.rooms.create.ui

import android.content.Context
import android.content.Intent
import com.theathletic.activity.SingleFragmentActivity

class LiveRoomCategoriesActivity : SingleFragmentActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, LiveRoomCategoriesActivity::class.java)
    }

    override fun getFragment() = LiveRoomCategoriesFragment.newInstance()
}