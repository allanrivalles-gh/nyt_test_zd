package com.theathletic.rooms.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.theathletic.followable.Followable
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon

object LiveRoomUserProfileUi {
    data class FollowedItem(
        val id: Followable.Id,
        val name: String,
        val imageUrl: String,
    )

    interface Interactor {
        fun onFollowClicked(id: Followable.Id, follow: Boolean)
        fun onLockUserClicked()
        fun onUnlockUserClicked()
        fun onRemoveMessageClicked(messageId: String)
    }
}

@Composable
fun LiveRoomUserProfile(
    showSpinner: Boolean,
    name: String,
    initials: String,
    isLocked: Boolean,
    showStaffControls: Boolean,
    messageId: String?,
    currentUserFollowedItems: Set<Followable.Id>,
    followedItems: List<LiveRoomUserProfileUi.FollowedItem>,
    onFlagMessageClicked: (messageId: String) -> Unit,
    interactor: LiveRoomUserProfileUi.Interactor,
) {
    var selectedFollowable by remember { mutableStateOf<LiveRoomUserProfileUi.FollowedItem?>(null) }

    when (val followable = selectedFollowable) {
        null -> FollowableList(
            showSpinner = showSpinner,
            name = name,
            initials = initials,
            isLocked = isLocked,
            showStaffControls = showStaffControls,
            messageId = messageId,
            followedItems = followedItems,
            onItemClicked = { selectedFollowable = it },
            onFlagMessageClicked = onFlagMessageClicked,
            interactor = interactor,
        )
        else -> FollowableDetail(
            followable = followable,
            isCurrentUserFollowing = currentUserFollowedItems.contains(followable.id),
            onBackClicked = { selectedFollowable = null },
            onFollowClicked = interactor::onFollowClicked,
        )
    }
}

@Composable
private fun FollowableList(
    showSpinner: Boolean,
    name: String,
    initials: String,
    isLocked: Boolean,
    showStaffControls: Boolean,
    messageId: String?,
    followedItems: List<LiveRoomUserProfileUi.FollowedItem>,
    onItemClicked: (LiveRoomUserProfileUi.FollowedItem) -> Unit,
    onFlagMessageClicked: (messageId: String) -> Unit,
    interactor: LiveRoomUserProfileUi.Interactor,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp)
    ) {
        UserProfileHeader(
            name = name,
            initials = initials,
            isLocked = isLocked,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
        )
        Spacer(Modifier.height(12.dp))

        if (showSpinner || followedItems.isEmpty()) {
            UserProfileLoadingOrEmpty(
                showSpinner = showSpinner,
                name = name,
            )
        } else {
            FollowedItemRow(
                followedItems = followedItems,
                onItemClicked = onItemClicked,
            )
        }

        Divider(
            color = AthTheme.colors.dark300,
            modifier = Modifier.padding(top = 12.dp)
        )

        UserProfileModerationButtons(
            showStaffControls = showStaffControls,
            isLocked = isLocked,
            messageId = messageId,
            onFlagMessageClicked = onFlagMessageClicked,
            interactor = interactor,
        )
    }
}

@Composable
fun UserProfileLoadingOrEmpty(
    showSpinner: Boolean,
    name: String,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(134.dp)
    ) {
        when {
            showSpinner -> CircularProgressIndicator(
                modifier = Modifier.padding(vertical = 20.dp),
                color = AthTheme.colors.dark600,
            )
            else -> Text(
                text = stringResource(R.string.rooms_user_profile_empty_following, name),
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark600,
            )
        }
    }
}

