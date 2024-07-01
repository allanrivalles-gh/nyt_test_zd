package com.theathletic.instrumentation.test

import androidx.recyclerview.widget.RecyclerView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.theathletic.instrumentation.AutomatorRunner
import com.theathletic.instrumentation.PACKAGE
import com.theathletic.instrumentation.Step
import com.theathletic.instrumentation.TIMEOUT
import com.theathletic.instrumentation.reliableScrollIntoView
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AutomatorRunner::class)
class BasicFeedScenario {

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Test
    @Step("Given that I'm on the Feed screen", order = 1)
    fun given_i_am_on_the_feed_screen() {
        assert(device.wait(Until.hasObject(By.res(PACKAGE, "feed_recycler_view")), TIMEOUT))
    }

    @Test
    @Step("When I scroll until I see Top Headlines", order = 2)
    fun scroll_until_i_see_top_headlines() {
        getFeed().reliableScrollIntoView(UiSelector().text("Top Headlines"), device)
    }

    @Test
    @Step("Then Top Headlines contains articles", order = 3)
    fun ensure_top_headlines_contains_articles() {
        var bulletPoint = device.findObject(By.res(PACKAGE, BULLET_POINT))

        if (bulletPoint == null) {
            getFeed().scrollForward()
            bulletPoint = device.findObject(By.res(PACKAGE, BULLET_POINT))
        }

        assert(bulletPoint != null)
    }

    @Test
    @Step("When I scroll until I see A1", order = 4)
    fun scroll_until_i_see_a1() {
        getFeed().reliableScrollIntoView(UiSelector().text("A1 Must-Read Stories"), device)
    }

    @Test
    @Step("Then A1 contains articles", order = 5)
    fun ensure_a1_contains_articles() {
        var spotlightImage = device.findObject(By.res(PACKAGE, SPOTLIGHT_IMAGE))
        var spotlightTitle = device.findObject(By.res(PACKAGE, SPOTLIGHT_TITLE))

        if (spotlightImage == null || spotlightTitle == null) {
            getFeed().scrollForward()
            spotlightImage = device.findObject(By.res(PACKAGE, SPOTLIGHT_IMAGE))
            spotlightTitle = device.findObject(By.res(PACKAGE, SPOTLIGHT_TITLE))
        }

        assert(spotlightImage != null || spotlightTitle != null)
    }

    @Test
    @Step("When I scroll until I see More For You", order = 6)
    fun scroll_until_i_see_more_for_you() {
        getFeed().reliableScrollIntoView(UiSelector().text("More For You"), device)
    }

    @Test
    @Step("Then More For You contains articles", order = 7)
    fun ensure_more_for_you_contains_articles() {
        var title = device.findObject(By.res(PACKAGE, TITLE))
        var image = device.findObject(By.res(PACKAGE, IMAGE))

        if (title == null || image == null) {
            getFeed().scrollForward()
            title = device.findObject(By.res(PACKAGE, TITLE))
            image = device.findObject(By.res(PACKAGE, IMAGE))
        }

        assert(title != null || image != null)
    }

    @Test
    @Step("When I scroll until I see Most Popular", order = 8)
    fun scroll_until_i_see_most_popular() {
        getFeed().reliableScrollIntoView(UiSelector().text("Most Popular"), device)
    }

    @Test
    @Step("Then Most Popular Contains Articles", order = 9)
    fun ensure_most_popular_contains_articles() {
        var mostPopularCell = device.findObject(By.res(PACKAGE, POST_POPULAR_ARTICLE))

        if (mostPopularCell == null) {
            getFeed().scrollForward()
            mostPopularCell = device.findObject(By.res(PACKAGE, POST_POPULAR_ARTICLE))
        }

        assert(mostPopularCell != null)
    }

    private fun getFeed(): UiScrollable = UiScrollable(UiSelector().className(RecyclerView::class.java))

    companion object {
        const val BULLET_POINT = "bullet_point"
        const val SPOTLIGHT_IMAGE = "spotlight_image"
        const val SPOTLIGHT_TITLE = "spotlight_title"
        const val TITLE = "title"
        const val IMAGE = "image"
        const val POST_POPULAR_ARTICLE = "most_popular_article"
    }
}