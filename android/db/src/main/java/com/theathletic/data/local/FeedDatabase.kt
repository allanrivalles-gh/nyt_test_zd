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
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import com.theathletic.data.local.FeedDatabaseTriggers.TRIGGER_TABLES_13
import com.theathletic.data.local.FeedDatabaseTriggers.TRIGGER_TABLES_16
import com.theathletic.entity.TopicTagEntity
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.main.FeedItem
import com.theathletic.entity.main.FeedItemAction
import com.theathletic.entity.main.FeedItemStyle
import com.theathletic.entity.main.FeedItemType
import com.theathletic.entity.main.FeedResponse
import com.theathletic.entity.main.ResponseMetadata
import com.theathletic.entity.main.TertiaryGroup
import com.theathletic.feed.data.local.FeedDao
import com.theathletic.utility.safeValueOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException
import java.util.ArrayList

@Database(
    entities = [
        FeedItem::class,
        FeedResponse::class
    ],
    version = 30,
    exportSchema = true
)
@TypeConverters(FeedDatabaseConverters::class)
abstract class FeedDatabase : RoomDatabase() {

    abstract fun feedDao(): FeedDao

    companion object {
        fun newInstance(context: Context): FeedDatabase = synchronized(this) {
            return Room.databaseBuilder(context, FeedDatabase::class.java, "feed-database")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        FeedDatabaseTriggers.createAllFeedEntityTriggers(db)
                    }
                })
                .addMigrations(Migration1To2())
                .addMigrations(Migration2To3())
                .addMigrations(Migration3To4())
                .addMigrations(Migration4To5())
                .addMigrations(Migration5To6())
                .addMigrations(Migration6To7())
                .addMigrations(Migration7To8())
                .addMigrations(Migration8To9())
                .addMigrations(Migration9To10())
                .addMigrations(Migration10To11())
                .addMigrations(Migration11To12())
                .addMigrations(Migration12To13())
                .addMigrations(Migration13To14())
                .addMigrations(Migration14To15())
                .addMigrations(Migration15To16())
                .addMigrations(Migration16To17())
                .addMigrations(Migration17To18())
                .addMigrations(Migration18To19())
                .addMigrations(Migration19To20())
                .addMigrations(Migration20To21())
                .addMigrations(Migration21To22())
                .addMigrations(Migration22To23())
                .addMigrations(Migration23To24())
                .addMigrations(Migration24To25())
                .addMigrations(Migration25To26())
                .addMigrations(Migration26To27())
                .addMigrations(Migration27To28())
                .addMigrations(Migration28To29())
                .addMigrations(Migration29to30())
                .build()
        }
    }
}

private class Migration1To2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // TT 1. Alter table feed_response. Add metadata column
        database.execSQL("ALTER TABLE `feed_response` ADD `metadataJSON` TEXT NOT NULL DEFAULT \"\"")
    }
}

private class Migration2To3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // TT 1. Add table for live games
        database.execSQL("CREATE TABLE IF NOT EXISTS `feed_game_live` (`gameType` TEXT NOT NULL, `awayTeamId` INTEGER NOT NULL, `awayTeamName` TEXT NOT NULL, `awayTeamScore` INTEGER NOT NULL, `homeTeamId` INTEGER NOT NULL, `homeTeamName` TEXT NOT NULL, `homeTeamScore` INTEGER NOT NULL, `backgroundColorHex` TEXT NOT NULL, `scoreStatusText` TEXT NOT NULL, `id` INTEGER NOT NULL, `itemId` TEXT NOT NULL, `feedId` TEXT NOT NULL, `index` INTEGER NOT NULL, `composedId` TEXT NOT NULL, `entityType` TEXT NOT NULL, `teamIds` TEXT NOT NULL, `cityIds` TEXT NOT NULL, `leagueIds` TEXT NOT NULL, `authorIds` TEXT NOT NULL, `entryDatetime` TEXT NOT NULL, `entityTags` TEXT NOT NULL, PRIMARY KEY(`composedId`))")
    }
}

private class Migration3To4 : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_article` ADD COLUMN `permalink` TEXT NOT NULL DEFAULT \"\"")
    }
}

private class Migration4To5 : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_article` ADD COLUMN `commentsDisabled` INTEGER NOT NULL DEFAULT 0")
    }
}

