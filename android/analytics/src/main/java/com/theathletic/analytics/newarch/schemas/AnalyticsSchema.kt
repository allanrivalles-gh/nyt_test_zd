package com.theathletic.analytics.newarch.schemas

import com.theathletic.analytics.BuildConfig

/**
 * Matches our backend avro schemas for analytics.
 *
 * NOTE: If this class is moved, we MUST update the proguard rule to ignore obfuscation on these
 * classes otherwise the flexible analytics schema pipeline will break.
 */
interface AnalyticsSchema {

    enum class Type(val schemaId: Int) {
        IMPRESSION(schemaId = 43)
    }

    interface Contract {

        interface Impression {
            val verb: String
            val view: String
            val object_type: String
            val object_id: String
            val impress_start_time: Long
            val impress_end_time: Long
            val filter_type: String?
            val filter_id: Long?
            val v_index: Long
            val h_index: Long
            val element: String
            val container: String?
            val page_order: Long?
            val parent_object_type: String?
            val parent_object_id: String?
        }
    }

    interface Local {

        data class Impression(
            override val verb: String,
            override val view: String,
            override val object_type: String,
            override val object_id: String,
            override val impress_start_time: Long,
            override val impress_end_time: Long,
            override val filter_type: String?,
            override val filter_id: Long?,
            override val v_index: Long,
            override val h_index: Long,
            override val element: String,
            override val container: String?,
            override val page_order: Long?,
            override val parent_object_type: String?,
            override val parent_object_id: String?
        ) : Contract.Impression
    }
}

enum class KafkaTopic(
    private val staging: String,
    private val prod: String,
    val schema: AnalyticsSchema.Type
) {
    REALTIME_IMPRESSION(
        staging = "staging-android-real-time-impressions",
        prod = "production-android-real-time-impressions",
        schema = AnalyticsSchema.Type.IMPRESSION
    ),
    FRONTPAGE_IMPRESSION(
        staging = "staging-android-front-page-impressions",
        prod = "production-android-front-page-impressions",
        schema = AnalyticsSchema.Type.IMPRESSION
    ),
    FEED_IMPRESSION(
        staging = "staging-android-feed-impressions",
        prod = "production-android-feed-impressions",
        schema = AnalyticsSchema.Type.IMPRESSION
    ),
    LIVE_BLOG_IMPRESSION(
        staging = "staging-android-live-blog-impressions",
        prod = "production-android-live-blog-impressions",
        schema = AnalyticsSchema.Type.IMPRESSION
    ),
    LISTEN_TAB_IMPRESSION(
        staging = "staging-android-listen-tab-impressions",
        prod = "production-android-listen-tab-impressions",
        schema = AnalyticsSchema.Type.IMPRESSION
    ),
    SCORES_IMPRESSION(
        staging = "staging-android-scores-impressions",
        prod = "production-android-scores-impressions",
        schema = AnalyticsSchema.Type.IMPRESSION
    ),
    COMMENT_IMPRESSION(
        staging = "staging-android-comment-impressions",
        prod = "production-android-comment-impressions",
        schema = AnalyticsSchema.Type.IMPRESSION
    ),
    ;

    val topic get() = when {
        BuildConfig.DEV_ENVIRONMENT -> staging
        else -> prod
    }
}