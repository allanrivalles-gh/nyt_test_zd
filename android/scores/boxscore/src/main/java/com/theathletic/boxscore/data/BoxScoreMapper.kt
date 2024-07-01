package com.theathletic.boxscore.data

import com.theathletic.GetBoxScoreFeedQuery
import com.theathletic.boxscore.data.local.Article
import com.theathletic.boxscore.data.local.BasicHeader
import com.theathletic.boxscore.data.local.BoxScore
import com.theathletic.boxscore.data.local.BoxScoreModules
import com.theathletic.boxscore.data.local.Items
import com.theathletic.boxscore.data.local.LatestNewsModule
import com.theathletic.boxscore.data.local.ModuleHeader
import com.theathletic.boxscore.data.local.PodcastEpisode
import com.theathletic.boxscore.data.local.PodcastEpisodeClip
import com.theathletic.boxscore.data.local.Section
import com.theathletic.boxscore.data.local.SectionType
import com.theathletic.datetime.Datetime
import com.theathletic.fragment.BoxScoreLatestNews
import com.theathletic.fragment.BoxScorePodcastEpisode
import com.theathletic.podcast.ui.DownloadState
import com.theathletic.podcast.ui.PlaybackState
import com.theathletic.type.BoxScoreSectionType

fun GetBoxScoreFeedQuery.Data.toDomain(): BoxScore {
    return boxScore.toDomain()
}

private fun GetBoxScoreFeedQuery.BoxScore.toDomain(): BoxScore {
    return BoxScore(
        id = id,
        sections = sections.toDomain()
    )
}

private fun List<GetBoxScoreFeedQuery.Section>.toDomain(): List<Section> {
    return this.map { it.toDomain() }
}

private fun GetBoxScoreFeedQuery.Section.toDomain(): Section {
    return Section(
        id = this.id,
        type = this.type.toDomain(),
        modules = this.modules.mapNotNull { it.toDomain() }
    )
}

private fun GetBoxScoreFeedQuery.Module.toDomain(): BoxScoreModules? {
    fragments.boxScoreLatestNews?.let { latestNews ->
        return LatestNewsModule(
            id = latestNews.id,
            header = latestNews.header?.toDomain(),
            blocks = latestNews.blocks.mapNotNull { it.toDomain() }
        )
    }
    return null
}

private fun BoxScoreLatestNews.Block.toDomain(): Items? {
    fragments.boxScoreArticle?.let { article ->
        return Article(
            id = article.id,
            authors = article.authors,
            commentCount = article.comment_count,
            description = article.description,
            imageUri = article.image_uri,
            permalink = article.permalink,
            title = article.title,
            articleId = article.article_id,
            isRead = false,
            isBookmarked = false
        )
    }
    fragments.boxScorePodcastEpisode?.let { podcast ->
        return PodcastEpisode(
            id = podcast.id,
            description = podcast.description,
            episodeId = podcast.episode_id,
            finished = podcast.finished,
            imageUrl = podcast.image_url,
            permalink = podcast.permalink,
            podcastId = podcast.podcast_id,
            podcastTitle = podcast.podcast_title,
            publishedAt = Datetime(podcast.published_at),
            title = podcast.title,
            duration = podcast.duration,
            mp3Url = podcast.mp3_url,
            commentCount = podcast.comment_count,
            timeElapsed = podcast.time_elapsed,
            playbackState = PlaybackState.None,
            downloadState = DownloadState.NOT_DOWNLOADED,
            clips = podcast.clips?.map { it.toDomain() }
        )
    }
    return null
}

private fun BoxScorePodcastEpisode.Clip.toDomain(): PodcastEpisodeClip {
    return this.fragments.boxScorePodcastEpisodeClip.let { clip ->
        PodcastEpisodeClip(
            id = clip.id,
            title = clip.title,
            startPosition = clip.start_position,
            endPosition = clip.end_position
        )
    }
}

private fun BoxScoreLatestNews.Header.toDomain(): ModuleHeader? {
    return fragments.boxScoreBasicHeader?.let { basicHeader ->
        BasicHeader(
            id = basicHeader.id,
            title = basicHeader.title
        )
    }
}

private fun BoxScoreSectionType.toDomain(): SectionType {
    return when (this) {
        BoxScoreSectionType.game -> SectionType.GAME
        else -> SectionType.UNKNOWN
    }
}