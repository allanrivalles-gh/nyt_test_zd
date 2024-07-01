package com.theathletic.profile.addfollowing

import com.google.common.truth.Truth.assertThat
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.followables.FollowItemUseCase
import com.theathletic.followables.ListFollowableUseCase
import com.theathletic.followables.UnfollowItemUseCase
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.followables.data.remote.FollowableFetcher
import com.theathletic.followables.data.remote.UnfollowFetcher
import com.theathletic.followables.test.fixtures.teamIdFixture
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.profile.following.FollowingAnalytics
import com.theathletic.profile.ui.FollowableItemUi
import com.theathletic.repository.user.UserFollowingItem
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

internal class AddFollowingViewModelTest {

    @get:Rule var coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var navigator: ScreenNavigator
    @Mock private lateinit var followingRepository: UserFollowingRepository
    @Mock private lateinit var transformer: AddFollowingTransformer
    @Mock private lateinit var analytics: FollowingAnalytics
    @Mock private lateinit var followItemUseCase: FollowItemUseCase
    @Mock private lateinit var unfollowItemUseCase: UnfollowItemUseCase
    @Mock private lateinit var listFollowableUseCase: ListFollowableUseCase

    private lateinit var viewModel: AddFollowingViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        viewModel = AddFollowingViewModel(
            navigator,
            followItemUseCase,
            unfollowItemUseCase,
            listFollowableUseCase,
            transformer,
            analytics
        )
    }

    @Test
    fun `when follow action and successful follow, then loadingFollowIds contains id`() = runTest {
        whenever(followItemUseCase(TEST_TEAM_ID)).thenReturn(Result.success(testFollowableModelFixture))
        whenever(followingRepository.followItem(TEST_TEAM_ID)).thenReturn(
            FollowableFetcher.LocalModels(
                navEntities = emptyList(),
                userFollowingItems = listOf(UserFollowingItem(TEST_TEAM_ID))
            )
        )

        viewModel.onFollowClick(followableItemUiFixture(id = TEST_TEAM_ID))

        assertThat(viewModel.state.addedFollowingItems.any { it.id == TEST_TEAM_ID }).isTrue()
        assertThat(viewModel.state.loadingIds.contains(TEST_TEAM_ID)).isFalse()
    }

    @Test
    fun `when follow action and unsuccessful follow, then loadingFollowIds doesn't contain id`() =
        runTest {
            whenever(followItemUseCase(TEST_TEAM_ID)).thenReturn(Result.failure(Throwable("error")))

            viewModel.onFollowClick(followableItemUiFixture(id = TEST_TEAM_ID))

            assertEquals(emptyList(), viewModel.state.loadingIds)
        }

    @Test
    fun `when unfollow action and successful unfollow, then loading and added ids do not contain item`() =
        runTest {
            whenever(unfollowItemUseCase(TEST_TEAM_ID)).thenReturn(Result.success(testFollowableModelFixture))
            whenever(followingRepository.unfollowItem(TEST_TEAM_ID)).thenReturn(
                UnfollowFetcher.LocalModels(
                    navEntities = emptyList(),
                    userFollowingItems = emptyList()
                )
            )
            viewModel.onUnfollowClick(followableItemUiFixture(id = TEST_TEAM_ID))

            assertThat(viewModel.state.addedFollowingItems.any { it.id == TEST_TEAM_ID}).isFalse()
            assertThat(viewModel.state.loadingIds.contains(TEST_TEAM_ID)).isFalse()
        }

    @Test
    fun `when unfollow action and unsuccessful unfollow, then remove the id from the loading id list`() =
        runTest {
            whenever(unfollowItemUseCase(TEST_TEAM_ID)).thenReturn(Result.failure(Throwable("failure")))

            viewModel.onUnfollowClick(followableItemUiFixture(id = TEST_TEAM_ID))

            assertEquals(emptyList(), viewModel.state.loadingIds)
        }

    companion object {
        private val TEST_TEAM_ID = Followable.Id(
            id = "1",
            type = Followable.Type.TEAM
        )
    }

    private val testFollowableModelFixture = object : Followable {
        override val id: Followable.Id = TEST_TEAM_ID
        override val name: String = "test name"
        override val shortName: String = "test short name"
        override val searchText: String = "test search text"
    }

    private fun followableItemUiFixture(
        id: FollowableId = teamIdFixture(),
        name: String = "Golden State ",
        imageUrl: String = "",
        isFollowing: Boolean = false,
        isLoading: Boolean = false
    ) = FollowableItemUi.FollowableItem(
        id = id,
        name = name,
        imageUrl = imageUrl,
        isFollowing = isFollowing,
        isLoading = isLoading,
        isCircular = false
    )
}