private class Migration5To6 : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_game_live` ADD COLUMN `possessionTeam` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_game_recent` ADD COLUMN `possessionTeam` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_game_upcoming` ADD COLUMN `possessionTeam` TEXT NOT NULL DEFAULT \"\"")
    }
}

private class Migration6To7 : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `feedVariant` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("CREATE TABLE IF NOT EXISTS `feed_discussion` (`articleTitle` TEXT NOT NULL, `articleExcerpt` TEXT NOT NULL, `articleAuthorName` TEXT NOT NULL, `articleAuthorImage` TEXT, `commentsCount` INTEGER NOT NULL, `backgroundColorHex` TEXT NOT NULL, `id` INTEGER NOT NULL, `itemId` TEXT NOT NULL, `feedId` TEXT NOT NULL, `index` INTEGER NOT NULL, `composedId` TEXT NOT NULL, `entityType` TEXT NOT NULL, `teamIds` TEXT NOT NULL, `cityIds` TEXT NOT NULL, `leagueIds` TEXT NOT NULL, `authorIds` TEXT NOT NULL, `entryDatetime` TEXT NOT NULL, `entityTags` TEXT NOT NULL, PRIMARY KEY(`composedId`))")
    }
}

private class Migration7To8 : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `feed_staff` (`authorName` TEXT NOT NULL, `imageUrl` TEXT, `role` TEXT, `id` INTEGER NOT NULL, `itemId` TEXT NOT NULL, `feedId` TEXT NOT NULL, `index` INTEGER NOT NULL, `itemType` TEXT NOT NULL, `itemStyle` TEXT NOT NULL, `composedId` TEXT NOT NULL, `entityType` TEXT NOT NULL, `teamIds` TEXT NOT NULL, `cityIds` TEXT NOT NULL, `leagueIds` TEXT NOT NULL, `authorIds` TEXT NOT NULL, `entryDatetime` TEXT NOT NULL, `entityTags` TEXT NOT NULL, PRIMARY KEY(`composedId`))")
    }
}

private class Migration8To9 : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS feed_article")
        database.execSQL("CREATE TABLE IF NOT EXISTS `feed_article` (`articleTitle` TEXT NOT NULL, `articleImg` TEXT NOT NULL, `articleAuthorName` TEXT NOT NULL, `excerpt` TEXT NOT NULL, `permalink` TEXT NOT NULL, `commentsCount` INTEGER NOT NULL, `teamName` TEXT, `teamHex` TEXT, `isReadByUser` INTEGER NOT NULL, `isBookmarked` INTEGER NOT NULL, `isTeaser` INTEGER NOT NULL, `isInkStory` INTEGER NOT NULL, `commentsDisabled` INTEGER NOT NULL, `versionNumber` INTEGER, `id` INTEGER NOT NULL, `itemId` TEXT NOT NULL, `feedId` TEXT NOT NULL, `index` INTEGER NOT NULL, `itemType` TEXT NOT NULL, `itemStyle` TEXT NOT NULL, `composedId` TEXT NOT NULL, `entityType` TEXT NOT NULL, `teamIds` TEXT NOT NULL, `cityIds` TEXT NOT NULL, `leagueIds` TEXT NOT NULL, `authorIds` TEXT NOT NULL, `entryDatetime` TEXT NOT NULL, `entityTags` TEXT NOT NULL, PRIMARY KEY(`composedId`))")

        database.execSQL("ALTER TABLE `feed_video_series` ADD COLUMN `itemType` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_video_series` ADD COLUMN `itemStyle` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("DELETE FROM feed_video_series")

        database.execSQL("ALTER TABLE `feed_podcast_episode` ADD COLUMN `itemType` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_podcast_episode` ADD COLUMN `itemStyle` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("DELETE FROM feed_podcast_episode")

        database.execSQL("ALTER TABLE `feed_recommended_podcast` ADD COLUMN `itemType` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_recommended_podcast` ADD COLUMN `itemStyle` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("DELETE FROM feed_recommended_podcast")

        database.execSQL("ALTER TABLE `feed_discussion` ADD COLUMN `itemType` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_discussion` ADD COLUMN `itemStyle` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("DELETE FROM feed_discussion")

        database.execSQL("ALTER TABLE `feed_live_discussion` ADD COLUMN `itemType` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_live_discussion` ADD COLUMN `itemStyle` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("DELETE FROM feed_live_discussion")

        database.execSQL("ALTER TABLE `feed_evergreen` ADD COLUMN `itemType` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_evergreen` ADD COLUMN `itemStyle` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("DELETE FROM feed_evergreen")

        database.execSQL("ALTER TABLE `feed_topics` ADD COLUMN `itemType` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_topics` ADD COLUMN `itemStyle` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("DELETE FROM feed_topics")

        database.execSQL("ALTER TABLE `feed_announcement` ADD COLUMN `itemType` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_announcement` ADD COLUMN `itemStyle` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("DELETE FROM feed_announcement")

        database.execSQL("ALTER TABLE `feed_game_recent` ADD COLUMN `itemType` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_game_recent` ADD COLUMN `itemStyle` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("DELETE FROM feed_game_recent")

        database.execSQL("ALTER TABLE `feed_game_upcoming` ADD COLUMN `itemType` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_game_upcoming` ADD COLUMN `itemStyle` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("DELETE FROM feed_game_upcoming")

        database.execSQL("ALTER TABLE `feed_game_live` ADD COLUMN `itemType` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_game_live` ADD COLUMN `itemStyle` TEXT NOT NULL DEFAULT \"\"")
        database.execSQL("DELETE FROM feed_game_live")

        database.execSQL("DELETE FROM feed_item")
        database.execSQL("DELETE FROM feed_response")
    }
}

private class Migration9To10 : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_discussion` ADD COLUMN `articleAuthorTitle` TEXT DEFAULT \"\"")
        database.execSQL("ALTER TABLE `feed_live_discussion` ADD COLUMN `articleAuthorTitle` TEXT DEFAULT \"\"")
    }
}

private class Migration10To11 : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS `feed_video`")
        database.execSQL("DROP TABLE IF EXISTS `feed_video_series`")
    }
}

private class Migration11To12 : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS `feed_staff`")
    }
}

private class Migration12To13 : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_response` ADD COLUMN `localChangeTimestamp` TEXT NOT NULL DEFAULT \"\"")
        FeedDatabaseTriggers.createFeedEntityTriggers(database, TRIGGER_TABLES_13)
    }
}

private class Migration13To14 : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `page` INTEGER NOT NULL DEFAULT 0")
    }
}

private class Migration14To15 : Migration(14, 15) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `feed_generic_entity` (`id` TEXT NOT NULL, `feedId` TEXT NOT NULL, `type` TEXT NOT NULL, `jsonBlob` TEXT NOT NULL, PRIMARY KEY(`id`, `feedId`))")
    }
}

