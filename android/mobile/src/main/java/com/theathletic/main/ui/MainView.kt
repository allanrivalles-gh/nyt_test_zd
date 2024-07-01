package com.theathletic.main.ui

import android.view.View
import com.theathletic.rooms.ui.LiveAudioRoomMiniPlayerInteractor

@Deprecated("Replace with MainContract")
interface MainView : LiveAudioRoomMiniPlayerInteractor {
    fun hideOfflineLabelClick(view: View)
    fun onProfileClick(view: View)
    fun onSearchClick(view: View)
    fun onReferralClick()
    fun onFilterFeedClicked()
}

interface MainContract {
    enum class AlertType {
        SuccessfulPurchaseAlert,
        GracePeriodAlert,
        InvalidEmailAlert
    }

    sealed class Event : com.theathletic.utility.Event() {
        object FinishAndRouteToAuth : Event()
        object NavigateToManageAccount : Event()
        object NavigateToGiftSheet : Event()
        object CloseApplication : Event()
        class NavigateToUpdateCreditCard(val email: String, val idHash: String) : Event()
    }
}