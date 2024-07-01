package com.theathletic.auth.registration

import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.analytics.AnalyticsManager
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.Event.Authentication.SignUp
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.auth.AuthViewModel
import com.theathletic.auth.Authenticator
import com.theathletic.auth.analytics.AuthenticationAnalyticsContext
import com.theathletic.auth.data.AuthenticationRepository
import com.theathletic.auth.registration.RegistrationContract.Effect.ContinueAuthFlow
import com.theathletic.auth.registration.RegistrationContract.Effect.ErrorCreatingAccount
import com.theathletic.auth.registration.RegistrationContract.Effect.NetworkError
import com.theathletic.auth.registration.RegistrationContract.Page.EMAIL
import com.theathletic.auth.registration.RegistrationContract.Page.NAME
import com.theathletic.auth.registration.RegistrationContract.State
import com.theathletic.auth.registration.RegistrationContract.StateType.DEFAULT
import com.theathletic.auth.registration.RegistrationContract.StateType.EMAIL_INVALID_EMAIL
import com.theathletic.auth.registration.RegistrationContract.StateType.EMAIL_INVALID_PASSWORD
import com.theathletic.auth.registration.RegistrationContract.StateType.ERROR_CREATING_ACCOUNT
import com.theathletic.auth.registration.RegistrationContract.StateType.ON_INPUT
import com.theathletic.auth.registration.RegistrationContract.StateType.TRANSITION_TO_NAME_PAGE
import com.theathletic.auth.registration.RegistrationContract.StateType.WAITING_FOR_CREATE_ACCOUNT_RESPONSE
import com.theathletic.event.SnackbarEvent
import com.theathletic.extension.extGetString
import com.theathletic.extension.safe
import com.theathletic.extension.toAnalyticsString
import com.theathletic.extension.toLong
import com.theathletic.network.ResponseStatus.Error
import com.theathletic.network.ResponseStatus.Success
import com.theathletic.repository.safeApiRequest
import com.theathletic.settings.data.remote.SettingsRestApi
import com.theathletic.ui.LegacyAthleticViewModel
import com.theathletic.ui.updateState
import com.theathletic.utility.LocaleUtilityImpl
import com.theathletic.utility.NetworkManager
import com.theathletic.utility.logging.ICrashLogHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await

