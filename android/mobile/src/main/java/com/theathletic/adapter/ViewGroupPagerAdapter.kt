package com.theathletic.adapter

import android.view.View
import android.view.ViewGroup
import java.util.ArrayList

class ViewGroupPagerAdapter : androidx.viewpager.widget.PagerAdapter {
    private val views = ArrayList<View>()
    private val titles = ArrayList<String>()

    constructor(viewGroup: ViewGroup) {
        while (viewGroup.childCount > 0) {
            views.add(viewGroup.getChildAt(0))
            viewGroup.removeViewAt(0)
        }
    }

    constructor(viewGroup: ViewGroup, titles: Array<String>) {
        while (viewGroup.childCount > 0) {
            views.add(viewGroup.getChildAt(0))
            viewGroup.removeViewAt(0)
        }
        this.titles.addAll(titles)
    }

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        val view = views[position]
        val lp = androidx.viewpager.widget.ViewPager.LayoutParams()
        lp.width = androidx.viewpager.widget.ViewPager.LayoutParams.MATCH_PARENT
        lp.height = androidx.viewpager.widget.ViewPager.LayoutParams.MATCH_PARENT
        if (view.parent != null)
            (view.parent as ViewGroup).removeView(view)
        view.layoutParams = lp
        parent.addView(view)
        return view
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        parent.removeView(view)
    }

    override fun getCount(): Int {
        return views.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position < titles.size) titles[position] else ""
    }
}