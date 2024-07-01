package com.theathletic.auth

import com.theathletic.analytics.KochavaWrapper
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.compass.CompassClient
import com.theathletic.entity.user.UserEntity
import com.theathletic.onboarding.CompleteOnboardingUseCase
import com.theathletic.user.UserManager
import com.theathletic.user.data.UserRepository
import com.theathletic.utility.Preferences
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.withContext

/**
 * This class is responsible for transitioning the user from an unauthenticated state to an
 * authenticated state.
 */
class Authenticator @AutoKoin(Scope.FACTORY) constructor(
    private val compassClient: CompassClient,
    private val dispatchers: DispatcherProvider,
    private val userRepository: UserRepository,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val kochavaWrapper: KochavaWrapper,
) {

    suspend fun onSuccessfulLogin(userEntity: UserEntity?, accessToken: String?) {
        withContext(dispatchers.io) {
            compassClient.loadConfig(
                isUserLoggedIn = true,
                forceFetchFromNetwork = true
            )
        }
        Preferences.accessToken = accessToken
        completeOnboardingUseCase()

        UserManager.saveCurrentUser(userEntity)
        // This populates fields from the user graphql endpoint
        userRepository.fetchUser(userEntity?.id)
    }

    suspend fun onSuccessfulSignup(userEntity: UserEntity?, accessToken: String?) {
        withContext(dispatchers.io) {
            compassClient.loadConfig(
                isUserLoggedIn = true,
                forceFetchFromNetwork = true
            )
        }
        if (accessToken != null) {
            Preferences.accessToken = accessToken
        }
        completeOnboardingUseCase()
        UserManager.saveCurrentUser(userEntity)
        // This populates fields from the user graphql endpoint
        userRepository.fetchUser(userEntity?.id)
        userEntity?.id?.let { userId ->
            kochavaWrapper.sendRegistrationComplete(userId.toString())
        }
    }
}