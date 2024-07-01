package com.theathletic.ui.utility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.core.context.GlobalContext.get
import org.koin.core.parameter.parametersOf

@Composable
inline fun <reified T> rememberKoin(
    vararg parameters: Any?,
): T = remember(*parameters) {
    get().get { parametersOf(*parameters) }
}