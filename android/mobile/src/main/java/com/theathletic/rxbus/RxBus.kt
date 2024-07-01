package com.theathletic.rxbus

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxBus {
    private val bus = PublishSubject.create<Any>()

    companion object {
        val instance = RxBus()
    }

    class CommentSendEvent
    class CommentDeleteEvent
    class PodcastEpisodePlayedStateChangeEvent(
        val episodeId: Long,
        val progress: Int,
        val finished: Boolean
    )
    class PodcastFollowedStatusChangeEvent(val podcastId: Long, val isFollowed: Boolean)
    class SwitchToPodcastDetailEvent(val podcastId: Long)
    class SwitchToPodcastEpisodeDetailEvent(val episodeId: Long, val podcastId: Long)
    class SleepTimerPauseEvent

    fun <T> register(eventClass: Class<T>): Observable<T> {
        return this.bus
            .filter { o -> o != null } // Filter out null objects, better safe than sorry
            .filter(({ eventClass.isInstance(it) })) // We're only interested in a specific event class
            .cast(eventClass) // Cast it for easier usage
    }

    fun post(event: Any) {
        bus.onNext(event)
    }
}