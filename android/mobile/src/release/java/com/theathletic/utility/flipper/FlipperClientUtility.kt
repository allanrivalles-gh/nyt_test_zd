package com.theathletic.utility.flipper

import android.content.Context
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.utility.IFlipperUtility

// We have added Flipper for debug implementation only
// FlipperClientUtility uses Flipper library, therefore on Release builds
// we will have build failing due to library not available
// therefore we have created this No-Op class to ensure release builds do not failed
// To view FlipperClientUtility implementation, check the class under debug source folder

class FlipperClientUtility @AutoKoin(Scope.SINGLE) constructor() : IFlipperUtility {

    override fun initializeAndStartFlipper(appContext: Context) {
        // No-Op
    }

    override fun getOkHttpInterceptor() = null
}