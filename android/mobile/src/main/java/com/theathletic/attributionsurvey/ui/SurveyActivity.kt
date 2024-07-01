package com.theathletic.attributionsurvey.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity

class SurveyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attribution_survey)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    companion object {
        const val ATTRIBUTION_SURVEY_REQUEST_CODE = 13234

        fun newIntent(context: Context) = Intent(context, SurveyActivity::class.java)
    }
}