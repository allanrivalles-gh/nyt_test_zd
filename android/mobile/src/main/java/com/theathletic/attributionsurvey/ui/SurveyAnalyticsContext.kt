package com.theathletic.attributionsurvey.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class SurveyAnalyticsContext @AutoKoin(Scope.SINGLE) constructor() {
    var navigationSource: String = ""
    var referralObjectType: String = ""
    var referralObjectId: Long = -1L

    fun clearValues() {
        navigationSource = ""
        referralObjectType = ""
        referralObjectId = -1L
    }
}