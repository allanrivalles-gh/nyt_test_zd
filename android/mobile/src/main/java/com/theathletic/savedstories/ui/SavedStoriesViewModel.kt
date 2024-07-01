package com.theathletic.savedstories.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.ArticleHasPaywallUseCase
import com.theathletic.article.data.ArticleRepository
import com.theathletic.datetime.GmtStringToDatetimeTransformer
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.filter
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.ui.list.AthleticListViewModel
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.launch

class SavedStoriesViewModel @AutoKoin constructor(
    @Assisted val navigator: ScreenNavigator,
    transformer: SavedStoriesTransformer,
    private val articleRepository: ArticleRepository,
    private val articleHasPaywall: ArticleHasPaywallUseCase,
    private val gmtStringToDatetime: GmtStringToDatetimeTransformer,
    private val analytics: Analytics
) : AthleticListViewModel<
    SavedStoriesMvpState,
    SavedStoriesContract.SavedStoriesViewState>(),
    SavedStoriesContract.Presenter,
    Transformer<
        SavedStoriesMvpState,
        SavedStoriesContract.SavedStoriesViewState> by transformer {

    override val initialState by lazy {
        SavedStoriesMvpState()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initialize() {
        setupFlows()
        loadData()
        analytics.track(Event.SavedStories.View())
    }

    fun setupFlows() {
        articleRepository.getSavedStoriesFlow()
            .filter<ArticleEntity>()
            .collectIn(viewModelScope) { stories ->
                val sortedStories = stories.sortedByDescending {
                    gmtStringToDatetime(it.articlePublishDate)
                }
                updateState {
                    copy(savedStories = sortedStories)
                }
            }
    }

    private fun loadData() = viewModelScope.launch {
        articleRepository.fetchSavedStories().join()
        updateState { copy(loadingState = LoadingState.FINISHED) }
    }

    override fun onRefresh() {
        updateState { copy(loadingState = LoadingState.RELOADING) }
        loadData()
    }

    override fun onArticleClicked(id: Long) {
        viewModelScope.launch {
            analytics.track(
                Event.SavedStories.Click(
                    element = "article",
                    object_type = "article_id",
                    object_id = id.toString()
                )
            )
            if (articleHasPaywall(id)) {
                navigator.startArticlePaywallActivity(id, ClickSource.FEED)
            } else {
                navigator.startArticleActivity(id, ClickSource.FEED)
            }
        }
    }

    override fun onArticleLongClicked(id: Long): Boolean {
        val isBookmarked = articleRepository.isArticleBookmarked(id)
        sendEvent(SavedStoriesContract.Event.ShowArticleLongClickSheet(id, isBookmarked))
        return true
    }

    fun changeArticleBookmarkStatus(articleId: Long, isBookmarked: Boolean) = viewModelScope.launch {
        updateState { copy(loadingState = LoadingState.RELOADING) }
        articleRepository.markArticleBookmarked(articleId, isBookmarked).join()
        if (!isBookmarked) {
            analytics.track(
                Event.SavedStories.Click(
                    element = "remove_article",
                    object_type = "article_id",
                    object_id = articleId.toString()
                )
            )
        }
        updateState { copy(loadingState = LoadingState.FINISHED) }
    }

    fun clearAllSavedStories() {
        articleRepository.deleteAllBookmarked()
        analytics.track(
            Event.SavedStories.Click(
                element = "remove_article",
                object_type = "all"
            )
        )
    }
}

data class SavedStoriesMvpState(
    val loadingState: LoadingState = LoadingState.INITIAL_LOADING,
    val savedStories: List<ArticleEntity> = emptyList()
) : DataState