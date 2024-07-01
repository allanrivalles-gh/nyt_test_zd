package com.theathletic.rooms.schedule.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.formatter.TimeAgoShortDateFormatter
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.links.deep.Deeplink
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.rooms.RoomsRepository
import com.theathletic.rooms.schedule.ui.ScheduledLiveRoomsContract.ViewState
import com.theathletic.rooms.ui.ScheduledRoomsUi
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.binding.toResourceString
import com.theathletic.utility.coroutines.collectIn

class ScheduledLiveRoomsViewModel @AutoKoin constructor(
    @Assisted private val navigator: ScreenNavigator,
    private val roomsRepository: RoomsRepository,
    private val timeAgoShortDateFormatter: TimeAgoShortDateFormatter,
) : AthleticViewModel<ScheduledLiveRoomsState, ViewState>(),
    ScheduledLiveRoomsContract.Presenter {

    override val initialState by lazy {
        ScheduledLiveRoomsState()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initialize() {
        fetchData()
    }

    private fun fetchData() {
        roomsRepository.getScheduledLiveRooms(fetch = true).collectIn(viewModelScope) {
            updateState { copy(rooms = it) }
        }
    }

    fun showModal(modal: ScheduledLiveRoomsContract.ModalSheetType?) {
        updateState { copy(currentBottomSheetModal = modal) }
    }

    override fun onLiveRoomClicked(roomId: String) {
        navigator.startLiveAudioRoomActivity(roomId)
    }

    override fun onLiveRoomLongClicked(roomId: String) {
        val room = state.rooms.find { it.id == roomId } ?: return
        showModal(
            ScheduledLiveRoomsContract.ModalSheetType.LinksMenu(
                deeplink = Deeplink.liveRoom(roomId),
                universalLink = room.permalink,
            )
        )
    }

    override fun transform(data: ScheduledLiveRoomsState): ViewState {
        return ViewState(
            uiModel = ScheduledRoomsUi(
                rooms = data.rooms.sortedByDescending { it.createdAt }.map { entity ->
                    ScheduledRoomsUi.Room(
                        id = entity.id,
                        title = entity.title,
                        subtitle = entity.subtitle,
                        createdAt = entity.createdAt?.let {
                            timeAgoShortDateFormatter.format(it).toResourceString()
                        } ?: StringWrapper("n/a"),
                    )
                }
            ),
            currentBottomSheetModal = data.currentBottomSheetModal,
        )
    }
}

data class ScheduledLiveRoomsState(
    val rooms: List<LiveAudioRoomEntity> = emptyList(),
    val currentBottomSheetModal: ScheduledLiveRoomsContract.ModalSheetType? = null,
) : DataState