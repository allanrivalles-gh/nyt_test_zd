package com.theathletic.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class EntityStateTest {
    @Test
    fun `verify EntityState comparison operator overrides`() {
        assertThat(EntityState.SUMMARY < EntityState.DETAIL).isTrue()
        assertThat(EntityState.SUMMARY <= EntityState.DETAIL).isTrue()
        assertThat(EntityState.SUMMARY > EntityState.DETAIL).isFalse()
        assertThat(EntityState.DETAIL < EntityState.SUMMARY).isFalse()
        assertThat(EntityState.SUMMARY <= EntityState.SUMMARY).isTrue()
    }
}