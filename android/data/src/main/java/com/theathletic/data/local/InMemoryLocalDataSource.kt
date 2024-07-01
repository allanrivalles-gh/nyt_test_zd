package com.theathletic.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class InMemoryLocalDataSource<Key, Item> {

    private val itemsMap = mutableMapOf<Key, MutableStateFlow<Item?>>()

    fun observeItem(key: Key): Flow<Item?> = getStateFlow(key)

    fun getItem(key: Key): Item? = getStateFlow(key).value

    protected fun getStateFlow(key: Key) = itemsMap.getOrPut(key) { MutableStateFlow(null) }

    open fun update(
        key: Key,
        data: Item
    ) {
        getStateFlow(key).value = data
    }

    open fun unset(key: Key) {
        getStateFlow(key).value = null
    }

    fun unset(isKey: (Key) -> Boolean) {
        itemsMap.keys.filter(isKey).forEach { unset(it) }
    }

    fun clearCache() {
        itemsMap.clear()
    }
}

abstract class InMemorySingleLocalDataSource<Item> {

    private val itemFlow = MutableStateFlow<Item?>(null)
    val item: Flow<Item?> get() = itemFlow

    fun update(data: Item) {
        itemFlow.value = data
    }
}

/**
 * Same as [InMemoryLocalDataSource] but this does not returns [Flow] that can be subscribed to for
 * updates, it just returns the static item.
 */
abstract class InMemoryStaticLocalDataSource<Key, Item> {
    private val itemsMap = mutableMapOf<Key, Item?>()

    fun get(key: Key): Item? = itemsMap[key]

    fun put(key: Key, item: Item?) {
        itemsMap[key] = item
    }

    fun containsKey(key: Key) = itemsMap.containsKey(key)
}