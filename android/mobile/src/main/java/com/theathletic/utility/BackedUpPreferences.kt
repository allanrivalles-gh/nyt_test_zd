package com.theathletic.utility

import android.content.Context
import android.content.SharedPreferences
import com.theathletic.AthleticApplication
import com.theathletic.AthleticConfig
import com.theathletic.extension.get
import com.theathletic.extension.set

class BackedUpPreferences private constructor() : IBackedUpPreferences {

    companion object {
        private const val PREF_BACKED_UP_LAST_KNOWN_USER_ID = "pref_backed_up_last_known_user_id"

        @Volatile
        private var instance: BackedUpPreferences? = null

        fun getInstance(): BackedUpPreferences {
            return instance ?: synchronized(this) {
                BackedUpPreferences().also {
                    instance = it
                }
            }
        }
    }

    private val sharedPreferences: SharedPreferences = AthleticApplication.getContext().getSharedPreferences(AthleticConfig.BACKED_UP_PREFS_NAME, Context.MODE_PRIVATE)

    override var lastKnownUserId: Long?
        get() = sharedPreferences[PREF_BACKED_UP_LAST_KNOWN_USER_ID, null as Long?]
        set(value) {
            sharedPreferences[PREF_BACKED_UP_LAST_KNOWN_USER_ID] = value
        }
}