package com.theathletic.ui.widgets.dialog

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.theathletic.ui.R

class MenuSheetBuilder {
    var entries = mutableListOf<AthleticMenuSheet.Entry>()
    private var listeners: MutableMap<AthleticMenuSheet.Entry, (() -> Unit)> = mutableMapOf()
    private var onCancelListener = { }

    fun addEntry(
        @DrawableRes iconRes: Int,
        @StringRes textRes: Int,
        onSelected: () -> Unit
    ) {
        val entry = AthleticMenuSheet.Entry(iconRes = iconRes, textRes = textRes)
        entries.add(entry)
        listeners[entry] = onSelected
    }

    fun addBookmark(
        @DrawableRes iconRes: Int = R.drawable.ic_bookmark_selected,
        @StringRes textRes: Int = R.string.fragment_feed_save,
        onSelected: () -> Unit
    ) {
        addEntry(iconRes, textRes, onSelected)
    }

    fun addUnbookmark(
        @DrawableRes iconRes: Int = R.drawable.ic_bookmark,
        @StringRes textRes: Int = R.string.fragment_feed_unsave,
        onSelected: () -> Unit
    ) {
        addEntry(iconRes, textRes, onSelected)
    }

    fun addShare(
        @DrawableRes iconRes: Int = R.drawable.ic_share_white,
        @StringRes textRes: Int = R.string.article_share_button,
        onSelected: () -> Unit
    ) {
        addEntry(iconRes, textRes, onSelected)
    }

    fun addOnCancelListener(listener: () -> Unit) {
        onCancelListener = listener
    }

    fun build(): AthleticMenuSheet {
        return AthleticMenuSheet.newInstance(entries).apply {
            listeners = this@MenuSheetBuilder.listeners
            onCancelListener = this@MenuSheetBuilder.onCancelListener
        }
    }
}

fun menuSheet(
    block: MenuSheetBuilder.() -> Unit
): AthleticMenuSheet {
    return MenuSheetBuilder().run {
        block()
        build()
    }
}