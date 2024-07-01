package com.theathletic

import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DateUnitTest {
    @Before
    fun setUp() {
        val application: AthleticApplication = mock()
        val resources: Resources = mock()
        val configuration: Configuration = mock()
        whenever(application.resources).thenReturn(resources)
        whenever(resources.configuration).thenReturn(configuration)
        configuration.locale = Locale.US
        AthleticApplication.setInstance(application)
    }
}