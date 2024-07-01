package com.theathletic.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.theathletic.fragment.compose.rememberViewModel
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.profile.following.ManageFollowingViewModel
import com.theathletic.profile.ui.ManageFollowingScreen
import com.theathletic.profile.ui.ViewMode

/**
 * Modal Bottom Sheet wrapper for Followables
 */
@Composable
fun ModalFollowableBottomSheet(
    navigator: ScreenNavigator
) {
    val bottomSheetViewModel: ManageFollowingViewModel = rememberViewModel(
        ManageFollowingViewModel.Params(view = "feed", autoFollowId = null),
        navigator
    )
    val viewState by bottomSheetViewModel.viewState.collectAsState(initial = null)
    val followingItems = viewState?.followingItems ?: return
    val viewMode = viewState?.viewMode ?: ViewMode.VIEW

    ManageFollowingScreen(
        followableItems = followingItems,
        interactor = bottomSheetViewModel,
        viewMode = viewMode
    )
}