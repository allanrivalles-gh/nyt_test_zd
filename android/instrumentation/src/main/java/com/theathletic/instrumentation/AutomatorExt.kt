package com.theathletic.instrumentation

import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import org.junit.Assert

/**
 * UiAutomator UiScrollable.scrollIntoView has issues where it fails randomly and stops scrolling
 * This version will attempt until some number of consecutive failures resulting in more reliable tests
 */
fun UiScrollable.reliableScrollIntoView(selector: UiSelector, device: UiDevice, scrollToTop: Boolean = false, consecutiveFailureThreshold: Int = 3) {
    if (device.exists(selector)) return

    if (scrollToTop) scrollToBeginning(100)

    var consecutiveFailingScrolls = 0
    while (consecutiveFailingScrolls < consecutiveFailureThreshold) {
        if (device.exists(selector)) return

        if (!scrollForward()) {
            consecutiveFailingScrolls++
        } else {
            consecutiveFailingScrolls = 0
        }
    }
    Assert.fail("Element matching $selector not found in scroll view")
}

fun UiDevice.exists(selector: UiSelector) = this.findObject(selector).exists()