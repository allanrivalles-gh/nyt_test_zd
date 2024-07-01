package com.theathletic.utility.flipper

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.theathletic.AthleticConfig
import com.theathletic.BuildConfig
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.utility.IFlipperUtility

class FlipperClientUtility @AutoKoin(Scope.SINGLE) constructor() : IFlipperUtility {

    private var client: FlipperClient? = null
    private lateinit var networkFlipperPlugin: NetworkFlipperPlugin

    override fun initializeAndStartFlipper(appContext: Context) {
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(appContext)) {
            SoLoader.init(appContext, false)
            client = AndroidFlipperClient.getInstance(appContext)
            addInspectorPlugin(appContext)
            client?.start()
            addNetworkPlugin()
            addDatabasePlugin(appContext)
            addSharedPrefPlugin(appContext)
        }
    }

    private fun addInspectorPlugin(appContext: Context) {
        client?.addPlugin(InspectorFlipperPlugin(appContext, DescriptorMapping.withDefaults()))
    }

    private fun addSharedPrefPlugin(context: Context) {
        val prefs = listOf(
            SharedPreferencesFlipperPlugin.SharedPreferencesDescriptor(
                AthleticConfig.PREFS_NAME,
                Context.MODE_PRIVATE
            ),
            SharedPreferencesFlipperPlugin.SharedPreferencesDescriptor(
                AthleticConfig.FEED_REFRESH_PREFS_NAME,
                Context.MODE_PRIVATE
            )
        )
        client?.addPlugin(SharedPreferencesFlipperPlugin(context, prefs))
    }

    private fun addDatabasePlugin(context: Context) {
        client?.addPlugin(DatabasesFlipperPlugin(context))
    }

    private fun addNetworkPlugin() {
        networkFlipperPlugin = NetworkFlipperPlugin()
        client?.addPlugin(networkFlipperPlugin)
    }

    override fun getOkHttpInterceptor() = FlipperOkhttpInterceptor(networkFlipperPlugin)
}