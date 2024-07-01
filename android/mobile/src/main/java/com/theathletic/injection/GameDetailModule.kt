package com.theathletic.injection

import com.theathletic.brackets.navigation.BracketsNavigator
import com.theathletic.gamedetail.ui.GameDetailEventConsumer
import com.theathletic.gamedetail.ui.GameDetailEventProducer
import com.theathletic.navigation.AthleticNavigator
import org.koin.dsl.module

val gameDetailModule = module {
    single { GameDetailEventProducer() }
    single { GameDetailEventConsumer(get()) }
    factory<BracketsNavigator> { (activity: androidx.fragment.app.FragmentActivity) ->
        AthleticNavigator(activity, get(), get(), get(), get(), get())
    }
}