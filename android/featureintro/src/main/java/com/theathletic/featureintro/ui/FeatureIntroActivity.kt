package com.theathletic.featureintro.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.theathletic.ui.collectWithLifecycle
import org.koin.androidx.compose.koinViewModel

class FeatureIntroActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(
            context,
            FeatureIntroActivity::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = koinViewModel<FeatureIntroViewModel>()

            viewModel.viewEvents.collectWithLifecycle { event ->
                when (event) {
                    is FeatureIntroViewEvent.CloseScreen -> finish()
                }
            }

            FeatureIntroScreen(viewModel = viewModel)
        }
    }
}