package com.theathletic.profile.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.extension.swap
import com.theathletic.followable.Followable
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.BottomSheetTopDragHandler
import com.theathletic.ui.widgets.DraggableItem
import com.theathletic.ui.widgets.dragContainer
import com.theathletic.ui.widgets.rememberDragDropState

object ManageFollowingUi {
    interface Interactor : FollowableItemUi.FollowableItem.Interactor {
        fun onBackClick()
        fun onAddClick()
        fun enableEditMode(enable: Boolean)
        fun onReorder(newOrder: Map<String, Int>)
        fun onFollowableClick(id: Followable.Id)
    }
}

private fun associateFollowables(followables: List<FollowableItemUi.FollowableItem>) =
    followables.withIndex().associate { it.value.id.toString() to it.index }

@Composable
fun ManageFollowingScreen(
    followableItems: List<FollowableItemUi.FollowableItem>,
    interactor: ManageFollowingUi.Interactor,
    showBackButton: Boolean = false,
    viewMode: ViewMode
) {
    Column(Modifier.animateContentSize()) {
        ManageFollowingToolbar(
            interactor = interactor,
            viewMode = viewMode,
            showBackButton = showBackButton,
            areFollowablesEmpty = followableItems.isEmpty()
        )

        if (followableItems.isEmpty()) {
            ManageFollowingEmptyContent()
        } else {
            ManageFollowingContent(followableItems, viewMode, interactor)
        }
    }
}

@Composable
private fun ManageFollowingToolbar(
    interactor: ManageFollowingUi.Interactor,
    viewMode: ViewMode = ViewMode.VIEW,
    showBackButton: Boolean = false,
    areFollowablesEmpty: Boolean = false
) {
    Column(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .padding(vertical = 4.dp)
    ) {
        if (showBackButton.not()) {
            Row { BottomSheetTopDragHandler() }
        }
        Crossfade(targetState = viewMode) { mode ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (mode == ViewMode.EDIT) {
                    RenderToolbarEditMode(interactor)
                } else {
                    RenderToolbarViewMode(showBackButton, interactor, areFollowablesEmpty)
                }
            }
        }
    }
}

@Composable
private fun RenderToolbarBackButton(interactor: ManageFollowingUi.Interactor) {
    IconButton(
        onClick = interactor::onBackClick,
        modifier = Modifier.padding(start = 16.dp, end = 36.dp)
    ) {
        Icon(
            Icons.Default.ArrowBack,
            tint = AthTheme.colors.dark800,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun RowScope.RenderToolbarViewMode(
    showBackButton: Boolean,
    interactor: ManageFollowingUi.Interactor,
    areFollowablesEmpty: Boolean
) {
    if (showBackButton) {
        RenderToolbarBackButton(interactor)
    } else {
        Spacer(modifier = Modifier.padding(horizontal = 52.dp))
    }
    Text(
        text = stringResource(id = R.string.global_following),
        color = AthTheme.colors.dark800,
        style = AthTextStyle.Slab.Bold.Small,
        textAlign = TextAlign.Center,
        modifier = Modifier.Companion.weight(1f)
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        RenderToolbarTitleAction(areFollowablesEmpty, interactor)
    }
}

@Composable
private fun RenderToolbarEditMode(interactor: ManageFollowingUi.Interactor) {
    Spacer(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .size(48.dp)
    )
    Text(
        text = stringResource(id = R.string.global_edit),
        color = AthTheme.colors.dark800,
        style = AthTextStyle.Slab.Bold.Small,
        textAlign = TextAlign.Center,
    )
    TextButton(
        onClick = { interactor.enableEditMode(false) },
        modifier = Modifier.padding(end = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_toolbar_done),
            color = AthTheme.colors.red,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge
        )
    }
}

@Composable
private fun RenderToolbarTitleAction(areFollowablesEmpty: Boolean, interactor: ManageFollowingUi.Interactor) {
    IconButton(
        enabled = !areFollowablesEmpty,
        onClick = { interactor.enableEditMode(true) },
    ) {
        Icon(
            imageVector = Icons.Default.EditNote,
            tint = if (areFollowablesEmpty) AthTheme.colors.dark500 else AthTheme.colors.dark800,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
    IconButton(
        onClick = interactor::onAddClick,
        modifier = Modifier.padding(end = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AddCircle,
            tint = AthTheme.colors.dark800,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun ColumnScope.ManageFollowingEmptyContent() {
    Text(
        text = stringResource(id = R.string.global_placeholder_no_user_topics),
        style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge,
        color = AthTheme.colors.dark500,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .align(Alignment.CenterHorizontally)
            .paddingFromBaseline(top = 16.dp, bottom = 16.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ManageFollowingContent(
    followableItems: List<FollowableItemUi.FollowableItem>,
    viewMode: ViewMode,
    interactor: ManageFollowingUi.Interactor
) {
    var followableList by remember(followableItems) { mutableStateOf(followableItems) }
    val isDraggingEnabled = remember(viewMode) { viewMode == ViewMode.EDIT }
    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropState(
        listState, followableItems, isDraggingEnabled,
        onMove = { fromIndex, toIndex ->
            followableList = followableList.swap(fromIndex, toIndex)
        },
        onStopDragging = {
            interactor.onReorder(associateFollowables(followableList))
        }
    )

    LazyColumn(
        state = listState,
        modifier = Modifier.dragContainer(dragDropState)
    ) {
        itemsIndexed(
            followableList,
            key = { _, item -> item.id.toString() }
        ) { index, item ->
            DraggableItem(
                dragDropState,
                index,
                modifier = Modifier.fillMaxWidth()
            ) { isDragging ->
                val elevation by animateDpAsState(if (isDragging && isDraggingEnabled) 4.dp else 0.dp)
                FollowableItem(
                    followableItem = item,
                    interactor = interactor,
                    viewMode = viewMode,
                    modifier = Modifier
                        .shadow(elevation)
                        .clickable(enabled = isDraggingEnabled.not()) {
                            interactor.onFollowableClick(item.id)
                        }
                )
            }
        }
    }
}

@Preview
@Composable
fun BottomToolbarViewModeShowBackBtn_LightPreview() {
    AthleticTheme(lightMode = true) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.VIEW,
            showBackButton = true
        )
    }
}

@Preview
@Composable
fun BottomToolbarViewModeShowBackBtn_DarkPreview() {
    AthleticTheme(lightMode = false) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.VIEW,
            showBackButton = true
        )
    }
}

@Preview
@Composable
fun BottomToolbarViewModeShowBackBtnEditDisabled_LightPreview() {
    AthleticTheme(lightMode = true) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.VIEW,
            showBackButton = true,
            areFollowablesEmpty = true
        )
    }
}

@Preview
@Composable
fun BottomToolbarViewModeShowBackBtnEditDisabled_DarkPreview() {
    AthleticTheme(lightMode = false) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.VIEW,
            showBackButton = true,
            areFollowablesEmpty = true
        )
    }
}

@Preview
@Composable
fun BottomToolbarViewMode_LightPreview() {
    AthleticTheme(lightMode = true) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.VIEW
        )
    }
}

