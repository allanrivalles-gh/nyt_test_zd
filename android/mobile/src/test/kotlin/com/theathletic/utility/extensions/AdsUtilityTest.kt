package com.theathletic.utility.extensions

import com.theathletic.ads.ui.AdWrapperUiModel
import com.theathletic.ui.list.DefaultEmptyUiModel
import com.theathletic.ui.list.ListRoot
import com.theathletic.ui.list.ListVerticalPadding
import kotlin.test.assertEquals
import org.junit.Test

class AdsUtilityTest {

    @Test
    fun `filter padding ui models around ad ui models`() {
        val models = listOf(
            ListRoot,
            ListVerticalPadding(10),
            AdWrapperUiModel("dropzone-1", 0, null),
            ListVerticalPadding(10),
            DefaultEmptyUiModel
        )

        val filteredModels = models.filterPaddingAroundAds()
        assertEquals(5, models.size)
        assertEquals(4, filteredModels.size)
    }

    @Test
    fun `no filter padding without adwrapperuimodel`() {
        val models = listOf(
            ListRoot,
            ListVerticalPadding(10),
            DefaultEmptyUiModel,
            ListVerticalPadding(10),
            DefaultEmptyUiModel
        )

        val filteredModels = models.filterPaddingAroundAds()
        assertEquals(5, models.size)
        assertEquals(5, filteredModels.size)
    }
}