package com.theathletic.ui

enum class LoadingState {
    INITIAL_LOADING,
    RELOADING,
    FINISHED,
    LOADING_MORE,
    NONE;

    val isFreshLoadingState: Boolean get() = this == INITIAL_LOADING || this == RELOADING
}