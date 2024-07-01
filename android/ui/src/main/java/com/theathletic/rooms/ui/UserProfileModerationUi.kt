package com.theathletic.rooms.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun UserProfileModerationButtons(
    showStaffControls: Boolean,
    isLocked: Boolean,
    messageId: String?,
    onFlagMessageClicked: (messageId: String) -> Unit,
    interactor: LiveRoomUserProfileUi.Interactor,
) {
    if (!showStaffControls && messageId == null) return

    Column(
        verticalArrangement = spacedBy(2.dp),
        modifier = Modifier
            .padding(top = 24.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(4.dp))
    ) {
        if (showStaffControls) {
            if (messageId != null) {
                UserProfileModerationButton(
                    text = stringResource(R.string.chat_moderation_remove_comment),
                    iconRes = R.drawable.ic_delete_comment,
                    iconTint = AthTheme.colors.yellow,
                    onClick = { interactor.onRemoveMessageClicked(messageId) }
                )
            }

            if (isLocked) {
                UserProfileModerationButton(
                    text = stringResource(R.string.chat_moderation_unlock_user),
                    iconRes = R.drawable.ic_unlocked,
                    iconTint = AthTheme.colors.red,
                    onClick = interactor::onUnlockUserClicked,
                )
            } else {
                UserProfileModerationButton(
                    text = stringResource(R.string.chat_moderation_lock_user),
                    iconRes = R.drawable.ic_locked,
                    iconTint = AthTheme.colors.red,
                    onClick = interactor::onLockUserClicked,
                )
            }
        } else {
            if (messageId != null) {
                UserProfileModerationButton(
                    text = stringResource(R.string.chat_moderation_flag_comment),
                    iconRes = R.drawable.ic_flag,
                    iconTint = AthTheme.colors.red,
                    onClick = { onFlagMessageClicked(messageId) }
                )
            }
        }
    }
}

@Composable
private fun UserProfileModerationButton(
    text: String,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    iconTint: Color = AthTheme.colors.dark800,
    onClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(AthTheme.colors.dark300)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp)
    ) {
        ResourceIcon(
            resourceId = iconRes,
            tint = iconTint,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = text,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark800,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

@Composable
@Preview
private fun UserProfileModerationButtons_PreviewUnlocked() {
    UserProfileModerationButtons(
        showStaffControls = true,
        isLocked = false,
        messageId = "1",
        onFlagMessageClicked = {},
        interactor = LiveRoomPreviewData.UserProfileInteractor,
    )
}

@Composable
@Preview
private fun UserProfileModerationButtons_PreviewLocked() {
    UserProfileModerationButtons(
        showStaffControls = true,
        isLocked = true,
        messageId = "1",
        onFlagMessageClicked = {},
        interactor = LiveRoomPreviewData.UserProfileInteractor,
    )
}