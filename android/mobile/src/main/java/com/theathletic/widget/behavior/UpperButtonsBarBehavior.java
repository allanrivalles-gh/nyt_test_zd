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


public class UpperButtonsBarBehavior extends VerticalScrollingBehavior<View>
{
    private static final Interpolator INTERPOLATOR = new LinearOutSlowInInterpolator();
    private final int defaultOffset;
    private final int bottomNavHeight;
    private boolean hidden = false;
    private ViewPropertyAnimatorCompat mTranslationAnimator;


    public UpperButtonsBarBehavior()
    {
        super();
        this.bottomNavHeight = AthleticApplication.getContext().getResources().getDimensionPixelSize(R.dimen.global_spacing_72);
        this.defaultOffset = 0;
    }


    public UpperButtonsBarBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.bottomNavHeight = AthleticApplication.getContext().getResources().getDimensionPixelSize(R.dimen.global_spacing_72);
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
            handleDirection(child, target, scrollDirection);
        else if(!(target instanceof RecyclerView))
            handleDirection(child, target, scrollDirection);
    }


    @Override
    boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection)
    {
        if(target instanceof RecyclerView && isRecyclerVerticallyScrollable((RecyclerView) target))
            handleDirection(child, target, scrollDirection);
        else if(!(target instanceof RecyclerView))
            handleDirection(child, target, scrollDirection);
        return true;
    }


    private boolean isRecyclerVerticallyScrollable(RecyclerView recyclerView)
    {
        return recyclerView.computeVerticalScrollRange() > recyclerView.getHeight();
    }


    private void handleDirection(View child, View target, int scrollDirection)
    {
        if(scrollDirection == ScrollDirection.SCROLL_DIRECTION_DOWN && hidden)
        {
            hidden = false;
            animateOffset(child, defaultOffset);
        }
        else if(scrollDirection == ScrollDirection.SCROLL_DIRECTION_UP && !hidden && target.getScrollY() > AthleticApplication.getContext().getResources().getDimensionPixelSize(R.dimen.global_spacing_88))
        {
            hidden = true;
            animateOffset(child, bottomNavHeight + defaultOffset);
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