package com.theathletic.profile.ui

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.theathletic.R
import com.theathletic.auth.ui.AuthenticationSpannableFormatter
import com.theathletic.databinding.ListItemProfileFollowingCarouselBinding
import com.theathletic.databinding.ListItemProfileLoginBinding
import com.theathletic.main.ui.FollowableNavigationBar
import com.theathletic.main.ui.NavItemBackground
import com.theathletic.main.ui.NavigationItem
import com.theathletic.main.ui.SimpleNavItem
import com.theathletic.presenter.Interactor
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.BindingDiffAdapter
import com.theathletic.ui.list.DataBindingViewHolder
import com.theathletic.ui.list.Divider
import com.theathletic.ui.list.ListVerticalPadding

class ProfileAdapter(
    lifecycleOwner: LifecycleOwner,
    interactor: Interactor
) : BindingDiffAdapter(lifecycleOwner, interactor) {

    override fun getLayoutForModel(model: UiModel) = when (model) {
        is ProfileHeaderItem -> R.layout.list_item_profile_header
        is ProfileAnonymousHeaderItem -> R.layout.list_item_profile_anonymous_header
        is ProfileFooterItem -> R.layout.list_item_profile_footer
        is ProfileListItem -> if (model.primaryItem) {
            R.layout.list_item_profile_row
        } else {
            R.layout.list_item_profile_row_secondary
        }

        is ProfileFollowingListItem -> R.layout.list_item_profile_following_carousel
        is ProfileSubscribeItem -> R.layout.list_item_profile_subscribe
        is ProfileLoginItem -> R.layout.list_item_profile_login
        is DayNightToggleItem -> R.layout.list_item_profile_day_night_toggle

        is Divider -> R.layout.list_item_profile_divider
        is ListVerticalPadding -> R.layout.list_padding_vertical

        else -> throw IllegalStateException("Adapter cannot support ${model.javaClass}")
    }

    override fun onPostBind(
        uiModel: UiModel,
        holder: DataBindingViewHolder<ViewDataBinding>
    ) {
        when (uiModel) {
            is ProfileFollowingListItem -> setupFollowingAdapter(uiModel, holder)
            is ProfileLoginItem -> setupLoginSpannable(holder)
        }
    }

    private fun setupFollowingAdapter(
        presentationModel: ProfileFollowingListItem,
        carouselHolder: DataBindingViewHolder<ViewDataBinding>
    ) {
        val holder = carouselHolder as DataBindingViewHolder<ListItemProfileFollowingCarouselBinding>
        val itemInteractor = view as ProfileFollowingCarouselItem.Interactor
        val addMoreInteractor = view as ProfileFollowingCarouselAddMoreItem.Interactor
        val navItems = presentationModel.toNavItemsList()
        holder.binding.composeProfileFollowables.setContent {
            FollowableNavigationBar(
                navItems = navItems,
                showEdit = false,
                showAddIfEmpty = true,
                onAddClick = { addMoreInteractor.onAddMoreClicked() },
                onFollowableClick = { followableId, _ -> itemInteractor.onFollowingItemClicked(followableId) },
            )
        }
    }

    private fun setupLoginSpannable(
        holder: DataBindingViewHolder<ViewDataBinding>
    ) {
        val loginHolder = holder as DataBindingViewHolder<ListItemProfileLoginBinding>
        with(loginHolder.binding.loginText) {
            AuthenticationSpannableFormatter.configureLoginRegisterSpannableCTA(
                this,
                R.string.authentication_text_note,
                R.string.authentication_text_spannable_log_in
            )
        }
    }
}

@Deprecated("Temporary, remove it when we move profile to compose")
private fun ProfileFollowingListItem.toNavItemsList(): List<NavigationItem> {
    return carouselItemModels.mapNotNull { uiModel ->
        when (uiModel) {
            is ProfileFollowingCarouselItem -> {
                SimpleNavItem(
                    id = uiModel.id,
                    title = uiModel.itemAbbreviation,
                    imageUrl = uiModel.iconUri,
                    color = uiModel.contrastColorHex,
                    background = uiModel.background
                )
            }

            else -> null
        }
    }
}

@Deprecated("Temporary, remove it when we move profile to compose")
private val ProfileFollowingCarouselItem.background: NavItemBackground
    get() = if (hasContrastColor) {
        NavItemBackground.Colored(contrastColorHex)
    } else {
        NavItemBackground.Empty
    }