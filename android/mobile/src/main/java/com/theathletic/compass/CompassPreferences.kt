package com.theathletic.compass

import android.content.Context
import androidx.core.content.edit

interface ICompassPreferences {
    var lastConfig: String
    var exposureSet: Set<String>
    var lastUpdatedInMillis: Long
}

class CompassPreferences(
    appContext: Context
) : ICompassPreferences {
    companion object {
        const val PREF_FILE = "compass_prefs"
        const val PREF_KEY_CONFIG = "key_config"
        const val KEY_EXPOSED = "key_experiments_user_has_been_exposed_to"
        const val PREF_LAST_UPDATED = "compass_last_updated"
    }

    private val sharedPreferences = appContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    override var lastConfig: String
        get() {
            return sharedPreferences.getString(PREF_KEY_CONFIG, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit {
                putString(PREF_KEY_CONFIG, value)
            }
        }

    /**
     * Set of experiment names the user has been exposed to
     */
    override var exposureSet: Set<String>
        get() {
            return sharedPreferences.getStringSet(KEY_EXPOSED, emptySet<String>())!!
        }
        set(value) {
            sharedPreferences.edit {
                putStringSet(KEY_EXPOSED, value)
            }
        }

    override var lastUpdatedInMillis: Long
        get() {
            return sharedPreferences.getLong(PREF_LAST_UPDATED, 0)
        }
        set(value) {
            sharedPreferences.edit {
                putLong(PREF_LAST_UPDATED, value)
            }
        }
}