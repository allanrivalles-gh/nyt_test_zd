package com.theathletic.article.ui

import android.content.Context
import android.content.Intent
import com.theathletic.activity.SingleFragmentActivity
import com.theathletic.fragment.AthleticFragment

class ArticleActivity : SingleFragmentActivity() {

    companion object {
        const val EXTRA_ARTICLE_ID = "article_id"
        const val EXTRA_SOURCE = "source"

        fun newIntent(
            context: Context,
            articleId: Long,
            source: String
        ): Intent {
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(EXTRA_ARTICLE_ID, articleId)
            intent.putExtra(EXTRA_SOURCE, source)
            return intent
        }
    }

    override fun getFragment(): AthleticFragment {
        val articleId = intent.getLongExtra(EXTRA_ARTICLE_ID, -1)
        val source = intent.getStringExtra(EXTRA_SOURCE) ?: "Unknown"
        crashLogHandler.setCurrentDataIdKey("ARTICLE_$articleId")

        return ArticleFragment.newInstance(articleId, source)
    }

    override fun shouldScreenBePortraitOnly() = false
}