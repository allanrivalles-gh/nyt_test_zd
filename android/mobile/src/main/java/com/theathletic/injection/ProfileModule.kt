package com.theathletic.injection

import com.theathletic.AthleticConfig
import com.theathletic.profile.data.remote.TranscendConsentWrapper
import com.theathletic.profile.ui.ProfileNavigationEventConsumer
import com.theathletic.profile.ui.ProfileNavigationEventProducer
import org.koin.core.qualifier.named
import org.koin.dsl.module

val profileModule = module {
    single { ProfileNavigationEventProducer() }
    single { ProfileNavigationEventConsumer(get()) }
    single {
        TranscendConsentWrapper(
            applicationContext = get(named("application-context")),
            transcendConsentUrl = AthleticConfig.TRANSCEND_CONSENT_URL,
            siteUrl = AthleticConfig.SITE_URL
        )
    }
}