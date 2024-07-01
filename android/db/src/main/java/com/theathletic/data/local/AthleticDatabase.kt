package com.theathletic.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.theathletic.billing.PurchaseDataDao
import com.theathletic.billing.PurchaseDataEntity
import com.theathletic.datetime.Datetime
import com.theathletic.entity.SavedStoriesEntity
import com.theathletic.entity.TopicTagEntity
import com.theathletic.entity.authentication.UserData
import com.theathletic.entity.discussions.CommentEntity
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.local.FollowedEntity
import com.theathletic.entity.local.SavedEntity
import com.theathletic.entity.local.SerializedEntity
import com.theathletic.entity.local.SerializedEntityDao
import com.theathletic.entity.local.SerializedEntityQueryDao
import com.theathletic.entity.main.FeedItemEntryType
import com.theathletic.entity.main.PodcastEpisodeDetailStoryItem
import com.theathletic.entity.main.PodcastEpisodeDetailTrackItem
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.PodcastFeed
import com.theathletic.entity.main.PodcastItem
import com.theathletic.entity.main.PodcastLeagueFeed
import com.theathletic.entity.main.PodcastTopic
import com.theathletic.entity.settings.UserTopicsItemAuthor
import com.theathletic.entity.settings.UserTopicsItemLeague
import com.theathletic.entity.settings.UserTopicsItemPodcast
import com.theathletic.entity.settings.UserTopicsItemTeam
import com.theathletic.followable.AuthorDao
import com.theathletic.followable.Followable
import com.theathletic.followable.LeagueDao
import com.theathletic.followable.TeamDao
import com.theathletic.navigation.data.local.NavigationDao
import com.theathletic.navigation.data.local.RoomNavigationEntity
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.repository.savedstories.SavedStoriesDao
import com.theathletic.repository.user.AuthorLocal
import com.theathletic.repository.user.FollowableDao
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.repository.user.TeamLocal
import com.theathletic.repository.user.UserDataDao
import com.theathletic.repository.user.UserFollowingDao
import com.theathletic.repository.user.UserFollowingItem
import com.theathletic.repository.user.UserTopicsDao
import com.theathletic.user.FollowableNotificationSettingsDao
import com.theathletic.user.LocalFollowableNotificationSettings
import com.theathletic.utility.safeValueOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Database(
    entities = [
        UserTopicsItemTeam::class,
        UserTopicsItemLeague::class,
        UserTopicsItemAuthor::class,
        UserTopicsItemPodcast::class,
        PodcastFeed::class,
        PodcastLeagueFeed::class,
        PodcastItem::class,
        PodcastEpisodeItem::class,
        SavedStoriesEntity::class,
        UserData::class,
        RoomNavigationEntity::class,
        SerializedEntity::class,
        FollowedEntity::class,
        SavedEntity::class,
        PurchaseDataEntity::class,
        LeagueLocal::class,
        TeamLocal::class,
        AuthorLocal::class,
        UserFollowingItem::class,
        LocalFollowableNotificationSettings::class
    ],
    version = 49,
    exportSchema = true
)
@TypeConverters(AthleticDatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun savedStoriesDao(): SavedStoriesDao
    abstract fun podcastDao(): PodcastDao
    abstract fun userTopicsDao(): UserTopicsDao
    abstract fun userDataDao(): UserDataDao
    abstract fun navigationDao(): NavigationDao
    abstract fun entityDao(): SerializedEntityDao
    abstract fun entityQueryDao(): SerializedEntityQueryDao
    abstract fun purchaseDataDao(): PurchaseDataDao
    abstract fun followableDao(): FollowableDao

    abstract fun userFollowingDao(): UserFollowingDao
    abstract fun getTeamDao(): TeamDao
    abstract fun getLeagueDao(): LeagueDao
    abstract fun getAuthorDao(): AuthorDao

    abstract fun getFollowableNotificationDao(): FollowableNotificationSettingsDao

    companion object {
        fun newInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "athletic-database-v2"
            )
                .addMigrations(AppDatabaseMigrations.Migration1To2())
                .addMigrations(AppDatabaseMigrations.Migration2To3())
                .addMigrations(AppDatabaseMigrations.Migration3To4())
                .addMigrations(AppDatabaseMigrations.Migration4To5())
                .addMigrations(AppDatabaseMigrations.Migration5To6())
                .addMigrations(AppDatabaseMigrations.Migration6To7())
                .addMigrations(AppDatabaseMigrations.Migration7To8())
                .addMigrations(AppDatabaseMigrations.Migration8To9())
                .addMigrations(AppDatabaseMigrations.Migration9To10())
                .addMigrations(AppDatabaseMigrations.Migration10To11())
                .addMigrations(AppDatabaseMigrations.Migration11To12())
                .addMigrations(AppDatabaseMigrations.Migration12To13())
                .addMigrations(AppDatabaseMigrations.Migration13To14())
                .addMigrations(AppDatabaseMigrations.Migration14To15())
                .addMigrations(AppDatabaseMigrations.Migration15To16())
                .addMigrations(AppDatabaseMigrations.Migration16To17())
                .addMigrations(AppDatabaseMigrations.Migration17To18())
                .addMigrations(AppDatabaseMigrations.Migration18To19())
                .addMigrations(AppDatabaseMigrations.Migration19To20())
                .addMigrations(AppDatabaseMigrations.Migration20To21())
                .addMigrations(AppDatabaseMigrations.Migration21To22())
                .addMigrations(AppDatabaseMigrations.Migration22To23())
                .addMigrations(AppDatabaseMigrations.Migration23To24())
                .addMigrations(AppDatabaseMigrations.Migration24To25())
                .addMigrations(AppDatabaseMigrations.Migration25To26())
                .addMigrations(AppDatabaseMigrations.Migration26To27())
                .addMigrations(AppDatabaseMigrations.Migration27To28())
                .addMigrations(AppDatabaseMigrations.Migration28To29())
                .addMigrations(AppDatabaseMigrations.Migration29To30())
                .addMigrations(AppDatabaseMigrations.Migration30To31())
                .addMigrations(AppDatabaseMigrations.Migration31To32())
                .addMigrations(AppDatabaseMigrations.Migration32To33())
                .addMigrations(AppDatabaseMigrations.Migration33To34())
                .addMigrations(AppDatabaseMigrations.Migration34To35())
                .addMigrations(AppDatabaseMigrations.Migration35To36())
                .addMigrations(AppDatabaseMigrations.Migration36To37())
                .addMigrations(AppDatabaseMigrations.Migration37To38())
                .addMigrations(AppDatabaseMigrations.Migration38To39())
                .addMigrations(AppDatabaseMigrations.Migration39To40())
                .addMigrations(AppDatabaseMigrations.Migration40To41())
                .addMigrations(AppDatabaseMigrations.Migration41To42())
                .addMigrations(AppDatabaseMigrations.Migration42To43())
                .addMigrations(AppDatabaseMigrations.Migration43To44())
                .addMigrations(AppDatabaseMigrations.Migration44To45())
                .addMigrations(AppDatabaseMigrations.Migration45To46())
                .addMigrations(AppDatabaseMigrations.Migration46To47())
                .addMigrations(AppDatabaseMigrations.Migration47To48())
                .addMigrations(AppDatabaseMigrations.Migration48To49())
                .build()
        }
    }
}