@Composable
private fun UserProfileHeader(
    name: String,
    initials: String,
    isLocked: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(AthTheme.colors.dark300, CircleShape)
                .padding(3.dp)
                .background(AthTheme.colors.dark200, CircleShape)
        ) {
            Text(
                text = initials,
                style = AthTextStyle.LiveRoom.SheetHeader,
                color = AthTheme.colors.dark800,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            text = name,
            style = AthTextStyle.LiveRoom.SheetHeader,
            color = AthTheme.colors.dark700,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 12.dp)
        )

        if (isLocked) {
            ResourceIcon(
                resourceId = R.drawable.ic_locked,
                tint = AthTheme.colors.red,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(14.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun FollowedItemRow(
    followedItems: List<LiveRoomUserProfileUi.FollowedItem>,
    onItemClicked: (LiveRoomUserProfileUi.FollowedItem) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = spacedBy(16.dp),
    ) {
        items(followedItems) { followable ->
            FollowedItem(
                item = followable,
                onItemClicked = onItemClicked,
            )
        }
    }
}

@Composable
private fun FollowedItem(
    item: LiveRoomUserProfileUi.FollowedItem,
    onItemClicked: (LiveRoomUserProfileUi.FollowedItem) -> Unit,
) {
    Surface(
        color = AthTheme.colors.dark300,
        shape = RoundedCornerShape(4.dp),
        elevation = 8.dp,
        modifier = Modifier
            .size(width = 90.dp, height = 110.dp)
            .clickable { onItemClicked(item) }
    ) {
        Column(
            verticalArrangement = spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 12.dp, start = 8.dp, end = 8.dp)
        ) {
            Image(
                painter = rememberImagePainter(
                    data = item.imageUrl,
                    builder = {
                        crossfade(true)
                        if (item.id.type == Followable.Type.AUTHOR) {
                            transformations(CircleCropTransformation())
                        }
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(54.dp)
            )
            Text(
                text = item.name,
                textAlign = TextAlign.Center,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                color = AthTheme.colors.dark700,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FollowableDetail(
    followable: LiveRoomUserProfileUi.FollowedItem,
    isCurrentUserFollowing: Boolean,
    onBackClicked: () -> Unit,
    onFollowClicked: (Followable.Id, Boolean) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AthTheme.colors.dark800),
            modifier = Modifier
                .padding(6.dp)
                .size(30.dp)
                .clickable(onClick = onBackClicked)
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
        Column(
            verticalArrangement = spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentSize()
                .padding(vertical = 20.dp)
        ) {
            Image(
                painter = rememberImagePainter(
                    data = followable.imageUrl,
                    builder = {
                        crossfade(true)
                        if (followable.id.type == Followable.Type.AUTHOR) {
                            transformations(CircleCropTransformation())
                        }
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = followable.name,
                textAlign = TextAlign.Center,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall.copy(fontSize = 20.sp),
                color = AthTheme.colors.dark700,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            FollowableDetailButton(isCurrentUserFollowing) {
                onFollowClicked(followable.id, !isCurrentUserFollowing)
            }
        }
    }
}

@Composable
private fun FollowableDetailButton(
    isCurrentUserFollowing: Boolean,
    onFollowClicked: () -> Unit
) {
    val buttonColors = when {
        isCurrentUserFollowing -> ButtonDefaults.buttonColors(
            backgroundColor = AthTheme.colors.dark800,
            contentColor = AthTheme.colors.dark100,
        )
        else -> ButtonDefaults.buttonColors(
            backgroundColor = AthTheme.colors.dark300,
            contentColor = AthTheme.colors.dark800,
        )
    }
    Button(
        onClick = onFollowClicked,
        colors = buttonColors,
        shape = RoundedCornerShape(2.dp),
        modifier = Modifier
            .padding(top = 24.dp, bottom = 8.dp)
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(
            text = stringResource(
                when {
                    isCurrentUserFollowing -> R.string.global_following
                    else -> R.string.global_action_follow
                }
            ),
            style = AthTextStyle.Calibre.Utility.Medium.Large,
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
fun LiveRoomUserProfile_Preview() {
    LiveRoomUserProfile(
        showSpinner = false,
        name = "Barry K.",
        initials = "BK",
        isLocked = false,
        showStaffControls = true,
        messageId = "1",
        followedItems = LiveRoomPreviewData.FollowedItems,
        currentUserFollowedItems = emptySet(),
        onFlagMessageClicked = {},
        interactor = LiveRoomPreviewData.UserProfileInteractor,
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
fun LiveRoomUserProfile_LightPreview() {
    AthleticTheme(lightMode = true) {
        LiveRoomUserProfile(
            showSpinner = false,
            name = "Barry K.",
            initials = "BK",
            isLocked = false,
            showStaffControls = true,
            messageId = "1",
            followedItems = LiveRoomPreviewData.FollowedItems,
            currentUserFollowedItems = emptySet(),
            onFlagMessageClicked = {},
            interactor = LiveRoomPreviewData.UserProfileInteractor,
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
fun LiveRoomUserProfile_LoadingPreview() {
    LiveRoomUserProfile(
        showSpinner = true,
        name = "Barry K.",
        initials = "BK",
        isLocked = true,
        showStaffControls = true,
        messageId = "1",
        followedItems = LiveRoomPreviewData.FollowedItems,
        currentUserFollowedItems = emptySet(),
        onFlagMessageClicked = {},
        interactor = LiveRoomPreviewData.UserProfileInteractor,
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
fun LiveRoomUserProfile_EmptyPreview() {
    LiveRoomUserProfile(
        showSpinner = false,
        name = "Barry K.",
        initials = "BK",
        isLocked = true,
        showStaffControls = true,
        messageId = "1",
        followedItems = emptyList(),
        currentUserFollowedItems = emptySet(),
        onFlagMessageClicked = {},
        interactor = LiveRoomPreviewData.UserProfileInteractor,
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
fun LiveRoomUserProfileFollowedItem_Preview() {
    FollowedItem(
        item = LiveRoomPreviewData.FollowedItems[0],
        onItemClicked = {},
    )
}