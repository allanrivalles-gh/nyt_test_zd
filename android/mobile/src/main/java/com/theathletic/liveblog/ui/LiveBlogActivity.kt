package com.theathletic.liveblog.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.extension.toDp
import com.theathletic.featureswitch.Features
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.ui.observe
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class LiveBlogActivity : BaseActivity() {
    private val features by inject<Features>()
    private val screenNavigator by inject<ScreenNavigator> { parametersOf(this) }

    companion object {
        private const val EXTRA_LIVE_BLOG_ID = "extra_live_blog_id"
        private const val EXTRA_POST_ID = "extra_post_id"

        fun newIntent(context: Context, liveBlogId: String, postId: String?): Intent {
            return Intent(context, LiveBlogActivity::class.java).apply {
                putExtra(EXTRA_LIVE_BLOG_ID, liveBlogId)
                postId?.let { putExtra(EXTRA_POST_ID, it) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val liveBlogId = intent.extras?.getString(EXTRA_LIVE_BLOG_ID, null) ?: return
        val postId = intent.extras?.getString(EXTRA_POST_ID)

        if (features.isLiveBlogWebViewEnabled) {
            val viewModel = get<LiveBlogWebViewViewModel> {
                parametersOf(
                    LiveBlogWebViewViewModel.Params(
                        liveBlogId = liveBlogId,
                        initialPostId = postId,
                        resources.displayMetrics.widthPixels.toDp,
                        resources.displayMetrics.heightPixels.toDp
                    )
                )
            }
            viewModel.observe<Event>(lifecycleScope) { event ->
                when (event) {
                    Event.OnBackClick -> screenNavigator.finishActivity()
                    is Event.OnShareClick -> screenNavigator.startShareTextActivity(event.permalink)
                }
            }
            setContentView(
                ComposeView(this).apply {
                    setContent { LiveBlogWebViewScreen(viewModel = viewModel) }
                }
            )
        } else {
            setContentFragment(LiveBlogComposeFragment.newInstance(liveBlogId, postId), savedInstanceState)
        }
    }

    private fun setContentFragment(fragment: Fragment, savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_fragment_base)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }
}