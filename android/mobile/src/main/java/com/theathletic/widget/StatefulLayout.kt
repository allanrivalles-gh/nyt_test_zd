package com.theathletic.widget

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IntDef
import com.theathletic.R
import java.util.ArrayList

// inspired by: https://github.com/jakubkinst/Android-StatefulView
class StatefulLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    companion object {
        const val CONTENT = 0
        const val PROGRESS = 1
        const val OFFLINE = 2
        const val EMPTY = 3

        private const val SAVED_STATE = "stateful_layout_state"
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(CONTENT, PROGRESS, OFFLINE, EMPTY)
    annotation class State

    interface OnStateChangeListener {
        fun onStateChange(view: View, @State state: Int)
    }

    @State
    private var mInitialState: Int = 0
    private var mProgressLayoutId: Int = 0
    private var mOfflineLayoutId: Int = 0
    private var mEmptyLayoutId: Int = 0
    private val mInvisibleWhenHidden: Boolean
    private var mContentLayoutList: MutableList<View>? = null
    private var mProgressLayout: View? = null
    private var mOfflineLayout: View? = null
    private var mEmptyLayout: View? = null
    @State
    @get:State
    var state: Int = 0
        set(@State state) {
            field = state

            for (i in mContentLayoutList!!.indices) {
                mContentLayoutList!![i].visibility = determineVisibility(state == CONTENT)
            }

            if (mProgressLayout != null)
                mProgressLayout!!.visibility = determineVisibility(state == PROGRESS)
            if (mOfflineLayout != null)
                mOfflineLayout!!.visibility = determineVisibility(state == OFFLINE)
            if (mEmptyLayout != null)
                mEmptyLayout!!.visibility = determineVisibility(state == EMPTY)

            if (mOnStateChangeListener != null) mOnStateChangeListener!!.onStateChange(this, state)
        }
    private var mOnStateChangeListener: OnStateChangeListener? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatefulLayout)

        if (typedArray.hasValue(R.styleable.StatefulLayout_state)) {

            mInitialState = typedArray.getInt(R.styleable.StatefulLayout_state, CONTENT)
        }

        if (typedArray.hasValue(R.styleable.StatefulLayout_progressLayout))
            mProgressLayoutId = typedArray.getResourceId(R.styleable.StatefulLayout_progressLayout, 0)
        if (typedArray.hasValue(R.styleable.StatefulLayout_offlineLayout))
            mOfflineLayoutId = typedArray.getResourceId(R.styleable.StatefulLayout_offlineLayout, 0)
        if (typedArray.hasValue(R.styleable.StatefulLayout_emptyLayout))
            mEmptyLayoutId = typedArray.getResourceId(R.styleable.StatefulLayout_emptyLayout, 0)

        mInvisibleWhenHidden = typedArray.getBoolean(R.styleable.StatefulLayout_invisibleWhenHidden, false)

        typedArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupView()
    }

    @Suppress("unused")
    fun showContent() {
        state = CONTENT
    }

    @Suppress("unused")
    fun showProgress() {
        state = PROGRESS
    }

    @Suppress("unused")
    fun showOffline() {
        state = OFFLINE
    }

    @Suppress("unused")
    fun showEmpty() {
        state = EMPTY
    }

    @Suppress("unused")
    fun setProgressLayout(@State progressLayoutId: Int) {
        mProgressLayoutId = progressLayoutId
        removeView(mProgressLayout)
        mProgressLayout = LayoutInflater.from(context).inflate(mProgressLayoutId, this, false)
        addView(mProgressLayout)
        state = state
    }

    @Suppress("unused")
    fun setOfflineLayout(@State offlineLayoutId: Int) {
        mOfflineLayoutId = offlineLayoutId
        removeView(mOfflineLayout)
        mOfflineLayout = LayoutInflater.from(context).inflate(mOfflineLayoutId, this, false)
        addView(mOfflineLayout)
        state = state
    }

    @Suppress("unused")
    fun setEmptyLayout(@State emptyLayoutId: Int) {
        mEmptyLayoutId = emptyLayoutId
        removeView(mEmptyLayout)
        mEmptyLayout = LayoutInflater.from(context).inflate(mEmptyLayoutId, this, false)
        addView(mEmptyLayout)
        state = state
    }

    @Suppress("unused")
    fun setOnStateChangeListener(l: StatefulLayout.OnStateChangeListener) {
        mOnStateChangeListener = l
    }

    @Suppress("unused")
    fun saveInstanceState(outState: Bundle) {
        outState.putInt(SAVED_STATE, state)
    }

    @State
    @Suppress("unused")
    fun restoreInstanceState(savedInstanceState: Bundle?): Int {
        @State var state = CONTENT
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_STATE)) {
            state = savedInstanceState.getInt(SAVED_STATE)
        }
        return state
    }

    private fun setupView() {
        if (mContentLayoutList == null && !isInEditMode) {
            mContentLayoutList = ArrayList()
            for (i in 0 until childCount) {
                mContentLayoutList!!.add(getChildAt(i))
            }

            if (mProgressLayoutId != 0)
                mProgressLayout = LayoutInflater.from(context).inflate(mProgressLayoutId, this, false)
            if (mOfflineLayoutId != 0)
                mOfflineLayout = LayoutInflater.from(context).inflate(mOfflineLayoutId, this, false)
            if (mEmptyLayoutId != 0)
                mEmptyLayout = LayoutInflater.from(context).inflate(mEmptyLayoutId, this, false)

            if (mProgressLayout != null)
                addView(mProgressLayout)
            if (mOfflineLayout != null)
                addView(mOfflineLayout)
            if (mEmptyLayout != null)
                addView(mEmptyLayout)

            state = mInitialState
        }
    }

    private fun determineVisibility(visible: Boolean): Int {
        return if (visible) {
            View.VISIBLE
        } else {
            if (mInvisibleWhenHidden) {
                View.INVISIBLE
            } else {
                View.GONE
            }
        }
    }
}