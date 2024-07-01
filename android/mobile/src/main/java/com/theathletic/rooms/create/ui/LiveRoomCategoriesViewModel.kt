package com.theathletic.rooms.create.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.room.LiveRoomCategory
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.rooms.create.data.local.LiveRoomCreationInput
import com.theathletic.rooms.create.data.local.LiveRoomCreationInputStateHolder
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.utility.safeValueOf

class LiveRoomCategoriesViewModel @AutoKoin constructor(
    @Assisted val navigator: ScreenNavigator,
    private val creationInputStateHolder: LiveRoomCreationInputStateHolder,
) :
    AthleticViewModel<LiveRoomCategoriesState, LiveRoomCategoriesContract.ViewState>(),
    LiveRoomCategoriesContract.Presenter {

    override val initialState = LiveRoomCategoriesState(
        currentInput = creationInputStateHolder.currentInput.value
    )

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initialize() {
        creationInputStateHolder.currentInput.collectIn(viewModelScope) {
            updateState { copy(currentInput = it) }
        }
    }

    override fun onCategoryClicked(value: String) {
        val category = safeValueOf<LiveRoomCategory>(value) ?: return
        if (state.currentInput.categories.contains(category)) {
            creationInputStateHolder.removeCategory(category)
        } else {
            creationInputStateHolder.addCategory(category)
        }
    }

    override fun onCloseClicked() {
        navigator.finishActivity()
    }

    override fun transform(data: LiveRoomCategoriesState): LiveRoomCategoriesContract.ViewState {
        return LiveRoomCategoriesContract.ViewState(
            categories = LiveRoomCategory.values().map {
                LiveRoomCategoriesUi.Category(
                    slug = it.name,
                    title = it.displayString,
                    isSelected = state.currentInput.categories.contains(it),
                )
            },
        )
    }
}

data class LiveRoomCategoriesState(
    val currentInput: LiveRoomCreationInput,
) : DataState