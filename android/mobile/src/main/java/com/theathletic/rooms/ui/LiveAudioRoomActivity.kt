package com.theathletic.rooms.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.featureswitches.FeatureSwitches
import org.koin.android.ext.android.inject

class LiveAudioRoomActivity : BaseActivity() {

    companion object {
        private const val EXTRA_LIVE_ROOM_ID = "extra_live_room_id"

        fun newIntent(
            context: Context,
            liveRoomId: String
        ) = Intent(context, LiveAudioRoomActivity::class.java).apply {
            putExtra(EXTRA_LIVE_ROOM_ID, liveRoomId)
        }
    }

    val featureSwitches: FeatureSwitches by inject()

    override var handlesSystemInsets = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_base)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        intent.extras?.getString(EXTRA_LIVE_ROOM_ID, null)?.let { liveRoomId ->
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    LiveAudioRoomFragment.newInstance(liveRoomId = liveRoomId),
                )
                .commit()
        } ?: finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}