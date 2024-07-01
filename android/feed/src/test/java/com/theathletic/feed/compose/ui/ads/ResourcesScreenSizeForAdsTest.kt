package com.theathletic.feed.compose.ui.ads

import android.content.Context
import android.util.Size
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class ResourcesScreenSizeForAdsTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    @Config(qualifiers = "w480dp-h800dp")
    fun `returns correct size when there is no gutter padding`() {
        val dimension = context.resources.screenSizeForAds
        assertThat(dimension).isEqualTo(Size(480, 800))
    }

    @Test
    @Config(qualifiers = "w720dp-h1280dp")
    fun `returns correct size when there is a small gutter padding`() {
        val dimension = context.resources.screenSizeForAds
        assertThat(dimension).isEqualTo(Size(560, 1280))
    }

    @Test
    @Config(qualifiers = "w720dp-h1280dp-land")
    fun `returns correct size when there is a large gutter padding`() {
        val dimension = context.resources.screenSizeForAds
        assertThat(dimension).isEqualTo(Size(840, 720))
    }
}