package com.theathletic.onboarding.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemorySingleLocalDataSource
import com.theathletic.onboarding.OnboardingPodcastItemResponse

class OnboardingPodcastsDataSource @AutoKoin(Scope.SINGLE) constructor() :
    InMemorySingleLocalDataSource<List<OnboardingPodcastItemResponse>>()