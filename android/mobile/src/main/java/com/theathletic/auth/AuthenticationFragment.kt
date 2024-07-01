package com.theathletic.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.auth.ui.AuthenticationSpannableFormatter
import com.theathletic.databinding.FragmentAuthenticationBinding
import com.theathletic.extension.viewModelProvider
import com.theathletic.fragment.AthleticBindingFragment
import com.theathletic.ui.authentication.AuthenticationView
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.NotificationPermissionRequest
import org.koin.android.ext.android.inject

class AuthenticationFragment :
    AthleticBindingFragment<AuthenticationViewModel, FragmentAuthenticationBinding>(),
    AuthFragment,
    AuthenticationView {
    private val analytics by inject<Analytics>()

    companion object {
        fun newInstance() = AuthenticationFragment()
    }

    override fun inflateBindingLayout(inflater: LayoutInflater) = FragmentAuthenticationBinding.inflate(inflater)

    override fun setupViewModel(): AuthenticationViewModel {
        val viewModel = viewModelProvider { AuthenticationViewModel() }
        lifecycle.addObserver(viewModel)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.executePendingBindings()
        AuthenticationSpannableFormatter.configureLoginRegisterSpannableCTA(
            binding.note,
            R.string.authentication_text_note,
            R.string.authentication_text_spannable_log_in
        )

        NotificationPermissionRequest(this, analytics).requestPermission()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onGetStartedClick() {
        viewModel.beginOnboarding()
        authActivity().continueFromAuthentication(shouldShowOnboarding = true)
    }

    override fun onLoginClick() {
        analytics.track(Event.Authentication.ClickLoginLink())
        authActivity().switchToFragment(AuthenticationActivity.FragmentType.LOGIN_OPTIONS)
    }

    override fun openDebugToolsClick() {
        ActivityUtility.startDebugToolsActivity(context)
    }
}