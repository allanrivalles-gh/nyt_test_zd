package com.theathletic.onboarding.data

data class OnboardingPodcastItem(
    val id: String = "",
    val title: String = "",
    val metadataString: String? = "",
    val imageUrl: String? = "",
    val description: String? = "",
    val notifEpisodesOn: Boolean
)