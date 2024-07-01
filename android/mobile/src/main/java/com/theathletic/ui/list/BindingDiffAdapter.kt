package com.theathletic.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.theathletic.BR
import com.theathletic.R
import com.theathletic.presenter.Interactor
import com.theathletic.ui.UiModel

abstract class BindingDiffAdapter(
    val lifecycleOwner: LifecycleOwner,
    val view: Interactor
) : ListAdapter<UiModel, DataBindingViewHolder<ViewDataBinding>>(DIFF_CALLBACK), MutableDataAdapter {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(
                oldItem: UiModel,
                newItem: UiModel
            ) = oldItem.stableId == newItem.stableId

            override fun areContentsTheSame(
                oldItem: UiModel,
                newItem: UiModel
            ) = oldItem.equals(newItem)
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun updateData(data: List<UiModel>) {
        submitList(data)
    }

    /**
     * Override this function if you have a few types that want use the same layout, but you do not
     * want them to shared recycled ViewHolders. An example of this is carousels in feeds. The
     * carousels all use the same layout file, but their contents differ in things such as height
     * making them not good to share recycled ViewHolders.
     */
    open fun viewTypeOverride(model: UiModel): Int? = null

    /**
     * When overrideing [viewTypeOverride], you also need to override this function to provide
     * the layout file that the overrides will use since the viewType that they provide will not
     * be the layout they need to inflate.
     */
    open fun layoutForViewType(viewType: Int) = viewType

    abstract fun getLayoutForModel(model: UiModel): Int

    override fun getItemViewType(position: Int): Int {
        val model = getItem(position)

        viewTypeOverride(model)?.let {
            return@getItemViewType it
        }

        return when (model) {
            ListRoot -> R.layout.list_root
            ListLoadingItem -> R.layout.list_loading
            DefaultEmptyUiModel -> R.layout.list_item_empty_default
            is ListVerticalPadding -> R.layout.list_padding_vertical
            is BasicRowItem.Text -> R.layout.list_item_basic_row
            is BasicRowItem.LeftDrawableUri -> R.layout.list_item_basic_row_uri_drawable
            else -> getLayoutForModel(model)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingViewHolder<ViewDataBinding> {
        return DataBindingViewHolder.create(
            LayoutInflater.from(parent.context),
            parent,
            layoutForViewType(viewType)
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
        val item = getItem(position)
        // Deprecated, use interactor instead of view in xml moving forward
        holder.binding.setVariable(BR.view, view)
        holder.binding.setVariable(BR.interactor, view)
        holder.binding.setVariable(BR.data, item)
        bindVariables(holder)
        holder.binding.lifecycleOwner = lifecycleOwner
        holder.binding.executePendingBindings()

        onPostBind(item, holder)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).stableId.hashCode().toLong()
    }
}