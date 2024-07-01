package com.theathletic.profile.following

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.extension.ifEmptyDo
import com.theathletic.feed.FeedType
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableType
import com.theathletic.followables.FollowItemUseCase
import com.theathletic.followables.ObserveUserFollowingUseCase
import com.theathletic.followables.UnfollowItemUseCase
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.followables.data.domain.UserFollowing
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.profile.manage.UserTopicId
import com.theathletic.profile.ui.FollowableItemUi
import com.theathletic.profile.ui.ViewMode
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.repository.user.toFollowableId
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.DataState
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class ManageFollowingViewModel @AutoKoin constructor(
    @Assisted private val params: Params,
    @Assisted private val navigator: ScreenNavigator,
    private val followItemUseCase: FollowItemUseCase,
    private val unfollowItemUseCase: UnfollowItemUseCase,
    private val followableRepository: FollowableRepository,
    private val userFollowingRepository: UserFollowingRepository,
    private val supportedLeagues: SupportedLeagues,
    private val observeUserFollowing: ObserveUserFollowingUseCase,
    analytics: FollowingAnalytics
) : AthleticViewModel<FollowingDataState, ManageFollowingContract.FollowingViewState>(),
    FollowingAnalytics by analytics,
    ManageFollowingContract.Interactor,
    ComposeViewModel,
    DefaultLifecycleObserver {

    data class Params(
        val view: String,
        val autoFollowId: UserTopicId?
    )

    override val initialState = FollowingDataState()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    override fun initialize() {
        view = params.view
        trackView()

        observeUserFollowing()
            .collectIn(viewModelScope) { items ->
                if (state.ncaaLeagues.isEmpty()) {
                    val ncaaLeagues = followableRepository.getFilteredLeagues(supportedLeagues.collegeLeagues)
                    updateState { copy(ncaaLeagues = ncaaLeagues) }
                }
                val followedIds = items.map { it.id }
                updateState {
                    copy(
                        loadingUnfollowIds = loadingUnfollowIds.filter { id -> followedIds.contains(id) },
                        followedItems = items
                    )
                }
                items.ifEmptyDo { enableEditMode(false) }
            }

        params.autoFollowId?.let { id ->
            viewModelScope.launch {
                followItemUseCase(id.toFollowableId())
            }
        }
    }

    override fun transform(data: FollowingDataState): ManageFollowingContract.FollowingViewState {
        return ManageFollowingContract.FollowingViewState(
            followingItems = transformFollowedItems(data),
            viewMode = data.viewMode
        )
    }

    private fun transformFollowedItems(data: FollowingDataState): List<FollowableItemUi.FollowableItem> {
        return data.followedItems.map { item ->
            FollowableItemUi.FollowableItem(
                id = item.id,
                name = item.name,
                imageUrl = item.imageUrl,
                isLoading = data.loadingUnfollowIds.contains(item.id),
                isFollowing = true,
                isCircular = item.id.type == FollowableType.AUTHOR
            )
        }
    }

    override fun onBackClick() {
        navigator.finishActivity()
    }

    override fun onAddClick() {
        trackClickAddFollow()
        navigator.startAddFollowingActivity()
    }

    override fun enableEditMode(enable: Boolean) {
        updateState {
            copy(
                viewMode = if (enable) {
                    trackClickEdit()
                    ViewMode.EDIT
                } else {
                    ViewMode.VIEW
                }
            )
        }
    }

    override fun onReorder(newOrder: Map<String, Int>) {
        trackReorder()
        userFollowingRepository.saveFollowablesReordering(newOrder)
    }

    override fun onFollowableClick(id: Followable.Id) {
        navigator.startStandaloneFeedActivity(FeedType.fromFollowable(id))
    }

    override fun onUnfollowClick(item: FollowableItemUi.FollowableItem) {
        if (state.loadingUnfollowIds.contains(item.id).not()) {
            updateState { copy(loadingUnfollowIds = loadingUnfollowIds + item.id) }
            unFollowItem(item.id)
        }
    }

    private fun unFollowItem(id: Followable.Id) {
        viewModelScope.launch {
            unfollowItemUseCase(id).onFailure {
                updateState { copy(loadingUnfollowIds = loadingUnfollowIds - id) }
            }.onSuccess {
                it?.let { trackUnfollow(it) }
            }
        }
    }

    override fun onFollowClick(item: FollowableItemUi.FollowableItem) {
        // No-op needed only items already followed will appear on this screen
    }
}

data class FollowingDataState(
    val followedItems: List<UserFollowing> = emptyList(),
    val viewMode: ViewMode = ViewMode.VIEW,
    val loadingUnfollowIds: List<Followable.Id> = emptyList(),
    val ncaaLeagues: List<LeagueLocal> = emptyList()
) : DataState