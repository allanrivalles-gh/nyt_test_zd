package com.theathletic.scores.standings.ui

import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayout
import com.theathletic.R
import com.theathletic.databinding.FragmentScoresStandingsMvpBinding
import com.theathletic.databinding.ListItemStandingsGroupBinding
import com.theathletic.entity.main.League
import com.theathletic.fragment.AthleticMvpBindingFragment
import com.theathletic.presenter.Interactor
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.BindingDiffAdapter
import com.theathletic.ui.list.DataBindingViewHolder
import com.theathletic.ui.list.bindData
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class ScoresStandingsFragment : AthleticMvpBindingFragment<
    ScoresStandingsViewModel,
    FragmentScoresStandingsMvpBinding,
    ScoresStandingsContract.ViewState
    >() {

    private var loadedTabs = false

    companion object {
        private const val EXTRA_LEAGUE = "extra_league"
        private const val EXTRA_TEAM_ID = "extra_team_id"

        fun newInstance(
            league: League,
            teamId: String?
        ): ScoresStandingsFragment {
            return ScoresStandingsFragment().apply {
                arguments = bundleOf(
                    EXTRA_LEAGUE to league,
                    EXTRA_TEAM_ID to teamId,
                )
            }
        }
    }

    private val statsAdapter: StandingsStatisticsListAdapter by lazy {
        StandingsStatisticsListAdapter(viewLifecycleOwner, presenter)
    }

    private val legendAdapter: RelegationLegendListAdapter by lazy {
        RelegationLegendListAdapter(viewLifecycleOwner, presenter)
    }

    private val tabListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            presenter.onGroupClick(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            // no implementation
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            // no implementation
        }
    }

    private val parameters by lazy {
        (arguments?.getSerializable(EXTRA_LEAGUE) as League?)?.let { league ->
            ScoresStandingsViewModel.Params(
                league = league,
                teamId = arguments?.getString(EXTRA_TEAM_ID),
            )
        } ?: navigator.finishActivity()
    }

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentScoresStandingsMvpBinding {
        val binding = FragmentScoresStandingsMvpBinding.inflate(inflater)
        binding.recyclerStandings.adapter = statsAdapter
        binding.recyclerScoresStandingsRelegationLegend.adapter = legendAdapter
        binding.tabLayoutStandings.addOnTabSelectedListener(tabListener)
        return binding
    }

    override fun setupViewModel() = getViewModel<ScoresStandingsViewModel> {
        parametersOf(parameters, navigator)
    }

    override fun renderState(viewState: ScoresStandingsContract.ViewState) {
        if (!loadedTabs && viewState.groupsTitleList.isNotEmpty()) {
            // use scrollable tab mode if we have more than 3 tabs
            if (viewState.groupsTitleList.size > 3) {
                binding.tabLayoutStandings.tabMode = TabLayout.MODE_SCROLLABLE
            }
            viewState.groupsTitleList.forEach { title ->
                val tab = binding.tabLayoutStandings.newTab()
                tab.text = title.title.parameters.first() as String
                binding.tabLayoutStandings.addTab(tab)
            }
            loadedTabs = true
        }

        if (loadedTabs && viewState.autoNavigationIndex > -1) {
            with(binding.tabLayoutStandings) {
                post {
                    selectTab(getTabAt(viewState.autoNavigationIndex))
                }
            }
        }

        statsAdapter.submitList(viewState.standingsGroupList)
        legendAdapter.submitList(viewState.relegationLegendItems)
    }

    private class RelegationLegendListAdapter(
        lifecycleOwner: LifecycleOwner,
        interactor: Interactor
    ) : BindingDiffAdapter(lifecycleOwner, interactor) {
        override fun getLayoutForModel(model: UiModel): Int {
            return when (model) {
                is ScoresStandingsRelegationLegendUiModel -> R.layout.list_item_standings_legend_item
                else -> throw IllegalArgumentException("$model not supported")
            }
        }
    }

    private class StandingsStatisticsListAdapter(
        lifecycleOwner: LifecycleOwner,
        val interactor: Interactor
    ) : BindingDiffAdapter(lifecycleOwner, interactor) {

        override fun getLayoutForModel(model: UiModel): Int {
            return when (model) {
                is ScoresStandingsGroupUiModel -> R.layout.list_item_standings_group
                else -> throw IllegalArgumentException("$model not supported")
            }
        }

        override fun onPostBind(model: UiModel, holder: DataBindingViewHolder<ViewDataBinding>) {
            when (model) {
                is ScoresStandingsGroupUiModel -> setupStandingGroupListAdapter(
                    holder.binding as ListItemStandingsGroupBinding,
                    model.rankAndTeamsList,
                    model.standingsList
                )
                else -> throw IllegalArgumentException("$model not supported")
            }
        }

        fun setupStandingGroupListAdapter(
            binding: ListItemStandingsGroupBinding,
            rankAndTeamList: List<UiModel>,
            statisticsList: List<UiModel>
        ) {
            binding.recyclerStandingsRankAndTeam.bindData(rankAndTeamList) {
                StandingRankAndTeamListAdapter(
                    lifecycleOwner,
                    interactor
                )
            }
            binding.recyclerStandingsStats.bindData(statisticsList) {
                StandingStatisticsListAdapter(
                    lifecycleOwner,
                    interactor
                )
            }
        }
    }

    private class StandingsGroupTitleAdapter(
        lifecycleOwner: LifecycleOwner,
        interactor: Interactor
    ) : BindingDiffAdapter(lifecycleOwner, interactor) {
        override fun getLayoutForModel(model: UiModel): Int {
            return when (model) {
                is ScoresStandingsGroupTitleUiModel -> R.layout.list_item_standings_page_switcher_title
                else -> throw IllegalArgumentException("$model not supported")
            }
        }
    }

    private class StandingRankAndTeamListAdapter(
        lifecycleOwner: LifecycleOwner,
        interactor: Interactor
    ) : BindingDiffAdapter(lifecycleOwner, interactor) {
        override fun getLayoutForModel(model: UiModel): Int {
            return when (model) {
                is ScoresStandingsRankAndTeamHeaderUiModel -> R.layout.list_item_standings_rank_team_header
                is ScoresStandingsRankAndTeamUiModel -> R.layout.list_item_standings_rank_team_details
                else -> throw IllegalArgumentException("$model not supported")
            }
        }
    }

    private class StandingStatisticsListAdapter(
        lifecycleOwner: LifecycleOwner,
        interactor: Interactor
    ) : BindingDiffAdapter(lifecycleOwner, interactor) {
        override fun getLayoutForModel(model: UiModel): Int {
            return when (model) {
                is ScoreStandingsStatsHeaderUiModel -> R.layout.list_item_standings_stats_header
                is ScoreStandingsStatsRowUiModel -> R.layout.list_item_standings_stats_row
                else -> throw IllegalArgumentException("$model not supported")
            }
        }
    }
}