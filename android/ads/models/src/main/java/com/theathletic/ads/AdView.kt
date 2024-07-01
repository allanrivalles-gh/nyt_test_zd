package com.theathletic.ads

import android.view.View

interface AdView {
    val view: View
    fun pause()
    fun resume()
    fun setLightMode(lightMode: Boolean)
}