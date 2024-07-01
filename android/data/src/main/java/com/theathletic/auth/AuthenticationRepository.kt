package com.theathletic.auth

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.auth.remote.LoginWithEmailFetcher

class AuthenticationRepository @AutoKoin constructor(
    private val loginWithEmailFetcher: LoginWithEmailFetcher
) {
    suspend fun loginWithEmail(
        email: String,
        password: String
    ) = loginWithEmailFetcher.fetchRemote(LoginWithEmailFetcher.Params(email, password))
}