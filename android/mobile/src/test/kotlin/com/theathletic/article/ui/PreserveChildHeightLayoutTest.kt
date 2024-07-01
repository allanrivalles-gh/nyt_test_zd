package com.theathletic.article.ui

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PreserveChildHeightLayoutTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `has height of 0 when it has no child view`() {
        val view = PreserveChildHeightLayout(context, null)

        view.measure(500, 500)

        assertThat(view.measuredHeight).isEqualTo(0)
    }

    @Test
    fun `matches the height of the child view when it has a child view`() {
        val view = PreserveChildHeightLayout(context, null)

        val childView = View(context)
        childView.minimumHeight = 100

        view.addView(childView)
        view.measure(500, 500)

        assertThat(view.measuredHeight).isEqualTo(100)
    }

    @Test
    fun `preserves the height of the child view after the child view has been removed`() {
        val view = PreserveChildHeightLayout(context, null)

        // we trigger the measurement with a child view
        val childView = View(context)
        childView.minimumHeight = 100

        view.addView(childView)
        view.measure(500, 500)

        // then after removing the child view, the child height should still be kept by the view
        view.removeView(childView)
        view.measure(500, 500)

        assertThat(view.measuredHeight).isEqualTo(100)
    }
}