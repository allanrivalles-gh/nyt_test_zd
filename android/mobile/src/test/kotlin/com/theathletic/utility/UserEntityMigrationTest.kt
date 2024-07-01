package com.theathletic.utility

import android.content.SharedPreferences
import androidx.core.content.edit
import com.theathletic.entity.user.UserEntity
import com.theathletic.extension.get
import com.theathletic.extension.set
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

private const val PREF_USER_ID = "pref_user_id"

internal class UserEntityMigrationTest {
    private val sharedPreferences: SharedPreferences = mock()
    private val edit: SharedPreferences.Editor = mock()

    @Before
    fun setup() {
        whenever(sharedPreferences.edit()).thenReturn(edit)
    }

    @Test
    fun `run migration then verify user is set and user_id field removed`() {
        var userEntity: UserEntity? = null
        sharedPreferences[PREF_USER_ID] = 123456
        UserEntityMigration.migrate(sharedPreferences) { user ->
            userEntity = user
        }

        assertNotNull(userEntity)
        assertFalse(sharedPreferences.contains(PREF_USER_ID))
    }

    @Test
    fun `verify migration doesn't run when user_id field does not exist`() {
        whenever(sharedPreferences[PREF_USER_ID, -1L]).thenReturn(-1L)
        var userEntity: UserEntity? = null
        sharedPreferences.edit { remove(PREF_USER_ID) }
        UserEntityMigration.migrate(sharedPreferences) { user ->
            userEntity = user
        }
        assertNull(userEntity)
    }
}