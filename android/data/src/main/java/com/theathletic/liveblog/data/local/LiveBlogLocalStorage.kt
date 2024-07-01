package com.theathletic.liveblog.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource

class LiveBlogLocalStorage @AutoKoin(Scope.SINGLE) constructor() :
    InMemoryLocalDataSource<String, NativeLiveBlog>()