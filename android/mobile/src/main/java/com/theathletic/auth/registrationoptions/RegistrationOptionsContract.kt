package com.theathletic.auth.registrationoptions

import com.theathletic.R
import com.theathletic.auth.OAuthFlow
import com.theathletic.auth.loginoptions.AuthorizationUrlCreator.AuthUrl

class RegistrationOptionsContract {
    enum class StateType {
        INITIAL,
        LOADING_OAUTH_FLOW,
        LAUNCH_OAUTH_FLOW,
        OAUTH_FLOW_ERROR,
        LOADING_ATHLETIC_SIGNUP_CALL,
        SIGNUP_SUCCESS,
        SIGNUP_ERROR
    }

    data class State(
        val type: StateType,
        val activeAuthFlow: OAuthFlow? = null,
        val oAuthUrl: AuthUrl? = null,
        val errorMessage: Int = R.string.global_error
    )
}