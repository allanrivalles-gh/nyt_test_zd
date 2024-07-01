package com.theathletic.referrals.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.referrals.data.remote.CreateReferralUrlResponse
import com.theathletic.referrals.data.remote.ReferralsApi
import retrofit2.Response

class ReferralsRepository @AutoKoin(Scope.SINGLE) constructor(
    private val referralsApi: ReferralsApi
) {
    suspend fun createReferralUrl(userId: Long): Response<CreateReferralUrlResponse> {
        return referralsApi.createReferralUrl(userId)
    }
}