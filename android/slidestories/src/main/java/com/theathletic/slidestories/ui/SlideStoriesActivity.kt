package com.theathletic.slidestories.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.theathletic.slidestories.R
import com.theathletic.ui.collectWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private const val EXTRA_STORIES_ID = "extra_stories_id"

class SlideStoriesActivity : AppCompatActivity() {

    companion object {
        fun newIntent(
            context: Context,
            storiesId: String
        ) = Intent(context, SlideStoriesActivity::class.java).apply {
            putExtra(EXTRA_STORIES_ID, storiesId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var storiesId: String? = null
        intent.extras?.let { extras ->
            storiesId = extras.getString(EXTRA_STORIES_ID)
        }
        storiesId?.let { id ->
            setContent {
                val viewModel = koinViewModel<SlideStoriesViewModel>(
                    parameters = {
                        parametersOf(SlideStoriesViewModel.Params(storiesId = id))
                    }
                )

                val viewState by viewModel.viewState.collectAsState()

                viewModel.viewEvents.collectWithLifecycle { event ->
                    when (event) {
                        SlideStoriesViewEvent.Close -> finish()
                    }
                }

                viewState.uiModel?.let { uiModel ->
                    SlideStoriesScreen(
                        slides = uiModel.slides,
                        slideProgress = viewState.slideProgress,
                        currentSlideIndex = viewState.currentSlideIndex,
                        onClose = { viewModel.onClose() },
                        onGesture = { event -> viewModel.onGesture(event) }
                    )
                }
            }
        } ?: finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_bottom)
    }
}