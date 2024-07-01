package com.theathletic.network.rest

import androidx.databinding.ObservableBoolean
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.theathletic.entity.main.FeedItemEntryType
import com.theathletic.entity.main.FeedItemStyle
import com.theathletic.entity.main.FeedItemType
import com.theathletic.entity.main.PodcastTopicEntryType
import com.theathletic.entity.settings.UserTopics
import com.theathletic.entity.user.UserEntity
import com.theathletic.network.rest.deserializer.BooleanTypeDeserializer
import com.theathletic.network.rest.deserializer.DateDeserializer
import com.theathletic.network.rest.deserializer.FeedEntryTypeDeserializer
import com.theathletic.network.rest.deserializer.FeedItemTypeDeserializer
import com.theathletic.network.rest.deserializer.LongTypeDeserializer
import com.theathletic.network.rest.deserializer.ObservableBooleanTypeDeserializer
import com.theathletic.network.rest.deserializer.PodcastTopicEntryTypeDeserializer
import com.theathletic.network.rest.deserializer.PodcastTopicEntryTypeSerializer
import com.theathletic.network.rest.deserializer.UserEntityDeserializer
import com.theathletic.network.rest.deserializer.UserTopicsDeserializer
import com.theathletic.network.rest.deserializer.feedItemEntryTypeSerializer
import com.theathletic.network.rest.deserializer.feedItemStyleDeserializer
import com.theathletic.network.rest.deserializer.feedItemStyleSerializer
import com.theathletic.network.rest.deserializer.feedItemTypeSerializer
import java.util.Date
import org.koin.core.component.KoinComponent

object GsonProvider : KoinComponent {
    fun buildGsonWithAllAdapters(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateDeserializer())
            .registerTypeAdapter(FeedItemEntryType::class.java, FeedEntryTypeDeserializer())
            .registerTypeAdapter(FeedItemEntryType::class.java, feedItemEntryTypeSerializer())
            .registerTypeAdapter(FeedItemType::class.java, FeedItemTypeDeserializer())
            .registerTypeAdapter(FeedItemType::class.java, feedItemTypeSerializer())
            .registerTypeAdapter(FeedItemStyle::class.java, feedItemStyleDeserializer())
            .registerTypeAdapter(FeedItemStyle::class.java, feedItemStyleSerializer())
            .registerTypeAdapter(PodcastTopicEntryType::class.java, PodcastTopicEntryTypeDeserializer())
            .registerTypeAdapter(PodcastTopicEntryType::class.java, PodcastTopicEntryTypeSerializer())
            .registerTypeAdapter(UserEntity::class.java, UserEntityDeserializer())
            .registerTypeAdapter(UserTopics::class.java, UserTopicsDeserializer())
            .registerTypeAdapter(Boolean::class.java, BooleanTypeDeserializer())
            .registerTypeAdapter(ObservableBoolean::class.java, ObservableBooleanTypeDeserializer())
            .registerTypeAdapter(Long::class.java, LongTypeDeserializer())
            .serializeNulls()
            .setLenient()
            .create()
    }
}