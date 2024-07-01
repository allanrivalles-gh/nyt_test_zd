package com.theathletic.fragment.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.theathletic.adapter.main.SearchAdapter
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor
import com.theathletic.databinding.FragmentSearchBinding
import com.theathletic.event.DataChangeEvent
import com.theathletic.feed.FeedType
import com.theathletic.fragment.BaseBindingFragment
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.search.data.local.SearchArticleItem
import com.theathletic.search.data.local.SearchAuthorItem
import com.theathletic.search.data.local.SearchBaseItem
import com.theathletic.search.data.local.SearchLeagueItem
import com.theathletic.search.data.local.SearchPopularItem
import com.theathletic.search.data.local.SearchTeamItem
import com.theathletic.ui.main.SearchView
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.ElevationAnimator
import com.theathletic.viewmodel.main.SearchType
import com.theathletic.viewmodel.main.SearchViewModel
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class SearchFragment : BaseBindingFragment<SearchViewModel, FragmentSearchBinding>(), SearchView {
    private var adapter: SearchAdapter? = null
    private val compositeDisposable = CompositeDisposable()
    private val analytics by inject<Analytics>()
    private var elevationAnimator: ElevationAnimator? = null
    private val navigator by inject<ScreenNavigator> { parametersOf(requireActivity()) }

    companion object {
        fun newInstance(): SearchFragment {
            val fragment = SearchFragment()

            // arguments
            val arguments = Bundle()
            fragment.arguments = arguments

            return fragment
        }
    }

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater)
    }

    override fun setupViewModel(): SearchViewModel {
        val viewModel = getViewModel<SearchViewModel>()
        lifecycle.addObserver(viewModel)
        viewModel.observeEvent(this, DataChangeEvent::class.java) { onDataChangeEvent() }
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.executePendingBindings()
        setupAdapter()

        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    viewModel.onScrolled()
                    recyclerView.removeOnScrollListener(this)
                }
            }
        })
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    override fun onCancelClick() {
        viewModel.cancelSearch()

        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEdittext.windowToken, 0)
    }

    override fun onItemClick(item: SearchBaseItem) {
        activity?.let { activity ->
            when (item) {
                is SearchArticleItem -> {
                    when {
                        item.isDiscussion -> navigateToDiscussions(ContentDescriptor(item.id, item.title), isQanda = false)
                        item.isLiveDiscussion -> navigateToDiscussions(ContentDescriptor(item.id, item.title), isQanda = true)
                        else -> ActivityUtility.startArticleActivity(context, item.id, ClickSource.SEARCH)
                    }
                }
                is SearchPopularItem -> {
                    analytics.track(
                        Event.Discover.Click(
                            element = "article",
                            object_type = "article_id",
                            object_id = item.id.toString()
                        )
                    )
                    ActivityUtility.startArticleActivity(context, item.id, ClickSource.SEARCH)
                }
                is SearchTeamItem -> {
                    navigator.startHubActivity(FeedType.Team(item.id))
                }
                is SearchLeagueItem -> navigator.startHubActivity(FeedType.League(item.id))
                is SearchAuthorItem -> ActivityUtility.startStandaloneFeedActivity(
                    context = activity,
                    feedType = FeedType.Author(item.id),
                    displayTitle = item.name
                )
            }
            viewModel.searchResultSelected(item)
        }
    }

    private fun navigateToDiscussions(contentDescriptor: ContentDescriptor, isQanda: Boolean) {
        navigator.startCommentsV2Activity(
            contentDescriptor = contentDescriptor,
            type = if (isQanda) CommentsSourceType.QANDA else CommentsSourceType.DISCUSSION,
            clickSource = ClickSource.SEARCH
        )
    }

    private fun onDataChangeEvent() {
        adapter?.notifyDataSetChanged()
    }

    private fun setupAdapter() {
        if (adapter == null) {
            adapter = SearchAdapter(this, viewModel.filteredSearchItems)
            binding.recycler.adapter = adapter

            elevationAnimator = ElevationAnimator(binding.recycler, binding.tabsContainer)
        }
    }

    override fun onArticleTabClick() {
        viewModel.changeCategory(SearchType.ARTICLE)
    }

    override fun onTeamTabClick() {
        viewModel.changeCategory(SearchType.TEAM)
    }

    override fun onLeagueTabClick() {
        viewModel.changeCategory(SearchType.LEAGUE)
    }

    override fun onAuthorTabClick() {
        viewModel.changeCategory(SearchType.AUTHOR)
    }
}