package com.theathletic.profile.legacy.account.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.theathletic.R
import com.theathletic.activity.BaseActivity

class LegacyManageAccountActivity : BaseActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LegacyManageAccountActivity::class.java)
        }
    }

    lateinit var fragment: Fragment

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_account)
        fragment = supportFragmentManager.findFragmentById(R.id.fragment_manage_account)!!
        setupActionBar(
            getString(R.string.profile_account_settings),
            findViewById(R.id.toolbar)
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (fragment.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun getStatusBarColor() = R.color.ath_grey_65
}