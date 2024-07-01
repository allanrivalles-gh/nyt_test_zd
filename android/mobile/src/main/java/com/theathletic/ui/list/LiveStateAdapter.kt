package com.theathletic.ui.list

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.theathletic.BR
import com.theathletic.presenter.Interactor
import com.theathletic.viewmodel.LiveViewModelState

abstract class LiveStateAdapter(
    lifecycleOwner: LifecycleOwner,
    view: Interactor,
    private val liveViewState: LiveData<LiveViewModelState>
) : BindingDiffAdapter(lifecycleOwner, view) {

    override fun bindVariables(holder: DataBindingViewHolder<ViewDataBinding>) {
        holder.binding.setVariable(BR.liveState, liveViewState)
    }
}