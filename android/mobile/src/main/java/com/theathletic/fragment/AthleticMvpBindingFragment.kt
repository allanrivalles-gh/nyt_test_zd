package com.theathletic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.theathletic.BR
import com.theathletic.event.SnackbarEvent
import com.theathletic.event.SnackbarEventRes
import com.theathletic.event.ToastEvent
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ViewState
import com.theathletic.ui.observe
import com.theathletic.ui.toaster.ToasterEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Deprecated("Use AthleticComposeFragment")
abstract class AthleticMvpBindingFragment<
    T : AthleticViewModel<*, VS>,
    B : ViewDataBinding,
    VS : ViewState
    > : AthleticFragment() {

    private var _binding: B? = null
    val binding: B get() = requireNotNull(_binding)
    val isBindingAvailable: Boolean get() = _binding != null

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
        presenter.observe<SnackbarEventRes>(this) {
            showSnackbar(it.msgResId)
        }
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
            binding.setVariable(BR.data, presenter.viewState.first())

            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                presenter.viewState.collect {
                    binding.setVariable(BR.data, it)
                    renderState(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupBinding(inflater: LayoutInflater) = inflateBindingLayout(inflater).apply {
        setVariable(BR.interactor, presenter)
        lifecycleOwner = viewLifecycleOwner
    }
}