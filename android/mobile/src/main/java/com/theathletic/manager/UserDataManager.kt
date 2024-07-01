package com.theathletic.manager

import androidx.databinding.ObservableArrayList
import com.theathletic.extension.extLogError
import com.theathletic.repository.resource.Resource
import com.theathletic.repository.user.UserDataRepository
import com.theathletic.user.UserManager
import com.theathletic.utility.Preferences
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.Date
import java.util.concurrent.TimeUnit
import timber.log.Timber

/*
	1) Load Data from DB
	2) Refresh data from network when needed
	3) Update local DB data on every userData change!
 */
object UserDataManager {
    @JvmStatic
    val unreadSavedStoriesIds = ObservableArrayList<Long>()
    private val userDataData = UserDataRepository.getUserData()
    var userDataDisposable: Disposable? = null

    init {
        if (UserDataRepository.userData == null)
            loadUserData()
    }

    fun onDestroy() {
        userDataData.dispose()
        userDataDisposable?.dispose()
    }

    fun loadUserData(force: Boolean = false) {
        if (!UserManager.isUserLoggedIn() || userDataDisposable?.isDisposed == false)
            return

        userDataDisposable = userDataData.getDataObservable()
            .debounce(100, TimeUnit.MILLISECONDS, Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    Timber.i("[UserDataManager] Articles read: ${it.data?.articlesRead}")
                    Timber.i("[UserDataManager] Articles saved: ${it.data?.articlesSaved}")
                    Timber.i("[UserDataManager] Articles rated: ${it.data?.articlesRated}")
                    Timber.i("[UserDataManager] Comments liked: ${it.data?.commentsLiked}")
                    Timber.i("[UserDataManager] Comments flagged: ${it.data?.commentsFlagged}")
                    if (it.status.canUpdateUserData()) {
                        UserDataRepository.userData = it.data
                    }
                    if (it?.status == Resource.Status.SUCCESS && !it.isCache)
                        Preferences.userDataLastFetchDate = Date()
                    UserDataRepository.updateUnreadSavedStoriesList()
                },
                Throwable::extLogError
            )

        if (force || (Preferences.userDataLastFetchDate.time + TimeUnit.MINUTES.toMillis(5) < Date().time)) {
            userDataData.load()
        } else {
            userDataData.loadOnlyCache()
        }
    }

    private fun Resource.Status.canUpdateUserData() = this != Resource.Status.ERROR
}