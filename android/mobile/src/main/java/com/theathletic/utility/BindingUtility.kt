package com.theathletic.utility

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.theathletic.AthleticApplication
import com.theathletic.R
import com.theathletic.extension.extGetDimensionPixelSize
import com.theathletic.extension.pixelsEqualTo
import com.theathletic.ui.widgets.TapPagingFrameLayout
import com.theathletic.utility.ui.OnThrottledClickListener
import com.theathletic.widget.GameTimeoutView
import com.theathletic.widget.decoration.GridSpacingItemDecoration
import com.theathletic.widget.decoration.LinearDividerItemDecoration
import com.theathletic.widget.decoration.SpaceItemDecoration
import com.theathletic.widget.layoutmanager.NestedLinearLayoutManager
import com.theathletic.widget.twitter.TweetView

@BindingAdapter("recyclerLayout")
fun setRecyclerLayout(recyclerView: RecyclerView, recyclerLayout: RecyclerLayout) {
    val layoutManager: RecyclerView.LayoutManager = when (recyclerLayout.recyclerLayoutType) {
        RecyclerLayout.RecyclerLayoutType.NESTED_LINEAR -> NestedLinearLayoutManager(recyclerView.context)
        RecyclerLayout.RecyclerLayoutType.LINEAR -> LinearLayoutManager(recyclerView.context)
        RecyclerLayout.RecyclerLayoutType.GRID -> androidx.recyclerview.widget.GridLayoutManager(
            recyclerView.context,
            recyclerLayout.gridRows
        )
        RecyclerLayout.RecyclerLayoutType.STAGGERED_GRID -> StaggeredGridLayoutManager(
            recyclerLayout.gridRows,
            recyclerLayout.recyclerLayoutOrientation
        )
    }

    if (layoutManager is LinearLayoutManager) {
        layoutManager.orientation = recyclerLayout.recyclerLayoutOrientation
        layoutManager.reverseLayout = recyclerLayout.isReversed
    }

    if (recyclerView.layoutManager == null || recyclerView.getTag(R.id.recyclerLayoutTag) != recyclerLayout) {
        recyclerView.layoutManager = layoutManager
        recyclerView.setTag(R.id.recyclerLayoutTag, recyclerLayout)

        if (recyclerLayout.recyclerLayoutType == RecyclerLayout.RecyclerLayoutType.NESTED_LINEAR) {
            recyclerView.isNestedScrollingEnabled = false
        } else {
            recyclerView.setHasFixedSize(true)
        }
    }
}

@BindingAdapter("supportsChangeAnimations")
fun setRecyclerLayout(recyclerView: RecyclerView, supportsChangeAnimations: Boolean) {
    (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = supportsChangeAnimations
}

@BindingAdapter("recyclerDecoration")
fun setRecyclerDecoration(recyclerView: RecyclerView, recyclerDecoration: RecyclerDecoration) {
    val itemDecoration: RecyclerView.ItemDecoration = when (recyclerDecoration) {
        RecyclerDecoration.LINEAR_SPACE -> SpaceItemDecoration(R.dimen.global_spacing_8)
        RecyclerDecoration.LINEAR_SPACE_SMALL -> SpaceItemDecoration(R.dimen.global_spacing_2)
        RecyclerDecoration.LINEAR_DIVIDER -> LinearDividerItemDecoration(recyclerView.context, null)
        RecyclerDecoration.LINEAR_DIVIDER_SCORE -> SpaceItemDecoration(R.dimen.divider_size)
        RecyclerDecoration.LINEAR_DIVIDER_FEED -> SpaceItemDecoration(R.dimen.feed_divider_padding)
        RecyclerDecoration.GRID_SPACE -> GridSpacingItemDecoration(R.dimen.feed_item_spacing_tiny.extGetDimensionPixelSize())
    }

    recyclerView.addItemDecoration(itemDecoration)
}

@BindingAdapter("animator")
fun setAnimator(view: View, animation: Animator) {
    animation.setTarget(view)
    animation.start()
}

@BindingAdapter("backgroundTint")
fun setBackgroundTint(view: View, color: Int) {
    if (view is ImageView && view.drawable != null) {
        val drawable = view.drawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.MULTIPLY)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
        view.setImageDrawable(drawable)
    } else if (view.background != null) {
        val drawable = view.background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.MULTIPLY)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
        view.background = drawable
    }
}

@BindingAdapter("colorTint")
fun setImageTint(view: ImageView, color: Int) {
    if (view.drawable != null) {
        val drawable = view.drawable
        drawable.setTint(color)
        view.setImageDrawable(drawable)
    }
}

