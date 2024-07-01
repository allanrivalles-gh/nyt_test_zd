package com.theathletic.referrals

class ReferralsContract {
    enum class StateType {
        INITIAL,
        FETCHING_SHARE_URL,
        NO_NETWORK,
        ERROR_FETCHING_SHARE_URL,
        OPEN_SHARE_SHEET,
        OUT_OF_GUEST_PASSES,
        REQUEST_FOR_MORE_SENT
    }

    data class State(
        val type: StateType,
        val passesRedeemed: Int,
        val totalPasses: Int,
        val shareUrl: String = "",
        val source: String = ""
    )
}