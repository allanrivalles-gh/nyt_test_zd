package com.theathletic.preferences.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.settings.EmailSettingsItem
import com.theathletic.event.SnackbarEvent
import com.theathletic.extension.extGetString
import com.theathletic.extension.extLogError
import com.theathletic.network.ResponseStatus
import com.theathletic.settings.data.EmailNewsletterRepository
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.AthleticListViewModel
import com.theathletic.ui.list.ListLoadingItem
import com.theathletic.ui.list.SimpleListViewState
import com.theathletic.user.IUserManager
import kotlinx.coroutines.launch

class NewsletterPreferencesViewModel @AutoKoin constructor(
    private val emailRepository: EmailNewsletterRepository,
    private val analytics: Analytics,
    private val userManager: IUserManager
) : AthleticListViewModel<NewsletterPreferenceState, SimpleListViewState>(),
    NewsletterPreferenceContract.Interactor {

    override val initialState by lazy {
        NewsletterPreferenceState()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initialize() {
        loadPreferences()
        analytics.track(
            Event.Preferences.View(
                element = "email_preference"
            )
        )
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            val result = emailRepository.getUserEmailSettings(userManager.getCurrentUserId())

            when (result) {
                is ResponseStatus.Success -> {
                    updateState {
                        copy(
                            loadingState = LoadingState.FINISHED,
                            emailItems = result.body.emailSettings.sortedBy { it.index }
                        )
                    }
                }

                is ResponseStatus.Error -> result.throwable.extLogError()
            }
        }
    }

    override fun onNewsletterToggled(item: NewsletterSwitchItem, isOn: Boolean) {
        viewModelScope.launch {
            val newsletter = state.emailItems.firstOrNull { it.title == item.title } ?: return@launch

            updateNewsletterSwitch(newsletter.title, isOn)

            val result = when {
                isOn -> emailRepository.emailNewsletterSubscribe(newsletter.emailType)
                else -> emailRepository.emailNewsletterUnsubscribe(newsletter.emailType)
            }

            analytics.track(
                Event.Preferences.Click(
                    element = if (isOn) "email_preference_on" else "email_preference_off",
                    object_type = newsletter.emailType
                )
            )

            if (result is ResponseStatus.Error) {
                sendEvent(SnackbarEvent(R.string.global_error.extGetString()))
                result.throwable.extLogError()
                updateNewsletterSwitch(newsletter.title, !isOn)
            }
        }
    }

    private fun updateNewsletterSwitch(title: String, isOn: Boolean) {
        updateState {
            copy(
                emailItems = state.emailItems.map { newsletter ->
                    if (newsletter.title != title) newsletter else newsletter.copy(value = isOn)
                }
            )
        }
    }

    override fun transform(state: NewsletterPreferenceState): SimpleListViewState {
        return SimpleListViewState(
            showSpinner = false,
            uiModels = if (this.state.loadingState == LoadingState.INITIAL_LOADING) {
                listOf(ListLoadingItem)
            } else {
                getPopulatedList()
            },
            backgroundColorRes = R.color.ath_grey_70
        )
    }

    private fun getPopulatedList(): List<UiModel> {
        val items = state.emailItems
        return items.map {
            val isLastItem = items.last() == it
            NewsletterSwitchItem.fromEmailSettingsItem(it, isLastItem)
        }
    }
}

data class NewsletterPreferenceState(
    val loadingState: LoadingState = LoadingState.INITIAL_LOADING,
    val emailItems: List<EmailSettingsItem> = emptyList()
) : DataState