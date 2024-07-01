package com.theathletic.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.utility.ActivityUtility

class GoogleServicesUnavailableActivity : BaseActivity() {
    companion object {
        fun newIntent(context: Context): Intent = Intent(context, GoogleServicesUnavailableActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_services_unavailable)
    }

    fun onGetNewVersionClick(view: View) {
        ActivityUtility.startWebViewActivity(this, AthleticConfig.GOOGLE_SERVICES_PLAY_STORE_URL)
        finish()
    }
}