@BindingAdapter("android:backgroundTint")
fun setBackGroundTint(view: ImageView, hexColor: String) {
    val color = try {
        Color.parseColor("#$hexColor")
    } catch (error: Throwable) {
        view.context.getColor(R.color.ath_grey_60)
    }

    view.background.current.setTint(color)
}

@BindingAdapter("android:bindableProgress")
fun setProgressBarProgress(view: ProgressBar, progress: Int) {
    view.progress = progress
}

@BindingAdapter("android:animatedBindableProgress")
fun setProgressBarAnimatedProgress(view: ProgressBar, progress: Int) {
    view.post {
        val animation = ObjectAnimator.ofInt(view, "progress", progress)
        animation.duration = 100
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }
}

@BindingAdapter("android:animatedBindableProgressSlow")
fun setProgressBarAnimatedProgressSlow(view: ProgressBar, progress: Int) {
    view.post {
        val animation = ObjectAnimator.ofInt(view, "progress", progress)
        animation.duration = 500
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }
}

@BindingAdapter("srcCompat")
fun setImageViewResource(imageView: ImageView, resource: Int) {
    if (resource != -1)
        imageView.setImageResource(resource)
}

@BindingAdapter("srcCompat")
fun setImageViewDrawable(imageView: ImageView, drawable: Drawable?) {
    imageView.setImageDrawable(drawable)
}

@BindingAdapter("onToggled")
fun onToggled(switch: CompoundButton, listener: CompoundButton.OnCheckedChangeListener) {
    switch.setOnCheckedChangeListener { buttonView, isChecked ->
        if (buttonView.isPressed) {
            listener.onCheckedChanged(buttonView, isChecked)
        }
    }
}

@BindingAdapter("animateDrawable")
fun animateDrawable(imageView: ImageView, animate: Boolean) {
    if (animate) {
        (imageView.drawable as? AnimatedVectorDrawable)?.start()
        (imageView.drawable as? AnimatedVectorDrawableCompat)?.start()
    } else {
        (imageView.drawable as? AnimatedVectorDrawable)?.stop()
        (imageView.drawable as? AnimatedVectorDrawableCompat)?.stop()
    }
}

@BindingAdapter("android:animatedSrc")
fun setImageViewAnimatedDrawable(imageView: ImageView, drawable: Drawable?) {
    if (imageView.drawable == null || imageView.drawable.pixelsEqualTo(drawable)) {
        imageView.setImageDrawable(drawable)
    } else {
        val animOut = AnimationUtils.loadAnimation(AthleticApplication.getContext(), R.anim.alpha_out)
        val animIn = AnimationUtils.loadAnimation(AthleticApplication.getContext(), R.anim.alpha_in)
        animOut.duration = 150
        animIn.duration = 150
        animIn.interpolator = DecelerateInterpolator()
        animOut.interpolator = AccelerateInterpolator()
        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                imageView.setImageDrawable(drawable)
                imageView.startAnimation(animIn)
            }
        })
        imageView.startAnimation(animOut)
    }
}

@BindingAdapter("android:layout_height")
fun setLayoutHeightRes(view: View, @DimenRes heightRes: Int) {
    val layoutParams = view.layoutParams
    layoutParams.height = view.resources.getDimensionPixelSize(heightRes)
    view.layoutParams = layoutParams
}

@BindingAdapter("tweetUrl", "inflateWebview")
fun setTweetUrl(tweetView: TweetView, url: String?, inflateWebview: Boolean) {
    tweetView.showTweet(url, inflateWebview)
}

@BindingAdapter("selected")
fun getSelectedView(view: View, isSelected: Boolean) {
    view.isSelected = isSelected
}

@BindingConversion
fun convertBooleanToVisibility(visible: Boolean): Int = if (visible) View.VISIBLE else View.GONE

@BindingAdapter("android:invisibility")
fun setViewVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter(
    value = [
        "usedTimeouts",
        "remainingTimeouts"
    ],
    requireAll = true
)
fun setGameTimeouts(gameTimeoutView: GameTimeoutView, usedTimeouts: Int, remainingTimeouts: Int) {
    gameTimeoutView.renderIndicators(usedTimeouts, remainingTimeouts)
}

@BindingAdapter(
    value = [
        "topMargin",
        "bottomMargin",
        "startMargin",
        "endMargin"
    ],
    requireAll = false
)

