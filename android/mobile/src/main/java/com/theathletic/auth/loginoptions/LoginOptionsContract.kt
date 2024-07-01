package com.theathletic.auth.loginoptions

import com.theathletic.R
import com.theathletic.auth.OAuthFlow
import com.theathletic.auth.loginoptions.AuthorizationUrlCreator.AuthUrl

class LoginOptionsContract {
    enum class StateType {
        INITIAL,
        LOADING_OAUTH_FLOW,
        LAUNCH_OAUTH_FLOW,
        OAUTH_FLOW_ERROR,
        LOADING_ATHLETIC_LOGIN_CALL,
        LOGIN_SUCCESS,
        LOGIN_ERROR
    }

    data class State(
        val type: StateType,
        val activeAuthFlow: OAuthFlow? = null,
        val oAuthUrl: AuthUrl? = null,
        val errorMessage: Int = R.string.global_error
    )
}