package com.theathletic.adapter.main

import androidx.databinding.ObservableArrayList
import com.theathletic.R
import com.theathletic.search.data.local.SearchArticleItem
import com.theathletic.search.data.local.SearchAuthorItem
import com.theathletic.search.data.local.SearchBaseItem
import com.theathletic.search.data.local.SearchLeagueItem
import com.theathletic.search.data.local.SearchPopularItem
import com.theathletic.search.data.local.SearchTeamItem
import com.theathletic.search.data.local.SearchTitleItem
import com.theathletic.ui.main.SearchView
import org.alfonz.adapter.BaseDataBoundRecyclerViewHolder
import org.alfonz.adapter.MultiDataBoundRecyclerAdapter

class SearchAdapter(val view: SearchView, searchItemsList: ObservableArrayList<SearchBaseItem>) : MultiDataBoundRecyclerAdapter(view, searchItemsList) {
    override fun getItemLayoutId(position: Int): Int {
        return when (val item = getItem(position)) {
            is SearchTitleItem -> R.layout.fragment_search_item_title
            is SearchPopularItem -> if (position == 1) R.layout.fragment_search_item_popular_header else R.layout.fragment_search_item_popular
            is SearchArticleItem -> if (item.isDiscussion || item.isLiveDiscussion) R.layout.fragment_search_item_topic else R.layout.fragment_search_item_article
            is SearchAuthorItem -> R.layout.fragment_search_item_author
            is SearchTeamItem -> R.layout.fragment_search_item_team
            is SearchLeagueItem -> R.layout.fragment_search_item_league
            else -> R.layout.fragment_main_item_not_implemented
        }
    }

    override fun bindItem(
        holder: BaseDataBoundRecyclerViewHolder<*>,
        position: Int,
        payloads: MutableList<Any?>?
    ) {
        super.bindItem(holder, position, payloads)
        val item = getItem(position)
        if (item is SearchBaseItem && item !is SearchTitleItem) {
            holder.binding.root.setOnClickListener { view.onItemClick(item) }
        }
    }
}