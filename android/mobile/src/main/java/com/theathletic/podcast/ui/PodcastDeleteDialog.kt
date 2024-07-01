package com.theathletic.podcast.ui

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.theathletic.R
import cz.helu.helubottombuttonsheet.HeluBottomButtonSheet

object PodcastDeleteDialog {
    fun show(activity: FragmentActivity, onDeleteClicked: () -> Unit) {
        val sheet = HeluBottomButtonSheet.Builder(
            activity
        ).build()
        sheet.addButton(
            R.drawable.ic_trash,
            activity.resources.getString(R.string.podcast_downloaded_delete_button),
            View.OnClickListener {
                onDeleteClicked()
                sheet.dismiss()
            }
        )
        sheet.show(activity.supportFragmentManager)
    }
}