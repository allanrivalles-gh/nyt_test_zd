package com.theathletic.data.local

import android.database.SQLException
import androidx.sqlite.db.SupportSQLiteDatabase
import timber.log.Timber

object FeedDatabaseTriggers {
    val TRIGGER_TABLES_13 = listOf(
        "feed_item"
    )

    val TRIGGER_TABLES_16 = listOf("feed_generic_entity")

    private val allTriggerTables = TRIGGER_TABLES_13

    fun createAllFeedEntityTriggers(database: SupportSQLiteDatabase) =
        createFeedEntityTriggers(database, allTriggerTables)

    fun createFeedEntityTriggers(
        database: SupportSQLiteDatabase,
        triggerTableNames: List<String>
    ) {
        triggerTableNames.forEach {
            dropAllFeedEntityTriggers(database, it)
            createFeedEntityTriggerForTable(database, it)
        }
    }

    private fun createFeedEntityTriggerForTable(
        database: SupportSQLiteDatabase,
        triggerTableName: String
    ) {
        try {
            database.execSQL(
                """
                    CREATE TRIGGER ${triggerTableName}_updated AFTER UPDATE ON $triggerTableName
                    BEGIN
                    UPDATE feed_response SET localChangeTimestamp = datetime() WHERE feed_response.feedId = new.feedId;
                    END;
                """.trimIndent()
            )
            database.execSQL(
                """
                    CREATE TRIGGER ${triggerTableName}_inserted AFTER INSERT ON $triggerTableName
                    BEGIN
                    UPDATE feed_response SET localChangeTimestamp = datetime() WHERE feed_response.feedId = new.feedId;
                    END;
                """.trimIndent()
            )
            database.execSQL(
                """
                    CREATE TRIGGER ${triggerTableName}_deleted AFTER DELETE ON $triggerTableName
                    BEGIN
                    UPDATE feed_response SET localChangeTimestamp = datetime() WHERE feed_response.feedId = old.feedId;
                    END;
                """.trimIndent()
            )
        } catch (e: SQLException) {
            Timber.e(e, "Failed to create triggers for table: $triggerTableName")
        }
    }

    private fun dropAllFeedEntityTriggers(
        database: SupportSQLiteDatabase,
        triggerTableName: String
    ) {
        try {
            database.execSQL("DROP TRIGGER IF EXISTS ${triggerTableName}_updated;")
            database.execSQL("DROP TRIGGER IF EXISTS ${triggerTableName}_inserted;")
            database.execSQL("DROP TRIGGER IF EXISTS ${triggerTableName}_deleted;")
        } catch (e: SQLException) {
            Timber.e(e, "Failed to drop triggers for table: $triggerTableName")
        }
    }
}