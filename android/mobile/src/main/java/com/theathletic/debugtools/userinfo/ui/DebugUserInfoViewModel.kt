package com.theathletic.debugtools.userinfo.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.kochava.tracker.Tracker
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Named
import com.theathletic.datetime.Datetime
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.utility.Preferences

class DebugUserInfoViewModel @AutoKoin constructor(
    @Named("user-agent") val userAgentValue: String,
    @Assisted private val navigator: ScreenNavigator,
    private val userManager: IUserManager,
    transformer: DebugUserInfoTransformer
) : AthleticViewModel<DebugUserInfoState, DebugUserInfoContract.ViewState>(),
    DebugUserInfoContract.Interactor,
    Transformer<DebugUserInfoState, DebugUserInfoContract.ViewState> by transformer,
    DefaultLifecycleObserver {

    override val initialState = DebugUserInfoState()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    fun initialize() {
        updateState {
            copy(
                email = userManager.getCurrentUser()?.email,
                userId = userManager.getCurrentUserId(),
                deviceId = userManager.getDeviceId(),
                kochavaDeviceId = Tracker.getInstance().deviceId,
                userAgent = userAgentValue,
                accessToken = Preferences.accessToken,
                subscriptionEndDate = Datetime(userManager.getCurrentUser()?.endDate?.time ?: 0),
                isAnonymous = userManager.getCurrentUser()?.isAnonymous ?: false,
                firebaseToken = Preferences.pushTokenKey
            )
        }
    }

    override fun onBackClick() {
        navigator.finishActivity()
    }

    override fun onCopyToClipboard(key: String, contents: String) {
        sendEvent(DebugUserInfoContract.Event.CopyToClipboard(key, contents))
    }
}

data class DebugUserInfoState(
    val email: String? = null,
    val userId: Long? = null,
    val deviceId: String? = null,
    val kochavaDeviceId: String? = null,
    val userAgent: String? = null,
    val accessToken: String? = null,
    val subscriptionEndDate: Datetime? = null,
    val isAnonymous: Boolean = false,
    val firebaseToken: String? = null
) : DataState