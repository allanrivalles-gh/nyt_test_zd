package com.theathletic.comments.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.data.QandaComment
import com.theathletic.data.local.InMemoryLocalDataSource

class QandaCommentsLocalDataStore @AutoKoin(Scope.SINGLE) constructor() :
    InMemoryLocalDataSource<String, QandaComment>()