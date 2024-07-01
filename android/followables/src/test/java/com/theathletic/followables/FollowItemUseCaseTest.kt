package com.theathletic.followables

import com.google.common.truth.Truth.assertThat
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.followables.data.remote.FollowableFetcher
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
internal class FollowItemUseCaseTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var followableRepository: FollowableRepository
    @Mock private lateinit var userFollowingRepository: UserFollowingRepository

    private lateinit var followItem: FollowItemUseCase

    @Before
    fun setUp() {
        followItem = FollowItemUseCase(followableRepository, userFollowingRepository)
    }

    @Test
    fun `UserTopics and Followables are both updated when calling followItem`() = runTest {
        followItem(FollowableId("someid", Followable.Type.TEAM))

        verify(userFollowingRepository).followItem(any())
    }

    @Test
    fun `followItem returns Success when item is included in repository response`() = runTest {
        val testFollowableId = FollowableId("testId", Followable.Type.TEAM)
        val followResponse = FollowableFetcher.LocalModels(
            navEntities = emptyList(),
            userFollowingItems = listOf(UserFollowingItem(testFollowableId))
        )
        whenever(userFollowingRepository.followItem(testFollowableId)).thenReturn(followResponse)

        val response = followItem(testFollowableId)

        assertThat(response.isSuccess)
    }

    @Test
    fun `followItem returns Followable item in Success response`() = runTest {
        val testFollowableId = FollowableId("testId", Followable.Type.TEAM)
        val testFollowable = mock<Followable>()
        val followResponse = FollowableFetcher.LocalModels(
            navEntities = emptyList(),
            userFollowingItems = listOf(UserFollowingItem(testFollowableId))
        )
        whenever(userFollowingRepository.followItem(testFollowableId)).thenReturn(followResponse)
        whenever(followableRepository.getFollowable(testFollowableId)).thenReturn(testFollowable)

        val response = followItem(testFollowableId)

        assertThat(response.getOrThrow()).isEqualTo(testFollowable)
    }

    @Test
    fun `followItem returns Error when item is missing from repository response`() = runTest {
        val testFollowableId = FollowableId("testId", Followable.Type.TEAM)
        val followResponse = FollowableFetcher.LocalModels(
            navEntities = emptyList(),
            userFollowingItems = emptyList()
        )
        whenever(userFollowingRepository.followItem(testFollowableId)).thenReturn(followResponse)

        val response = followItem(testFollowableId)

        assertThat(response.isFailure)
    }
}