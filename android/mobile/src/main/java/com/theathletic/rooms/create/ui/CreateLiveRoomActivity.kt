package com.theathletic.rooms.create.ui

import android.content.Context
import android.content.Intent
import com.theathletic.activity.SingleFragmentActivity

class CreateLiveRoomActivity : SingleFragmentActivity() {

    companion object {
        private const val EXTRA_ROOM_TO_EDIT = "extra_room_to_edit"

        fun newIntent(
            context: Context,
            roomToEditId: String? = null
        ) = Intent(context, CreateLiveRoomActivity::class.java).apply {
            putExtra(EXTRA_ROOM_TO_EDIT, roomToEditId)
        }
    }

    override fun getFragment() = CreateLiveRoomComposeFragment.newInstance(
        roomToEditId = intent.getStringExtra(EXTRA_ROOM_TO_EDIT)
    )
}