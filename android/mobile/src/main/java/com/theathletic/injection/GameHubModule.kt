package com.theathletic.injection

import androidx.fragment.app.FragmentActivity
import com.theathletic.hub.game.navigation.GameHubNavigator
import com.theathletic.navigation.AthleticNavigator
import org.koin.dsl.module

val gameHubModule = module {
    factory<GameHubNavigator> { (activity: FragmentActivity) ->
        AthleticNavigator(activity, get(), get(), get(), get(), get())
    }
}