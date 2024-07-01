package com.theathletic.presenter

/**
 * Marker interface that indicates implementing classes will handle interactions
 *
 * e.g.
 *   interface ArticleInteractor : Interactor {
 *        fun onCommentsClick()
 *        fun onRateArticle()
 *   }
 *
 *   class ArticlePresenter: ArticleInteractor {
 *        override fun onCommentsClick()...
 *   }
 */
interface Interactor