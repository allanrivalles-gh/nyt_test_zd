package com.theathletic.rooms.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.theathletic.ui.R
import com.theathletic.ui.menu.BottomSheetMenu
import com.theathletic.ui.menu.BottomSheetMenuItem

@Composable
fun LiveRoomStaffModerationMenu(
    userId: String,
    isUserLocked: Boolean,
    showMuteOption: Boolean,
    showDemoteOption: Boolean,
    messageId: String?,
    onMuteUser: (userId: String) -> Unit,
    onDemoteUser: (userId: String) -> Unit,
    onLockUser: (userId: String) -> Unit,
    onUnlockUser: (userId: String) -> Unit,
    onDeleteMessage: (messageId: String) -> Unit,
) {
    BottomSheetMenu {
        if (showMuteOption) {
            BottomSheetMenuItem(
                icon = R.drawable.ic_live_audio_mic_off,
                text = stringResource(R.string.rooms_mute_user),
                onClick = { onMuteUser(userId) },
            )
        }
        if (showDemoteOption) {
            BottomSheetMenuItem(
                icon = R.drawable.ic_x,
                text = stringResource(R.string.rooms_demote_user),
                onClick = { onDemoteUser(userId) },
            )
        }
        messageId?.let { messageId ->
            BottomSheetMenuItem(
                icon = R.drawable.ic_delete_comment,
                text = stringResource(R.string.chat_moderation_remove_comment),
                onClick = { onDeleteMessage(messageId) },
            )
        }
        if (isUserLocked) {
            BottomSheetMenuItem(
                icon = R.drawable.ic_unlocked,
                text = stringResource(R.string.chat_moderation_unlock_user),
                onClick = { onUnlockUser(userId) },
            )
        } else {
            BottomSheetMenuItem(
                icon = R.drawable.ic_locked,
                text = stringResource(R.string.chat_moderation_lock_user),
                onClick = { onLockUser(userId) },
            )
        }
    }
}