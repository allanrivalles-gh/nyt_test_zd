package com.theathletic.main.ui

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SecondaryNavigationAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    var primaryNavigationItem: PrimaryNavigationItem? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemId(position: Int) = items[position].stableId.hashCode().toLong()

    override fun containsItem(itemId: Long) =
        items.find { itemId == it.stableId.hashCode().toLong() } != null

    val tabCount get() = primaryNavigationItem?.tabCount ?: items.size

    override fun getItemCount(): Int = primaryNavigationItem?.fragmentCount ?: items.size

    override fun createFragment(position: Int) = primaryNavigationItem!!.createFragment(position)

    private val items get() = primaryNavigationItem?.secondaryNavigationItems?.value ?: emptyList()
}