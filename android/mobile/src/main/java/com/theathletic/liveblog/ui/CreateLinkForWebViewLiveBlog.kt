package com.theathletic.liveblog.ui

import android.net.Uri

fun createLinkForWebViewLiveBlog(
    linkForEmbed: String,
    lightMode: Boolean,
    postId: String?,
): String {
    val builder = Uri.parse(linkForEmbed)
        .buildUpon()
        .appendQueryParameter("theme", if (lightMode) "light" else "dark")
    if (postId != null) {
        builder.appendPath(postId)
        // necessary to append trailing slash
        // without it the WebView keeps redirecting indefinitely
        builder.appendPath("")
    }
    return builder.build().toString()
}