package com.theathletic.injection

import com.theathletic.data.local.AppDatabase
import com.theathletic.data.local.FeedDatabase
import com.theathletic.debugtools.DebugToolsDatabase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val databaseModule = module {
    single { DebugToolsDatabase.newInstance(get(named("application-context"))) }
    single { get<DebugToolsDatabase>().debugToolsDao() }

    single { FeedDatabase.newInstance(get(named("application-context"))) }
    single { get<FeedDatabase>().feedDao() }

    single { AppDatabase.newInstance(get(named("application-context"))) }
    single { get<AppDatabase>().savedStoriesDao() }
    single { get<AppDatabase>().podcastDao() }
    single { get<AppDatabase>().userTopicsDao() }
    single { get<AppDatabase>().userDataDao() }
    single { get<AppDatabase>().navigationDao() }
    single { get<AppDatabase>().entityDao() }
    single { get<AppDatabase>().entityQueryDao() }
    single { get<AppDatabase>().purchaseDataDao() }
    single { get<AppDatabase>().followableDao() }
    single { get<AppDatabase>().userFollowingDao() }

    factory { get<AppDatabase>().getTeamDao() }
    factory { get<AppDatabase>().getLeagueDao() }
    factory { get<AppDatabase>().getAuthorDao() }

    factory { get<AppDatabase>().getFollowableNotificationDao() }
}