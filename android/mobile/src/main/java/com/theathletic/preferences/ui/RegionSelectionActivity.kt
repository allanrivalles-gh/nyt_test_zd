package com.theathletic.preferences.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity

class RegionSelectionActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, RegionSelectionActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_base)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, RegionSelectionFragment.newInstance())
            .commit()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}