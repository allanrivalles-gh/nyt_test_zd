package com.theathletic.widget.snaphelper;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;


@SuppressWarnings("unused")
public class FeedRecyclerCarouselViewSnapHelper extends LinearSnapHelper
{
    private final SnapListener mSnapListener = null;
    private RecyclerView mRecyclerView;
    private View mLastView;
    private Boolean mIsLastItem = false;
    private Boolean mInterruptNextCalculation = false;
    private int mLastPosition = 0;


    public interface SnapListener
    {
        void onSnapFinished();
    }


    public FeedRecyclerCarouselViewSnapHelper(RecyclerView recycler)
    {
        mRecyclerView = recycler;
        recycler.setOnFlingListener(null);
        attachToRecyclerView(recycler);
    }


    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY)
    {
        int targetPosition;
        if(velocityX < 0)
        {
            targetPosition = mLastPosition - 1;
        }
        else
        {
            targetPosition = mLastPosition + 1;
        }

        final int firstItem = 0;
        final int lastItem = layoutManager.getItemCount() - 1;
        targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
        mLastPosition = targetPosition;
        mIsLastItem = targetPosition == lastItem;
        return targetPosition;
    }


    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView)
    {
        if(mInterruptNextCalculation)
        {
            mInterruptNextCalculation = false;
            return new int[]{0, 0};            // won't snap to any axis
        }

        boolean childBiggerThanParent = layoutManager.getWidth() < targetView.getWidth();
        // handles bigger views than parent
        if(mLastView == targetView && childBiggerThanParent && !mIsLastItem)
        {
            return new int[]{0, 0};            // won't snap to any axis
        }
        mLastView = targetView;

        // handles toolbar shift
        int[] out = super.calculateDistanceToFinalSnap(layoutManager, targetView);
        int sizeDiff = layoutManager.getWidth() - targetView.getWidth();
        if(sizeDiff > layoutManager.getWidth() / 2 && mIsLastItem)
            mInterruptNextCalculation = true;

        if(out != null)
        {
            out[1] = out[1] - (int) (sizeDiff * 0.5);
        }

        return out;
    }


    @Override
    @SuppressWarnings("unused")
    public View findSnapView(RecyclerView.LayoutManager layoutManager)
    {
        return mLastView;
    }


    public int getPosition()
    {
        return mLastPosition;
    }


    public void setPosition(int position)
    {
        mLastPosition = position;
    }


    public void snap()
    {
        mRecyclerView.post(() -> {
                View view = mRecyclerView.getLayoutManager().findViewByPosition(mLastPosition);
                if(view == null)
                {
                    Timber.e("Cant find target View for initial Snap");
                    return;
                }

                int[] snapDistance = calculateDistanceToFinalSnap(mRecyclerView.getLayoutManager(), view);
                if((snapDistance != null ? snapDistance[0] : 0) != 0 || (snapDistance != null ? snapDistance[1] : 0) != 0)
                {
                    mRecyclerView.scrollBy(snapDistance[0], snapDistance[1]);
                }
            }
        );
    }
}