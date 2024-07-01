package com.theathletic.article

import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FreeArticleTrackerDataSourceTest {

    private lateinit var dataSource: FreeArticleTrackerDataSource
    private val sharedPreferences: SharedPreferences = mock()
    private val editor: SharedPreferences.Editor = mock()
    private val snapshotJson = "{\"lastMoment\":{\"year\":2023,\"month\":6},\"readIds\":[1,2]}"
    private val snapshot: FreeArticleTracker.Snapshot = FreeArticleTracker.Snapshot(
        FreeArticleTracker.Moment(2023, 6),
        hashSetOf(1L, 2L),
    )

    @Before
    fun setUp() {
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putString(any(), any())).thenReturn(editor)

        dataSource = FreeArticleTrackerDataSource(sharedPreferences, Gson())
    }

    @Test
    fun `load returns null when no snapshot is saved`() {
        whenever(sharedPreferences.getString(any(), isNull())).thenReturn(null)

        val result = dataSource.load()

        assertThat(result).isNull()
    }

    @Test
    fun `load returns snapshot when it is saved`() {
        whenever(sharedPreferences.getString(any(), isNull())).thenReturn(snapshotJson)

        val result = dataSource.load()

        assertThat(result).isEqualTo(snapshot)
    }

    @Test
    fun `save puts snapshot json to sharedPreferences`() {
        dataSource.save(snapshot)

        verify(editor).putString(FreeArticleTrackerDataSource.snapshotKeyInSharePreferences, snapshotJson)
        verify(editor).apply()
    }
}