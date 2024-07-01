package com.theathletic.network.rest

import com.google.gson.annotations.SerializedName
import com.theathletic.injection.baseModule
import com.theathletic.network.rest.GsonProvider.buildGsonWithAllAdapters
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest

class GsonTest : AutoCloseKoinTest() {
    private val containerJsonWithRealValues = "{\"some_string\": \"string\", \"some_boolean\": true, \"some_int\": 12, \"some_float\": 13.0}"
    private val containerJsonWithNullValues = "{\"some_string\": null, \"some_boolean\": null, \"some_int\": null, \"some_float\": null}"
    private val containerJsonWithMissingValues = "{}"

    @Before
    fun setUp() {
        startKoin {
            modules(baseModule)
        }
    }

    @Test
    fun fromJson_ConvertsValidContainer() {
        val container = buildGsonWithAllAdapters().fromJson(containerJsonWithRealValues, DataContainer::class.java)
        assertEquals(DataContainer("string", true, 12, 13f), container)
    }

    @Test
    fun fromJson_ConvertsNullValuesToDefaults() {
        val container = buildGsonWithAllAdapters().fromJson(containerJsonWithNullValues, DataContainer::class.java)
        assertEquals(DataContainer(null, false, 0, 0f), container)
    }

    @Test
    fun fromJson_ConvertsMissingValuesToDefaults() {
        val container = buildGsonWithAllAdapters().fromJson(containerJsonWithMissingValues, DataContainer::class.java)
        assertEquals(DataContainer(null, false, 0, 0f), container)
    }

    data class DataContainer(
        @SerializedName("some_string")
        val someString: String?,
        @SerializedName("some_boolean")
        val someBoolean: Boolean,
        @SerializedName("some_int")
        val someInt: Int,
        @SerializedName("some_float")
        val someFloat: Float
    )
}