package com.theathletic.twitter.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryStaticLocalDataSource

class TwitterLocalDataSource @AutoKoin(Scope.SINGLE) constructor() :
    InMemoryStaticLocalDataSource<TwitterLocalDataSource.Key, TwitterUrl>() {
    data class Key(val url: String, val theme: String)
}