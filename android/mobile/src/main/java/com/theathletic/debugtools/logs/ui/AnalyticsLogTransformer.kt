package com.theathletic.debugtools.logs.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.ui.Transformer

class AnalyticsLogTransformer @AutoKoin constructor() :
    Transformer<AnalyticsLogState, AnalyticsLogContract.ViewState> {

    override fun transform(data: AnalyticsLogState): AnalyticsLogContract.ViewState {
        val logsList = data.analyticsLogs.map { model ->
            AnalyticsLogUi.AnalyticsLogItem(
                label = model.name,
                params = model.params,
                collectors = "Collectors: ${model.collectors}"
            )
        }
        return AnalyticsLogContract.ViewState(logsList)
    }
}