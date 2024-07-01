package com.theathletic.rooms.ui

import com.theathletic.R
import com.theathletic.ui.toaster.ToasterEvent
import com.theathletic.ui.toaster.ToasterStyle

interface LiveRoomToasterEvent {
    object PendingRequest : ToasterEvent(
        textRes = R.string.rooms_request_pending,
        iconRes = R.drawable.ic_circle_checkmark,
        iconMaskRes = R.color.ath_bright_green,
    )
    object RequestApproved : ToasterEvent(
        textRes = R.string.rooms_request_approved,
        iconRes = R.drawable.ic_circle_checkmark,
        style = ToasterStyle.GREEN,
    )
    object RemovedByHost : ToasterEvent(
        textRes = R.string.rooms_request_removed,
        iconRes = R.drawable.ic_circle_x_red,
    )
    object NotifyUserLocked : ToasterEvent(
        textRes = R.string.rooms_user_locked,
        style = ToasterStyle.RED,
    )
    object AutoPushSent : ToasterEvent(textRes = R.string.rooms_auto_push_sent)
}