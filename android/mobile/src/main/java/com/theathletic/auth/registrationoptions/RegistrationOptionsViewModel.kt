package com.theathletic.auth.registrationoptions

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.theathletic.AthleticApplication
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.auth.OAuthFlow
import com.theathletic.auth.OAuthHelper
import com.theathletic.auth.OAuthResult
import com.theathletic.auth.analytics.AuthenticationAnalyticsContext
import com.theathletic.auth.loginoptions.AuthorizationUrlCreator
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.State
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.INITIAL
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.LAUNCH_OAUTH_FLOW
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.LOADING_ATHLETIC_SIGNUP_CALL
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.LOADING_OAUTH_FLOW
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.OAUTH_FLOW_ERROR
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.SIGNUP_ERROR
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.SIGNUP_SUCCESS
import com.theathletic.extension.toAnalyticsString
import com.theathletic.ui.updateState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class RegistrationOptionsViewModel @AutoKoin constructor(
    private val analytics: Analytics,
    private val authorizationUrlCreator: AuthorizationUrlCreator,
    private val analyticsContext: AuthenticationAnalyticsContext,
    private val oauthHelper: OAuthHelper
) : AndroidViewModel(AthleticApplication.getContext()) {
    private val _state = MutableStateFlow(State(INITIAL))

    val state = _state.onEach { state ->
        state.activeAuthFlow?.analyticsName?.let { method ->
            trackCreateAccountIfNeeded(method, state.type)
        }
    }

    init {
        analytics.track(
            Event.Authentication.SignUpPageView(
                login_entry_point = analyticsContext.navigationSource.analyticsKey
            )
        )
    }

    fun onStartOAuthFlow(authFlow: OAuthFlow) {
        viewModelScope.launch {
            _state.updateState { copy(type = LOADING_OAUTH_FLOW, activeAuthFlow = authFlow) }
            val nonceAndUrl = authorizationUrlCreator.createAuthRequestUrl(authFlow)
            _state.updateState { copy(type = LAUNCH_OAUTH_FLOW, oAuthUrl = nonceAndUrl) }
        }
    }

    /**
     * We do not have a guarantee on when onNewIntent will be called relative to other lifecycle
     * hooks. To avoid hiding the spinner prematurely, we wait 500ms before confirming that the
     * state has not been changed (as it would if we proceeded with a login call after receiving
     * success intent from oauth redirect) which is our signal that the user backed out of the
     * oauth flow rather than completing it.
     */
    fun onResume() {
        viewModelScope.launch {
            if (_state.value.type == LAUNCH_OAUTH_FLOW) {
                delay(500)
                if (_state.value.type == LAUNCH_OAUTH_FLOW) {
                    _state.updateState { copy(type = INITIAL) }
                }
            }
        }
    }

    fun signup(rawOAuthResult: String) {
        Timber.d("rawOAuthResult: $rawOAuthResult")
        viewModelScope.launch {
            _state.updateState { copy(type = LOADING_ATHLETIC_SIGNUP_CALL) }
            when (oauthHelper.useOAuth(rawOAuthResult, _state.value.activeAuthFlow)) {
                OAuthResult.SUCCESS -> _state.updateState { copy(type = SIGNUP_SUCCESS) }
                OAuthResult.FAILURE -> _state.updateState { copy(type = SIGNUP_ERROR) }
                OAuthResult.CANCELLED -> _state.updateState { copy(type = INITIAL) }
            }
        }
    }

    private fun trackCreateAccountIfNeeded(
        loginMethodUsed: String,
        type: RegistrationOptionsContract.StateType
    ) {
        when (type) {
            SIGNUP_SUCCESS -> trackCreateAccountSuccess(loginMethodUsed)
            SIGNUP_ERROR -> trackCreateAccountError(loginMethodUsed)
            OAUTH_FLOW_ERROR -> trackCreateAccountError(loginMethodUsed)
            else -> {}
        }
    }

    private fun trackCreateAccountSuccess(loginMethodUsed: String) {
        analytics.track(
            Event.Onboarding.AccountCreated(
                object_id = loginMethodUsed,
                success = true.toAnalyticsString(),
                login_entry_point = analyticsContext.navigationSource.analyticsKey
            )
        )
    }

    private fun trackCreateAccountError(loginMethodUsed: String) {
        analytics.track(
            Event.Onboarding.AccountCreated(
                object_id = loginMethodUsed,
                success = false.toAnalyticsString(),
                error_code = "Error creating new account",
                login_entry_point = analyticsContext.navigationSource.analyticsKey
            )
        )
    }
}