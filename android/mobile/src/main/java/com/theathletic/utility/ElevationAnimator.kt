package com.theathletic.utility

import android.view.View
import android.widget.ScrollView
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.widget.NestedScrollView
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.theathletic.AthleticApplication
import com.theathletic.R
import com.theathletic.widget.FlingableNestedScrollView

class ElevationAnimator {
    private var elevation = AthleticApplication.getContext().resources.getDimensionPixelSize(R.dimen.global_elevation_4).toFloat()
    private var translationAnimator: ViewPropertyAnimatorCompat? = null
    private var scrollContainer: View
    private var elevationView: View?
    internal var isEnabled = true
    internal var isElevationVisible = false

    constructor(recyclerView: RecyclerView, elevationView: View, elevation: Float) {
        this.elevation = elevation
        scrollContainer = recyclerView
        this.elevationView = elevationView
        setupRecyclerViewListener(recyclerView, elevationView)
    }

    constructor(recyclerView: RecyclerView, elevationView: View?) {
        scrollContainer = recyclerView
        this.elevationView = elevationView
        elevationView?.let { setupRecyclerViewListener(recyclerView, elevationView) }
    }

    constructor(scrollView: ScrollView, elevationView: View?) {
        scrollContainer = scrollView
        this.elevationView = elevationView
        elevationView?.let { setupScrollViewListener(scrollView, elevationView) }
    }

    constructor(scrollView: NestedScrollView, elevationView: View?) {
        scrollContainer = scrollView
        this.elevationView = elevationView
        elevationView?.let { setupScrollViewListener(scrollView, elevationView) }
    }

    constructor(scrollView: FlingableNestedScrollView, elevationView: View?) {
        scrollContainer = scrollView
        this.elevationView = elevationView
        elevationView?.let { setupScrollViewListener(scrollView, elevationView) }
    }

    constructor(scrollView: FlingableNestedScrollView, elevationView: View?, elevation: Float) {
        this.elevation = elevation
        scrollContainer = scrollView
        this.elevationView = elevationView
        elevationView?.let { setupScrollViewListener(scrollView, elevationView) }
    }

    fun checkElevationStatus() {
        val scrollY = when (scrollContainer) {
            is RecyclerView -> (scrollContainer as RecyclerView).computeVerticalScrollOffset()
            is ScrollView -> (scrollContainer as ScrollView).scrollY
            is NestedScrollView -> (scrollContainer as NestedScrollView).scrollY
            else -> 0
        }

        if (scrollY <= 0) {
            isElevationVisible = false
            elevationView?.let { animateViewElevation(it) }
            translationAnimator?.translationZ(0f)?.start()
        } else {
            isElevationVisible = true
            elevationView?.let { animateViewElevation(it) }
            translationAnimator?.translationZ(elevation)?.start()
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (!enabled) {
            isElevationVisible = false
            elevationView?.let { animateViewElevation(it) }
            translationAnimator?.translationZ(0f)?.start()
        }
    }

    private fun setupScrollViewListener(scrollView: NestedScrollView, elevationView: View) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (!isEnabled)
                return@addOnScrollChangedListener

            val scrollY = scrollView.scrollY // For ScrollView

            if (scrollY == 0) {
                isElevationVisible = false

                animateViewElevation(elevationView)
                translationAnimator?.translationZ(0f)?.start()
            } else if (!isElevationVisible) {
                isElevationVisible = true

                animateViewElevation(elevationView)
                translationAnimator?.translationZ(elevation)?.start()
            }
        }
    }

    private fun setupScrollViewListener(scrollView: ScrollView, elevationView: View) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (!isEnabled)
                return@addOnScrollChangedListener

            val scrollY = scrollView.scrollY // For ScrollView

            if (scrollY == 0) {
                isElevationVisible = false

                animateViewElevation(elevationView)
                translationAnimator?.translationZ(0f)?.start()
            } else if (!isElevationVisible) {
                isElevationVisible = true

                animateViewElevation(elevationView)
                translationAnimator?.translationZ(elevation)?.start()
            }
        }
    }

    private fun setupRecyclerViewListener(recyclerView: RecyclerView, elevationView: View) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isEnabled)
                    return

                if (recyclerView.computeVerticalScrollOffset() == 0) {
                    isElevationVisible = false

                    animateViewElevation(elevationView)
                    translationAnimator?.translationZ(0f)?.start()
                } else if (!isElevationVisible) {
                    isElevationVisible = true

                    animateViewElevation(elevationView)
                    translationAnimator?.translationZ(elevation)?.start()
                }
            }
        })
    }

    private fun animateViewElevation(child: View) {
        if (translationAnimator == null) {
            translationAnimator = ViewCompat.animate(child)
            translationAnimator?.duration = 150
            translationAnimator?.interpolator = LinearOutSlowInInterpolator()
        } else {
            translationAnimator?.cancel()
        }
    }
}