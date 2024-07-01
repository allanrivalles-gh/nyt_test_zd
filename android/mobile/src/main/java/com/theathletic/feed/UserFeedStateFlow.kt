package com.theathletic.feed

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// TEMPORARY state bridge between FeedFragment and MainFeedScreen composable
@Deprecated("Remove this when FeedFragment is moved to compose")
data class UserFeedState(val isEmptyAndLoading: Boolean = true)

@Deprecated("Remove this when FeedFragment is moved to compose")
class UserFeedStateProducer(
    private val mutableStateFlow: MutableStateFlow<UserFeedState> = MutableStateFlow(UserFeedState())
) : MutableStateFlow<UserFeedState> by mutableStateFlow

@Deprecated("Remove this when FeedFragment is moved to compose")
class UserFeedStateObserver(
    private val observer: UserFeedStateProducer
) : StateFlow<UserFeedState> by observer