package com.theathletic.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.theathletic.BR
import com.theathletic.ui.LegacyAthleticViewModel

@SuppressLint("Registered")
abstract class AthleticBindingActivity<T : LegacyAthleticViewModel, B : ViewDataBinding> : BaseActivity() {
    lateinit var viewModel: T
        private set
    lateinit var binding: B
        private set

    abstract fun setupViewModel(): T
    abstract fun inflateBindingLayout(inflater: LayoutInflater): B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = setupViewModel()
        binding = setupBinding(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupBinding(inflater: LayoutInflater): B {
        val binding = inflateBindingLayout(inflater)
        binding.setVariable(BR.view, this)
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this
        return binding
    }
}