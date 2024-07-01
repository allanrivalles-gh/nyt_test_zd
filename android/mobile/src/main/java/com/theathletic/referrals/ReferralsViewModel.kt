package com.theathletic.referrals

import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.theathletic.AthleticApplication
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.referrals.ReferralsContract.StateType
import com.theathletic.referrals.data.ReferralsRepository
import com.theathletic.share.ShareBroadcastReceiver
import com.theathletic.share.ShareEventConsumer
import com.theathletic.user.UserManager
import com.theathletic.utility.coroutines.collectIn
import java.net.UnknownHostException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class ReferralsViewModel(
    private val referralsRepository: ReferralsRepository,
    private val analytics: Analytics,
    private val userManager: UserManager = UserManager,
    private val shareEventConsumer: ShareEventConsumer,
    extras: Bundle?
) : AndroidViewModel(AthleticApplication.getContext()) {

    private val _state = MutableLiveData(
        ReferralsContract.State(StateType.INITIAL, 0, 5)
    )
    val state = _state as LiveData<ReferralsContract.State>
    private val referralsAvailable: String
        get() {
            return (_state.value!!.totalPasses - _state.value!!.passesRedeemed).toString()
        }

    init {
        val source = extras?.getString("source") ?: "icon"
        val userEntity = userManager.getCurrentUser()
        val referralsRedeemed = userEntity?.referralsRedeemed ?: 0
        val totalPasses = userEntity?.referralsTotal ?: 5
        val initialState = if (referralsRedeemed < totalPasses) {
            StateType.INITIAL
        } else {
            StateType.OUT_OF_GUEST_PASSES
        }
        _state.value = _state.value!!.copy(
            type = initialState,
            passesRedeemed = referralsRedeemed,
            totalPasses = totalPasses,
            source = source
        )
        waitToConfirmShareIntent()
        analytics.track(Event.Referrals.PageView())
    }

    /**
     * Listen to a flow that emits events whenever the OS lets us know a user is returning to our
     * app after leaving it via a share intent.
     */
    private fun waitToConfirmShareIntent() {
        shareEventConsumer.filter { it.hasExtra(ShareBroadcastReceiver.ShareKey.REFERRALS.value) }
            .collectIn(viewModelScope) {
                val sharedLinkId = _state.value!!.shareUrl.split("/").last()
                analytics.track(
                    Event.Referrals.ShareLink(
                        object_id = sharedLinkId,
                        referrals_available = referralsAvailable
                    )
                )
            }
    }

    val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        if (throwable is UnknownHostException) {
            _state.value = _state.value!!.copy(type = StateType.NO_NETWORK)
        } else {
            throw throwable
        }
    }

    fun onClickSendGuestPass() {
        _state.value = _state.value!!.copy(type = StateType.FETCHING_SHARE_URL)
        viewModelScope.launch(handler) {
            val response = referralsRepository.createReferralUrl(userManager.getCurrentUserId())
            if (response.isSuccessful) {
                val referralUrl = response.body()?.referralUrl
                referralUrl?.let { url ->
                    _state.value = _state.value!!.copy(
                        type = StateType.OPEN_SHARE_SHEET,
                        shareUrl = url
                    )
                    return@launch
                }
            }
            _state.value!!.copy(type = StateType.ERROR_FETCHING_SHARE_URL)
        }
    }

    fun onClickRequestMorePasses() {
        analytics.track(Event.Referrals.RequestMore())
        _state.value = _state.value!!.copy(type = StateType.REQUEST_FOR_MORE_SENT)
    }
}