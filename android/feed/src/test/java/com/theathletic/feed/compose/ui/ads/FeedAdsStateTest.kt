package com.theathletic.feed.compose.ui.ads

import android.view.View
import com.google.common.truth.Truth.assertThat
import com.theathletic.ads.ui.AdState
import io.mockk.mockk
import org.junit.Test
import kotlin.test.fail

class FeedAdsStateTest {
    @Test
    fun `has loaded ad with view as null in the correct id if updated ad has a view but is collapsed`() {
        val updatedAd = FeedAdsState.UpdatedAd(id = "1", isCollapsed = true, view = mockk())
        val state = FeedAdsState().updatingAd(updatedAd)
        val loadedAd = state.loadedAds[updatedAd.id] ?: fail()

        assertThat(loadedAd.view).isNull()
    }

    @Test
    fun `has loaded ad with view as null in the correct id if updated ad not collapsed but does not have a view`() {
        val updatedAd = FeedAdsState.UpdatedAd(id = "2", isCollapsed = false, view = null)
        val state = FeedAdsState().updatingAd(updatedAd)
        val loadedAd = state.loadedAds[updatedAd.id] ?: fail()

        assertThat(loadedAd.view).isNull()
    }

    @Test
    fun `has loaded ad with view in the correct id if updated ad not collapsed and has a view`() {
        val updatedAd = FeedAdsState.UpdatedAd(id = "1", isCollapsed = false, view = mockk())
        val state = FeedAdsState().updatingAd(updatedAd)
        val loadedAd = state.loadedAds[updatedAd.id] ?: fail()

        assertThat(loadedAd.view).isEqualTo(updatedAd.view)
    }

    @Test
    fun `stateForAd returns Placeholder when no ad information is present`() {
        val state = FeedAdsState()
        val stateForAd = state.stateForAd(id = "1")
        assertThat(stateForAd).isEqualTo(AdState.Placeholder)
    }

    @Test
    fun `stateForAd returns Collapsed if there is ad information but no view`() {
        val state = FeedAdsState(
            loadedAds = mapOf("1" to FeedAdsState.LoadedAd(null)),
        )
        val stateForAd = state.stateForAd(id = "1")
        assertThat(stateForAd).isEqualTo(AdState.Collapsed)
    }

    @Test
    fun `stateForAd returns Visible if there is ad information with a view`() {
        val view: View = mockk()
        val state = FeedAdsState(
            loadedAds = mapOf("1" to FeedAdsState.LoadedAd(view))
        )
        val stateForAd = state.stateForAd(id = "1")
        assertThat(stateForAd).isEqualTo(AdState.Visible(view))
    }
}