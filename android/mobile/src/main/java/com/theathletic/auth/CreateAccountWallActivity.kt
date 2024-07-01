package com.theathletic.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.theathletic.BR
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.auth.analytics.AuthenticationNavigationSource
import com.theathletic.databinding.ActivityCreateAccountWallBinding
import com.theathletic.extension.extGetDrawable
import com.theathletic.ui.authentication.CreateAccountWallView
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.LocaleUtilityImpl

class CreateAccountWallActivity : BaseActivity(), CreateAccountWallView {
    var binding: ActivityCreateAccountWallBinding? = null

    companion object {
        fun newIntent(context: Context) = Intent(context, CreateAccountWallActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_out)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account_wall)
        binding?.setVariable(BR.view, this)
        val headerResource = if (LocaleUtilityImpl.isUnitedStatesOrCanada()) R.drawable.bg_create_account_wall_header_us
        else R.drawable.bg_create_account_wall_header
        binding?.headerImage?.setImageDrawable(headerResource.extGetDrawable())
    }

    override fun onCreateAccountClick() {
        ActivityUtility.startAuthenticationActivityOnRegistrationScreen(
            this,
            AuthenticationNavigationSource.PAYWALL
        )
    }

    override fun onLoginClick() {
        ActivityUtility.startAuthenticationActivityOnLoginScreen(
            this,
            AuthenticationNavigationSource.PAYWALL
        )
    }

    override fun onCancelClick() {
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.alpha_in, R.anim.slide_out_to_bottom)
    }
}