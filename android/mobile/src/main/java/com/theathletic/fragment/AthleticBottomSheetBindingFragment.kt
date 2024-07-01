package com.theathletic.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.theathletic.BR
import com.theathletic.R
import com.theathletic.event.SnackbarEvent
import com.theathletic.event.SnackbarEventRes
import com.theathletic.event.ToastEvent
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ViewState
import com.theathletic.ui.observe
import com.theathletic.ui.toaster.Toaster
import com.theathletic.ui.toaster.ToasterEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * Equivalent of AthleticMvpBindingFragment but for when we want the Fragment to extend
 * [BottomSheetDialogFragment].
 */
@Deprecated("Use AthleticBottomSheetComposeFragment")
abstract class AthleticBottomSheetBindingFragment<
    T : AthleticViewModel<*, VS>,
    B : ViewDataBinding,
    VS : ViewState
    > : BottomSheetDialogFragment() {

    private var _binding: B? = null
    val binding: B get() = requireNotNull(_binding)

    lateinit var presenter: T
        private set

    protected val navigator by inject<ScreenNavigator> { parametersOf(requireActivity()) }

    abstract fun inflateBindingLayout(inflater: LayoutInflater): B

    abstract fun setupViewModel(): T

    abstract fun renderState(viewState: VS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = setupViewModel().apply {
            lifecycle.addObserver(this)
        }

        presenter.observe<ToastEvent>(this) { showToast(it.message) }
        presenter.observe<SnackbarEventRes>(this) { showSnackbar(it.msgResId) }
        presenter.observe<ToasterEvent>(this) { showToaster(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = setupBinding(inflater)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.observe<SnackbarEvent>(viewLifecycleOwner) { showSnackbar(it.message) }

        lifecycleScope.launch {
            presenter.viewState.collect {
                binding.setVariable(BR.data, it)
                renderState(it)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        (bottomSheetDialog as? BottomSheetDialog)?.behavior?.let { customizeBottomSheetBehavior(it) }
        return bottomSheetDialog
    }

    open fun customizeBottomSheetBehavior(behavior: BottomSheetBehavior<*>) { }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupBinding(inflater: LayoutInflater) = inflateBindingLayout(inflater).apply {
        setVariable(BR.interactor, presenter)
        lifecycleOwner = viewLifecycleOwner
    }

    fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    fun showToast(stringRes: Int) {
        showToast(getString(stringRes))
    }

    fun showSnackbar(message: String) {
        view?.let { view ->
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
        }
    }

    fun showSnackbar(stringRes: Int) {
        showSnackbar(requireActivity().getString(stringRes))
    }

    fun showToaster(event: ToasterEvent) {
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