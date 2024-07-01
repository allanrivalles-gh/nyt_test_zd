package com.theathletic.followables

import com.google.common.truth.Truth.assertThat
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.followables.data.remote.UnfollowFetcher
import com.theathletic.repository.user.UserFollowingItem
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
internal class UnfollowItemUseCaseTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var followableRepository: FollowableRepository
    @Mock private lateinit var userFollowingRepository: UserFollowingRepository

    private lateinit var unfollowItem: UnfollowItemUseCase

    @Before
    fun setUp() {
        unfollowItem = UnfollowItemUseCase(
            followableRepository,
            userFollowingRepository
        )
    }

    @Test
    fun `UserTopics and Followables are both updated when calling unfollowItem`() = runTest {
        unfollowItem(FollowableId("someid", Followable.Type.TEAM))

        verify(userFollowingRepository).unfollowItem(any())
    }

    @Test
    fun `unfollowItem returns Error when item is still included in repository response`() = runTest {
        val testFollowableId = FollowableId("testId", Followable.Type.TEAM)
        val unfollowResponse = UnfollowFetcher.LocalModels(
            navEntities = emptyList(),
            userFollowingItems = listOf(UserFollowingItem(testFollowableId))
        )
        whenever(userFollowingRepository.unfollowItem(testFollowableId)).thenReturn(unfollowResponse)

        val result = unfollowItem(testFollowableId)

        assertThat(result.isFailure)
    }

    @Test
    fun `unfollowItem returns Followable item in Success response`() = runTest {
        val testFollowableId = FollowableId("testId", Followable.Type.TEAM)
        val testFollowable = mock<Followable>()
        val unfollowResponse = UnfollowFetcher.LocalModels(
            navEntities = emptyList(),
            userFollowingItems = emptyList()
        )
        whenever(userFollowingRepository.unfollowItem(testFollowableId)).thenReturn(unfollowResponse)
        whenever(followableRepository.getFollowable(testFollowableId)).thenReturn(testFollowable)

        val result = unfollowItem(testFollowableId)

        assertThat(result.getOrThrow()).isEqualTo(testFollowable)
    }

    @Test
    fun `followItem returns Success when item is no longer in repository response`() = runTest {
        val testFollowableId = FollowableId("testId", Followable.Type.TEAM)
        val unfollowResponse = UnfollowFetcher.LocalModels(
            navEntities = emptyList(),
            userFollowingItems = emptyList()
        )
        whenever(userFollowingRepository.unfollowItem(testFollowableId)).thenReturn(unfollowResponse)

        val response = unfollowItem(testFollowableId)

        assertThat(response.isSuccess)
    }
}