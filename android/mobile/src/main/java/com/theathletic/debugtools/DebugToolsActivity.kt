package com.theathletic.debugtools

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.theathletic.R
import com.theathletic.activity.BaseActivity

class DebugToolsActivity : BaseActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, DebugToolsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_debug_tools)

        setupActionBar()
    }

    private fun setupActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        val bar = supportActionBar
        bar?.let {
            with(bar) {
                setDisplayUseLogoEnabled(false)
                setDisplayShowTitleEnabled(false)
                setDisplayShowHomeEnabled(true)
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }
        }
    }
}