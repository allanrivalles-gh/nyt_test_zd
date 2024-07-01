package com.theathletic.boxscore.data.local

import com.google.common.truth.Truth.assertThat
import com.theathletic.boxscore.data.BoxScoreFeedFixtures
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class BoxScoreLocalDataSourceTest {

    private lateinit var dataSource: BoxScoreLocalDataSource

    private val KEY = "001"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dataSource = BoxScoreLocalDataSource()
    }

    @Test
    fun `stores the data in memeory when key and value provided`() {
        dataSource.update(KEY, BoxScoreFeedFixtures.boxScore)

        val data = dataSource.getItem(KEY)
        assertThat(data?.id).isEqualTo("001")
    }

    @Test
    fun `updates the stored data when updated using the key`() {
        dataSource.update(KEY, BoxScoreFeedFixtures.boxScore)
        var boxScoreData = dataSource.getItem(KEY)
        assertThat(boxScoreData?.id).isEqualTo("001")

        dataSource.update(KEY, BoxScoreFeedFixtures.boxScore.copy(id = "0001"))
        boxScoreData = dataSource.getItem(KEY)
        assertThat(boxScoreData?.id).isEqualTo("0001")
    }

    @Test
    fun `no data is returned when using a key that is not stored`() {
        var boxScoreData = dataSource.getItem(KEY)
        assertThat(boxScoreData).isNull()
    }
}