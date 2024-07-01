package com.theathletic.profile.ui.consent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import io.transcend.webview.TranscendListener
import io.transcend.webview.TranscendWebView
import org.koin.android.ext.android.inject

class ConsentWebViewActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_ALLOW_BACK_PRESS = "EXTRA_ALLOW_BACK_PRESS"
        fun newIntent(context: Context, allowBackPress: Boolean): Intent {
            val intent = Intent(context, ConsentWebViewActivity::class.java)
            intent.putExtra(EXTRA_ALLOW_BACK_PRESS, allowBackPress)
            return intent
        }
    }

    private val viewModel by inject<ConsentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transcendWebView = TranscendWebView(
            this,
            viewModel.consentUrl,
            TranscendListener.OnCloseListener { finish() }
        )
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val allowBackPress = intent.getBooleanExtra(EXTRA_ALLOW_BACK_PRESS, true)
                    if (allowBackPress) {
                        finish()
                    }
                }
            }
        )
        setContentView(transcendWebView)
    }
}