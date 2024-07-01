package com.theathletic.ui.widgets

import android.content.Context
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.text.getSpans
import androidx.test.core.app.ApplicationProvider
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class FormattedTextTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun `should map named color in resource string to theme-appropriate color`() {
        composeTestRule.setContent {
            AthleticTheme(lightMode = false) {
                val text = context.resources.getText(R.string.comments_commenting_on, AthTheme.colors, "Jordan is Back")
                assertTrue { text.contains("Jordan is Back") }
                val spans = (text as Spanned).getSpans(0, text.length, Any::class.java)
                assertEquals(2, spans.size)
                assertEquals(AthColor.Gray500.toArgb(), (spans[0] as ForegroundColorSpan).foregroundColor)
                assertEquals(AthTheme.colors.dark500.toArgb(), (spans[0] as ForegroundColorSpan).foregroundColor)
            }

            AthleticTheme(lightMode = true) {
                val text = context.resources.getText(R.string.comments_commenting_on, AthTheme.colors, "Jordan is Back")
                assertTrue { text.contains("Jordan is Back") }
                val spans = (text as Spanned).getSpans(0, text.length, Any::class.java)
                assertEquals(2, spans.size)
                assertEquals(AthColor.Gray400.toArgb(), (spans[0] as ForegroundColorSpan).foregroundColor)
                assertEquals(AthTheme.colors.dark500.toArgb(), (spans[0] as ForegroundColorSpan).foregroundColor)
            }
        }
    }
}