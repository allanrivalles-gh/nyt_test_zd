package com.theathletic.settings.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.DispatcherProvider

class UpdateCommentNotifications @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val settingsRestApi: SettingsRestApi,
    private val userManager: IUserManager
) : RemoteToLocalFetcher<
    UpdateCommentNotifications.Params,
    Unit,
    Unit
    >(dispatcherProvider) {

    companion object {
        const val COMMENT_NOTIFICATION_NAME = "comments"
        const val COMMENT_NOTIFICATION_TYPE = "comments"
    }

    data class Params(val notifyReplies: Boolean)

    override suspend fun makeRemoteRequest(params: Params) {
        if (params.notifyReplies) {
            settingsRestApi.addPushSettings(
                notifType = COMMENT_NOTIFICATION_TYPE,
                notifName = COMMENT_NOTIFICATION_NAME,
                notifValue = params.notifyReplies.settingsValue
            )
        } else {
            settingsRestApi.removePushSettings(
                notifType = COMMENT_NOTIFICATION_TYPE,
                notifName = COMMENT_NOTIFICATION_NAME,
                notifValue = params.notifyReplies.settingsValue
            )
        }
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: Unit
    ) {
        // Do nothing
    }

    override suspend fun saveLocally(
        params: Params,
        dbModel: Unit
    ) {
        userManager.saveCurrentUser(
            userManager.getCurrentUser()?.apply {
                commentsNotification = params.notifyReplies.settingsValue.toInt()
            },
            withRefresh = false
        )
    }

    val Boolean.settingsValue get() = if (this) 1L else 0L
}