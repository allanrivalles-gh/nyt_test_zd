package com.theathletic.compass

import com.theathletic.compass.codegen.CompassExperiment
import com.theathletic.compass.codegen.HomeAdsV2
import com.theathletic.feed.FeedType
import java.util.Locale

private const val ADS_EXPERIMENT_PREFIX = "Ads "
private const val HOME_ADS_EXPERIMENT_PREFIX = "Home Ads"

fun CompassExperiment.getAdExperiments(): List<String> {
    val adExperiments = mutableListOf<String>()
    for (experiment in this.experimentMap) {
        if (experiment.key.startsWith(ADS_EXPERIMENT_PREFIX)) {
            experiment.value.getFormattedExperimentName()?.let {
                adExperiments.add(it)
            }
        }
    }
    return adExperiments
}

fun CompassExperiment.getHomeAdExperiments(): List<String> {
    val adExperiments = mutableListOf<String>()
    for (experiment in this.experimentMap) {
        if (experiment.key.startsWith(HOME_ADS_EXPERIMENT_PREFIX) ||
            experiment.key.startsWith(ADS_EXPERIMENT_PREFIX)
        ) {
            experiment.value.getFormattedExperimentName()?.let {
                adExperiments.add(it)
            }
        }
    }
    return adExperiments
}

private fun Experiment.getFormattedExperimentName(): String? {
    val activeVariant = this.activeVariant ?: return null
    val compassAdExperiment = "%s_%s".format(
        Locale.getDefault(),
        this.name.replace(" ", ""),
        activeVariant._name
    )
    return compassAdExperiment.lowercase()
}

fun HomeAdsV2.shouldImproveAdImpressions(feedType: FeedType): Boolean {
    var improveAdImpressions = false
    var expose = false
    when (val variant = this.activeVariant) {
        is HomeAdsV2.HomeAdsV2Variant.A -> {
            improveAdImpressions = feedType is FeedType.User
            expose = variant.shouldExpose
        }
        is HomeAdsV2.HomeAdsV2Variant.CTRL -> {
            expose = variant.shouldExpose
        }
    }
    if (expose) {
        this.postExposure()
    }

    return improveAdImpressions
}