class RegistrationViewModel @AutoKoin constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val analytics: Analytics,
    private val networkManager: NetworkManager,
    private val authenticator: Authenticator,
    private val crashLogHandler: ICrashLogHandler,
    private val settingsRestApi: SettingsRestApi,
    private val analyticsContext: AuthenticationAnalyticsContext
) : LegacyAthleticViewModel(), AuthViewModel {
    private val _state = MutableStateFlow(State(DEFAULT))
    val state = _state

    private var hasAttemptedToSubmitMalformedEmail = false
    private var hasAttemptedToSubmitMalformedPassword = false

    private var completeAccountJob: Job? = null

    init {
        updateState {
            copy(
                isPromoCheckboxVisible = LocaleUtilityImpl.isGDPRCountry(),
                receivePromos = !LocaleUtilityImpl.isGDPRCountry()
            )
        }
    }

    fun onInput(
        email: String = _state.value.email,
        password: String = _state.value.password,
        firstName: String = _state.value.firstName,
        lastName: String = _state.value.lastName,
        receivePromoEmails: Boolean = _state.value.receivePromos
    ) {
        val showInvalidEmailError = hasAttemptedToSubmitMalformedEmail &&
            !isEmailValid(_state.value.email)
        if (hasAttemptedToSubmitMalformedEmail && !showInvalidEmailError) {
            hasAttemptedToSubmitMalformedEmail = false
        }
        updateState {
            copy(
                type = ON_INPUT,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                showInvalidEmailError = showInvalidEmailError,
                isNextBtnEnabled = isEmailValid(email) && password.isNotEmpty(),
                isCompleteSignupBtnEnabled = firstName.isNotEmpty() && lastName.isNotEmpty(),
                showPasswordValidationError = hasAttemptedToSubmitMalformedPassword &&
                    isPasswordLengthInvalid(password),
                receivePromos = receivePromoEmails
            )
        }
    }

    fun onBackPressed(): Boolean {
        return if (_state.value.activePage == NAME) {
            updateState { copy(activePage = EMAIL) }
            true
        } else {
            false
        }
    }

    fun onEmailLostFocus() {
        updateState {
            hasAttemptedToSubmitMalformedEmail = true
            copy(
                type = EMAIL_INVALID_EMAIL,
                showInvalidEmailError = !isEmailValid(_state.value.email)
            )
        }
    }

    fun onEmailPageNextClicked() {
        analytics.track(Event.Authentication.ClickEmailContinue())
        if (!networkManager.isOnline()) {
            sendEvent(NetworkError)
            return
        }

        if (isPasswordLengthInvalid(_state.value.password)) {
            hasAttemptedToSubmitMalformedPassword = true
            updateState { copy(type = EMAIL_INVALID_PASSWORD, showPasswordValidationError = true) }
            return
        }

        proceedToNamePage()
    }

    private fun proceedToNamePage() {
        _state.updateState {
            copy(
                type = TRANSITION_TO_NAME_PAGE,
                showAccountAlreadyExistsError = false,
                activePage = NAME,
                showLoading = false
            )
        }
    }

    @Suppress("ComplexCondition")
    fun sendRegistrationRequest() {
        if (_state.value.email.isEmpty() ||
            _state.value.password.isEmpty() ||
            _state.value.firstName.isEmpty() ||
            _state.value.lastName.isEmpty()
        ) {
            sendEvent(SnackbarEvent(R.string.global_error.extGetString()))
            return
        }

        if (!networkManager.isOnline()) {
            sendEvent(NetworkError)
            return
        }

        if (completeAccountJob == null || completeAccountJob?.isActive == false) {
            completeAccountJob = viewModelScope.launch {
                _state.updateState { copy(type = WAITING_FOR_CREATE_ACCOUNT_RESPONSE, showLoading = true) }
                createAccount()
            }
        }
    }

    private suspend fun registerPromoEmailPreference(shouldReceive: Boolean) {
        if (LocaleUtilityImpl.isGDPRCountry()) {
            safeApiRequest {
                settingsRestApi.togglePromoEmail(shouldReceive.toLong()).await()
            }.onError {
                crashLogHandler.trackException(
                    it,
                    message = "Exception when setting promotional email preference on sign-up"
                )
            }
        }
    }

    private suspend fun createAccount() {
        val receivePromoEmails = _state.value.receivePromos
        val response = safeApiRequest {
            authenticationRepository.createAccount(
                _state.value.email,
                _state.value.password,
                _state.value.firstName,
                _state.value.lastName
            )
        }
        when (response) {
            is Success -> {
                val userEntity = response.body.first
                val authToken = response.body.second
                authenticator.onSuccessfulSignup(
                    userEntity,
                    authToken
                )
                trackCreateAccountSuccess()
                registerPromoEmailPreference(receivePromoEmails)
                sendEvent(ContinueAuthFlow)
            }
            is Error -> {
                trackCreateAccountError(response.throwable)
                if (response.throwable.localizedMessage.isNullOrEmpty()) {
                    sendEvent(SnackbarEvent(R.string.registration_error_email.extGetString()))
                } else {
                    sendEvent(SnackbarEvent(response.throwable.localizedMessage ?: ""))
                }
                _state.updateState { copy(type = ERROR_CREATING_ACCOUNT, showLoading = false) }
                sendEvent(ErrorCreatingAccount)
            }
        }.safe
    }

    private fun trackCreateAccountSuccess() {
        analytics.track(SignUp(AnalyticsManager.SignInServiceType.EMAIL.value, true.toString()))
        analytics.track(
            Event.Onboarding.AccountCreated(
                object_id = AnalyticsManager.SignInServiceType.EMAIL.value,
                success = true.toAnalyticsString(),
                login_entry_point = analyticsContext.navigationSource.analyticsKey
            )
        )
    }

    private fun trackCreateAccountError(error: Throwable) {
        analytics.track(
            SignUp(AnalyticsManager.SignInServiceType.EMAIL.toString(), false.toString())
        )
        analytics.track(
            Event.Onboarding.AccountCreated(
                object_id = AnalyticsManager.SignInServiceType.EMAIL.value,
                success = false.toAnalyticsString(),
                error_code = error.localizedMessage ?: "unknown error",
                login_entry_point = analyticsContext.navigationSource.analyticsKey
            )
        )
    }

    private fun isPasswordLengthInvalid(password: String) = password.length !in 8..64

    private fun updateState(updateBlock: State.() -> State) {
        viewModelScope.launch {
            _state.updateState { updateBlock() }
        }
    }
}