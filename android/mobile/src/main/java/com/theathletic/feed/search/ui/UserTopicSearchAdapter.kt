package com.theathletic.feed.search.ui

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.theathletic.R
import com.theathletic.databinding.ListItemTopicSearchFollowingGridBinding
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.BindingDiffAdapter
import com.theathletic.ui.list.DataBindingViewHolder
import com.theathletic.ui.list.ListSectionTitleItem

class UserTopicSearchAdapter(
    lifecycleOwner: LifecycleOwner,
    val interactor: UserTopicSearch.Interactor
) : BindingDiffAdapter(lifecycleOwner, interactor) {

    override fun getLayoutForModel(model: UiModel): Int {
        return when (model) {
            is UserSearchFollowableItem -> R.layout.list_item_user_topic
            is ListSectionTitleItem -> R.layout.list_item_manage_user_topic_section_title
            is UserSearchFollowingGrid -> R.layout.list_item_topic_search_following_grid
            else -> throw IllegalStateException("UserTopicSearchAdapter doesn't support $model")
        }
    }

    override fun onPostBind(
        uiModel: UiModel,
        holder: DataBindingViewHolder<ViewDataBinding>
    ) {
        when (uiModel) {
            is UserSearchFollowingGrid -> setupFollowingAdapter(uiModel, holder.binding)
        }
    }

    private fun setupFollowingAdapter(
        presentationModel: UserSearchFollowingGrid,
        binding: ViewDataBinding
    ) {
        val gridBinding = binding as ListItemTopicSearchFollowingGridBinding
        gridBinding.recyclerView.adapter = FollowingAdapter(lifecycleOwner, interactor).apply {
            submitList(presentationModel.carouselItemModels)
        }
    }
}

private class FollowingAdapter(
    lifecycleOwner: LifecycleOwner,
    interactor: UserTopicSearch.Interactor
) : BindingDiffAdapter(lifecycleOwner, interactor) {
    override fun getLayoutForModel(model: UiModel): Int {
        return when (model) {
            is UserSearchFollowableItem -> R.layout.carousel_item_topic_search_following
            else -> throw IllegalStateException("FollowingAdapter doesn't support $model")
        }
    }
}