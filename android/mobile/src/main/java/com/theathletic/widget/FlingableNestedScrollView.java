package com.theathletic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ScrollingView;
import androidx.core.view.ViewCompat;


/**
 * Workaround for
 * http://stackoverflow.com/questions/42783030/nestedscrollview-fling-stopping-bug-on-nougat-api-25
 * https://code.google.com/p/android/issues/detail?id=246551
 * <p>
 * Should be used only for API 21+. On API <21 It won't properly work as a nested scrolling child.
 */
public class FlingableNestedScrollView extends ScrollView implements NestedScrollingChild, NestedScrollingParent, ScrollingView
{
    private final NestedScrollingParentHelper parentHelper;
    private final NestedScrollingChildHelper childHelper;
    private OnScrollChangeListener onScrollChangeListener;


    /**
     * Interface definition for a callback to be invoked when the scroll
     * X or Y positions of a view change.
     * <p>
     * <p>This version of the interface works on all versions of Android, back to API v4.</p>
     *
     * @see #setOnScrollChangeListener(OnScrollChangeListener)
     */
    interface OnScrollChangeListener
    {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param v          The view whose scroll position has changed.
         * @param scrollX    Current horizontal scroll origin.
         * @param scrollY    Current vertical scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        void onScrollChange(FlingableNestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }


    {
        parentHelper = new NestedScrollingParentHelper(this);
        childHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }


    public FlingableNestedScrollView(Context context)
    {
        super(context);
    }


    public FlingableNestedScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    public FlingableNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }


    // Measurement algorithm is taken from NestedScrollView
    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec)
    {
        ViewGroup.LayoutParams lp = child.getLayoutParams();

        int childWidthMeasureSpec;
        int childHeightMeasureSpec;

        childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight(), lp.width);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    // NestedScrollingChild


    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed)
    {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        int padding = getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + widthUsed;

        int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, padding, lp.width);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.topMargin + lp.bottomMargin, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }


    @Override
    public boolean isNestedScrollingEnabled()
    {
        return childHelper.isNestedScrollingEnabled();
    }


    @Override
    public void setNestedScrollingEnabled(boolean enabled)
    {
        childHelper.setNestedScrollingEnabled(enabled);
    }


    @Override
    public boolean startNestedScroll(int axes)
    {
        return childHelper.startNestedScroll(axes);
    }


    @Override
    public void stopNestedScroll()
    {
        childHelper.stopNestedScroll();
    }


    @Override
    public boolean hasNestedScrollingParent()
    {
        return childHelper.hasNestedScrollingParent();
    }


    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow)
    {
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow);
    }


    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow)
    {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }


    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed)
    {
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    // NestedScrollingParent


    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY)
    {
        return childHelper.dispatchNestedPreFling(velocityX, velocityY);
    }


    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes)
    {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }


    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes)
    {
        parentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
    }


    @Override
    public void onStopNestedScroll(@NonNull View target)
    {
        parentHelper.onStopNestedScroll(target);
        stopNestedScroll();
    }


    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed)
    {
        dispatchNestedPreScroll(dx, dy, consumed, null);
    }


    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY)
    {
        return dispatchNestedPreFling(velocityX, velocityY);
    }


    @Override
    public int getNestedScrollAxes()
    {
        return parentHelper.getNestedScrollAxes();
    }


    @Override
    public int computeVerticalScrollRange()
    {
        return super.computeVerticalScrollRange();
    }


    @Override
    public int computeVerticalScrollOffset()
    {
        return Math.max(0, super.computeVerticalScrollOffset());
    }


    @Override
    public int computeVerticalScrollExtent()
    {
        return super.computeVerticalScrollExtent();
    }


    @Override
    public int computeHorizontalScrollRange()
    {
        return super.computeHorizontalScrollRange();
    }


    @Override
    public int computeHorizontalScrollOffset()
    {
        return super.computeHorizontalScrollOffset();
    }


    @Override
    public int computeHorizontalScrollExtent()
    {
        return super.computeHorizontalScrollExtent();
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldL, int oldT)
    {
        super.onScrollChanged(l, t, oldL, oldT);

        if(onScrollChangeListener != null)
        {
            onScrollChangeListener.onScrollChange(this, l, t, oldL, oldT);
        }
    }


    /**
     * Register a callback to be invoked when the scroll X or Y positions of
     * this view change.
     * <p>This version of the method works on all versions of Android, back to API v4.</p>
     *
     * @param listener The listener to notify when the scroll X or Y position changes.
     * @see android.view.View#getScrollX()
     * @see android.view.View#getScrollY()
     */
    @SuppressWarnings("unused")
    public void setOnScrollChangeListener(@Nullable OnScrollChangeListener listener)
    {
        onScrollChangeListener = listener;
    }
}