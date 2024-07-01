package com.theathletic.comments.utility

import com.google.common.truth.Truth.assertThat
import com.theathletic.test.runTest
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test

class CommentsViewVisibilityManagerTest {
    private lateinit var manager: CommentsViewVisibilityManager

    @Before
    fun setup() {
        manager = CommentsViewVisibilityManager()
    }

    @Test
    fun `emits true when fragment resumes`() = runTest {
        manager.onResumed()

        val visible = manager.isVisible.first()
        assertThat(visible).isTrue()
    }

    @Test
    fun `emits false when fragment resumes and then pauses`() = runTest {
        manager.onResumed() // First make it visible
        manager.onPaused() // Then pause it

        val visible = manager.isVisible.first()
        assertThat(visible).isFalse()
    }

    @Test
    fun `emits true when fragment resumes and tab is selected`() = runTest {
        manager.onResumed() // First make it visible
        manager.onTabSelectionChanged(true) // Then select the tab

        val visible = manager.isVisible.first()
        assertThat(visible).isTrue()
    }

    @Test
    fun `emits false when fragment resumes and tab is deselected`() = runTest {
        manager.onResumed() // First make it visible
        manager.onTabSelectionChanged(false) // Then deselect the tab

        val visible = manager.isVisible.first()
        assertThat(visible).isFalse()
    }
}