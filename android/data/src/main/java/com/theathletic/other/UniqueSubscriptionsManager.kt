package com.theathletic.other

class UniqueSubscriptionsManager<ID>(private val subscribe: (ids: Set<ID>) -> () -> Unit) {
    private val subscribedIds = mutableSetOf<ID>()
    private var cancelSubscription: (() -> Unit)? = null

    fun add(ids: Set<ID>): Boolean {
        // make sure at least one new element was added
        if (subscribedIds.addAll(ids)) {
            onSubscribedChanged()
            return true
        }
        return false
    }

    fun remove(ids: Set<ID>): Boolean {
        // make sure at least one element was removed
        if (subscribedIds.removeAll(ids)) {
            onSubscribedChanged()
            return true
        }
        return false
    }

    fun set(ids: Set<ID>): Boolean {
        if (subscribedIds != ids) {
            subscribedIds.clear()
            subscribedIds.addAll(ids)
            onSubscribedChanged()
            return true
        }
        return false
    }

    fun pause() {
        cancelSubscription?.invoke()
        cancelSubscription = null
    }

    fun resume() {
        // we don't want to resume if already running
        if (cancelSubscription != null) return
        // we don't want to subscribe if there is nothing to subscribe to
        if (subscribedIds.isEmpty()) return
        cancelSubscription = subscribe(subscribedIds)
    }

    private fun onSubscribedChanged() {
        pause()
        resume()
    }
}