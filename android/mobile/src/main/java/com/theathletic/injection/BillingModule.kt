package com.theathletic.injection

import com.android.billingclient.api.BillingClient
import com.theathletic.BuildConfig
import com.theathletic.billing.BillingClientProvider
import com.theathletic.billing.RegisterGooglePurchaseScheduler
import com.theathletic.billing.debug.SkuDetailsFactory
import com.theathletic.debugtools.DebugPreferences
import org.koin.dsl.module

val billingModule = module {

    factory {
        RegisterGooglePurchaseScheduler(
            userManager = get(),
            workManager = get(),
            analytics = get()
        ) { get<DebugPreferences>().enableDebugBillingTools }
    }

    factory {
        BillingClientProvider(
            get(), get(),
            BuildConfig.DEBUG_TOOLS_ENABLED,
        ) { get<DebugPreferences>().enableDebugBillingTools }
    }

    factory {
        BillingClient.newBuilder(get())
    }

    factory {
        SkuDetailsFactory(
            get(),
            get<DebugPreferences>().debugBillingCurrency
        )
    }
}