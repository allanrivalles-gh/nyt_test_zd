package com.theathletic.release.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PullRequestParams(
    val title: String,
    val body: String,
    val head: String,
    val base: String
)

@JsonClass(generateAdapter = true)
data class ReleaseParams(
    val name: String,
    @Json(name = "tag_name")
    val tagName: String,
    @Json(name = "target_commitish")
    val targetCommitish: String,
    val prerelease: Boolean,
    @Json(name = "generate_release_notes")
    val generateReleaseNotes: Boolean
)