package com.theathletic.debugtools.logs.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.debugtools.logs.AnalyticsLogModel
import com.theathletic.debugtools.logs.db.AnalyticsLogDao
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class AnalyticsLogViewModel @AutoKoin constructor(
    @Assisted private val navigator: ScreenNavigator,
    private val debugPreferences: DebugPreferences,
    private val analyticsHistoryLogDao: AnalyticsLogDao,
    transformer: AnalyticsLogTransformer
) : AthleticViewModel<AnalyticsLogState, AnalyticsLogContract.ViewState>(),
    AnalyticsLogContract.Interactor,
    DefaultLifecycleObserver,
    Transformer<AnalyticsLogState, AnalyticsLogContract.ViewState> by transformer {

    override val initialState = AnalyticsLogState()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        loadAnalyticsLogData()
    }

    override fun onBackClick() {
        navigator.finishActivity()
    }

    override fun onClearClicked() {
        viewModelScope.launch {
            analyticsHistoryLogDao.clearAllData()
        }
    }

    private fun loadAnalyticsLogData() {
        if (debugPreferences.showNoisyEvents) {
            analyticsHistoryLogDao.getAllLogs()
        } else {
            analyticsHistoryLogDao.getNonNoisyLogs()
        }.collectIn(viewModelScope) {
            updateState { copy(analyticsLogs = it) }
        }
    }
}

data class AnalyticsLogState(
    val analyticsLogs: List<AnalyticsLogModel> = emptyList()
) : DataState