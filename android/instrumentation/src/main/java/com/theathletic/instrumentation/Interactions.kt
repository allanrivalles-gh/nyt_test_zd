package com.theathletic.instrumentation

import android.os.Build
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assume

fun UiDevice.waitForTheWelcomeScreen() {
    val isWelcomeScreenVisible = wait(Until.hasObject(By.text("Start Reading")), TRASH_TIMEOUT)
    assert(isWelcomeScreenVisible)
}

fun UiDevice.dismissNotificationPrompt() {
    Assume.assumeTrue(Build.VERSION.SDK_INT > 32)
    wait(Until.findObject(By.text("NOT NOW")), TIMEOUT).click()

    val isEnableNotificationPromptGone = wait(Until.gone(By.text("NOT NOW")), TIMEOUT)
    assert(isEnableNotificationPromptGone)
}

fun UiDevice.goToLogin() {
    findObject(By.textContains("Log in")).click()
}

fun UiDevice.selectLoginWithEmail() {
    wait(Until.findObject(By.textContains("Email")), TIMEOUT).click()
}

fun UiDevice.fillEmailAndPassword() {
    val emailInputText = wait(Until.findObject(By.text("Email")), TIMEOUT)
    val passwordInputText = findObject(By.text("Password"))

    emailInputText.text = "demo@theathletic.com"
    passwordInputText.text = "Th3Athl3tic"

    assert(emailInputText.text.isNotBlank())
    assert(passwordInputText.text.isNotBlank())
}

fun UiDevice.confirmLogin() {
    val loginButton = findObject(By.res(PACKAGE, "button_login"))
    loginButton.wait(Until.enabled(true), TRASH_TIMEOUT)
    loginButton.click()
}

fun UiDevice.dismissTheFeatureIntroScreen() {
    val closeButtonSelector = By.res("CloseButton")
    val hasFeatureIntro = wait(Until.hasObject(closeButtonSelector), LONG_TIMEOUT)
    Assume.assumeTrue(hasFeatureIntro)

    findObject(closeButtonSelector).click()
}

fun UiDevice.waitForTheUserFeed() {
    val isMyFeedVisible = wait(Until.hasObject(By.res(PACKAGE, "feed_recycler_view")), TIMEOUT)

    assert(isMyFeedVisible)
}