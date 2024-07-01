package com.theathletic.feed.search.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theathletic.AthleticApplication
import com.theathletic.R
import com.theathletic.databinding.FragmentUserTopicSearchBinding
import com.theathletic.feed.search.SearchFollowableItem
import com.theathletic.followable.FollowableId
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.ui.observe
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

typealias UserTopicSearchFragmentListener = (item: FollowableId?) -> Unit

class UserTopicSearchFragment : BottomSheetDialogFragment() {

    companion object {
        val PEEK_HEIGHT = (AthleticApplication.getContext().resources.displayMetrics.heightPixels * 0.9f).roundToInt()

        private const val ARG_SELECTED_TOPIC = "selected_topic"
        private const val ARG_APPLY_SCORES_FILTERING = "apply_scores_filtering"

        fun newInstance(
            selectedTopic: FollowableId?,
            applyScoresFiltering: Boolean
        ): UserTopicSearchFragment {
            return UserTopicSearchFragment().apply {
                arguments = bundleOf(
                    ARG_SELECTED_TOPIC to selectedTopic,
                    ARG_APPLY_SCORES_FILTERING to applyScoresFiltering
                )
            }
        }
    }

    data class UserTopicSearchParameters(
        val selectedTopic: FollowableId?,
        val applyScoresFiltering: Boolean
    )

    private val navigator: ScreenNavigator by inject { parametersOf(requireActivity()) }

    private val presenter by viewModel<UserTopicSearchViewModel> {
        val params = UserTopicSearchParameters(
            selectedTopic = arguments?.getSerializable(ARG_SELECTED_TOPIC) as FollowableId?,
            applyScoresFiltering = arguments?.getBoolean(ARG_APPLY_SCORES_FILTERING) == true
        )
        parametersOf(params, navigator)
    }
    private var selectedItem: SearchFollowableItem? = null

    var selectionListener: UserTopicSearchFragmentListener? = null

    lateinit var adapter: UserTopicSearchAdapter
    lateinit var binding: FragmentUserTopicSearchBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)

        bottomSheetDialog.setOnShowListener { sheetDialog ->
            val dialog = (sheetDialog as BottomSheetDialog)
            dialog.findViewById<View>(R.id.design_bottom_sheet)?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.peekHeight = PEEK_HEIGHT
                behavior.isHideable = false
            }
        }

        return bottomSheetDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(presenter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserTopicSearchBinding.inflate(inflater).apply {
            interactor = presenter
            lifecycleOwner = viewLifecycleOwner
        }

        presenter.observe<UserTopicSearch.Event>(viewLifecycleOwner) {
            when (it) {
                UserTopicSearch.Event.CloseDialog -> {
                    selectionListener?.invoke(null)
                    dismiss()
                }
                UserTopicSearch.Event.ClearSearch -> binding.topicSearch.setText("")
                is UserTopicSearch.Event.ItemSelected -> {
                    selectedItem = it.topic
                    selectionListener?.invoke(it.topic?.followableId)
                }
            }
        }

        setupAdapter(binding.recycler)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            presenter.viewState.collect {
                adapter.submitList(it.uiModels)
                binding.data = it
            }
        }
    }

    private fun setupAdapter(recyclerView: RecyclerView) {
        adapter = UserTopicSearchAdapter(viewLifecycleOwner, presenter)
        recyclerView.adapter = adapter
    }

    override fun getTheme() = R.style.Widget_Ath_BottomSheetDialogCustom
}