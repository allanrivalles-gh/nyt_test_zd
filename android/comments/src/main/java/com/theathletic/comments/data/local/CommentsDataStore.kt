package com.theathletic.comments.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.data.CommentsFeed
import com.theathletic.data.local.InMemoryLocalDataSource

class CommentsDataStore @AutoKoin(Scope.SINGLE) constructor() : InMemoryLocalDataSource<String, CommentsFeed>()