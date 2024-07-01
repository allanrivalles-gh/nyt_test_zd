package com.theathletic.conduct

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.SingleFragmentActivity
import com.theathletic.fragment.AthleticFragment

const val CODE_OF_CONDUCT_ACTIVITY: Int = 1758

class CodeOfConductSheetActivity : SingleFragmentActivity() {
    companion object {
        fun newIntent(context: Context) = Intent(context, CodeOfConductSheetActivity::class.java)
    }

    override fun getFragment(): AthleticFragment = CodeOfConductSheetFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.alpha_in, R.anim.slide_out_to_bottom)
    }
}