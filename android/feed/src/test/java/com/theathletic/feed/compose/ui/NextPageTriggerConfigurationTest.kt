package com.theathletic.feed.compose.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NextPageTriggerConfigurationTest {
    @Test
    fun `shouldTrigger returns true when index exceeds threshold`() {
        val configuration = NextPageTriggerConfiguration(threshold = 3, currentItemsCount = 10)
        assertThat(configuration.shouldTrigger(index = 8)).isTrue()
    }

    @Test
    fun `shouldTrigger returns false when index is below threshold`() {
        val configuration = NextPageTriggerConfiguration(threshold = 3, currentItemsCount = 10)
        assertThat(configuration.shouldTrigger(index = 6)).isFalse()
    }

    @Test
    fun `shouldTrigger returns true when index equals currentItemsCount minus threshold`() {
        val configuration = NextPageTriggerConfiguration(threshold = 3, currentItemsCount = 10)
        assertThat(configuration.shouldTrigger(index = 7)).isTrue()
    }
}