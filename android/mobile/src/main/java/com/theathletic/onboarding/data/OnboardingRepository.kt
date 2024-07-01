package com.theathletic.onboarding.data

import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.GetUserAttributionSurveyQuery
import com.theathletic.RecommendedTeamsQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.attributionsurvey.data.local.AttributionSurvey
import com.theathletic.attributionsurvey.data.local.AttributionSurveyOption
import com.theathletic.attributionsurvey.data.local.SurveyCache
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.domain.FollowableItem
import com.theathletic.followables.data.remote.TeamApi
import com.theathletic.fragment.Podcast
import com.theathletic.network.ResponseStatus
import com.theathletic.onboarding.data.remote.OnboardingApi
import com.theathletic.onboarding.data.remote.OnboardingApiException
import com.theathletic.onboarding.data.remote.OnboardingFollowPodcastFetcher
import com.theathletic.repository.Repository
import com.theathletic.repository.safeApiRequest
import com.theathletic.utility.LogoUtility
import com.theathletic.utility.OnboardingPreferences
import kotlinx.coroutines.flow.flow
import timber.log.Timber

@Suppress("LongParameterList")
class OnboardingRepository @AutoKoin(Scope.SINGLE) constructor(
    private val onboardingApi: OnboardingApi,
    private val teamApi: TeamApi,
    private val onboardingPreferences: OnboardingPreferences,
    private val followableRepository: FollowableRepository,
    private val onboardingFollowPodcastFetcher: OnboardingFollowPodcastFetcher,
    private val surveyCache: SurveyCache
) : Repository {

    val isOnboarding: Boolean
        get() { return onboardingPreferences.isOnboarding }

    val recommendedTeamsStream
        get() = flow { emit(teamApi.getRecommendedTeams().toDomain()) }

    suspend fun getChosenFollowables(): List<FollowableItem> {
        return onboardingPreferences.chosenFollowables.map { it.toDomain() }
    }

    fun setChosenFollowables(value: List<FollowableItem>) {
        onboardingPreferences.chosenFollowables = value.map { it.followableId }
    }

    fun isChosenPodcast(podcast: OnboardingPodcastItem): Boolean {
        return onboardingPreferences.chosenPodcasts.contains(podcast.id)
    }

    fun setChosenPodcasts(value: List<OnboardingPodcastItem>) {
        onboardingPreferences.chosenPodcasts = value.map { it.id }
    }

    fun getRecommendedPodcasts() = flow<List<OnboardingPodcastItem>> {
        val followableItemsHolder = onboardingApi.fetchOnboardingFollowableItems()
        val recommendedPodcasts = followableItemsHolder?.recommendedPodcastsForUser?.map {
            // We currently use only the podcast recommendations; teams and leagues are pulled from local storage instead
            it.fragments.podcast.toDomain()
        } ?: throw OnboardingApiException()
        emit(recommendedPodcasts)
    }

    suspend fun saveFollowedPodcasts(podcastIds: List<String>) {
        podcastIds.forEach { id ->
            onboardingFollowPodcastFetcher.fetchRemote(OnboardingFollowPodcastFetcher.Params(id))
        }
    }

    suspend fun fetchSurvey(): ResponseStatus<Unit> {
        return safeApiRequest {
            val remoteData = onboardingApi.fetchUserAttributionSurveyOptions()
            surveyCache.updateCache(remoteData?.toDomain())
        }
    }

    fun getLocalSurvey() = surveyCache.survey

    suspend fun setHasSeenAttributionSurvey(): ResponseStatus<Unit> {
        return safeApiRequest {
            onboardingApi.postHasSeenSurvey()
        }
    }

    suspend fun submitSurveySelection(value: String, displayOrder: Int): ResponseStatus<Unit> {
        return safeApiRequest {
            onboardingApi.postSurveySelection(value, displayOrder)
        }
    }

    private fun ApolloResponse<RecommendedTeamsQuery.Data>.toDomain(): List<com.theathletic.followables.data.domain.Followable.Team> {
        val response = data ?: return emptyList()

        return response.onboardingRecommendedTeams.map {
            com.theathletic.followables.data.domain.Followable.Team(
                id = FollowableId(it?.id ?: "", Followable.Type.TEAM),
                name = it?.name ?: "",
                shortName = it?.shortname ?: "",
                displayName = it?.short_display_name ?: "",
                color = "",
                leagueId = FollowableId(it?.league_id ?: "", Followable.Type.LEAGUE),
                graphqlId = "",
                url = it?.url ?: "",
                searchText = it?.search_text ?: ""
            )
        }
    }

    private suspend fun Followable.Id.toDomain(): FollowableItem {
        val followable = followableRepository.getFollowable(this) ?: throw NoSuchFollowableException(this)
        val logoUrl = when (type) {
            Followable.Type.LEAGUE -> LogoUtility.getColoredLeagueLogoPath(id)
            Followable.Type.TEAM -> LogoUtility.getTeamLogoPath(id)
            else -> {
                Timber.w("Unsupported followable type: $type")
                LogoUtility.getTeamLogoPath(0)
            }
        }
        return FollowableItem(this, followable.name, logoUrl, true)
    }

    private fun Podcast.toDomain(): OnboardingPodcastItem {
        return OnboardingPodcastItem(
            id = id,
            title = title,
            metadataString = metadata_string,
            imageUrl = image_url,
            description = description,
            notifEpisodesOn = notif_episodes_on ?: false
        )
    }

    private fun GetUserAttributionSurveyQuery.GetUserAttributionSurvey.toDomain(): AttributionSurvey {
        return AttributionSurvey(
            headerText = header_text,
            subheaderText = subheader_text,
            ctaText = cta_text,
            surveyOptions = attribution_survey_options.map { option ->
                AttributionSurveyOption(
                    displayName = option.display_name,
                    remoteKey = option.value,
                    displayOrder = option.display_order
                )
            }
        )
    }
}

class NoSuchFollowableException(id: Followable.Id) : Exception("No such followable in local data store: $id")