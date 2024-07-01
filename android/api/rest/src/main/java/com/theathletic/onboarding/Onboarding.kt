package com.theathletic.onboarding

import androidx.databinding.ObservableBoolean
import com.google.gson.annotations.SerializedName
import com.theathletic.adapter.TheSame
import com.theathletic.entity.settings.UserTopicsItemLeague
import com.theathletic.entity.settings.UserTopicsItemTeam
import com.theathletic.utility.LogoUtility
import java.io.Serializable
import com.theathletic.entity.main.League as SportLeague

data class OnboardingPodcastItemResponse(
    @SerializedName("id") var id: Long = -1L,
    @SerializedName("title") var title: String = "",
    @SerializedName("metadata_string") var metadataString: String? = "",
    @SerializedName("image_url") var imageUrl: String? = "",
    @SerializedName("team_ids") var teamIds: ArrayList<Long> = ArrayList(),
    @SerializedName("league_ids") var leagueIds: ArrayList<Long> = ArrayList(),
    @SerializedName("is_following") var selected: Boolean
)

sealed class OnboardingItem(
    var id: Long = -1L,
    var title: String = "",
    var imageUrl: String? = "",
    var selected: ObservableBoolean = ObservableBoolean(false)
) : Serializable, TheSame {

    data class Podcast(
        var description: String? = "",
        var teamIds: ArrayList<Long> = ArrayList(),
        var leagueIds: ArrayList<Long> = ArrayList(),
        var isLoading: ObservableBoolean = ObservableBoolean(false)
    ) : OnboardingItem() {
        companion object {
            fun createFrom(item: OnboardingPodcastItemResponse) = Podcast().apply {
                id = item.id
                title = item.title
                description = item.metadataString
                imageUrl = item.imageUrl
                selected.set(item.selected)
            }
        }

        override fun isContentTheSame(other: Any?): Boolean = when {
            other !is Podcast -> false
            description != other.description -> false
            teamIds != other.teamIds -> false
            leagueIds != other.leagueIds -> false
            isLoading.get() != other.isLoading.get() -> false
            else -> super.isContentTheSame(other)
        }
    }

    class Team(var searchText: String = "") : OnboardingItem() {
        companion object {
            fun createFrom(item: UserTopicsItemTeam) = Team().apply {
                id = item.id
                title = item.name
                searchText = item.searchText ?: item.name
                imageUrl = LogoUtility.getTeamLogoPath(item.id)
                selected.set(item.isFollowed)
            }
        }

        override fun isContentTheSame(other: Any?): Boolean = when {
            other !is Team -> false
            searchText != other.searchText -> false
            else -> super.isContentTheSame(other)
        }
    }

    class League(var searchText: String = "") : OnboardingItem() {
        companion object {
            fun createFrom(item: UserTopicsItemLeague) = League().apply {
                id = item.id
                title = item.name
                searchText = item.searchText ?: item.name
                imageUrl = LogoUtility.getColoredLeagueLogoPath(item.id)
                selected.set(item.isFollowed)
            }
        }

        override fun isContentTheSame(other: Any?): Boolean = when {
            other !is League -> false
            searchText != other.searchText -> false
            else -> super.isContentTheSame(other)
        }

        fun isNCAAFBItem() = SportLeague.parseFromId(id) == SportLeague.NCAA_FB

        fun isNCAABBItem() = SportLeague.parseFromId(id) == SportLeague.NCAA_BB
    }

    class UpdateLocation : OnboardingItem()
    class LeagueNote : OnboardingItem()
    class PodcastNote : OnboardingItem()
    class RecommendedForYouTitle : OnboardingItem()
    class VerticalSpace : OnboardingItem()
    class BasedOnLocationTitle(var updateLocationButtonVisible: Boolean) : OnboardingItem() {
        override fun isContentTheSame(other: Any?): Boolean = when {
            other !is BasedOnLocationTitle -> false
            updateLocationButtonVisible != other.updateLocationButtonVisible -> false
            else -> super.isContentTheSame(other)
        }
    }

    override fun isItemTheSame(other: Any?): Boolean = when {
        this === other -> true
        other !is OnboardingItem -> false
        id == other.id -> true
        else -> false
    }

    override fun isContentTheSame(other: Any?): Boolean = when {
        other !is OnboardingItem -> false
        id != other.id -> false
        title != other.title -> false
        imageUrl != other.imageUrl -> false
        selected.get() != other.selected.get() -> false
        else -> true
    }
}