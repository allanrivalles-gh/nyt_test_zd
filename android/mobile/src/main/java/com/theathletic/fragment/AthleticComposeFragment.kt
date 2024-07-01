package com.theathletic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.theathletic.event.HapticFailureFeedback
import com.theathletic.event.HapticSuccessFeedback
import com.theathletic.event.SnackbarEvent
import com.theathletic.event.SnackbarEventRes
import com.theathletic.event.ToastEvent
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.ViewState
import com.theathletic.ui.observe
import com.theathletic.ui.toaster.ToasterEvent
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * A [Fragment] for UIs that are meant to be built using Jetpack Compose. It provides a [Compose]
 * function which is marked as [Composable] so all you need to do is call your [Composable]
 * functions inside of it.
 *
 * The [AthleticTheme] is already applied to the [Compose] function so any screens that use this
 * [Fragment] do not need to manually add it around their compositions.
 */
@Immutable
abstract class AthleticComposeFragment<
    T : AthleticViewModel<*, VS>,
    VS : ViewState
    > : AthleticFragment() {

    lateinit var viewModel: T
        private set

    protected val displayPreferences by inject<DisplayPreferences>()
    protected val navigator by inject<ScreenNavigator> { parametersOf(requireActivity()) }
    protected val isResumed = MutableStateFlow(false)

    abstract fun setupViewModel(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = setupViewModel().also(lifecycle::addObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        return ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val viewState by viewModel.viewState.collectAsState(initial = null)
                val state = viewState ?: return@setContent

                AthleticTheme(lightMode = displayPreferences.shouldDisplayDayMode(context)) {
                    Compose(state = state)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isResumed.value = false
    }

    override fun onResume() {
        super.onResume()
        isResumed.value = true
    }

    /**
     * The root [Composable] function for [AthleticComposeFragment]. When implementing a new
     * [AthleticComposeFragment], override this function so you can call [Composable] functions.
     * This is already wrapped in an [AthleticTheme] so no need to wrap your [Composable] in
     * [AthleticTheme] yourself.
     */
    @Composable
    abstract fun Compose(state: VS)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.apply {
            observe<SnackbarEvent>(viewLifecycleOwner) { showSnackbar(it.message) }
            observe<ToastEvent>(viewLifecycleOwner) { showToast(it.message) }
            observe<SnackbarEventRes>(viewLifecycleOwner) { showSnackbar(it.msgResId) }
            observe<ToasterEvent>(viewLifecycleOwner) { showToaster(it) }
            observe<HapticSuccessFeedback>(viewLifecycleOwner) { performHapticSuccessFeedback() }
            observe<HapticFailureFeedback>(viewLifecycleOwner) { performHapticFailureFeedback() }
        }
    }
}