@Preview
@Composable
fun BottomToolbarViewMode_DarkPreview() {
    AthleticTheme(lightMode = false) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.VIEW
        )
    }
}

@Preview
@Composable
fun BottomToolbarViewModeEditDisabled_LightPreview() {
    AthleticTheme(lightMode = true) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.VIEW,
            areFollowablesEmpty = true
        )
    }
}

@Preview
@Composable
fun BottomToolbarViewModeEditDisabled_DarkPreview() {
    AthleticTheme(lightMode = false) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.VIEW,
            areFollowablesEmpty = true
        )
    }
}

@Preview
@Composable
fun BottomToolbarEditMode_LightPreview() {
    AthleticTheme(lightMode = true) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.EDIT
        )
    }
}

@Preview
@Composable
fun BottomToolbarEditMode_DarkPreview() {
    AthleticTheme(lightMode = false) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.EDIT
        )
    }
}

@Preview
@Composable
fun BottomToolbarAddMode_LightPreview() {
    AthleticTheme(lightMode = true) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
fun BottomToolbarAddMode_DarkPreview() {
    AthleticTheme(lightMode = false) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
fun BottomToolbarNoViewMode_LightPreview() {
    AthleticTheme(lightMode = true) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
fun BottomToolbarNoViewMode_DarkPreview() {
    AthleticTheme(lightMode = false) {
        ManageFollowingToolbar(
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
fun BottomSheetNoItems_LightPreview() {
    AthleticTheme(lightMode = true) {
        ManageFollowingScreen(
            followableItems = emptyList(),
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
fun BottomSheetNoItems_DarkPreview() {
    AthleticTheme(lightMode = false) {
        ManageFollowingScreen(
            followableItems = emptyList(),
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
fun BottomSheetFewItems_LightPreview() {
    AthleticTheme(lightMode = true) {
        ManageFollowingScreen(
            followableItems = dummyData.dropLast(5),
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
fun BottomSheetFewItems_DarkPreview() {
    AthleticTheme(lightMode = false) {
        ManageFollowingScreen(
            followableItems = dummyData.dropLast(5),
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
fun BottomSheetManyItems_LightPreview() {
    AthleticTheme(lightMode = true) {
        ManageFollowingScreen(
            followableItems = dummyData,
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
fun BottomSheetManyItems_DarkPreview() {
    AthleticTheme(lightMode = false) {
        ManageFollowingScreen(
            followableItems = dummyData,
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
fun BottomSheetAdd_LightPreview() {
    AthleticTheme(lightMode = true) {
        ManageFollowingScreen(
            followableItems = dummyData,
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

@Preview
@Composable
fun BottomSheetAdd_DarkPreview() {
    AthleticTheme(lightMode = false) {
        ManageFollowingScreen(
            followableItems = dummyData,
            interactor = dummyInteractor,
            viewMode = ViewMode.ADD
        )
    }
}

private val dummyData =
    listOf(
        FollowableItemUi.FollowableItem(
            id = Followable.Id("1", Followable.Type.TEAM),
            name = "Dodgers",
            imageUrl = "",
            isLoading = false,
            isFollowing = true
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("2", Followable.Type.TEAM),
            name = "49ers",
            imageUrl = "",
            isLoading = false,
            isFollowing = true
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("3", Followable.Type.TEAM),
            name = "Sharks",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("4", Followable.Type.TEAM),
            name = "Stars",
            imageUrl = "",
            isLoading = false,
            isFollowing = true
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("5", Followable.Type.TEAM),
            name = "Wild",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("6", Followable.Type.TEAM),
            name = "NBA",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("7", Followable.Type.TEAM),
            name = "NHL",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("8", Followable.Type.TEAM),
            name = "NFL",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("9", Followable.Type.TEAM),
            name = "Olympics",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("10", Followable.Type.TEAM),
            name = "Fantasy Football",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        )
    )
private val dummyInteractor = object : ManageFollowingUi.Interactor {
    override fun onBackClick() {}
    override fun onAddClick() {}
    override fun onFollowClick(item: FollowableItemUi.FollowableItem) {}
    override fun onUnfollowClick(item: FollowableItemUi.FollowableItem) {}
    override fun enableEditMode(enable: Boolean) {}
    override fun onReorder(newOrder: Map<String, Int>) {}
    override fun onFollowableClick(id: Followable.Id) {}
}