package com.theathletic.gamedetail.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.scores.GameDetailTabParams
import com.theathletic.utility.getSerializableCompat

private const val EXTRA_GAME_ID = "extra_game_id"
private const val EXTRA_COMMENT_ID = "extra_comment_id"
private const val EXTRA_SCROLL_TO_MODULE = "extra_scroll_to_module"
private const val EXTRA_SELECTED_TAB_PARAMS = "extra_selected_tab_params"
private const val EXTRA_SOURCE = "extra_source"

class GameDetailActivity : BaseActivity() {

    companion object {
        fun newIntent(
            context: Context,
            gameId: String,
            commentId: String?,
            gameDetailTabParams: GameDetailTabParams,
            scrollToModule: ScrollToModule,
            analyticsPayload: String?,
        ): Intent {
            return Intent(context, GameDetailActivity::class.java).apply {
                putExtra(EXTRA_GAME_ID, gameId)
                putExtra(EXTRA_COMMENT_ID, commentId)
                putExtra(EXTRA_SELECTED_TAB_PARAMS, gameDetailTabParams)
                putExtra(EXTRA_SCROLL_TO_MODULE, scrollToModule)
                putExtra(EXTRA_SOURCE, analyticsPayload)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_base)

        intent.extras?.let { extras ->
            val gameId = extras.getString(EXTRA_GAME_ID)
            val commentId = extras.getString(EXTRA_COMMENT_ID)
            val scrollToModule = extras.getSerializableCompat(EXTRA_SCROLL_TO_MODULE) ?: ScrollToModule.NONE
            val selectedTabParams = extras.getParcelable<GameDetailTabParams>(EXTRA_SELECTED_TAB_PARAMS)
            val analyticsPayload = extras.getString(EXTRA_SOURCE)
            gameId?.let { safeGameId ->
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        GameDetailFragment.newInstance(
                            safeGameId,
                            commentId,
                            selectedTabParams,
                            scrollToModule,
                            analyticsPayload
                        )
                    ).commit()
            } ?: finish()
        }
    }

    override fun getStatusBarColor() = R.color.ath_grey_65
}