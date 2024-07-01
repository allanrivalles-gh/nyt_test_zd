package com.theathletic.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Briefly describes a piece of content the user has taken action on. Originally created to support the
 * "Commenting on:" text in the comments-input header, this can be reused and expanded on for other areas
 * of the app where we need to pass along some contextual information about what the user was looking at
 * when they opened a new view.
 */
@Parcelize
data class ContentDescriptor(
    val id: String,
    val title: String = ""
) : Parcelable {
    constructor(id: Long, title: String?) : this(id.toString(), title ?: "")
}