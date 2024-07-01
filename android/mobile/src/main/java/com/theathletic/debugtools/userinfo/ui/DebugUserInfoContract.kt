package com.theathletic.debugtools.userinfo.ui

import com.theathletic.debugtools.ui.userinfo.DebugUserInfoUi

interface DebugUserInfoContract {

    interface Interactor : DebugUserInfoUi.Interactor

    data class ViewState(
        val userInfoList: List<DebugUserInfoUi.UserInfoItem>
    ) : com.theathletic.ui.ViewState

    sealed class Event : com.theathletic.utility.Event() {
        class CopyToClipboard(val key: String, val contents: String) : Event()
    }
}