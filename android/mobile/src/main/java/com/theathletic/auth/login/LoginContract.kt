package com.theathletic.auth.login

import com.theathletic.utility.Event

class LoginContract {
    enum class StateType {
        DEFAULT,
        DEFAULT_EMAIL_DISPLAY,
        INVALID_EMAIL,
        ENABLE_LOGIN,
        DISABLE_LOGIN,
        SHOW_LOADER
    }

    data class State(
        val type: StateType,
        val email: String = "",
        val password: String = "",
        val showEmailError: Boolean = false,
        val isLoginBtnEnabled: Boolean = false,
        val showLoader: Boolean = false
    ) {
        override fun toString(): String {
            return "State(type=$type, showEmailError=$showEmailError, " +
                "isLoginBtnEnabled=$isLoginBtnEnabled, showLoader=$showLoader)"
        }
    }

    sealed class Effect : Event() {
        class AuthError(val isInvalidLogin: Boolean) : Effect()
        object AuthSuccess : Effect()
        object NetworkError : Effect()
        object OpenForgotPassword : Effect()
    }
}