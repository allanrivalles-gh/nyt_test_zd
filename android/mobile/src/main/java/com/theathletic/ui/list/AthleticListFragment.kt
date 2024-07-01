package com.theathletic.ui.list

import android.view.LayoutInflater
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.theathletic.R
import com.theathletic.databinding.FragmentRecyclerBaseBinding
import com.theathletic.fragment.AthleticBindingFragment
import com.theathletic.presenter.Interactor
import com.theathletic.ui.UiModel

@Deprecated("Use AthleticComposeFragment")
abstract class AthleticListFragment<VModel : LegacyAthleticListViewModel, AView : Interactor> :
    AthleticBindingFragment<VModel, FragmentRecyclerBaseBinding>(),
    IBaseListView {

    open val backgroundColorRes = R.color.black
    override val backgroundColor get() = ContextCompat.getColor(requireContext(), backgroundColorRes)

    override val showActionBar = true

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentRecyclerBaseBinding {
        return FragmentRecyclerBaseBinding.inflate(inflater).apply {
            view = this@AthleticListFragment
            viewModel = this@AthleticListFragment.viewModel
            setupRecyclerView(recyclerView)
        }
    }

    @CallSuper
    open fun setupRecyclerView(recyclerView: RecyclerView) {
        val adapter = getAdapter()

        recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }

        viewModel.uiModels.observe(
            viewLifecycleOwner,
            Observer { adapter.submitList(it) }
        )
    }

    @SuppressWarnings("unchecked")
    open fun getAdapter(): BindingDiffAdapter = ListFragmentAdapter(viewLifecycleOwner, this as AView)

    abstract fun getLayoutForModel(model: UiModel): Int

    open fun onPostBind(
        uiModel: UiModel,
        holder: DataBindingViewHolder<ViewDataBinding>
    ) {}

    private inner class ListFragmentAdapter(
        lifecycleOwner: LifecycleOwner,
        view: AView
    ) : BindingDiffAdapter(lifecycleOwner, view) {

        override fun getLayoutForModel(model: UiModel): Int {
            return this@AthleticListFragment.getLayoutForModel(model)
        }

        override fun onPostBind(
            uiModel: UiModel,
            holder: DataBindingViewHolder<ViewDataBinding>
        ) {
            return this@AthleticListFragment.onPostBind(uiModel, holder)
        }
    }
}

interface IBaseListView {
    val showActionBar: Boolean
    val backgroundColor: Int
}