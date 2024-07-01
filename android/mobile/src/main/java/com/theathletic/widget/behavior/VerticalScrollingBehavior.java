package com.theathletic.widget.behavior;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.WindowInsetsCompat;


/**
 * Created by Nikola D. on 11/22/2015.
 * <p>
 * Credit goes to Nikola Despotoski:
 * https://github.com/NikolaDespotoski
 */
@SuppressWarnings({"deprecation", "unused"})
abstract class VerticalScrollingBehavior<V extends View> extends CoordinatorLayout.Behavior<V>
{
    private int totalDyUnconsumed = 0;
    private int totalDy = 0;
    @ScrollDirection
    private int overScrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;
    @ScrollDirection
    private int scrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ScrollDirection.SCROLL_DIRECTION_UP, ScrollDirection.SCROLL_DIRECTION_DOWN})
    @interface ScrollDirection
    {
        int SCROLL_DIRECTION_UP = 1;
        int SCROLL_DIRECTION_DOWN = -1;
    }


    VerticalScrollingBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    VerticalScrollingBehavior()
    {
        super();
    }


    /**
     * @param coordinatorLayout coordinatorLayout
     * @param child             child
     * @param direction         Direction of the over-scroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
     * @param currentOverScroll Unconsumed value, negative or positive based on the direction;
     * @param totalOverScroll   Cumulative value for current direction
     */
    @SuppressWarnings("EmptyMethod")
    abstract void onNestedVerticalOverScroll(CoordinatorLayout coordinatorLayout, V child, @ScrollDirection int direction, int currentOverScroll, int totalOverScroll);


    /**
     * @param scrollDirection Direction of the over-scroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
     */
    abstract void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed, @ScrollDirection int scrollDirection);


    @SuppressWarnings("SameReturnValue")
    abstract boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection);


    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int nestedScrollAxes)
    {
        return (nestedScrollAxes & View.SCROLL_AXIS_VERTICAL) != 0;
    }


    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int nestedScrollAxes)
    {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }


    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target)
    {
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }


    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed)
    {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if(dyUnconsumed > 0 && totalDyUnconsumed < 0)
        {
            totalDyUnconsumed = 0;
            overScrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;
        }
        else if(dyUnconsumed < 0 && totalDyUnconsumed > 0)
        {
            totalDyUnconsumed = 0;
            overScrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN;
        }
        totalDyUnconsumed += dyUnconsumed;
        onNestedVerticalOverScroll(coordinatorLayout, child, overScrollDirection, dyConsumed, totalDyUnconsumed);
    }


    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dx, int dy, @NonNull int[] consumed)
    {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        if(dy > 0 && totalDy < 0)
        {
            totalDy = 0;
            scrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;
        }
        else if(dy < 0 && totalDy > 0)
        {
            totalDy = 0;
            scrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN;
        }
        totalDy += dy;
        onDirectionNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, scrollDirection);
    }


    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, float velocityX, float velocityY, boolean consumed)
    {
        super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
        scrollDirection = velocityY > 0 ? ScrollDirection.SCROLL_DIRECTION_UP : ScrollDirection.SCROLL_DIRECTION_DOWN;
        return onNestedDirectionFling(coordinatorLayout, child, target, velocityX, velocityY, scrollDirection);
    }


    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, float velocityX, float velocityY)
    {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }


    @NonNull
    @Override
    public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout coordinatorLayout, V child, WindowInsetsCompat insets)
    {
        return super.onApplyWindowInsets(coordinatorLayout, child, insets);
    }


    /*
       @return Over-scroll direction: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
   */
    @ScrollDirection
    int getOverScrollDirection()
    {
        return overScrollDirection;
    }


    /**
     * @return Scroll direction: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
     */

    @ScrollDirection
    int getScrollDirection()
    {
        return scrollDirection;
    }
}