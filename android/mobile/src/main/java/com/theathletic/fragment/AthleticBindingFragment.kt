package com.theathletic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.theathletic.BR
import com.theathletic.R
import com.theathletic.event.NetworkErrorEvent
import com.theathletic.event.SnackbarEvent
import com.theathletic.event.SnackbarEventRes
import com.theathletic.event.ToastEvent
import com.theathletic.ui.LegacyAthleticViewModel

@Deprecated("Use AthleticComposeFragment if possible, otherwise AthleticMvpBindingFragment")
abstract class AthleticBindingFragment<T : LegacyAthleticViewModel, B : ViewDataBinding> : AthleticFragment() {
    private var _binding: B? = null
    val binding: B get() = requireNotNull(_binding)
    lateinit var viewModel: T
        private set

    val isViewModelInitialized: Boolean get() = ::viewModel.isInitialized

    abstract fun inflateBindingLayout(inflater: LayoutInflater): B

    abstract fun setupViewModel(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = setupViewModel()
        viewModel.observe<ToastEvent>(this) { showToast(it.message) }

        viewModel.observe<SnackbarEventRes>(this) {
            showSnackbar(it.msgResId)
        }
        viewModel.observe<NetworkErrorEvent>(this) {
            showSnackbar(R.string.global_network_offline)
        }
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
        viewModel.observe<SnackbarEvent>(viewLifecycleOwner) { showSnackbar(it.message) }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupBinding(inflater: LayoutInflater) = inflateBindingLayout(inflater).apply {
        setVariable(BR.view, this@AthleticBindingFragment)
        setVariable(BR.viewModel, viewModel)
        lifecycleOwner = viewLifecycleOwner
    }
}