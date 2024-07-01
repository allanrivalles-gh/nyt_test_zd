package com.theathletic.podcast.ui.widget

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.theathletic.BR
import com.theathletic.databinding.PodcastMiniPlayerBinding
import com.theathletic.fragment.main.PodcastBigPlayerSheetDialogFragment
import com.theathletic.manager.PodcastManager
import com.theathletic.widget.SwipeDismissTouchListener

object PodcastMiniPlayer {
    @SuppressLint("ClickableViewAccessibility")
    fun create(context: FragmentActivity, viewGroup: ViewGroup): View {
        val binding = PodcastMiniPlayerBinding.inflate(LayoutInflater.from(context))
        binding.setVariable(BR.playerVisible, PodcastManager.shouldBeMiniPlayerVisible)
        binding.content.setOnClickListener {
            PodcastBigPlayerSheetDialogFragment.newInstance().show(context.supportFragmentManager, "podcast_big_player_bottom_bar_sheet")
        }
        binding.content.setOnTouchListener(
            SwipeDismissTouchListener(
                binding.miniPlayerContent,
                null,
                object : SwipeDismissTouchListener.DismissCallbacks {
                    override fun canDismiss(token: Any?): Boolean = true

                    override fun onDismissingStarted(view: View?, token: Any?) {
                        binding.content.setOnClickListener(null)
                    }

                    override fun onDismissed(view: View, token: Any?) {
                        viewGroup.removeView(binding.root)
                        PodcastManager.destroy()
                    }
                }
            )
        )

        viewGroup.addView(binding.root)
        return binding.root
    }
}