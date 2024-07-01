package com.theathletic.viewmodel.main

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.event.DataChangeEvent
import com.theathletic.extension.ObservableStringNonNull
import com.theathletic.extension.applySchedulers
import com.theathletic.extension.extAddOnPropertyChangedCallback
import com.theathletic.extension.extLogError
import com.theathletic.extension.isNetworkUnavailable
import com.theathletic.followables.ListFollowableUseCase
import com.theathletic.followables.data.domain.Filter
import com.theathletic.repository.safeApiRequest
import com.theathletic.search.data.SearchRepository
import com.theathletic.search.data.local.SearchArticleItem
import com.theathletic.search.data.local.SearchBaseItem
import com.theathletic.search.data.local.SearchPopularItem
import com.theathletic.search.data.local.SearchTitleItem
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.utility.NetworkManager
import com.theathletic.viewmodel.BaseViewModel
import com.theathletic.widget.SearchStatefulLayout
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SearchViewModel @AutoKoin constructor(
    val analytics: Analytics,
    private val searchRepository: SearchRepository,
    val listFollowableUseCase: ListFollowableUseCase,
) : BaseViewModel(), DefaultLifecycleObserver, KoinComponent {
    val state = ObservableInt(SearchStatefulLayout.PROGRESS)
    val searchText = ObservableStringNonNull("")
    var searchType = ObservableSearchType(SearchType.ARTICLE)
    val filteredSearchItems = ObservableArrayList<SearchBaseItem>()
    private val articlesList = ObservableArrayList<SearchArticleItem>()
    private val popularList = ObservableArrayList<SearchPopularItem>()
    private var isInitCallRunning = false
    private val searchSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private var onBoardingDisposable: Disposable? = null
    private var searchArticleJob: Job? = null
    private var searchSubjectDisposable: Disposable? = null

    private var isInitialAnalyticsEvent = true
    private val followableFilter = Filter.Simple(type = Filter.Type.TEAM)

    private val initializationErrorHandler = CoroutineExceptionHandler { _, error ->
        isInitCallRunning = false
        logExceptionCheckForError(error)
    }

    init {
        loadData()
        setupFilter()
        bindSearchFilled()
    }

    private fun setupFilter() {
        listFollowableUseCase(followableFilter)
            .onEach { followablesList ->
                val searchType = searchType.get()
                if (searchType == SearchType.ARTICLE) {
                    filterArticleTab()
                } else {
                    addTitleFollowables(followablesList.toSearchItem(searchType))
                }
            }
            .catch { e -> logExceptionCheckForError(e) }
            .launchIn(viewModelScope)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        analytics.track(Event.Search.View())
    }

    override fun onCleared() {
        onBoardingDisposable?.dispose()
        searchSubjectDisposable?.dispose()
        super.onCleared()
    }

    fun changeCategory(searchType: SearchType) {
        this.searchType.set(searchType)

        if (isInitialAnalyticsEvent) {
            analytics.track(
                Event.Discover.SubTabView(
                    element = getSearchTabAnalyticsType()
                )
            )
        }

        if (this.searchType.get() == SearchType.ARTICLE && searchText.get().isNotBlank()) {
            searchSubject.onNext(searchText.get())
        } else {
            changeFilter()
        }
    }

    fun cancelSearch() {
        analytics.track(Event.Search.CancelClick())
        searchText.set("")
    }

    fun searchResultSelected(item: SearchBaseItem) {
        analytics.track(
            Event.Search.Click(
                object_type = item.v2AnalyticsObjectType(),
                object_id = item.id.toString()
            )
        )
    }

    private fun getSearchTabAnalyticsType() = when (this.searchType.get()) {
        SearchType.ARTICLE -> "article"
        SearchType.TEAM -> "team"
        SearchType.LEAGUE -> "league"
        SearchType.AUTHOR -> "author"
    }

    private fun loadData() {
        isInitCallRunning = true
        state.set(SearchStatefulLayout.PROGRESS)

        viewModelScope.launch(initializationErrorHandler) {
            val articlesRequest = async(Dispatchers.IO) {
                searchRepository.getMostPopularArticles()
            }

            popularList.clear()
            popularList.addAll(articlesRequest.await().map { SearchPopularItem(it) })
            isInitCallRunning = false

            changeFilter()
        }
    }

    private fun loadArticleData(searchText: String) {
        searchArticleJob?.cancel()
        state.set(SearchStatefulLayout.PROGRESS)
        searchArticleJob = viewModelScope.launch(initializationErrorHandler) {
            safeApiRequest { searchRepository.getSearchArticles(searchText) }
                .onSuccess { response ->
                    response.body()?.let {
                        articlesList.clear()
                        articlesList.addAll(it.entries)
                        articlesList.forEachIndexed { index, item -> item.adapterId = 40000L + index }
                        changeFilter()
                    }
                }
                .onError { error ->
                    if (!this.isActive) return@onError // leave early if job is canceled
                    logExceptionCheckForError(error)
                }
        }
    }

    private fun bindSearchFilled() {
        if (searchSubjectDisposable?.isDisposed == false)
            return

        searchSubjectDisposable = searchSubject.debounce(500, TimeUnit.MILLISECONDS)
            .applySchedulers()
            .subscribe(
                {
                    loadArticleData(it)
                },
                { error ->
                    logExceptionCheckForError(error)
                }
            )

        searchText.extAddOnPropertyChangedCallback { _, _, _ ->
            if (searchType.get() == SearchType.ARTICLE && searchText.get().isNotBlank()) {
                searchSubject.onNext(searchText.get())
            } else {
                changeFilter()
            }
        }
    }

    private fun changeFilter() {
        val searchText = searchText.get()
        try {
            filteredSearchItems.clear()
            val fiterType = when (searchType.get()) {
                SearchType.ARTICLE -> Filter.Type.ALL
                SearchType.TEAM -> Filter.Type.TEAM
                SearchType.LEAGUE -> Filter.Type.LEAGUE
                SearchType.AUTHOR -> Filter.Type.AUTHOR
            }
            followableFilter.update { Filter.Simple(query = searchText, type = fiterType) }
        } catch (exception: ConcurrentModificationException) {
            exception.extLogError()
        }
    }

    private fun logExceptionCheckForError(error: Throwable) {
        error.extLogError()
        if (error.isNetworkUnavailable()) {
            state.set(SearchStatefulLayout.OFFLINE)
        } else {
            state.set(SearchStatefulLayout.NOT_FOUND)
        }
    }

    private fun updateState() {
        val searchText = searchText.get()
        val currentCategory = searchType.get()
        val newState = when {
            isInitCallRunning -> SearchStatefulLayout.PROGRESS
            currentCategory == SearchType.TEAM && searchText.isBlank() -> SearchStatefulLayout.TEAM_EMPTY
            currentCategory == SearchType.LEAGUE && searchText.isBlank() -> SearchStatefulLayout.LEAGUE_EMPTY
            currentCategory == SearchType.AUTHOR && searchText.isBlank() -> SearchStatefulLayout.AUTHOR_EMPTY
            filteredSearchItems.isNotEmpty() -> SearchStatefulLayout.CONTENT
            NetworkManager.getInstance().isOffline() -> SearchStatefulLayout.OFFLINE
            else -> SearchStatefulLayout.NOT_FOUND
        }
        state.set(newState)
        sendEvent(DataChangeEvent())
    }

    private fun filterArticleTab() {
        if (searchText.get().isBlank()) {
            if (popularList.isNotEmpty())
                filteredSearchItems.add(SearchTitleItem(ParameterizedString(R.string.search_header_most_popular)))
            filteredSearchItems.addAll(popularList)
        } else {
            if (articlesList.isNotEmpty()) {
                filteredSearchItems.add(
                    SearchTitleItem(
                        ParameterizedString(R.string.search_header_results, articlesList.size)
                    )
                )
            }
            filteredSearchItems.addAll(articlesList)
        }

        updateState()
    }

    private fun addTitleFollowables(items: List<SearchBaseItem>) {
        if (items.isNotEmpty()) {
            filteredSearchItems.add(
                SearchTitleItem(
                    ParameterizedString(R.string.search_header_results, items.size)
                )
            )
        }
        filteredSearchItems.addAll(items)
        updateState()
    }

    fun onScrolled() {
        if (searchType.get() == SearchType.ARTICLE) {
            analytics.track(Event.Discover.SubTabView(element = "articles"))
        }
    }
}

class ObservableSearchType(val default: SearchType) : ObservableField<SearchType>() {
    override fun get(): SearchType {
        return super.get() ?: default
    }
}

enum class SearchType(val index: Int) {
    ARTICLE(0),
    TEAM(1),
    LEAGUE(2),
    AUTHOR(3),
}