fun setProgrammableMargins(
    view: View,
    @DimenRes marginTop: Int? = null,
    @DimenRes marginBottom: Int? = null,
    @DimenRes marginStart: Int? = null,
    @DimenRes marginEnd: Int? = null
) {
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        setMargins(
            marginStart?.let { view.resources.getDimensionPixelSize(it) } ?: leftMargin,
            marginTop?.let { view.resources.getDimensionPixelSize(it) } ?: topMargin,
            marginEnd?.let { view.resources.getDimensionPixelSize(it) } ?: rightMargin,
            marginBottom?.let { view.resources.getDimensionPixelSize(it) } ?: bottomMargin
        )
    }
}

@BindingAdapter("onTextChanged")
fun onTextChanged(editText: EditText, listener: OnTextChangedListener) {
    var lastValue = editText.text.toString()
    editText.addTextChangedListener {
        val newValue = it.toString()
        // Prevent a recursive loop if the listener triggers a presenter state update which then
        // sets the EditText to the same value.
        if (lastValue != newValue) {
            listener.onTextChanged(newValue)
            lastValue = newValue
        }
    }
}

@BindingAdapter("itemSnap")
fun itemSnap(recyclerView: RecyclerView, itemSnap: Boolean = false) {
    val hasSnapHelper = recyclerView.onFlingListener is PagerSnapHelper

    if (itemSnap && !hasSnapHelper) {
        PagerSnapHelper().attachToRecyclerView(recyclerView)
    } else if (!itemSnap && hasSnapHelper) {
        recyclerView.onFlingListener = null
    }
}

@BindingAdapter("onThrottledClick")
fun onThrottledClick(view: View, listener: OnThrottledClickListener) {
    var savedLastClickTime: Long = 0
    view.setOnClickListener {
        val lastClickTime = savedLastClickTime
        val now = System.currentTimeMillis()
        savedLastClickTime = now
        if (now - lastClickTime >= 1000) { // interval of 1000ms
            listener.onThrottledClick(view)
        }
    }
}

@BindingAdapter("applyLinkMovementMethod")
fun applyLinkMovementMethod(textView: TextView, apply: Boolean = false) {
    if (apply) {
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}

@BindingAdapter("textColorResource")
fun textColorResource(textView: TextView, @ColorRes colorRes: Int) {
    if (colorRes != 0) {
        textView.setTextColor(
            ContextCompat.getColor(
                textView.context,
                colorRes
            )
        )
    }
}

@BindingAdapter("layoutHeight")
fun layoutHeight(view: View, height: Float) {
    view.layoutParams = view.layoutParams.apply { this.height = height.toInt() }
}

@BindingAdapter("layoutWidth")
fun layoutWidth(view: View, width: Float) {
    view.layoutParams = view.layoutParams.apply { this.width = width.toInt() }
}

@BindingAdapter("underline")
fun underline(textView: TextView, underline: Boolean) {
    if (underline) {
        textView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }
}

@BindingAdapter("onScrollTap")
fun onScrollTap(layout: TapPagingFrameLayout, listener: TapPagingFrameLayout.OnScrollTapListener) {
    layout.setOnTapPagingListener(listener)
}

@BindingAdapter("drawableStart")
fun setDrawableStart(view: TextView, resourceId: Int) {
    val drawable = ContextCompat.getDrawable(view.context, resourceId)
    drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val drawables = view.compoundDrawablesRelative
    view.setCompoundDrawablesRelative(drawable, drawables[1], drawables[2], drawables[3])
}

@BindingAdapter("drawableEnd")
fun setDrawableEnd(view: TextView, resourceId: Int) {
    val drawable = ContextCompat.getDrawable(view.context, resourceId)
    drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val drawables = view.compoundDrawablesRelative
    view.setCompoundDrawablesRelative(drawables[0], drawables[1], drawable, drawables[3])
}

@BindingAdapter("viewRatio")
fun setViewRatio(view: View, ratio: String?) {
    val constraintLayout = (view.parent as? ConstraintLayout) ?: return
    with(ConstraintSet()) {
        clone(constraintLayout)
        setDimensionRatio(view.id, ratio)
        applyTo(constraintLayout)
    }
}

@BindingAdapter("expandedMaxLines", "expandedMinLines")
fun setExpandableTextClickListener(textView: TextView, expandedMaxLines: Int, expandedMinLines: Int) {
    textView.setOnClickListener {
        val animation = ObjectAnimator.ofInt(
            textView,
            "maxLines",
            if (textView.maxLines == expandedMinLines) expandedMaxLines else expandedMinLines
        )
        animation.setDuration(200).start()
    }
}