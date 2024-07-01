package com.theathletic.profile.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.followable.Followable
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.widgets.RemoteImage

class FollowableItemUi {
    data class FollowableItem(
        val id: Followable.Id,
        val name: String,
        val imageUrl: String,
        val isFollowing: Boolean,
        val isLoading: Boolean = false,
        val isCircular: Boolean = false
    ) {
        interface Interactor {
            fun onFollowClick(item: FollowableItem)
            fun onUnfollowClick(item: FollowableItem)
        }
    }
}

enum class ViewMode {
    VIEW,
    EDIT,
    ADD
}

@Composable
fun FollowableItem(
    followableItem: FollowableItemUi.FollowableItem,
    interactor: FollowableItemUi.FollowableItem.Interactor,
    modifier: Modifier = Modifier,
    viewMode: ViewMode = ViewMode.VIEW
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(top = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            AnimatedVisibility(visible = viewMode == ViewMode.EDIT) {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.DragHandle,
                        tint = AthTheme.colors.dark800,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (viewMode == ViewMode.VIEW) {
                Spacer(modifier = Modifier.size(height = 48.dp, width = 1.dp))
            }

            RemoteImage(
                url = followableItem.imageUrl,
                circular = followableItem.isCircular,
                modifier = Modifier.size(28.dp)
            )

            Text(
                text = followableItem.name,
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 18.dp, end = 12.dp)
            )

            Crossfade(targetState = followableItem.isLoading) { isLoading ->
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp),
                        color = AthTheme.colors.dark700,
                        strokeWidth = 2.dp
                    )
                } else if (viewMode != ViewMode.VIEW) {
                    if (followableItem.isFollowing) {
                        IconButton(onClick = { interactor.onUnfollowClick(followableItem) }) {
                            Icon(
                                Icons.Outlined.Cancel,
                                tint = AthTheme.colors.dark800,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        IconButton(onClick = { interactor.onFollowClick(followableItem) }) {
                            Icon(
                                Icons.Default.Add,
                                tint = AthTheme.colors.dark800,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
        Divider(
            thickness = 1.dp,
            color = AthTheme.colors.dark300,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview
@Composable
private fun FollowedItemUiView_LightPreview() {
    AthleticTheme(lightMode = true) {
        FollowableItem(
            followableItem = item,
            interactor = PreviewInteractor,
        )
    }
}

@Preview
@Composable
private fun FollowedItemUiView_DarkPreview() {
    AthleticTheme(lightMode = false) {
        FollowableItem(
            followableItem = item,
            interactor = PreviewInteractor
        )
    }
}

@Preview
@Composable
private fun FollowableItemUiEdit_LightPreview() {
    AthleticTheme(lightMode = true) {
        FollowableItem(
            followableItem = item.copy(isFollowing = true),
            interactor = PreviewInteractor,
            viewMode = ViewMode.EDIT
        )
    }
}

@Preview
@Composable
private fun FollowableItemUiEdit_DarkPreview() {
    AthleticTheme(lightMode = false) {
        FollowableItem(
            followableItem = item.copy(isFollowing = true),
            interactor = PreviewInteractor,
            viewMode = ViewMode.EDIT
        )
    }
}

@Preview
@Composable
private fun FollowableItemUiAdd_LightPreview() {
    AthleticTheme(lightMode = true) {
        FollowableItem(
            followableItem = item,
            interactor = PreviewInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
private fun FollowableItemUiAdd_DarkPreview() {
    AthleticTheme(lightMode = false) {
        FollowableItem(
            followableItem = item,
            interactor = PreviewInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

private val item = FollowableItemUi.FollowableItem(
    id = Followable.Id("1", Followable.Type.TEAM),
    name = "Boston Red Sox",
    imageUrl = "",
    isLoading = false,
    isFollowing = false
)

private object PreviewInteractor : FollowableItemUi.FollowableItem.Interactor {
    override fun onUnfollowClick(item: FollowableItemUi.FollowableItem) {}
    override fun onFollowClick(item: FollowableItemUi.FollowableItem) {}
}