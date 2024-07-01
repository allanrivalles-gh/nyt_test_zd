package com.theathletic.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class DataBindingViewHolder<T : ViewDataBinding>(
    val binding: T
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun <T : ViewDataBinding> create(
            inflater: LayoutInflater,
            parent: ViewGroup,
            @LayoutRes layoutId: Int
        ): DataBindingViewHolder<T> {
            val binding = DataBindingUtil.inflate<T>(inflater, layoutId, parent, false)
            return DataBindingViewHolder(binding)
        }
    }
}