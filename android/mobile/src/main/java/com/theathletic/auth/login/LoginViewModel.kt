package com.theathletic.auth.login

import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.auth.AuthViewModel
import com.theathletic.auth.Authenticator
import com.theathletic.auth.analytics.AuthenticationAnalyticsContext
import com.theathletic.auth.data.AuthenticationRepository
import com.theathletic.auth.login.LoginContract.Effect.AuthError
import com.theathletic.auth.login.LoginContract.Effect.AuthSuccess
import com.theathletic.auth.login.LoginContract.Effect.NetworkError
import com.theathletic.auth.login.LoginContract.Effect.OpenForgotPassword
import com.theathletic.auth.login.LoginContract.StateType.DEFAULT
import com.theathletic.auth.login.LoginContract.StateType.DEFAULT_EMAIL_DISPLAY
import com.theathletic.auth.login.LoginContract.StateType.DISABLE_LOGIN
import com.theathletic.auth.login.LoginContract.StateType.ENABLE_LOGIN
import com.theathletic.auth.login.LoginContract.StateType.INVALID_EMAIL
import com.theathletic.auth.login.LoginContract.StateType.SHOW_LOADER
import com.theathletic.entity.authentication.PasswordCredentials
import com.theathletic.extension.toAnalyticsString
import com.theathletic.network.ResponseStatus
import com.theathletic.repository.safeApiRequest
import com.theathletic.ui.LegacyAthleticViewModel
import com.theathletic.ui.updateState
import com.theathletic.utility.NetworkManager
import com.theathletic.utility.logging.ICrashLogHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class LoginViewModel @AutoKoin constructor(
    private val analytics: Analytics,
    private val authenticationRepository: AuthenticationRepository,
    private val authenticator: Authenticator,
    private val crashHandler: ICrashLogHandler,
    private val analyticsContext: AuthenticationAnalyticsContext
) : LegacyAthleticViewModel(), AuthViewModel {
    private companion object {
        const val INVALID_LOGIN_CODE = 401
    }

    private var emailLoginJob: Job? = null

    private val _state = MutableStateFlow(LoginContract.State(DEFAULT))
    val state = _state

    private var hasEmailBeenInvalidatedAndNotSincePassedValidation = false

    fun onTapForgotPassword() {
        analytics.track(Event.Authentication.ClickSignInPage(element = "forgot_password"))
        sendEvent(OpenForgotPassword)
    }

    fun onInput(email: String, password: String) {
        viewModelScope.launch {
            _state.updateState { copy(email = email, password = password) }
            val isEmailValid = withContext(Dispatchers.Default) { isEmailValid(email) }
            val isPasswordValid = isPasswordValid(password)
            if (hasEmailBeenInvalidatedAndNotSincePassedValidation && !isEmailValid) {
                _state.updateState { copy(type = INVALID_EMAIL, showEmailError = true) }
                hasEmailBeenInvalidatedAndNotSincePassedValidation = true
            }
            if (hasEmailBeenInvalidatedAndNotSincePassedValidation && isEmailValid) {
                _state.updateState { copy(type = DEFAULT_EMAIL_DISPLAY, showEmailError = false) }
                hasEmailBeenInvalidatedAndNotSincePassedValidation = false
            }
            if (isEmailValid && isPasswordValid) {
                hasEmailBeenInvalidatedAndNotSincePassedValidation = false
                _state.updateState { copy(type = ENABLE_LOGIN, isLoginBtnEnabled = true, showEmailError = false) }
            } else {
                _state.updateState { copy(type = DISABLE_LOGIN, isLoginBtnEnabled = false) }
            }
        }
    }

    fun onEmailFieldLosesFocus(input: String) {
        Timber.d("onEmailFieldLosesFocus")
        viewModelScope.launch {
            if (isEmailValid(input)) {
                _state.updateState { copy(type = DEFAULT_EMAIL_DISPLAY, showEmailError = false) }
            } else {
                hasEmailBeenInvalidatedAndNotSincePassedValidation = true
                _state.updateState { copy(type = INVALID_EMAIL, showEmailError = true) }
            }
        }
    }

    fun sendLoginRequest(email: String, password: String) {
        if (emailLoginJob?.isActive == true) return
        emailLoginJob = viewModelScope.launch {
            if (NetworkManager.getInstance().isOffline()) {
                sendEvent(NetworkError)
                _state.updateState { copy(type = DEFAULT, showLoader = false) }
                return@launch
            }

            _state.updateState { copy(type = SHOW_LOADER, showLoader = true) }
            createV5AuthRequest(email, password)
        }
    }

    private fun isPasswordValid(input: String): Boolean {
        return input.length > 7
    }

    private suspend fun createV5AuthRequest(email: String, password: String) {
        val credentials = PasswordCredentials(email, password)
        val response = safeApiRequest { authenticationRepository.authV5WithEmail(credentials) }
        when (response) {
            is ResponseStatus.Success -> {
                authenticator.onSuccessfulLogin(response.body.user, response.body.accessToken)
                sendEvent(AuthSuccess)
                analytics.track(
                    Event.Authentication.Login(
                        object_id = "email",
                        success = true.toAnalyticsString(),
                        login_entry_point = analyticsContext.navigationSource.analyticsKey
                    )
                )
            }
            is ResponseStatus.Error -> {
                analytics.track(
                    Event.Authentication.Login(
                        object_id = "email",
                        error_code = "Error logging in",
                        success = false.toAnalyticsString(),
                        login_entry_point = analyticsContext.navigationSource.analyticsKey
                    )
                )
                crashHandler.trackException(response.throwable, cause = "LOGIN_V5_EXCEPTION")

                sendEvent(AuthError((response.throwable as? HttpException)?.code() == INVALID_LOGIN_CODE))
                _state.updateState { copy(type = DEFAULT, showLoader = false) }
            }
        }
    }
}