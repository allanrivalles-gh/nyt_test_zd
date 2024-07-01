package com.theathletic.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.theathletic.R
import com.theathletic.event.SnackbarEvent
import com.theathletic.event.SnackbarEventRes
import com.theathletic.event.ToastEvent
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.ViewState
import com.theathletic.ui.observe
import com.theathletic.ui.toaster.Toaster
import com.theathletic.ui.toaster.ToasterEvent
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * Equivalent of [AthleticComposeFragment] but for when you want to extend
 * [BottomSheetDialogFragment] instead of [Fragment].
 */
abstract class AthleticBottomSheetComposeFragment<
    T : AthleticViewModel<*, VS>,
    VS : ViewState
    > : BottomSheetDialogFragment() {

    lateinit var presenter: T
        private set

    private val displayPreferences by inject<DisplayPreferences>()
    protected val navigator by inject<ScreenNavigator> { parametersOf(requireActivity()) }

    abstract fun setupViewModel(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = setupViewModel().apply { lifecycle.addObserver(this) }
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
                val viewState by presenter.viewState.collectAsState(initial = null)
                val state = viewState ?: return@setContent

                AthleticTheme(lightMode = displayPreferences.shouldDisplayDayMode(context)) {
                    Compose(state = state)
                }
            }
        }
    }

    @Composable
    abstract fun Compose(state: VS)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.apply {
            observe<SnackbarEvent>(viewLifecycleOwner) { showSnackbar(it.message) }
            observe<ToastEvent>(viewLifecycleOwner) { showToast(it.message) }
            observe<SnackbarEventRes>(viewLifecycleOwner) { showSnackbar(it.msgResId) }
            observe<ToasterEvent>(viewLifecycleOwner) { showToaster(it) }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        (bottomSheetDialog as? BottomSheetDialog)?.behavior?.let { customizeBottomSheetBehavior(it) }
        return bottomSheetDialog
    }

    open fun customizeBottomSheetBehavior(behavior: BottomSheetBehavior<*>) { }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun showSnackbar(message: String) {
        view?.let { view ->
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showSnackbar(stringRes: Int) {
        showSnackbar(requireActivity().getString(stringRes))
    }

    private fun showToaster(event: ToasterEvent) {
        Toaster.show(
            requireActivity(),
            textRes = event.textRes,
            iconRes = event.iconRes,
            iconMaskRes = event.iconMaskRes,
            style = event.style,
        )
    }

    override fun getTheme() = R.style.Widget_Ath_BottomSheetDialogCustom
}