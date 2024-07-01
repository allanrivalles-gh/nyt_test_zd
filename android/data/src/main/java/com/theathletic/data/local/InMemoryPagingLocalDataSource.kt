package com.theathletic.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class InMemoryPagingLocalDataSource<Key, ListItem> {

    private val paginatedLists = mutableMapOf<Key, MutableStateFlow<PaginatedList<ListItem>?>>()

    fun getPaginatedList(key: Key): StateFlow<PaginatedList<ListItem>?> = getStateFlow(key)

    private fun getStateFlow(key: Key) = paginatedLists.getOrPut(key) { MutableStateFlow(null) }

    fun update(
        feedType: Key,
        data: List<ListItem>,
        pageIndex: Int,
        hasNextPage: Boolean
    ) {
        val batch = getStateFlow(feedType).value?.items?.toMutableList() ?: mutableListOf()

        if (pageIndex in batch.indices) {
            batch.subList(pageIndex, batch.size).clear()
        }

        batch.add(data - batch.flatten())
        getStateFlow(feedType).value = PaginatedList(batch, hasNextPage)
    }

    fun updateItem(check: (ListItem) -> Boolean, feedType: Key, updateBlock: (ListItem) -> ListItem) {
        val paginatedValues = getStateFlow(feedType).value
        val paginatedList = paginatedValues?.items?.toMutableList() ?: return

        val pageWithItemIndex = paginatedList.indexOfFirst { list -> list.any { item -> check(item) } }
        if (pageWithItemIndex in paginatedList.indices) {
            val list = paginatedList[pageWithItemIndex].toMutableList()

            val itemIndex = list.indexOfFirst { check(it) }
            list[itemIndex] = updateBlock.invoke(list[itemIndex])
            paginatedList[pageWithItemIndex] = list

            getStateFlow(feedType).value = PaginatedList(paginatedList, paginatedValues.hasNextPage)
        }
    }

    fun clear(feedType: Key) {
        paginatedLists.remove(feedType)
    }
}

data class PaginatedList<T>(
    val items: List<List<T>>,
    val hasNextPage: Boolean = true
)