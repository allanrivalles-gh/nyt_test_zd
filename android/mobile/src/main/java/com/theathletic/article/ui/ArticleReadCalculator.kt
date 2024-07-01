package com.theathletic.article.ui

import androidx.core.widget.NestedScrollView
import com.theathletic.ads.articles.AdsScrollBehavior
import kotlin.math.max
import kotlin.math.min
class ArticleReadCalculator(
    private val onArticleReadListener: OnArticleReadListener,
    private val adScrollBehavior: AdsScrollBehavior,
    private val articleScrollPercentToConsiderRead: Int
) : NestedScrollView.OnScrollChangeListener {

    companion object {
        // Consider article is completely read if user reaches to 95 percent of the content
        const val ARTICLE_SCROLL_PERCENT_TO_CONSIDER_COMPLETED = 95
    }

    private var isMarkedAsRead = false
    private var maxScrollDepth = 0

    private var topperItemsSize = mutableMapOf<String, Int>()
    private var screenSize = 0
    var articleSize = 0

    private var currentScrollPosition = 0
    var articleMaxReadPercent = 0
    var articleCurrentScrollPercent = 0
    var isMarkAsCompleted = false

    fun updateTopperSize(id: String, size: Int) {
        if (size > 0) {
            topperItemsSize[id] = size
        }
    }

    fun convertLastScrollPercentageToPosition(percentage: Int): Int {
        return (articleSize * (percentage / 100.0f)).toInt()
    }

    override fun onScrollChange(
        scrollView: NestedScrollView,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        scrollView.let { screenSize = it.height }

        currentScrollPosition = scrollY
        maxScrollDepth = max(maxScrollDepth, currentScrollPosition)
        calculateCurrentAndMaxPercentRead().let {
            articleCurrentScrollPercent = it.first
            articleMaxReadPercent = it.second
        }

        if (articleMaxReadPercent > articleScrollPercentToConsiderRead && !isMarkedAsRead) {
            onArticleReadListener.onArticleRead()
            isMarkedAsRead = true
        }

        if (articleCurrentScrollPercent >= ARTICLE_SCROLL_PERCENT_TO_CONSIDER_COMPLETED && !isMarkAsCompleted) {
            onArticleReadListener.onArticleCompleted()
            isMarkAsCompleted = true
        }
    }

    private fun calculateCurrentAndMaxPercentRead(): Pair<Int, Int> {
        if (articleSize == 0) return Pair(0, 0)

        val topperSize = topperItemsSize.map { it.value }.sum()
        val articleScrollDepth = maxScrollDepth - topperSize + screenSize
        adScrollBehavior.onScrolled(articleScrollDepth)
        val percentRead = ((articleScrollDepth.toFloat() / articleSize) * 100).toInt()
        val roundedPercentRead = percentRead - percentRead % 5
        // current scroll percentage and max read percentage of the article
        return Pair(min(100, ((currentScrollPosition.toFloat() / articleSize) * 100).toInt()), min(100, max(0, roundedPercentRead)))
    }

    interface OnArticleReadListener {
        fun onArticleRead()
        fun onArticleCompleted()
    }
}