package com.theathletic.share

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.theathletic.core.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

enum class ShareTitle {
    DEFAULT, // Share
    NEWS_HEADLINE, // Share this headline!
    ARTICLE // Share this article!
}

fun ShareTitle.asString(context: Context): String {
    return context.getString(
        when (this) {
            ShareTitle.DEFAULT -> R.string.feed_article_action_share
            ShareTitle.NEWS_HEADLINE -> R.string.news_container_share_title
            ShareTitle.ARTICLE -> R.string.article_share_title
        }
    )
}

class ShareEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<Intent> = MutableSharedFlow()
) : MutableSharedFlow<Intent> by mutableSharedFlow

class ShareEventConsumer(
    private val producer: ShareEventProducer
) : Flow<Intent> by producer

/**
 * Listens for when an application is picked from the system sharesheet.
 */
class ShareBroadcastReceiver : BroadcastReceiver(), KoinComponent {
    companion object {
        const val REQUEST_CODE_REFERRAL = 1
    }

    enum class ShareKey(val value: String) {
        ARTICLE("article_share_key"),
        REFERRALS("referrals_share_key"),
        PODCAST_EPISODE("podcast_episode_key")
    }

    private val shareEventProducer by inject<ShareEventProducer>()

    override fun onReceive(context: Context?, intent: Intent?) {
        // Send out broadcast
        GlobalScope.launch {
            intent?.let {
                shareEventProducer.emit(intent)
            }
        }
    }
}