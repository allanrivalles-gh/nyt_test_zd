package com.theathletic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

/**
 * A generic ViewHolder that wraps a generated ViewDataBinding class.
 *
 * @param <T> The type of the ViewDataBinding class
 */
class AthleticDataBoundRecyclerViewHolder<T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        /**
         * Creates a new ViewHolder for the given layout file.
         *
         *
         * The provided layout must be using data binding.
         *
         * @param inflater The LayoutInflater
         * @param parent The RecyclerView
         * @param layoutId The layout id that should be inflated. Must use data binding
         * @param <T> The type of the Binding class that will be generated for the `layoutId`.
         * @return A new ViewHolder that has a reference to the binding class
         */
        fun <T : ViewDataBinding> create(
            inflater: LayoutInflater,
            parent: ViewGroup,
            @LayoutRes layoutId: Int
        ): AthleticDataBoundRecyclerViewHolder<T> {
            val binding: T = DataBindingUtil.inflate(inflater, layoutId, parent, false)
            return AthleticDataBoundRecyclerViewHolder(binding)
        }

        /**
         * Creates a new ViewHolder for the given layout file.
         *
         *
         * The provided layout must be using data binding.
         *
         * @param lifecycleOwner The LifecycleOwner
         * @param inflater The LayoutInflater
         * @param parent The RecyclerView
         * @param layoutId The layout id that should be inflated. Must use data binding
         * @param <T> The type of the Binding class that will be generated for the `layoutId`.
         * @return A new ViewHolder that has a reference to the binding class
         */
        fun <T : ViewDataBinding> create(
            lifecycleOwner: LifecycleOwner,
            inflater: LayoutInflater,
            parent: ViewGroup,
            @LayoutRes layoutId: Int
        ): AthleticDataBoundRecyclerViewHolder<T> {
            val binding: T = DataBindingUtil.inflate(inflater, layoutId, parent, false)
            binding.lifecycleOwner = lifecycleOwner
            return AthleticDataBoundRecyclerViewHolder(binding)
        }
    }
}