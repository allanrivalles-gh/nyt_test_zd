package com.theathletic.entity.article

val ArticleEntity.isArticlePost: Boolean get() = postTypeId == 1L
val ArticleEntity.isHeadlinePost: Boolean get() = postTypeId == 39L
val ArticleEntity.isDiscussionPost: Boolean get() = postTypeId == 29L
val ArticleEntity.isQAndAPost: Boolean get() = postTypeId == 31L

val ArticleEntity.isLeadPost: Boolean get() = postTypeId == 26L
val ArticleEntity.isExclusivePost: Boolean get() = postTypeId == 26L
val ArticleEntity.isFeaturedPost: Boolean get() = postTypeId == 26L