package com.theathletic.ui.main

import com.theathletic.search.data.local.SearchBaseItem
import com.theathletic.ui.BaseView
import org.alfonz.adapter.AdapterView

interface SearchView : BaseView, AdapterView {
    fun onCancelClick()
    fun onItemClick(item: SearchBaseItem)
    fun onArticleTabClick()
    fun onAuthorTabClick()
    fun onTeamTabClick()
    fun onLeagueTabClick()
}