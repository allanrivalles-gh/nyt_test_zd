package com.theathletic.utility

import com.google.common.truth.Truth.assertThat
import com.theathletic.utility.MathUtility.incrementIfLessThanOrEqual
import org.junit.Test

class MathUtilityTest {

    @Test
    fun incrementIfLessThanOrEqual_DoesIncrement_WhenEqual() {
        assertThat(incrementIfLessThanOrEqual(3, 3)).isEqualTo(4)
    }

    @Test
    fun incrementIfLessThanOrEqual_DoesIncrement_WhenLessThan() {
        assertThat(incrementIfLessThanOrEqual(3, 2)).isEqualTo(4)
    }

    @Test
    fun incrementIfLessThanOrEqual_DoesNotIncrement() {
        assertThat(incrementIfLessThanOrEqual(3, 5)).isEqualTo(5)
    }
}