package com.theathletic.utility

import android.content.Context
import okhttp3.Interceptor

interface IFlipperUtility {

    fun initializeAndStartFlipper(appContext: Context)

    fun getOkHttpInterceptor(): Interceptor?
}