private class Migration15To16 : Migration(15, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        FeedDatabaseTriggers.createFeedEntityTriggers(database, TRIGGER_TABLES_16)
    }
}

private class Migration16To17 : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `entityIds` TEXT NOT NULL DEFAULT \"\"")
    }
}

private class Migration17To18 : Migration(17, 18) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS `feed_live_discussion`")
        database.execSQL("DROP TABLE IF EXISTS `feed_discussion`")
        database.execSQL("DROP TABLE IF EXISTS `feed_game_upcoming`")
        database.execSQL("DROP TABLE IF EXISTS `feed_game_live`")
        database.execSQL("DROP TABLE IF EXISTS `feed_game_recent`")
        database.execSQL("DROP TABLE IF EXISTS `feed_topics`")
        database.execSQL("DROP TABLE IF EXISTS `feed_generic_entity`")
        database.execSQL("DROP TABLE IF EXISTS `feed_podcast_episode_entity`")
        database.execSQL("DROP TABLE IF EXISTS `feed_recommended_podcast`")
        database.execSQL("DROP TABLE IF EXISTS `feed_evergreen`")
        database.execSQL("DROP TABLE IF EXISTS `feed_announcement`")
    }
}

private class Migration18To19 : Migration(18, 19) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `compoundEntityIds` TEXT NOT NULL DEFAULT \"\"")
    }
}