private object AppDatabaseMigrations {
    class Migration1To2 : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Add column to Podcast Feed table
            database.execSQL("ALTER TABLE `podcast_feed` ADD `featuredPodcasts` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration2To3 : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Add table for referral data
            database.execSQL("CREATE TABLE IF NOT EXISTS `referral_data` (`id` INTEGER NOT NULL, `referralLink` TEXT NOT NULL, `headline` TEXT NOT NULL, `entryPoint` TEXT NOT NULL, `footerOffer` TEXT NOT NULL, `shareText` TEXT NOT NULL, `footer` TEXT NOT NULL, PRIMARY KEY(`id`))")
        }
    }

    class Migration3To4 : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Add table for referral data
            database.execSQL("DROP TABLE IF EXISTS `podcast_league_feed`")
            database.execSQL("CREATE TABLE IF NOT EXISTS `podcast_league_feed` (`id` INTEGER NOT NULL, `national` TEXT NOT NULL, `teams` TEXT NOT NULL, PRIMARY KEY(`id`))")
        }
    }

    class Migration4To5 : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Add permalink column into article table
            database.execSQL("ALTER TABLE `feed_article` ADD `permalink` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration5To6 : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Add rewardString and percentOffString into referral_data
            database.execSQL("ALTER TABLE `referral_data` ADD `rewardString` TEXT NOT NULL DEFAULT \"\"")
            database.execSQL("ALTER TABLE `referral_data` ADD `percentOffString` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration6To7 : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Add tracks column
            database.execSQL("ALTER TABLE `podcast_episode` ADD `tracks` TEXT NOT NULL DEFAULT \"\"")
            database.execSQL("ALTER TABLE `podcast_episode` ADD `stories` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration7To8 : Migration(7, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Add commentsDisabled column
            database.execSQL("ALTER TABLE `feed_article` ADD `commentsDisabled` TEXT NOT NULL DEFAULT \"0\"")
            database.execSQL("ALTER TABLE `article_entity` ADD `commentsLocked` INTEGER NOT NULL DEFAULT 0")
        }
    }

    class Migration8To9 : Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Add commentsDisabled/locked and numberOfComments columns
            database.execSQL("ALTER TABLE `podcast_episode` ADD `commentsDisabled` INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE `podcast_episode` ADD `commentsLocked` INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE `podcast_episode` ADD `numberOfComments` INTEGER NOT NULL DEFAULT 0")
        }
    }

    class Migration9To10 : Migration(9, 10) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Add notifyStories to the author item
            database.execSQL("ALTER TABLE `user_topics_author` ADD `notifyStories` INTEGER NOT NULL DEFAULT 0")
        }
    }

    class Migration10To11 : Migration(10, 11) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Remove old FeedV1 tables
            database.execSQL("DROP TABLE IF EXISTS `feed_article`")
            database.execSQL("DROP TABLE IF EXISTS `feed_video`")
            database.execSQL("DROP TABLE IF EXISTS `feed_video_series`")
            database.execSQL("DROP TABLE IF EXISTS `feed_topic`")
            database.execSQL("DROP TABLE IF EXISTS `feed_live_discussion`")
            database.execSQL("DROP TABLE IF EXISTS `feed_category`")
            database.execSQL("DROP TABLE IF EXISTS `feed_game_recent`")
            database.execSQL("DROP TABLE IF EXISTS `feed_game_upcoming`")
            database.execSQL("DROP TABLE IF EXISTS `feed_podcast_episode`")
            database.execSQL("DROP TABLE IF EXISTS `feed_recommended_video_series`")
            database.execSQL("DROP TABLE IF EXISTS `feed_recommended_podcast_episode`")
            database.execSQL("DROP TABLE IF EXISTS `feed_recommended_podcast`")
            database.execSQL("DROP TABLE IF EXISTS `feed_post_team_ids`")
            database.execSQL("DROP TABLE IF EXISTS `feed_post_city_ids`")
            database.execSQL("DROP TABLE IF EXISTS `feed_post_league_ids`")
            database.execSQL("DROP TABLE IF EXISTS `feed_post_author_ids`")
            database.execSQL("DROP TABLE IF EXISTS `feed_post_category_ids`")
        }
    }

    class Migration11To12 : Migration(11, 12) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Adding shortname to the league topic
            database.execSQL("ALTER TABLE `user_topics_league` ADD `shortname` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration12To13 : Migration(12, 13) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Adding author image to the community live discussion model
            database.execSQL("ALTER TABLE `community_live_discussions` ADD `articleAuthorImage` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration13To14 : Migration(13, 14) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT Recreate the table with articleAuthorImage as nullable
            database.execSQL("DROP TABLE IF EXISTS `community_live_discussions`")
            database.execSQL("CREATE TABLE IF NOT EXISTS `community_live_discussions` (`articleTitle` TEXT NOT NULL, `articleExcerpt` TEXT, `articleAuthorName` TEXT, `articleAuthorImage` TEXT, `commentsCount` INTEGER, `backgroundColorHex` TEXT, `startTimeGmt` TEXT, `endTimeGmt` TEXT, `isReadByUser` INTEGER NOT NULL, `id` INTEGER NOT NULL, `entryType` TEXT NOT NULL, `entryDatetime` TEXT NOT NULL, `teamIds` TEXT NOT NULL, `cityIds` TEXT NOT NULL, `leagueIds` TEXT NOT NULL, `entityTags` TEXT NOT NULL, PRIMARY KEY(`id`))")
        }
    }

    class Migration14To15 : Migration(14, 15) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `navigation_entity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sourceKey` TEXT NOT NULL, `title` TEXT NOT NULL, `deeplinkUrl` TEXT NOT NULL, `entityType` TEXT NOT NULL, `index` INTEGER NOT NULL)")
        }
    }

    class Migration15To16 : Migration(15, 16) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `community_live_discussions` ADD COLUMN `articleAuthorTitle` TEXT DEFAULT \"\"")
        }
    }

    class Migration16To17 : Migration(16, 17) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // TT 1. Adding shortname to the league topic
            database.execSQL("ALTER TABLE `user_topics_author` ADD `shortname` TEXT DEFAULT \"\"")
        }
    }

    class Migration17To18 : Migration(17, 18) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE IF EXISTS `video_series`")
            database.execSQL("DROP TABLE IF EXISTS `video_episode`")
            database.execSQL("DROP TABLE IF EXISTS `video_feed_series`")
            database.execSQL("DROP TABLE IF EXISTS `video_series_summary`")
        }
    }

    class Migration18To19 : Migration(18, 19) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `article_entity` ADD `excerpt` TEXT DEFAULT \"\"")
        }
    }

    class Migration19To20 : Migration(19, 20) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `article_entity` ADD `authorDescription` TEXT DEFAULT \"\"")
        }
    }

    class Migration20To21 : Migration(20, 21) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE IF EXISTS `user_topics_city`")
        }
    }

    class Migration21To22 : Migration(21, 22) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `entity_interaction` (`entityType` TEXT NOT NULL, `entityId` TEXT NOT NULL, `interaction` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`entityType`, `entityId`, `interaction`))")
        }
    }

    class Migration22To23 : Migration(22, 23) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `serialized_entity` (`id` TEXT NOT NULL, `type` TEXT NOT NULL, `rawId` TEXT NOT NULL DEFAULT \"\", `jsonBlob` TEXT NOT NULL, `updatedTime` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`id`))")
        }
    }

    class Migration23To24 : Migration(23, 24) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE IF EXISTS `community_topic`")
            database.execSQL("DROP TABLE IF EXISTS `community_user_discussions`")
            database.execSQL("DROP TABLE IF EXISTS `community_live_discussions`")
        }
    }

    class Migration24To25 : Migration(24, 25) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE IF EXISTS `article_entity`")
        }
    }

    class Migration25To26 : Migration(25, 26) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `followed_entities` (`id` TEXT NOT NULL, `type` TEXT NOT NULL, `rawId` TEXT NOT NULL DEFAULT \"\", PRIMARY KEY(`id`))")
        }
    }

    class Migration26To27 : Migration(26, 27) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `saved_entities` (`id` TEXT NOT NULL, `type` TEXT NOT NULL, `rawId` TEXT NOT NULL DEFAULT \"\", PRIMARY KEY(`id`))")
        }
    }

    class Migration27To28 : Migration(27, 28) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE IF EXISTS `entity_interaction`")
        }
    }

    class Migration28To29 : Migration(28, 29) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `user_topics_team` ADD `graphqlId` TEXT")
        }
    }

    class Migration29To30 : Migration(29, 30) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE IF EXISTS `referral_data`")
        }
    }

    class Migration30To31 : Migration(30, 31) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `purchase_data` (`googleToken` TEXT NOT NULL, `userId` TEXT NOT NULL, `googleOrderId` TEXT NOT NULL, `price` REAL NOT NULL, `priceCurrency` TEXT NOT NULL, `planId` TEXT NOT NULL, `planTerm` TEXT, `lastArticleId` INTEGER, `lastPodcastId` INTEGER, `source` TEXT, `isSubPurchase` INTEGER NOT NULL, PRIMARY KEY(`googleToken`))")
        }
    }

    class Migration31To32 : Migration(31, 32) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `new_purchase_data` (`googleToken` TEXT NOT NULL, `userId` TEXT NOT NULL, `googleOrderId` TEXT NOT NULL, `price` REAL, `priceCurrency` TEXT, `planId` TEXT NOT NULL, `planTerm` TEXT, `lastArticleId` INTEGER, `lastPodcastId` INTEGER, `source` TEXT, `isSubPurchase` INTEGER NOT NULL, PRIMARY KEY(`googleToken`))")
            database.execSQL("INSERT INTO `new_purchase_data` SELECT `googleToken`, `userId`, `googleOrderId`, `price`, `priceCurrency`, `planId`, `planTerm`, `lastArticleId`, `lastPodcastId`, `source`, `isSubPurchase` from `purchase_data`")
            database.execSQL("DROP TABLE `purchase_data`")
            database.execSQL("ALTER TABLE `new_purchase_data` RENAME TO `purchase_data`")
        }
    }

    class Migration32To33 : Migration(32, 33) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `purchase_data` ADD `planNum` TEXT NOT NULL DEFAULT \"\"")
            database.execSQL("ALTER TABLE `purchase_data` ADD `productSku` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration33To34 : Migration(33, 34) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `team` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `shortName` TEXT NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `league` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `shortName` TEXT NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `author` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `shortName` TEXT NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `user_following` (`id` TEXT NOT NULL, PRIMARY KEY(`id`))")
        }
    }

    class Migration34To35 : Migration(34, 35) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `author` ADD `imageUrl` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration35To36 : Migration(35, 36) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `author` ADD `searchText` TEXT NOT NULL DEFAULT \"\"")
            database.execSQL("ALTER TABLE `team` ADD `searchText` TEXT NOT NULL DEFAULT \"\"")
            database.execSQL("ALTER TABLE `league` ADD `searchText` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration36To37 : Migration(36, 37) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `team` ADD `colorScheme` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration37To38 : Migration(37, 38) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `team` ADD `leagueId` TEXT NOT NULL DEFAULT \"\"")
            database.execSQL("ALTER TABLE `league` ADD `league` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration38To39 : Migration(38, 39) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `team` ADD `displayName` TEXT NOT NULL DEFAULT \"\"")
            database.execSQL("ALTER TABLE `team` ADD `graphqlId` TEXT DEFAULT \"\"")
        }
    }

    class Migration39To40 : Migration(39, 40) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `league` ADD `url` TEXT NOT NULL DEFAULT \"\"")
            database.execSQL("ALTER TABLE `league` ADD `sportType` TEXT ")
            database.execSQL("ALTER TABLE `team` ADD `url` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration40To41 : Migration(40, 41) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `author` ADD `url` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration41To42 : Migration(41, 42) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `league` ADD `hasScores` INTEGER NOT NULL DEFAULT(1)")
        }
    }

    class Migration42To43 : Migration(42, 43) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `league` ADD `displayName` TEXT NOT NULL DEFAULT \"\"")
        }
    }

    class Migration43To44 : Migration(43, 44) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `league` ADD COLUMN `hasActiveBracket` INTEGER DEFAULT 0 NOT NULL")
        }
    }

    class Migration44To45 : Migration(44, 45) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `followable_notification_settings` (`id` TEXT NOT NULL, `notifyStories` INTEGER NOT NULL DEFAULT(0), `notifyGames` INTEGER NOT NULL DEFAULT(0), PRIMARY KEY(`id`))")
        }
    }

    class Migration45To46 : Migration(45, 46) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `followable_notification_settings` ADD COLUMN `notifyGamesStart` INTEGER NOT NULL DEFAULT(0)")
        }
    }

    class Migration46To47 : Migration(46, 47) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create the new table
            database.execSQL(
                """
                CREATE TABLE `new_saved_stories` (
                    `id` TEXT PRIMARY KEY NOT NULL,
                    `postTitle` TEXT NOT NULL,
                    `authorName` TEXT NOT NULL,
                    `postDateGmt` TEXT NOT NULL,
                    `postImgUrl` TEXT,
                    `isReadByUser` INTEGER NOT NULL,
                    `commentsCount` INTEGER NOT NULL
                )
                """.trimIndent()
            )

            // Copy the data
            database.execSQL(
                """
                INSERT INTO `new_saved_stories` (`id`, `postTitle`, `authorName`, `postDateGmt`, `postImgUrl`, `isReadByUser`, `commentsCount`)
                SELECT `id`, `postTitle`, `authorName`, `postDateGmt`, `postImgUrl`, `isReadByUser`, 0 FROM `saved_stories`
                """.trimIndent()
            )

            // Remove the old table
            database.execSQL("DROP TABLE `saved_stories`")

            // Rename the new table to the old one
            database.execSQL("ALTER TABLE `new_saved_stories` RENAME TO `saved_stories`")
        }
    }

    class Migration47To48 : Migration(47, 48) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Drop unused columns googleOrderId and userId from purchase_data table.
            database.execSQL("CREATE TABLE IF NOT EXISTS `new_purchase_data` (`googleToken` TEXT NOT NULL, `price` REAL, `priceCurrency` TEXT, `planId` TEXT NOT NULL, `planTerm` TEXT, `lastArticleId` INTEGER, `lastPodcastId` INTEGER, `source` TEXT, `isSubPurchase` INTEGER NOT NULL, `planNum` TEXT NOT NULL DEFAULT \"\", `productSku` TEXT NOT NULL DEFAULT \"\", PRIMARY KEY(`googleToken`))")
            database.execSQL("INSERT INTO `new_purchase_data` SELECT `googleToken`, `price`, `priceCurrency`, `planId`, `planTerm`, `lastArticleId`, `lastPodcastId`, `source`, `isSubPurchase`, `planNum`, `productSku` from `purchase_data`")
            database.execSQL("DROP TABLE `purchase_data`")
            database.execSQL("ALTER TABLE `new_purchase_data` RENAME TO `purchase_data`")
        }
    }

    class Migration48To49 : Migration(48, 49) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Drop unused columns googleOrderId and userId from purchase_data table.
            database.execSQL("ALTER TABLE `podcast_episode` ADD `episodeNumber` INTEGER NOT NULL DEFAULT(-1)")
        }
    }
}

