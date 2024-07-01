package com.theathletic.ui.list

import com.theathletic.ui.ListSection
import com.theathletic.ui.UiModel

fun list(block: ListBuilder.() -> Unit): List<UiModel> = ListBuilder().let { builder ->
    block.invoke(builder)
    builder.models
}

/**
 * A utility class for building lists of [UiModel]. This allows us to build a declarative,
 * React-like API for complex lists in a compact way. Do not use this class directly, rather use
 * it through the [list] function.
 */
class ListBuilder {
    val models = mutableListOf<UiModel>(ListRoot)
    var seed = 0

    fun add(model: UiModel) {
        models.add(model)
    }

    fun addAll(list: List<UiModel>) {
        models.addAll(list)
    }

    fun section(
        title: ListSection?,
        builder: (String) -> List<UiModel>
    ) {
        val items = builder.invoke(seed++.toString())

        if (items.isEmpty()) return

        title?.let { models.add(ListSectionTitleItem(it.titleResId)) }
        models.addAll(items)
    }

    /**
     * Provides the current size of the list. This can be used as a seed for
     * [UiModel.stableId] to keep items unique.
     */
    fun single(builder: (String) -> UiModel?) {
        builder(seed++.toString())?.let { models.add(it) }
    }
}