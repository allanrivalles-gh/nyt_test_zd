package com.theathletic.fragment.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ComposeViewModel
import com.theathletic.ui.ViewState
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.context.GlobalContext.get
import org.koin.core.parameter.parametersOf

/**
 * Call this from inside a [Composable] function inside of an [AthleticComposeFragment] in order
 * to inject an [AthleticViewModel]. We can use this as we start to Compose-ify our app and get
 * away from Fragment and Activities.
 *
 * The parameters you pass to this function are the same as you would pass to the [getViewModel]
 * Koin function. They just get piped into the Koin injection function.
 */
@Deprecated("We should be using Koin's built in method", ReplaceWith("koinViewModel"))
@Composable
inline fun <
    reified VS : ViewState,
    reified ViewModel : AthleticViewModel<*, VS>
    > AthleticComposeFragment<*, *>.rememberViewModel(
    vararg parameters: Any?,
): ViewModel {
    return remember(*parameters) {
        val viewModel = get<ViewModel> { parametersOf(*parameters) }
        lifecycle.addObserver(viewModel)
        viewModel
    }
}

@Deprecated("We should be using Koin's built in method", ReplaceWith("koinViewModel"))
@Composable
inline fun <reified ViewModel : ComposeViewModel> rememberViewModel(
    vararg parameters: Any?,
): ViewModel {
    val viewModel = remember(*parameters) {
        get().get<ViewModel> { parametersOf(*parameters) }
    }

    DisposableEffect(Unit) {
        viewModel.initialize()
        onDispose { viewModel.dispose() }
    }

    return viewModel
}