private class Migration19To20 : Migration(19, 20) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS `feed_article`")
    }
}

private class Migration20To21 : Migration(20, 21) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `tertiaryGroup` TEXT")
    }
}

private class Migration21To22 : Migration(21, 22) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `action` TEXT")
    }
}

private class Migration22To23 : Migration(22, 23) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS `new_feed_item`")
        database.execSQL("CREATE TABLE `new_feed_item` (`id` TEXT NOT NULL, `feedId` TEXT NOT NULL, `index` INTEGER NOT NULL, `page` INTEGER NOT NULL, `tertiaryGroup` TEXT, `itemType` TEXT NOT NULL, `style` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `action` TEXT, `entityIds` TEXT NOT NULL, `compoundEntityIds` TEXT NOT NULL, PRIMARY KEY(`id`, `feedId`))")

        database.execSQL("INSERT INTO `new_feed_item` SELECT `id`, `feedId`, `index`, `page`, `tertiaryGroup`, `itemType`, `style`, `title`, `description`, `action`, `entityIds`, `compoundEntityIds` FROM `feed_item`")

        database.execSQL("DROP TABLE IF EXISTS `feed_item`")
        database.execSQL("ALTER TABLE `new_feed_item` RENAME TO `feed_item`")
    }
}

private class Migration23To24 : Migration(23, 24) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `hasNextPage` INTEGER DEFAULT 1 NOT NULL")
        FeedDatabaseTriggers.createFeedEntityTriggers(database, TRIGGER_TABLES_13)
    }
}

private class Migration24To25 : Migration(24, 25) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `entityCuratedTitles` TEXT DEFAULT \"\" NOT NULL")
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `entityCuratedImageUrls` TEXT DEFAULT \"\" NOT NULL")
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `entityCuratedDescriptions` TEXT DEFAULT \"\" NOT NULL")
    }
}

private class Migration25To26 : Migration(25, 26) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `container` TEXT")
    }
}

private class Migration26To27 : Migration(26, 27) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `titleImageUrl` TEXT DEFAULT \"\" NOT NULL")
    }
}

private class Migration27To28 : Migration(27, 28) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `entityCuratedDisplayOrder` TEXT DEFAULT \"\" NOT NULL")
    }
}

private class Migration28To29 : Migration(28, 29) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `feedSlug` TEXT DEFAULT NULL")
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `feedSportType` TEXT DEFAULT NULL")
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `feedLeagueName` TEXT DEFAULT NULL")
    }
}

private class Migration29to30 : Migration(29, 30) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `feed_item` ADD COLUMN `adUnitPath` TEXT DEFAULT NULL")
    }
}

class FeedDatabaseConverters : KoinComponent {
    private val gson by inject<Gson>()
    private val moshi by inject<Moshi>()

    @TypeConverter
    fun responseMetadataV2ToString(value: ResponseMetadata?): String = value?.lastRefreshed.toString()

    @TypeConverter
    fun stringToResponseMetadataV2(value: String?): ResponseMetadata = ResponseMetadata(value?.toLong())

    @TypeConverter
    fun topicTagEntityListToString(list: ArrayList<TopicTagEntity>?): String = gson.toJson(list)
        ?: ""

    @TypeConverter
    fun stringToTopicTagEntityList(value: String?): ArrayList<TopicTagEntity> = gson.fromJson(
        value
            ?: "",
        object : TypeToken<ArrayList<TopicTagEntity>>() {}.type
    ) ?: ArrayList()

    @TypeConverter
    fun feedItemTypeV2ToString(value: FeedItemType?): String = value?.value
        ?: FeedItemType.UNKNOWN.value

    @TypeConverter
    fun stringToFeedItemTypeV2(value: String?): FeedItemType = FeedItemType.from(value)

    @TypeConverter
    fun feedItemStyleV2ToString(
        value: FeedItemStyle?
    ): String = value?.name ?: FeedItemStyle.UNKNOWN.name

    @TypeConverter
    fun stringToFeedItemStyleV2(
        value: String?
    ): FeedItemStyle = safeValueOf<FeedItemStyle>(value) ?: FeedItemStyle.UNKNOWN

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
    fun arrayListOfStringToGson(list: ArrayList<String>?): String = gson.toJson(list) ?: ""

