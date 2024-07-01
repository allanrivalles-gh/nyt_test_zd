package com.theathletic.profile.addfollowing

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.Followable
import com.theathletic.followables.FollowItemUseCase
import com.theathletic.followables.ListFollowableUseCase
import com.theathletic.followables.UnfollowItemUseCase
import com.theathletic.followables.data.domain.Filter
import com.theathletic.followables.data.domain.FollowableItem
import com.theathletic.followables.data.domain.UserFollowing
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.profile.following.FollowingAnalytics
import com.theathletic.profile.ui.FollowableItemUi
import com.theathletic.profile.ui.FollowingFilter
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AddFollowingViewModel @AutoKoin constructor(
    @Assisted private val navigator: ScreenNavigator,
    private val followItemUseCase: FollowItemUseCase,
    private val unfollowItemUseCase: UnfollowItemUseCase,
    private val listFollowableUseCase: ListFollowableUseCase,
    transformer: AddFollowingTransformer,
    analytics: FollowingAnalytics
) : AthleticViewModel<AddFollowingDataState, AddFollowingContract.AddFollowingViewState>(),
    Transformer<AddFollowingDataState, AddFollowingContract.AddFollowingViewState> by transformer,
    FollowingAnalytics by analytics,
    AddFollowingContract.Interactor,
    DefaultLifecycleObserver {

    override val initialState = AddFollowingDataState()
    private val followableFilter = Filter.NonFollowing()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    fun initialize() {
        view = "add_drawer"
        trackView()

        listFollowableUseCase(followableFilter)
            .onEach { updateState { copy(searchableItems = it, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    override fun onBackClick() {
        navigator.finishActivity()
    }

    override fun onUpdateSearchText(updatedText: String) {
        updateState { copy(searchText = updatedText) }
        followableFilter.update { currentFilter ->
            Filter.NonFollowing(updatedText, currentFilter.type)
        }
    }

    override fun onFilterSelected(filter: FollowingFilter) {
        updateState { copy(currentFilter = filter) }
        val filterType = when (filter) {
            FollowingFilter.All -> Filter.Type.ALL
            FollowingFilter.Teams -> Filter.Type.TEAM
            FollowingFilter.Leagues -> Filter.Type.LEAGUE
            FollowingFilter.Authors -> Filter.Type.AUTHOR
        }

        followableFilter.update { currentFilter -> Filter.NonFollowing(currentFilter.query, filterType) }
    }

    override fun onUnfollowClick(item: FollowableItemUi.FollowableItem) {
        if (state.loadingIds.contains(item.id).not()) {
            updateState { copy(loadingIds = loadingIds + item.id) }
            unfollowItem(item)
        }
    }

    override fun onFollowClick(item: FollowableItemUi.FollowableItem) {
        if (state.loadingIds.contains(item.id).not()) {
            updateState { copy(loadingIds = loadingIds + item.id) }
            followItem(item)
        }
    }

    private fun unfollowItem(item: FollowableItemUi.FollowableItem) {
        viewModelScope.launch {
            unfollowItemUseCase(item.id).onFailure {
                updateState {
                    copy(loadingIds = loadingIds - item.id)
                }
            }.onSuccess { followable ->
                updateState {
                    copy(
                        addedFollowingItems = addedFollowingItems.filterNot { it.id == item.id },
                        loadingIds = loadingIds - item.id
                    )
                }
                followable?.let { trackUnfollow(it) }
            }
        }
    }

    private fun followItem(item: FollowableItemUi.FollowableItem) {
        viewModelScope.launch {
            followItemUseCase(item.id).onFailure {
                updateState { copy(loadingIds = loadingIds - item.id) }
            }
                .onSuccess { followable ->
                    updateState {
                        copy(
                            addedFollowingItems = addedFollowingItems + item.copy(isFollowing = true),
                            loadingIds = loadingIds - item.id
                        )
                    }
                    followable?.let { trackFollow(it) }
                }
        }
    }
}

data class AddFollowingDataState(
    val isLoading: Boolean = true,
    val isInitializing: Boolean = true,
    val searchText: String = "",
    val currentFilter: FollowingFilter = FollowingFilter.All,
    val searchableItems: List<FollowableItem> = emptyList(),
    val recommendedItems: List<Followable> = emptyList(),
    val addedFollowingItems: List<FollowableItemUi.FollowableItem> = emptyList(),
    val loadingIds: List<Followable.Id> = emptyList(),
    val initialFollowedItems: Set<UserFollowing> = emptySet(),
    val followedIds: List<Followable.Id> = emptyList(),
    val ncaaLeagues: List<LeagueLocal> = emptyList()
) : DataState