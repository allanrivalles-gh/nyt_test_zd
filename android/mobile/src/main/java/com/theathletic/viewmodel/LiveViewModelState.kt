package com.theathletic.viewmodel

import com.theathletic.ui.list.LiveStateAdapter

/**
 * When a view that has data that updates frequently (e.g. podcast playback percentage) or has a
 * value that should be animated (e.g. podcast download) we don't want to rerender an entire list
 * for the [RecyclerView] for each update. So instead a [ViewModel] can define a live state that
 * updates frequently which we can bind to in XML. Use this with [LiveStateAdapter] to support list
 * items which can read values from this.
 */
interface LiveViewModelState