    @TypeConverter
    fun gsonToArrayListOfString(value: String?): ArrayList<String> = gson.fromJson(
        value
            ?: "",
        object : TypeToken<ArrayList<String>>() {}.type
    ) ?: ArrayList()

    @TypeConverter
    fun entityIdListToString(
        ids: List<AthleticEntity.Id>
    ): String = ids.joinToString(",") { it.toString() }

    @TypeConverter
    fun stringToEntityIdList(value: String): List<AthleticEntity.Id> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").mapNotNull(AthleticEntity.Id.Companion::parse)
    }

    @TypeConverter
    fun entityIdTieredListToString(
        lists: List<List<AthleticEntity.Id>>
    ): String {
        val stringList = lists.map(this::entityIdListToString)

        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)

        return adapter.toJson(stringList)
    }

    @TypeConverter
    fun stringToEntityIdTieredList(value: String): List<List<AthleticEntity.Id>> {
        if (value.isEmpty()) return emptyList()

        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)

        val stringList = adapter.fromJson(value) ?: emptyList()

        return stringList.map(this::stringToEntityIdList)
    }

    @TypeConverter
    fun tertiaryGroupToString(
        tertiaryGroup: TertiaryGroup?
    ): String? = moshi.adapter(TertiaryGroup::class.java).toJson(tertiaryGroup)

    @TypeConverter
    fun stringToTertiaryGroup(value: String?): TertiaryGroup? {
        if (value == null) return null
        return moshi.adapter(TertiaryGroup::class.java).fromJson(value)
    }

    @TypeConverter
    fun feedItemActionToString(
        feedItemAction: FeedItemAction?
    ): String? = moshi.adapter(FeedItemAction::class.java).toJson(feedItemAction)

    @TypeConverter
    fun stringToFeedItemAction(
        value: String?
    ): FeedItemAction? = value?.let {
        moshi.adapter(FeedItemAction::class.java).fromJson(it)
    }

    @TypeConverter
    fun entityIdMapToString(map: MutableMap<AthleticEntity.Id, String?>): String {
        val tempMap = mutableMapOf<String, String?>()
        for ((key, value) in map) {
            tempMap[key.toString()] = value
        }

        val type = Types.newParameterizedType(
            MutableMap::class.java,
            String::class.java,
            String::class.java
        )
        return moshi.adapter<MutableMap<String, String?>>(type).toJson(tempMap)
    }

    @TypeConverter
    fun stringToEntityIdMap(mapValue: String): MutableMap<AthleticEntity.Id, String?> {
        val type = Types.newParameterizedType(
            MutableMap::class.java,
            String::class.java,
            String::class.java
        )

        val tempMap = try {
            moshi.adapter<MutableMap<String, String?>>(type).fromJson(mapValue) ?: return mutableMapOf()
        } catch (e: IOException) {
            return mutableMapOf()
        }

        val map = mutableMapOf<AthleticEntity.Id, String?>()
        for ((key, value) in tempMap) {
            AthleticEntity.Id.parse(key)?.let {
                map[it] = value
            }
        }
        return map
    }

    @TypeConverter
    fun entityIdShortMapToString(map: Map<AthleticEntity.Id, Short?>): String {
        val tempMap = map.mapKeys { it.key.toString() }

        val type = Types.newParameterizedType(
            Map::class.java,
            String::class.java,
            Short::class.javaObjectType
        )
        return moshi.adapter<Map<String, Short?>>(type).toJson(tempMap)
    }

    @TypeConverter
    fun stringToEntityIdShortMap(mapValue: String): Map<AthleticEntity.Id, Short?> {
        val type = Types.newParameterizedType(
            Map::class.java,
            String::class.java,
            Short::class.javaObjectType
        )

        val tempMap = try {
            moshi.adapter<Map<String, Short?>>(type).fromJson(mapValue) ?: return mapOf()
        } catch (e: IOException) {
            return mapOf()
        }

        val map = mutableMapOf<AthleticEntity.Id, Short?>()
        for ((key, value) in tempMap) {
            AthleticEntity.Id.parse(key)?.let {
                map[it] = value
            }
        }
        return map
    }
}