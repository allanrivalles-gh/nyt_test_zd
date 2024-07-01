package com.theathletic.attributionsurvey.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class SurveyCache @AutoKoin(Scope.SINGLE) constructor() {
    var survey: AttributionSurvey? = null
        private set

    fun updateCache(newSurvey: AttributionSurvey?) {
        survey = newSurvey
    }
}