package com.theathletic.onboarding

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.domain.FollowableItem
import com.theathletic.onboarding.data.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetChosenFollowablesUseCase @AutoKoin constructor(
    private val onboardingRepository: OnboardingRepository,
    private val followableRepository: FollowableRepository
) {
    operator fun invoke(): Flow<List<FollowableItem>> {
        return flow {
            if (followableRepository.getCountOfTeams() == 0L) {
                Timber.w("No followables were loaded prior to onboarding; we expect them to be loaded on splash screen")
                throw NoFollowablesException()
            }
            emit(onboardingRepository.getChosenFollowables())
        }
    }
}

class NoFollowablesException : Exception("No followables in local data store")