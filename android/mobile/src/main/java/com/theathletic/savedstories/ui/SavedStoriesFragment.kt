package com.theathletic.savedstories.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.theathletic.R
import com.theathletic.savedstories.ui.models.SavedStoriesEmptyItem
import com.theathletic.savedstories.ui.models.SavedStoryListItem
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.AthleticMvpListFragment
import com.theathletic.ui.list.ListVerticalPadding
import com.theathletic.ui.observe
import com.theathletic.ui.widgets.dialog.menuSheet
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class SavedStoriesFragment :
    AthleticMvpListFragment<
        SavedStoriesContract.SavedStoriesViewState,
        SavedStoriesViewModel>() {

    private var clearMenuItem: MenuItem? = null

    override fun setupViewModel() = getViewModel<SavedStoriesViewModel> {
        parametersOf(navigator)
    }

    override fun getLayoutForModel(model: UiModel) = when (model) {
        is SavedStoryListItem -> R.layout.list_item_saved_story
        is SavedStoriesEmptyItem -> R.layout.list_item_saved_stories_empty
        is ListVerticalPadding -> R.layout.list_padding_vertical
        else -> throw IllegalArgumentException("$model not supported")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarBrand.mainAppbar.apply {
            background = ColorDrawable(resources.getColor(R.color.ath_grey_70, null))
        }
        presenter.observe<SavedStoriesContract.Event>(this) { event ->
            when (event) {
                is SavedStoriesContract.Event.ShowArticleLongClickSheet -> showArticleOptionsSheet(
                    event.articleId,
                    event.isBookmarked
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_saved_stories, menu)
        clearMenuItem = menu.findItem(R.id.action_clear)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_clear -> {
            showDeleteAllDialog()
            true
        }
        else -> false
    }

    override fun renderState(viewState: SavedStoriesContract.SavedStoriesViewState) {
        super.renderState(viewState)
        clearMenuItem?.setEnabled(viewState.isDeleteAllEnabled)
    }

    private fun showDeleteAllDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.saved_stories_dialog_title)
            .setMessage(R.string.saved_stories_dialog_text)
            .setPositiveButton(android.R.string.yes) { _, _ -> presenter.clearAllSavedStories() }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    private fun showArticleOptionsSheet(
        articleId: Long,
        isArticleBookmarked: Boolean
    ) {
        menuSheet {
            if (isArticleBookmarked) {
                addUnbookmark { presenter.changeArticleBookmarkStatus(articleId, false) }
            } else {
                addBookmark { presenter.changeArticleBookmarkStatus(articleId, true) }
            }
        }.show(requireActivity().supportFragmentManager, null)
    }
}