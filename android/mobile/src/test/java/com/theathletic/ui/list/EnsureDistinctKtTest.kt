package com.theathletic.ui.list

import com.theathletic.ui.UiModel
import org.junit.Assert.assertEquals
import org.junit.Test

data class TestModel(
    val id: Long
) : UiModel {
    override val stableId = "TestModel:$id"
}

class EnsureDistinctKtTest {
    @Test
    fun `ensureDistinct does not filter out non-duplicates`() {
        val models = listOf(
            TestModel(1),
            TestModel(2),
            TestModel(3)
        )

        val actual = models.ensureDistinct()

        assertEquals(models, actual)
    }

    @Test
    fun `ensureDistinct does filter out duplicates`() {
        val models = listOf(
            TestModel(1),
            TestModel(2),
            TestModel(2),
            TestModel(3)
        )

        val actual = models.ensureDistinct()

        assertEquals(
            listOf(
                TestModel(1),
                TestModel(2),
                TestModel(3)
            ),
            actual
        )
    }

    @Test
    fun `ensureDistinct logs out duplicates`() {
        var duplicate: String? = null

        val models = listOf(
            TestModel(1),
            TestModel(2),
            TestModel(2),
            TestModel(3)
        )

        models.ensureDistinct { duplicate = it }

        assertEquals("TestModel:2", duplicate)
    }

    @Test
    fun `ensureDistinct logs out multiple duplicates`() {
        val duplicates = mutableListOf<String>()

        val models = listOf(
            TestModel(1),
            TestModel(2),
            TestModel(2),
            TestModel(3),
            TestModel(4),
            TestModel(4)
        )

        models.ensureDistinct { duplicates.add(it) }

        assertEquals(
            listOf("TestModel:2", "TestModel:4"),
            duplicates
        )
    }

    @Test
    fun `ensureDistinct works on empty list`() {
        val models = emptyList<UiModel>()

        val actual = models.ensureDistinct()

        assertEquals(
            emptyList<UiModel>(),
            actual
        )
    }
}