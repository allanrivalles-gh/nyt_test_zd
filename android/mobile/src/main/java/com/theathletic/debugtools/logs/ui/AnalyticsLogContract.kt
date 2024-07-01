package com.theathletic.debugtools.logs.ui

interface AnalyticsLogContract {

    interface Interactor : AnalyticsLogUi.Interactor

    data class ViewState(val analyticsLogList: List<AnalyticsLogUi.AnalyticsLogItem>) :
        com.theathletic.ui.ViewState
}