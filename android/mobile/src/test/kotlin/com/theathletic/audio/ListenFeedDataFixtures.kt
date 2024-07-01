package com.theathletic.audio

import com.theathletic.datetime.Datetime
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.local.PodcastSeriesEntity

interface ListenFeedDataFixtures {
    object LiveAudioRoom {
        private fun liveRoomFixture(id: String = "1"): LiveAudioRoomEntity = LiveAudioRoomEntity(id = id)

        private fun endedLiveRoomFixture(
            id: String = "1"
        ): LiveAudioRoomEntity = LiveAudioRoomEntity(
            id = id,
            startedAt = Datetime(System.currentTimeMillis() - 5000),
            endedAt = Datetime(System.currentTimeMillis()),
        )

        fun listOfLiveRoomFixtures(
            vararg ids: String
        ) = if (ids.isNotEmpty()) {
            ids.map { id ->
                liveRoomFixture(id)
            }
        } else {
            listOf(liveRoomFixture(), endedLiveRoomFixture())
        }
    }

    object PodcastEpisode {
        private fun podcastEpisodeFixture(
            id: String = "1",
            title: String = "podcastTitle",
            seriesId: String = "seriesId"
        ) = PodcastEpisodeEntity(
            id = id,
            title = title,
            seriesId = seriesId
        )

        private fun podcastEpisodeFixtures(
            startingIndex: Int = 1,
            seriesId: String,
            vararg titles: String,
        ) = titles.mapIndexed { index, title ->
            podcastEpisodeFixture(
                id = (startingIndex + index).toString(),
                title = title,
                seriesId = seriesId
            )
        }

        fun listOfFollowingPodcastsFixtures() = podcastEpisodeFixtures(
            seriesId = PodcastSeries.Series.TheAthleticNbaShow.id,
            titles = arrayOf(
                "Does the regular season matter?",
                "Daily Ding: Klay Thompson eviscerates Lakers"
            )
        ) + podcastEpisodeFixtures(
            startingIndex = 3,
            seriesId = PodcastSeries.Series.TheAthleticFootballShow.id,
            titles = arrayOf(
                "What roster holes remain after the 2023 NFL Draft?",
                "Prospects to Pros: 2023 NFL Draft"
            )
        ) + podcastEpisodeFixtures(
            startingIndex = 5,
            seriesId = PodcastSeries.Series.BetweenTheLines.id,
            titles = arrayOf(
                "Ep. 5: Searching for Answers",
                "Ep. 4: Rumblings of Hope"
            )
        )

        private fun listOfNotFollowingPodcastsFixtures() = podcastEpisodeFixtures(
            seriesId = PodcastSeries.Series.TalkOfTheDevils.id,
            titles = arrayOf(
                "Bruno & TUrner Down Villans & Old Trafford Protests",
                "Europa League Horror Show: United Crash Out in Serville"
            )
        ) + podcastEpisodeFixtures(
            startingIndex = 2,
            seriesId = PodcastSeries.Series.TheLeafReport.id,
            titles = arrayOf(
                "Maple Leafs make mistakes in game 1 loss to Florida in round 2",
                "Tavares hat trick leads Maple Leafs as they even up series with Tampa Bay"
            )
        )

        fun listOfPodcastsFixtures() = listOfFollowingPodcastsFixtures() + listOfNotFollowingPodcastsFixtures()
        fun listOfLatestPodcastsFixtures() = listOfFollowingPodcastsFixtures().chunked(2).map { it.last() }
    }

    object PodcastSeries {
        sealed class Series(val name: String) {
            val id: String get() = name.replace(" ", "")
            override fun toString() = name

            object TheAthleticNbaShow : Series("The Athletic NBA Show")
            object TheAthleticFootballShow : Series("The Athletic Football Show")
            object BetweenTheLines : Series("Between The Lines")
            object TalkOfTheDevils : Series("Talk of The Devils")
            object TheLeafReport : Series("The Leaft Report")
        }

        private fun podcastSeriesFixture(
            id: String = "seriesId",
            title: String = "podcastTitle",
            isFollowing: Boolean = true,
        ) = PodcastSeriesEntity(
            id = id,
            title = title,
            isFollowing = isFollowing
        )

        private fun podcastSeriesFixtures(
            vararg series: Series,
            isFollowing: Boolean
        ) = series.map { serie ->
            podcastSeriesFixture(
                id = serie.id,
                title = serie.name,
                isFollowing = isFollowing
            )
        }

        fun listOfFollowingPodcastsSeriesFixtures() = podcastSeriesFixtures(
            isFollowing = true,
            series = arrayOf(
                Series.TheAthleticNbaShow,
                Series.TheAthleticFootballShow,
                Series.BetweenTheLines
            )
        )

        fun listOfNotFollowingPodcastsSeriesFixtures() = podcastSeriesFixtures(
            isFollowing = false,
            series = arrayOf(
                Series.TalkOfTheDevils,
                Series.TheLeafReport,
            )
        )

        fun listOfPodcastsSeriesFixtures() =
            listOfFollowingPodcastsSeriesFixtures() + listOfNotFollowingPodcastsSeriesFixtures()
    }
}