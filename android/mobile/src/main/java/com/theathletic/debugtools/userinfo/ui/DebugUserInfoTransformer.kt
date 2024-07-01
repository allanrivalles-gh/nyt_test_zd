package com.theathletic.debugtools.userinfo.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.debugtools.ui.userinfo.DebugUserInfoUi
import com.theathletic.ui.Transformer
import com.theathletic.utility.orShortDash

class DebugUserInfoTransformer @AutoKoin constructor(
    private val dateUtility: DateUtility
) :
    Transformer<DebugUserInfoState, DebugUserInfoContract.ViewState> {

    override fun transform(data: DebugUserInfoState): DebugUserInfoContract.ViewState {
        return DebugUserInfoContract.ViewState(
            userInfoList = listOf(
                DebugUserInfoUi.UserInfoItem(
                    label = "Email",
                    value = data.email.orShortDash()
                ),
                DebugUserInfoUi.UserInfoItem(
                    label = "User Id",
                    value = data.userId?.toString().orShortDash()
                ),
                DebugUserInfoUi.UserInfoItem(
                    label = "Device Id",
                    value = data.deviceId.orShortDash()
                ),
                DebugUserInfoUi.UserInfoItem(
                    label = "Kochava Device Id",
                    value = data.kochavaDeviceId.orShortDash()
                ),
                DebugUserInfoUi.UserInfoItem(
                    label = "User Agent",
                    value = data.userAgent.orShortDash()
                ),
                DebugUserInfoUi.UserInfoItem(
                    label = "Access Token",
                    value = data.accessToken.orShortDash()
                ),
                DebugUserInfoUi.UserInfoItem(
                    label = "Subscription End Date",
                    value = data.subscriptionEndDate?.let { date ->
                        dateUtility.formatGMTDate(date, DisplayFormat.MONTH_DATE_YEAR)
                    } ?: "-"
                ),
                DebugUserInfoUi.UserInfoItem(
                    label = "Is Anonymous?",
                    value = data.isAnonymous.toString()
                ),
                DebugUserInfoUi.UserInfoItem(
                    label = "Firebase Cloud Messaging (FCM) Token",
                    value = data.firebaseToken.orShortDash()
                )
            )
        )
    }
}