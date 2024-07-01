package com.theathletic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.theathletic.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


// inspired by: https://github.com/jakubkinst/Android-StatefulView
public class SearchStatefulLayout extends FrameLayout
{
    public static final int CONTENT = 0;
    public static final int PROGRESS = 1;
    public static final int OFFLINE = 2;
    public static final int AUTHOR_EMPTY = 3;
    public static final int TEAM_EMPTY = 4;
    public static final int LEAGUE_EMPTY = 5;
    public static final int NOT_FOUND = 6;

    private static final String SAVED_STATE = "stateful_layout_state";

    @State private int mInitialState;
    private int mProgressLayoutId;
    private int mOfflineLayoutId;
    private int mAuthorEmptyLayoutId;
    private int mTeamEmptyLayoutId;
    private int mLeagueEmptyLayoutId;
    private int mNotFoundLayoutId;
    private boolean mInvisibleWhenHidden;
    private List<View> mContentLayoutList;
    private View mProgressLayout;
    private View mOfflineLayout;
    private View mAuthorEmptyLayout;
    private View mTeamEmptyLayout;
    private View mLeagueEmptyLayout;
    private View mNotFoundLayout;
    @State private int mState;
    private OnStateChangeListener mOnStateChangeListener;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CONTENT, PROGRESS, OFFLINE, AUTHOR_EMPTY, TEAM_EMPTY, NOT_FOUND})
    public @interface State {}


    public interface OnStateChangeListener
    {
        void onStateChange(View view, @State int state);
    }


    public SearchStatefulLayout(@NonNull Context context)
    {
        this(context, null);
    }


    public SearchStatefulLayout(@NonNull Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }


    public SearchStatefulLayout(@NonNull Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchStatefulLayout);

        if(typedArray.hasValue(R.styleable.SearchStatefulLayout_searchState))
        {
            // noinspection ResourceType
            mInitialState = typedArray.getInt(R.styleable.SearchStatefulLayout_searchState, CONTENT);
        }

        if(typedArray.hasValue(R.styleable.SearchStatefulLayout_searchProgressLayout) &&
            typedArray.hasValue(R.styleable.SearchStatefulLayout_searchOfflineLayout) &&
            typedArray.hasValue(R.styleable.SearchStatefulLayout_searchAuthorEmptyLayout) &&
            typedArray.hasValue(R.styleable.SearchStatefulLayout_searchTeamEmptyLayout) &&
            typedArray.hasValue(R.styleable.SearchStatefulLayout_searchLeagueEmptyLayout) &&
            typedArray.hasValue(R.styleable.SearchStatefulLayout_searchNotFoundLayout))
        {
            mProgressLayoutId = typedArray.getResourceId(R.styleable.SearchStatefulLayout_searchProgressLayout, 0);
            mOfflineLayoutId = typedArray.getResourceId(R.styleable.SearchStatefulLayout_searchOfflineLayout, 0);
            mAuthorEmptyLayoutId = typedArray.getResourceId(R.styleable.SearchStatefulLayout_searchAuthorEmptyLayout, 0);
            mTeamEmptyLayoutId = typedArray.getResourceId(R.styleable.SearchStatefulLayout_searchTeamEmptyLayout, 0);
            mLeagueEmptyLayoutId = typedArray.getResourceId(R.styleable.SearchStatefulLayout_searchLeagueEmptyLayout, 0);
            mNotFoundLayoutId = typedArray.getResourceId(R.styleable.SearchStatefulLayout_searchNotFoundLayout, 0);
        }
        else
        {
            throw new IllegalArgumentException("Attributes progressLayout, offlineLayout and emptyLayout are mandatory");
        }

        mInvisibleWhenHidden = typedArray.getBoolean(R.styleable.SearchStatefulLayout_searchInvisibleWhenHidden, false);

        typedArray.recycle();
    }


    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        setupView();
    }


    public void showContent()
    {
        setState(CONTENT);
    }


    public void showProgress()
    {
        setState(PROGRESS);
    }


    public void showOffline()
    {
        setState(OFFLINE);
    }


    public void showAuthorEmpty()
    {
        setState(AUTHOR_EMPTY);
    }


    public void showTeamEmpty()
    {
        setState(TEAM_EMPTY);
    }


    public void showNotFound()
    {
        setState(NOT_FOUND);
    }


    @State
    public int getState()
    {
        return mState;
    }


    @SuppressWarnings("ResourceType")
    public void setState(@State int state)
    {
        mState = state;

        for(int i = 0; i < mContentLayoutList.size(); i++)
        {
            mContentLayoutList.get(i).setVisibility(determineVisibility(state == CONTENT));
        }

        mProgressLayout.setVisibility(determineVisibility(state == PROGRESS));
        mOfflineLayout.setVisibility(determineVisibility(state == OFFLINE));
        mAuthorEmptyLayout.setVisibility(determineVisibility(state == AUTHOR_EMPTY));
        mTeamEmptyLayout.setVisibility(determineVisibility(state == TEAM_EMPTY));
        mLeagueEmptyLayout.setVisibility(determineVisibility(state == LEAGUE_EMPTY));
        mNotFoundLayout.setVisibility(determineVisibility(state == NOT_FOUND));

        if(mOnStateChangeListener != null) mOnStateChangeListener.onStateChange(this, state);
    }


    public void setOnStateChangeListener(SearchStatefulLayout.OnStateChangeListener l)
    {
        mOnStateChangeListener = l;
    }


    public void saveInstanceState(@NonNull Bundle outState)
    {
        outState.putInt(SAVED_STATE, mState);
    }


    @State
    public int restoreInstanceState(@Nullable Bundle savedInstanceState)
    {
        @State int state = CONTENT;
        if(savedInstanceState != null && savedInstanceState.containsKey(SAVED_STATE))
        {
            // noinspection ResourceType
            state = savedInstanceState.getInt(SAVED_STATE);
            setState(state);
        }
        return state;
    }


    private void setupView()
    {
        if(mContentLayoutList == null && !isInEditMode())
        {
            mContentLayoutList = new ArrayList<>();
            for(int i = 0; i < getChildCount(); i++)
            {
                mContentLayoutList.add(getChildAt(i));
            }

            mProgressLayout = LayoutInflater.from(getContext()).inflate(mProgressLayoutId, this, false);
            mOfflineLayout = LayoutInflater.from(getContext()).inflate(mOfflineLayoutId, this, false);
            mAuthorEmptyLayout = LayoutInflater.from(getContext()).inflate(mAuthorEmptyLayoutId, this, false);
            mTeamEmptyLayout = LayoutInflater.from(getContext()).inflate(mTeamEmptyLayoutId, this, false);
            mLeagueEmptyLayout = LayoutInflater.from(getContext()).inflate(mLeagueEmptyLayoutId, this, false);
            mNotFoundLayout = LayoutInflater.from(getContext()).inflate(mNotFoundLayoutId, this, false);

            addView(mProgressLayout);
            addView(mOfflineLayout);
            addView(mAuthorEmptyLayout);
            addView(mTeamEmptyLayout);
            addView(mLeagueEmptyLayout);
            addView(mNotFoundLayout);

            setState(mInitialState);
        }
    }


    private int determineVisibility(boolean visible)
    {
        if(visible)
        {
            return View.VISIBLE;
        }
        else
        {
            if(mInvisibleWhenHidden)
            {
                return View.INVISIBLE;
            }
            else
            {
                return View.GONE;
            }
        }
    }
}
