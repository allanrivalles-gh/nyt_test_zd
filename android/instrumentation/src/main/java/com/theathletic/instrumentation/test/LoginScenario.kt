package com.theathletic.instrumentation.test

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.theathletic.instrumentation.AutomatorRunner
import com.theathletic.instrumentation.Step
import com.theathletic.instrumentation.confirmLogin
import com.theathletic.instrumentation.dismissTheFeatureIntroScreen
import com.theathletic.instrumentation.fillEmailAndPassword
import com.theathletic.instrumentation.goToLogin
import com.theathletic.instrumentation.selectLoginWithEmail
import com.theathletic.instrumentation.waitForTheUserFeed
import com.theathletic.instrumentation.waitForTheWelcomeScreen
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AutomatorRunner::class)
class LoginScenario {

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Test
    @Step("Given that I'm in the welcome screen", order = 1)
    fun given_i_have_the_athletic_installed() {
        device.waitForTheWelcomeScreen()
    }

    @Test
    @Step("And I tap in the Log in button", order = 2)
    fun and_i_clicked_login() {
        device.goToLogin()
    }

    @Test
    @Step("And I select Log in with Email", order = 3)
    fun and_i_select_log_in_with_email() {
        device.selectLoginWithEmail()
    }

    @Test
    @Step("And I set my credentials", order = 4)
    fun and_i_give_my_credentials() {
        device.fillEmailAndPassword()
    }

    @Test
    @Step("When i tap on login button", order = 5)
    fun when_i_tap_on_log_in_button() {
        device.confirmLogin()
    }

    @Test
    @Step("And I close the Feature Intro screen", order = 6)
    fun and_i_close_the_feature_intro_screen() {
        device.dismissTheFeatureIntroScreen()
    }

    @Test
    @Step("Then I see the user feed", order = 7)
    fun then_i_see_the_user_feed() {
        device.waitForTheUserFeed()
    }
}