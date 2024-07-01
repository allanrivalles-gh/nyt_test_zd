package com.theathletic.ui.list

import android.view.LayoutInflater
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.theathletic.databinding.FragmentRecyclerBaseMvpBinding
import com.theathletic.extension.betterSmoothScrollToPosition
import com.theathletic.fragment.AthleticMvpBindingFragment
import com.theathletic.ui.UiModel

/**
 * A class which helps minimize boilerplate when creating a screen that is just a list of [UiModel].
 *
 * By using this class, you do not need to create an xml layout, an Adapter, ViewState, or handle
 * the [renderState] changes yourself. All you need to do is override [getLayoutForModel] and
 * specify which list layout corresponds to each supported [UiModel].
 *
 * Use this in conjunction with [AthleticListViewModel].
 */
@Deprecated("Use AthleticComposeFragment")
abstract class AthleticMvpListFragment<V : ListViewState, ViewModel : AthleticListViewModel<*, V>> :
    AthleticMvpBindingFragment<ViewModel, FragmentRecyclerBaseMvpBinding, V>() {

    open val listAdapter: BindingDiffAdapter by lazy {
        ListFragmentAdapter(viewLifecycleOwner, presenter)
    }

    abstract fun getLayoutForModel(model: UiModel): Int

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentRecyclerBaseMvpBinding {
        return FragmentRecyclerBaseMvpBinding.inflate(inflater).apply {
            interactor = this@AthleticMvpListFragment.presenter
            setupRecyclerView(recyclerView)
        }
    }

    @CallSuper
    open fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(requireContext())
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }

    override fun renderState(viewState: V) {
        binding.data = viewState
        listAdapter.submitList(viewState.uiModels)
    }

    open fun onPostBind(
        uiModel: UiModel,
        holder: DataBindingViewHolder<ViewDataBinding>
    ) {
    }

    fun scrollListToPosition(position: Int) {
        if (isBindingAvailable) {
            binding.recyclerView.betterSmoothScrollToPosition(position)
        }
    }

    fun addOnScrollListener(listener: RecyclerView.OnScrollListener) {
        if (isBindingAvailable) {
            binding.recyclerView.addOnScrollListener(listener)
        }
    }

    fun removeOnScrollListener(listener: RecyclerView.OnScrollListener) {
        if (isBindingAvailable) {
            binding.recyclerView.removeOnScrollListener(listener)
        }
    }

    /** use this method to toggle swipe to refresh action of refresh layout */
    fun enableSwipeRefreshing(flag: Boolean) {
        binding.refreshLayout.isEnabled = flag
    }

    private inner class ListFragmentAdapter(
        lifecycleOwner: LifecycleOwner,
        interactor: AthleticListInteractor
    ) : BindingDiffAdapter(lifecycleOwner, interactor) {

        override fun getLayoutForModel(model: UiModel): Int {
            return this@AthleticMvpListFragment.getLayoutForModel(model)
        }

        override fun onPostBind(
            uiModel: UiModel,
            holder: DataBindingViewHolder<ViewDataBinding>
        ) {
            return this@AthleticMvpListFragment.onPostBind(uiModel, holder)
        }
    }
}