package com.theathletic.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R

class FullscreenPhotoActivity : BaseActivity() {
    companion object {
        const val EXTRA_URL = "url"

        fun newIntent(context: Context, url: String): Intent {
            val intent = Intent(context, FullscreenPhotoActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_photo)
    }
}