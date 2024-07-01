package com.theathletic.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.theathletic.themes.AthleticTheme

typealias ComposableFunction<T> = @Composable (T) -> Unit
typealias ComposableContent = @Composable () -> Unit
typealias PreviewContent = ComposableFunction<ComposableContent>

/**
 * This [PreviewParameterProvider] generates both light / dark previews
 * for a given composable with @Preview annotation
 */
class AthleticThemeProvider : PreviewParameterProvider<PreviewContent> {
    override val values: Sequence<PreviewContent>
        get() = sequenceOf(athleticLightTheme, athleticDarkTheme)

    companion object {
        private val athleticLightTheme: PreviewContent = { content ->
            AthleticTheme(lightMode = true, content = content)
        }
        private val athleticDarkTheme: PreviewContent = { content ->
            AthleticTheme(lightMode = false, content = content)
        }
    }
}