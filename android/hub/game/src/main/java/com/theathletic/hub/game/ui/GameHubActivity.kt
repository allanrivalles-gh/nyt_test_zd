package com.theathletic.hub.game.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.boxscore.ui.GameDetailScreen
import com.theathletic.hub.game.navigation.GameHubNavigator
import com.theathletic.scores.GameDetailTab
import com.theathletic.scores.GameDetailTabParams
import com.theathletic.ui.collectWithLifecycle
import com.theathletic.ui.utility.rememberKoin
import com.theathletic.utility.getSerializableCompat
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private const val EXTRA_GAME_ID = "extra_game_id"
private const val EXTRA_COMMENT_ID = "extra_comment_id"
private const val EXTRA_INITIAL_TAB_PARAMS = "extra_initial_tab_params"
private const val EXTRA_SCROLL_TO_MODULE = "extra_scroll_to_module"
private const val EXTRA_SOURCE = "extra_source"

// by default we want it to be true, which means, it is not in some tabbed context
val LocalCommentsTabSelected = compositionLocalOf { true }

class GameHubActivity : AppCompatActivity() {

    companion object {
        fun newIntent(
            context: Context,
            gameId: String,
            commentId: String?,
            gameDetailTabParams: GameDetailTabParams,
            scrollToModule: ScrollToModule,
            analyticsPayload: String?,
        ): Intent {
            return Intent(context, GameHubActivity::class.java).apply {
                putExtra(EXTRA_GAME_ID, gameId)
                putExtra(EXTRA_COMMENT_ID, commentId)
                putExtra(EXTRA_INITIAL_TAB_PARAMS, gameDetailTabParams)
                putExtra(EXTRA_SCROLL_TO_MODULE, scrollToModule)
                putExtra(EXTRA_SOURCE, analyticsPayload)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.also { extras ->
            val gameId: String = extras.getString(EXTRA_GAME_ID) ?: return
            val commentId: String? = extras.getString(EXTRA_COMMENT_ID)
            val initialTabParams: GameDetailTabParams? = extras.getParcelable(EXTRA_INITIAL_TAB_PARAMS)
            val scrollToModule: ScrollToModule? = extras.getSerializableCompat(EXTRA_SCROLL_TO_MODULE)
            val analyticsPayload: String? = extras.getString(EXTRA_SOURCE)

            setContent {
                val navigator = rememberKoin<GameHubNavigator>(LocalContext.current)

                val viewModel = koinViewModel<GameHubViewModel>(
                    parameters = {
                        parametersOf(
                            GameHubViewModel.Params(
                                gameId = gameId,
                                commentId = commentId.orEmpty(),
                                initialTab = initialTabParams?.initialTab ?: GameDetailTab.GAME,
                                initialTabExtras = initialTabParams?.extras ?: emptyMap(),
                                scrollToModule = scrollToModule ?: ScrollToModule.NONE,
                                view = analyticsPayload.orEmpty()
                            )
                        )
                    }
                )

                viewModel.viewEvents.collectWithLifecycle { event ->
                    when (event) {
                        is GameHubViewEvent.NavigateBack -> navigator.gameHubNavigateBack()
                    }
                }

                val viewState by viewModel.viewState.collectAsState()

                val isDiscussTabSelected = viewState.selectedTab == GameDetailTab.DISCUSS
                CompositionLocalProvider(LocalCommentsTabSelected provides isDiscussTabSelected) {
                    GameDetailScreen(
                        title = viewState.toolbarLabel,
                        firstTeam = viewState.firstTeam,
                        secondTeam = viewState.secondTeam,
                        gameStatus = viewState.gameStatus,
                        firstTeamStatus = viewState.firstTeamStatus,
                        secondTeamStatus = viewState.secondTeamStatus,
                        shareLink = viewState.shareLink,
                        showShareLink = viewState.showShareLink,
                        tabs = viewState.tabItems,
                        tabModules = viewState.tabModules,
                        fragmentManager = { supportFragmentManager },
                        interactor = viewModel,
                        gameTitle = viewState.gameTitle,
                        gameInfo = viewState.gameInfo,
                        selectedTab = viewState.selectedTab,
                    )
                }
            }
        }
    }
}