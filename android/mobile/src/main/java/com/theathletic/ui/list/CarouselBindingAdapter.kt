package com.theathletic.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.theathletic.BR
import com.theathletic.presenter.Interactor
import com.theathletic.ui.UiModel

/**
 * Same as BindindgDiffAdapter but does not have the same list diffing functionality. This is
 * because the list diffing happens on a background thread then gets posted onto the UI thread. We
 * don't want this because when carousels get recycled, we see the previous views for a few frames
 * until the diff-ing is done.
 */
abstract class CarouselBindingAdapter(
    val lifecycleOwner: LifecycleOwner,
    val interactor: Interactor,
    private val initialItems: List<UiModel> = emptyList()
) : RecyclerView.Adapter<DataBindingViewHolder<ViewDataBinding>>(), MutableDataAdapter {

    private val items = mutableListOf<UiModel>().apply { addAll(initialItems) }

    init {
        setHasStableIds(true)
    }

    override fun updateData(data: List<UiModel>) {
        items.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

    abstract fun getLayoutForModel(model: UiModel): Int

    override fun getItemViewType(position: Int): Int {
        return getLayoutForModel(items[position])
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingViewHolder<ViewDataBinding> {
        return DataBindingViewHolder.create(
            LayoutInflater.from(parent.context),
            parent,
            viewType
        )
    }

    open fun bindVariables(holder: DataBindingViewHolder<ViewDataBinding>) {
    }

    open fun onPostBind(
        uiModel: UiModel,
        holder: DataBindingViewHolder<ViewDataBinding>
    ) {
    }

    override fun onBindViewHolder(
        holder: DataBindingViewHolder<ViewDataBinding>,
        position: Int
    ) {
        val item = items[position]
        // Deprecated, use interactor instead of view in xml moving forward
        holder.binding.setVariable(BR.view, interactor)
        holder.binding.setVariable(BR.interactor, interactor)
        holder.binding.setVariable(BR.data, item)
        bindVariables(holder)
        holder.binding.lifecycleOwner = lifecycleOwner
        holder.binding.executePendingBindings()

        onPostBind(item, holder)
    }

    override fun getItemId(position: Int): Long {
        return items[position].stableId.hashCode().toLong()
    }

    override fun getItemCount() = items.size
}