internal class AthleticDatabaseConverters : KoinComponent {
    private val gson by inject<Gson>()

    // TT Global classes
    @TypeConverter
    fun feedEntryTypeToString(value: FeedItemEntryType?): String = value?.value
        ?: FeedItemEntryType.UNKNOWN.value

    @TypeConverter
    fun stringToFeedEntryType(value: String?): FeedItemEntryType = FeedItemEntryType.from(value)

    // TT Global Lists
    @TypeConverter
    fun arrayListOfLongToGson(list: ArrayList<Long>?): String = gson.toJson(list) ?: ""

    @TypeConverter
    fun gsonToArrayListOfLong(value: String?): ArrayList<Long> = gson.fromJson(
        value
            ?: "",
        object : TypeToken<ArrayList<Long>>() {}.type
    ) ?: ArrayList()

    @TypeConverter
    fun mutableListOfStringToGson(list: MutableList<String>?): String = gson.toJson(list) ?: ""

    @TypeConverter
    fun gsonToMutableListOfString(value: String?): MutableList<String> = gson.fromJson(
        value
            ?: "",
        object : TypeToken<MutableList<String>>() {}.type
    ) ?: mutableListOf()

    // TT Topic Entity Tag
    @TypeConverter
    fun topicTagEntityListToString(list: ArrayList<TopicTagEntity>?): String = gson.toJson(list)
        ?: ""

