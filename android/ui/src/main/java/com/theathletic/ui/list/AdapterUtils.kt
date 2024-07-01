package com.theathletic.ui.list

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.theathletic.ui.UiModel

inline fun <reified A> RecyclerView.bindData(
    data: List<UiModel>,
    adapterIfNull: () -> A
) where A : MutableDataAdapter,
        A : RecyclerView.Adapter<*> {
    var myAdapter = adapter as? A

    if (myAdapter == null) {
        myAdapter = adapterIfNull()
        adapter = myAdapter
    }

    myAdapter.updateData(data)
}

inline fun <reified A> ViewPager2.bindData(
    data: List<UiModel>,
    adapterIfNull: () -> A
) where A : MutableDataAdapter,
        A : RecyclerView.Adapter<*> {
    var myAdapter = adapter as? A

    if (myAdapter == null) {
        myAdapter = adapterIfNull()
        adapter = myAdapter
    }

    myAdapter.updateData(data)
}