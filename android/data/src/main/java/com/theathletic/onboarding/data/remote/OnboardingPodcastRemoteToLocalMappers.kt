package com.theathletic.onboarding.data.remote

import com.theathletic.entity.main.PodcastItem
import com.theathletic.onboarding.OnboardingItem
import com.theathletic.onboarding.OnboardingPodcastItemResponse

fun OnboardingPodcastItemResponse.toPodcastEntity(): PodcastItem {
    return PodcastItem().also {
        it.id = id
        it.title = title
        it.metadataString = metadataString
        it.imageUrl = imageUrl
    }
}

@Deprecated("Use OnboardingPodcastItemResponse instead")
fun OnboardingItem.Podcast.toPodcastEntity(): PodcastItem {
    return PodcastItem().also {
        it.id = id
        it.title = title
        it.description = description
        it.imageUrl = imageUrl
    }
}