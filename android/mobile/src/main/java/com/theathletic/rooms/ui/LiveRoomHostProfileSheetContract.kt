package com.theathletic.rooms.ui

import com.theathletic.presenter.Interactor

interface LiveRoomHostProfileSheetContract {

    interface Presenter : Interactor {
        fun onFollowClicked(shouldFollow: Boolean)
        fun onTwitterHandleClicked(twitterHandle: String?)
    }

    data class ViewState(
        val name: String,
        val avatarUrl: String?,
        val twitterHandle: String?,
        val bio: String,
        val isFollowing: Boolean = false
    ) : com.theathletic.ui.ViewState
}