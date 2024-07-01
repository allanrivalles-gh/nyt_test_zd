package com.theathletic.instrumentation.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import com.theathletic.instrumentation.PACKAGE
import com.theathletic.instrumentation.TIMEOUT
import com.theathletic.instrumentation.confirmLogin
import com.theathletic.instrumentation.dismissNotificationPrompt
import com.theathletic.instrumentation.fillEmailAndPassword
import com.theathletic.instrumentation.goToLogin
import com.theathletic.instrumentation.selectLoginWithEmail
import com.theathletic.instrumentation.waitForTheUserFeed
import com.theathletic.instrumentation.waitForTheWelcomeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedScreenBenchmark {
    @get:Rule val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun feedScreenTimingMetrics() = benchmarkRule.measureRepeated(
        packageName = PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
            loginWithEmail()
            device.wait(Until.findObject(By.res("Discover")), TIMEOUT).click()
        }
    ) {
        scrollUpAndDownInTheFeed()
    }
}

private fun MacrobenchmarkScope.scrollUpAndDownInTheFeed() {
    val percent = 0.8f
    val speed = 6000

    val feedList = device.wait(Until.findObject(By.res("FeedList")), TIMEOUT)
    repeat(3) { feedList.scroll(Direction.DOWN, percent, speed) }
    feedList.fling(Direction.DOWN)

    repeat(3) { feedList.scroll(Direction.UP, percent, speed) }
    feedList.fling(Direction.UP)
}

fun MacrobenchmarkScope.loginWithEmail() {
    if (iteration == null) {
        device.waitForTheWelcomeScreen()
        device.dismissNotificationPrompt()
        device.goToLogin()
        device.selectLoginWithEmail()
        device.fillEmailAndPassword()
        device.confirmLogin()
        device.waitForTheUserFeed()
    }
}