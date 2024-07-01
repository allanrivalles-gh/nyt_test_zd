package com.theathletic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.theathletic.BR
import com.theathletic.event.SnackbarEvent
import com.theathletic.event.SnackbarEventRes
import com.theathletic.event.ToastEvent
import com.theathletic.viewmodel.BaseViewModel

@Deprecated("We should use AthleticBindingFragment instead")
abstract class BaseBindingFragment<T : BaseViewModel, B : ViewDataBinding> : BaseFragment() {
    private var _binding: B? = null
    val binding: B get() = requireNotNull(_binding)
    lateinit var viewModel: T
        private set
    val isViewModelInitialized: Boolean get() = ::viewModel.isInitialized
    val isBindingInitialized: Boolean get() = _binding != null

    abstract fun inflateBindingLayout(inflater: LayoutInflater): B

    abstract fun setupViewModel(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = setupViewModel()
        viewModel.observeEvent(
            this, ToastEvent::class.java,
            Observer { toastEvent -> showToast(toastEvent.message) }
        )
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
        viewModel.observeEvent(
            viewLifecycleOwner, SnackbarEvent::class.java,
            Observer { snackbarEvent -> showSnackbar(snackbarEvent.message) }
        )
        viewModel.observeEvent(
            viewLifecycleOwner, SnackbarEventRes::class.java,
            Observer { showSnackbar(it.msgResId) }
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupBinding(inflater: LayoutInflater) = inflateBindingLayout(inflater).apply {
        setVariable(BR.view, this@BaseBindingFragment)
        setVariable(BR.viewModel, viewModel)
        lifecycleOwner = viewLifecycleOwner
    }
}