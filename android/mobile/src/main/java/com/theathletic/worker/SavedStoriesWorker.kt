package com.theathletic.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Named
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.article.data.ArticleRepository
import com.theathletic.repository.user.IUserDataRepository
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

private const val SAVED_STORIES_WORKER = "SavedStoriesWorker"

class SavedStoriesScheduler @AutoKoin(Scope.SINGLE) constructor(
    @Named("application-context") private val context: Context
) {
    fun schedule() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val savedStoriesWorker = OneTimeWorkRequestBuilder<SavedStoriesWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SAVED_STORIES_WORKER,
            ExistingWorkPolicy.REPLACE,
            savedStoriesWorker
        )
    }
}

class SavedStoriesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
    private val articleRepository by inject<ArticleRepository>()
    private val userDataRepository by inject<IUserDataRepository>()

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        Timber.v("Attempting to fetch saved articles")

        val articlesSavedIds = userDataRepository.savedArticleIds()
        val databaseIds = articlesSavedIds.mapNotNull { id ->
            articleRepository.getArticle(id)
        }.filterNot { it.articleBody.isNullOrEmpty() }.map { entity -> entity.articleId }
        val idsToFetch = articlesSavedIds.filterNot { databaseIds.contains(it) }

        try {
            idsToFetch.forEach { id -> articleRepository.fetchArticle(id) }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Saved Stories Worker failed to fetch articles")
            Result.failure()
        }
    }
}