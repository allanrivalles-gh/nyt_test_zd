package com.theathletic.gamedetail.playergrades.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.Sport
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches

class SportsWithPlayerGrades @AutoKoin constructor(
    featureSwitches: FeatureSwitches
) {
    fun isSupported(sport: Sport) = supportedSports.contains(sport)

    private val supportedSports = setOfNotNull(
        Sport.FOOTBALL,
        Sport.SOCCER,
        Sport.BASKETBALL,
        Sport.HOCKEY,
        if (featureSwitches.isFeatureEnabled(FeatureSwitch.PLAYER_GRADES_BASEBALL)) Sport.BASEBALL else null,
    )
}