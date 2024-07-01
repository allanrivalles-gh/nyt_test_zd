package com.theathletic.debugtools.logs

import android.widget.Toast
import com.theathletic.AthleticApplication
import com.theathletic.AthleticConfig
import com.theathletic.analytics.newarch.AnalyticsEventConsumer
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.debugtools.logs.db.AnalyticsLogDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * Code pertaining to the database for holding analytics logging events.
 */
class AnalyticsLogHelper(
    private val analyticsLogDao: AnalyticsLogDao,
    private val debugPreferences: DebugPreferences
) {
    fun initialize(analyticsEventConsumer: AnalyticsEventConsumer) {
        if (!AthleticConfig.DEBUG_TOOLS_ENABLED) return

        GlobalScope.launch(Dispatchers.Main) {
            analyticsEventConsumer.flatMapConcat {
                delay(1000)
                flowOf(it)
            }.collect { debugToolEvent ->
                if (debugToolEvent.isNoisy && !debugPreferences.showNoisyEvents) return@collect
                if (debugPreferences.areToastsEnabled) {
                    Toast.makeText(
                        AthleticApplication.getContext(),
                        """
                            event=${debugToolEvent.event.eventName}
                            props=${debugToolEvent.properties}
                            collectors=${debugToolEvent.event.collectors}
                        """.trimIndent(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            analyticsEventConsumer.collect { debugToolEvent ->
                val analyticsEvent = AnalyticsLogModel(
                    name = debugToolEvent.event.eventName,
                    params = debugToolEvent.properties,
                    isNoisy = debugToolEvent.isNoisy,
                    collectors = debugToolEvent.event.collectors
                )

                analyticsLogDao.insertLog(analyticsEvent)
            }
        }
    }
}