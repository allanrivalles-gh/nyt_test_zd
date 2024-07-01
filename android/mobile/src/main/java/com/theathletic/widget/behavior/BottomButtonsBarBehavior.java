package com.theathletic.widget.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.theathletic.AthleticApplication;
import com.theathletic.R;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;


public class BottomButtonsBarBehavior extends VerticalScrollingBehavior<View>
{
    private static final Interpolator INTERPOLATOR = new LinearOutSlowInInterpolator();
    private static final Integer SCROLL_THRESHOLD = AthleticApplication.getContext().getResources().getDimensionPixelSize(R.dimen.global_spacing_16);
    private final int defaultOffset;
    private boolean hidden = false;
    private ViewPropertyAnimatorCompat mTranslationAnimator;
    private int lastChangePosition = 0;
    private int lastScrollDirection = -1;


    public BottomButtonsBarBehavior()
    {
        super();
        this.defaultOffset = 0;
    }


    public BottomButtonsBarBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.defaultOffset = 0;
    }


    @Override
    void onNestedVerticalOverScroll(CoordinatorLayout coordinatorLayout, View child, @ScrollDirection int direction, int currentOverScroll, int totalOverScroll)
    {
    }


    @Override
    void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed, @ScrollDirection int scrollDirection)
    {
        if(target instanceof RecyclerView && isRecyclerVerticallyScrollable((RecyclerView) target))
            handleDirection(child, scrollDirection, target.getScrollY());
        else if(!(target instanceof RecyclerView))
            handleDirection(child, scrollDirection, target.getScrollY());
    }


    @Override
    boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection)
    {
        if(velocityY < 1000 && velocityY > -1000)
            return true;

        if(target instanceof RecyclerView && isRecyclerVerticallyScrollable((RecyclerView) target))
            handleDirection(child, scrollDirection);
        else if(!(target instanceof RecyclerView))
            handleDirection(child, scrollDirection);
        return true;
    }


    private boolean isRecyclerVerticallyScrollable(RecyclerView recyclerView)
    {
        return recyclerView.computeVerticalScrollRange() > recyclerView.getHeight();
    }


    private void handleDirection(View child, int scrollDirection, int y)
    {
        if(scrollDirection != lastScrollDirection)
        {
            lastScrollDirection = scrollDirection;
            lastChangePosition = y;
        }

        if(scrollDirection == ScrollDirection.SCROLL_DIRECTION_DOWN && hidden && y + SCROLL_THRESHOLD < lastChangePosition)
        {
            hidden = false;
            animateOffset(child, defaultOffset);
        }
        else if(scrollDirection == ScrollDirection.SCROLL_DIRECTION_UP && !hidden && y - SCROLL_THRESHOLD > lastChangePosition)
        {
            hidden = true;
            animateOffset(child, child.getHeight() + defaultOffset);
        }
    }


    private void handleDirection(View child, int scrollDirection)
    {
        if(scrollDirection == ScrollDirection.SCROLL_DIRECTION_DOWN && hidden)
        {
            hidden = false;
            animateOffset(child, defaultOffset);
        }
        else if(scrollDirection == ScrollDirection.SCROLL_DIRECTION_UP && !hidden)
        {
            hidden = true;
            animateOffset(child, child.getHeight() + defaultOffset);
        }
    }


    private void animateOffset(final View child, final int offset)
    {
        ensureOrCancelAnimator(child);
        mTranslationAnimator.translationY(offset).start();
    }


    private void ensureOrCancelAnimator(View child)
    {
        if(mTranslationAnimator == null)
        {
            mTranslationAnimator = ViewCompat.animate(child);
            mTranslationAnimator.setDuration(300);
            mTranslationAnimator.setInterpolator(INTERPOLATOR);
        }
        else
        {
            mTranslationAnimator.cancel();
        }
    }
}