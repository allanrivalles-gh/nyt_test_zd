package com.theathletic.auth

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.material.textfield.TextInputLayout
import com.theathletic.activity.BaseActivity
import com.theathletic.extension.toStringOrEmpty
import com.theathletic.fragment.AthleticFragment
import java.util.regex.Pattern

interface AuthFragment
interface AuthViewModel {
    fun isEmailValid(input: String): Boolean {
        return input.isNotEmpty() && emailRegex.matcher(input).matches()
    }
}

private val emailRegex = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
)

interface OAuthFragment : AuthFragment {
    fun processOAuthCallback(oAuthResult: String)
}

fun <T> T.authActivity(): AuthenticationActivity where T : AuthFragment, T : AthleticFragment {
    return activity as AuthenticationActivity
}

fun <T> T.setupToolbar(toolbar: Toolbar, title: String = "") where T : AuthFragment, T : AthleticFragment {
    when (val safeActivity = activity) {
        is BaseActivity -> {
            safeActivity.setupActionBar(title, toolbar)
        }
        is AppCompatActivity -> {
            safeActivity.apply {
                setSupportActionBar(toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setDisplayShowHomeEnabled(true)
            }
        }
    }
}

fun <T> T.launchOAuthCustomTab(url: String) where T : AuthFragment, T : AthleticFragment {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(activity as Context, Uri.parse(url))
}

fun TextInputLayout.inputText(): String {
    return editText?.text.toStringOrEmpty()
}