    @TypeConverter
    fun stringToTopicTagEntityList(value: String?): ArrayList<TopicTagEntity> = gson.fromJson(
        value
            ?: "",
        object : TypeToken<ArrayList<TopicTagEntity>>() {}.type
    ) ?: ArrayList()

    // TT Article
    @TypeConverter
    fun listOfCommentsEntityToGson(list: List<CommentEntity>): String = gson.toJson(list)

    @TypeConverter
    fun gsonToListOfComments(value: String): List<CommentEntity> = gson.fromJson(
        value,
        object : TypeToken<ArrayList<CommentEntity>>() {}.type
    )

    // TT Podcast Feed
    // Tt PodcastItem
    @TypeConverter
    fun podcastItemToString(list: List<PodcastItem>?): String = gson.toJson(list)
        ?: ""

    @TypeConverter
    fun stringToPodcastItem(value: String?): List<PodcastItem> = gson.fromJson(
        value
            ?: "",
        object : TypeToken<List<PodcastItem>>() {}.type
    ) ?: ArrayList()

    // Tt PodcastTopic
    @TypeConverter
    fun podcastTopicsToString(list: List<PodcastTopic>?): String = gson.toJson(list) ?: ""

    @TypeConverter
    fun stringToPodcastTopics(value: String?): List<PodcastTopic> = gson.fromJson(
        value
            ?: "",
        object : TypeToken<List<PodcastTopic>>() {}.type
    ) ?: ArrayList()

