package com.theathletic.auth.loginoptions

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
import com.theathletic.auth.analytics.AuthenticationNavigationSource
import com.theathletic.auth.loginoptions.LoginOptionsContract.State
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.INITIAL
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.LAUNCH_OAUTH_FLOW
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.LOADING_ATHLETIC_LOGIN_CALL
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.LOADING_OAUTH_FLOW
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.LOGIN_ERROR
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.LOGIN_SUCCESS
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.OAUTH_FLOW_ERROR
import com.theathletic.extension.toAnalyticsString
import com.theathletic.ui.updateState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginOptionsViewModel @AutoKoin constructor(
    private val analytics: Analytics,
    private val authorizationUrlCreator: AuthorizationUrlCreator,
    private val analyticsContext: AuthenticationAnalyticsContext,
    private val oAuthHelper: OAuthHelper
) : AndroidViewModel(AthleticApplication.getContext()) {
    private val _state = MutableStateFlow(State(INITIAL))
    val state = _state.onEach { state ->
        state.activeAuthFlow?.analyticsName?.let { method ->
            trackLoginIfNeeded(method, state.type)
        }
    }

    init {
        analytics.track(
            Event.Authentication.SignInPageView(
                login_entry_point = analyticsContext.navigationSource.analyticsKey
            )
        )
    }

    fun showSignUp() = analyticsContext.navigationSource != AuthenticationNavigationSource.START_SCREEN

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

    fun login(rawOAuthResult: String) {
        Timber.d("rawOAuthResult: $rawOAuthResult")
        viewModelScope.launch {
            _state.updateState { copy(type = LOADING_ATHLETIC_LOGIN_CALL) }
            when (oAuthHelper.useOAuth(rawOAuthResult, _state.value.activeAuthFlow)) {
                OAuthResult.SUCCESS -> _state.updateState { copy(type = LOGIN_SUCCESS) }
                OAuthResult.FAILURE -> _state.updateState { copy(type = LOGIN_ERROR) }
                OAuthResult.CANCELLED -> _state.updateState { copy(type = INITIAL) }
            }
        }
    }

    private fun trackLoginIfNeeded(
        loginMethodUsed: String,
        type: LoginOptionsContract.StateType
    ) {
        when (type) {
            LOGIN_SUCCESS -> trackLoginSuccess(loginMethodUsed)
            LOGIN_ERROR -> trackLoginFailure(loginMethodUsed)
            OAUTH_FLOW_ERROR -> trackLoginFailure(loginMethodUsed)
            else -> {}
        }
    }

    private fun trackLoginSuccess(loginMethodUsed: String) {
        analytics.track(
            Event.Authentication.Login(
                object_id = loginMethodUsed,
                success = true.toAnalyticsString(),
                login_entry_point = analyticsContext.navigationSource.analyticsKey
            )
        )
    }

    private fun trackLoginFailure(loginMethodUsed: String) {
        analytics.track(
            Event.Authentication.Login(
                object_id = loginMethodUsed,
                error_code = "Error logging in",
                success = false.toAnalyticsString(),
                login_entry_point = analyticsContext.navigationSource.analyticsKey
            )
        )
    }
}