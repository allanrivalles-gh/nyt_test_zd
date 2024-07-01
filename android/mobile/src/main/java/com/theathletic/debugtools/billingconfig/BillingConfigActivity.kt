package com.theathletic.debugtools.billingconfig

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity

class BillingConfigActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, BillingConfigActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing_config)
    }
}