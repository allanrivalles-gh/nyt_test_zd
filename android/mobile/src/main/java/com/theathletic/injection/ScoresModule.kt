package com.theathletic.injection

import com.theathletic.navigation.AthleticNavigator
import com.theathletic.scores.navigation.ScoresFeedNavigator
import com.theathletic.scores.ui.DateChangeEventConsumer
import com.theathletic.scores.ui.DateChangeEventProducer
import org.koin.dsl.module

val scoresModule = module {
    factory<ScoresFeedNavigator> { (activity: androidx.fragment.app.FragmentActivity) ->
        AthleticNavigator(activity, get(), get(), get(), get(), get())
    }

    single { DateChangeEventProducer() }
    single { DateChangeEventConsumer(get()) }
}