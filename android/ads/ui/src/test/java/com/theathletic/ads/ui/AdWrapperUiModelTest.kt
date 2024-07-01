package com.theathletic.ads.ui

import com.theathletic.ads.AdView
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals

class AdWrapperUiModelTest {

    private lateinit var adWrapper: AdWrapperUiModel

    @Mock lateinit var adView: AdView

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        adWrapper = AdWrapperUiModel("test", 0, adView)
    }

    @Test
    fun `verify stableId on uiModel`() {
        assertEquals("test", adWrapper.stableId)
    }
}