package com.theathletic.topics

import com.theathletic.entity.settings.UserTopics

interface LegacyUserTopicsManager {
    fun setFollowedTopics(topics: UserTopics)
}