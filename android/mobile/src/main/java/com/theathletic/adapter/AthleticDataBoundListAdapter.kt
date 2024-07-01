package com.theathletic.adapter

import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.theathletic.BR
import com.theathletic.ui.BaseView

/**
 * ListAdapter which calculates diff on background thread and applies it on main thread.
 * Is able to be used with data binding.
 *
 * @param view to be bound into items
 * @param initialData this items will be shown the first time adapter is created
 * @param diffItemCallback diff item callback, may be overriden for custom logic
 */
abstract class AthleticDataBoundListAdapter<T : TheSame>(
    val view: BaseView,
    initialData: List<T>? = null,
    protected open val diffItemCallback: DiffUtil.ItemCallback<T> = DefaultDiffItemCallback()
) : AthleticDataBoundRecyclerAdapter<ViewDataBinding>(view) {
    private val helper by lazy(LazyThreadSafetyMode.NONE) { AsyncListDiffer<T>(this, diffItemCallback) }

    init {
        initialData?.let { submitList(it) }
    }

    final override fun bindItem(
        holder: AthleticDataBoundRecyclerViewHolder<ViewDataBinding>,
        position: Int,
        payloads: List<Any>?
    ) {
        val item = getItem(position)
        bindItem(holder, position, item, payloads)
    }

    open fun bindItem(
        holder: AthleticDataBoundRecyclerViewHolder<ViewDataBinding>,
        position: Int,
        item: T,
        payloads: List<Any>?
    ) {
        holder.binding.setVariable(BR.view, view)
        holder.binding.setVariable(BR.data, item)
    }

    /**
     * Entry point for adapter. Sets new data with regards to DiffUtil.
     * Asynchronously calculates the diff in background thread and applies the result on main thread.
     *
     * Inherited classes may override to transform data.
     * Typically this would be used when model contains title + items and therefore list of items need to "unwrap" the children.
     * @param commitCallback may be used if some operation is needed, after data is changed
     */
    @CallSuper
    open fun submitList(list: List<T>?, commitCallback: (() -> Unit)? = null) = helper.submitList(list, commitCallback)

    fun getItem(position: Int): T = helper.currentList[position]

    override fun getItemCount(): Int = helper.currentList.size
}

/**
 * Default implementation of diff item callback.
 * Using objects implementing [TheSame], so basically just calls the functions
 */
open class DefaultDiffItemCallback<T : TheSame> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.isItemTheSame(newItem)
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem.isContentTheSame(newItem)
    override fun getChangePayload(oldItem: T, newItem: T): Any? = oldItem.getChangePayload(newItem)
}