    // Tt PodcastEpisodeTrackItem
    @TypeConverter
    fun podcastEpisodeDetailTrackItemToString(list: List<PodcastEpisodeDetailTrackItem>?): String = gson.toJson(list) ?: ""

    @TypeConverter
    fun stringToPodcastEpisodeDetailTrackItem(value: String?): List<PodcastEpisodeDetailTrackItem> = gson.fromJson(
        value
            ?: "",
        object : TypeToken<List<PodcastEpisodeDetailTrackItem>>() {}.type
    ) ?: ArrayList()

    // Tt PodcastEpisodeStoryItem
    @TypeConverter
    fun podcastEpisodeDetailStoryItemToString(list: List<PodcastEpisodeDetailStoryItem>?): String = gson.toJson(list) ?: ""

    @TypeConverter
    fun stringToPodcastEpisodeDetailStoryItem(value: String?): List<PodcastEpisodeDetailStoryItem> = gson.fromJson(
        value
            ?: "",
        object : TypeToken<List<PodcastEpisodeDetailStoryItem>>() {}.type
    ) ?: ArrayList()

    @TypeConverter
    fun entityIdToString(id: AthleticEntity.Id) = id.toString()

    @TypeConverter
    fun stringToEntityId(value: String) = AthleticEntity.Id.parse(value)

    @TypeConverter
    fun entityTypeSerialize(type: AthleticEntity.Type) = type.name

    @TypeConverter
    fun entityTypeDeserialize(value: String): AthleticEntity.Type {
        return safeValueOf<AthleticEntity.Type>(value) ?: AthleticEntity.Type.UNKNOWN
    }

    @TypeConverter
    fun longToDatetime(value: Long) = Datetime(value)

    @TypeConverter
    fun datetimeToLong(datetime: Datetime) = datetime.timeMillis

    @TypeConverter
    fun followableIdToString(id: Followable.Id) = id.toString()

    @TypeConverter
    fun followableStringToId(value: String) = Followable.Id.parse(value) ?: Followable.Id("-1", Followable.Type.LEAGUE)
}