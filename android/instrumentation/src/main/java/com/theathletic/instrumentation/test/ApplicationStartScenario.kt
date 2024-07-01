package com.theathletic.instrumentation.test

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import com.theathletic.instrumentation.AutomatorRunner
import com.theathletic.instrumentation.LONG_TIMEOUT
import com.theathletic.instrumentation.PACKAGE
import com.theathletic.instrumentation.Step
import com.theathletic.instrumentation.TIMEOUT
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AutomatorRunner::class)
class ApplicationStartScenario {

    private val device = UiDevice.getInstance(getInstrumentation())
    private val context = getApplicationContext<Context>()

    @Test
    @Step("Given I have The Athletic app installed", order = 1)
    fun given_i_have_the_athletic_installed() {
        device.pressHome()

        // Wait for the launcher
        val launcherPackage = device.launcherPackageName
        assertThat(launcherPackage).isNotNull()
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), TIMEOUT)

        // Check app installed
        val packageInfo = context.packageManager.getPackageInfo(PACKAGE, 0)
        assertThat(packageInfo).isNotNull()
    }

    @Test
    @Step("When I launch it", order = 2)
    fun when_i_launch_it() {
        val intent: Intent? = context.packageManager.getLaunchIntentForPackage(PACKAGE)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        context.startActivity(intent)

        val isStartScreenVisible = device.wait(Until.hasObject(By.res(PACKAGE, "wrapper")), LONG_TIMEOUT)
        assertThat(isStartScreenVisible).isTrue()
    }

    @Test
    @Step("And I see welcome screen", order = 3)
    fun and_i_see_the_home_screen_with_loaded_locations() {
        val isStartReadingVisible = device.wait(Until.hasObject(By.res(PACKAGE, "note")), TIMEOUT)
        assertThat(isStartReadingVisible).isTrue()
    }

    @Test
    @Step("And I dismiss the notification request prompt", order = 4)
    fun and_i_dismiss_the_notification_request_prompt() {
        device.wait(Until.findObject(By.text("NOT NOW")), TIMEOUT)?.click()
        device.wait(Until.gone(By.text("NOT NOW")), TIMEOUT)
    }

    @Test
    @Step("Then I see the start reading button", order = 5)
    fun then_i_see_the_start_reading_button() {
        val isStartReadingVisible = device.wait(Until.hasObject(By.text("Start Reading")), TIMEOUT)

        assert(isStartReadingVisible)
    }
}