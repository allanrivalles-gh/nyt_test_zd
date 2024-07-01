package com.theathletic.article

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.theathletic.notifications.maybeFromJson

class FreeArticleTrackerDataSource constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
) {
    constructor(context: Context, gson: Gson) : this(
        context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE),
        gson,
    )

    fun load(): FreeArticleTracker.Snapshot? {
        val json = sharedPreferences.getString(snapshotKeyInSharePreferences, null) ?: return null
        return gson.maybeFromJson(json, FreeArticleTracker.Snapshot::class.java)
    }

    fun save(tracker: FreeArticleTracker.Snapshot) {
        val json = gson.toJson(tracker)
        sharedPreferences.edit().putString(snapshotKeyInSharePreferences, json).apply()
    }

    companion object {
        private const val sharedPreferencesName = "free_article_tracker_persistence"
        const val snapshotKeyInSharePreferences = "free_article_tracker"
    }
}