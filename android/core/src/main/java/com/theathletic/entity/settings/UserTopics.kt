package com.theathletic.entity.settings

import androidx.databinding.ObservableBoolean
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.theathletic.core.BuildConfig
import com.theathletic.entity.main.FEED_MY_FEED_ID
import com.theathletic.entity.main.League
import com.theathletic.entity.main.Sport
import com.theathletic.utility.LogoUtility
import java.io.Serializable

// TT Response
data class UserTopics(
    @SerializedName("teams") var teams: List<UserTopicsItemTeam>,
    @SerializedName("leagues") var leagues: MutableList<UserTopicsItemLeague>,
    @SerializedName("authors") var authors: List<UserTopicsItemAuthor>,
    @SerializedName("podcasts") var podcasts: List<UserTopicsItemPodcast>
)

// TT User Topics Base Item
open class UserTopicsBaseItem : Serializable, Cloneable {
    @PrimaryKey
    @SerializedName("id")
    open var id: Long = 0L

    @SerializedName("name")
    open var name: String = ""

    @SerializedName("subtitle")
    open var subName: String = ""

    @SerializedName("search_text")
    var searchText: String? = null

    @SerializedName("color")
    var color: String? = ""

    var isFollowed: Boolean = false

    @Ignore
    var selected = ObservableBoolean(false)

    @Ignore
    open val abbreviatedName: String = ""

    public override fun clone(): UserTopicsBaseItem {
        val o = super.clone() as UserTopicsBaseItem
        o.selected = ObservableBoolean(false)
        return o
    }

    override fun hashCode(): Int {
        return this::class.java.simpleName.hashCode() + id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return when {
            (other == null || other !is UserTopicsBaseItem) -> false

            (this::class.java == other::class.java) -> this.id == other.id

            else -> false
        }
    }

    fun getComposedId(): String = this.javaClass.simpleName + "_" + id

    open fun isMyFeedItem() = false

    open fun getImageUrl() = ""

    open fun isNCAAFBItem() = false

    open fun isNCAABBItem() = false

    open fun getSport() = Sport.UNKNOWN
}

// TT User Topics Team Item
@Entity(tableName = "user_topics_team")
open class UserTopicsItemTeam :
    UserTopicsBaseItem(),
    GameNotificationsTopic,
    StoriesNotificationsTopic {
    @SerializedName("shortname")
    var shortname: String? = ""

    @SerializedName("city_id")
    var cityId: Long = -1L

    @SerializedName("league_id")
    var leagueId: Long = -1L

    @SerializedName("ath_team_id")
    var graphqlId: String? = null

    @SerializedName("notif_stories")
    override var notifyStories: Boolean = false

    @SerializedName("notif_games")
    override var notifyGames: Boolean = false

    @SerializedName("evergreen_posts")
    var evergreenPosts: Long = 0

    var evergreenPostsReadCount: Long = -1

    @SerializedName("color_gradient")
    var colorGradient: String? = ""

    private val league: League
        get() = League.parseFromId(leagueId)

    override val abbreviatedName: String get() {
        val safeShortname = shortname
        return when {
            safeShortname == null -> ""
            safeShortname.isEmpty() -> name
            else -> safeShortname
        }
    }

    override fun getImageUrl(): String = LogoUtility.getTeamLogoPath(id)

    override fun isMyFeedItem() = id == FEED_MY_FEED_ID

    override fun isNCAAFBItem() = league == League.NCAA_FB

    override fun isNCAABBItem() = league == League.NCAA_BB

    override fun getSport() = league.sport

    override fun equals(other: Any?): Boolean {
        return when {
            (other !is UserTopicsItemTeam) -> false

            (this.colorGradient != other.colorGradient || this.color != other.color) -> false

            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return this::class.java.simpleName.hashCode() + id.hashCode()
    }
}

// TT User Topics League Item
@Entity(tableName = "user_topics_league")
class UserTopicsItemLeague : UserTopicsBaseItem(), StoriesNotificationsTopic {
    @SerializedName("notif_stories")
    override var notifyStories: Boolean = false

    @SerializedName("shortname")
    var shortname: String = ""

    @SerializedName("has_scores")
    var hasScores: Boolean = false

    @SerializedName("display_order")
    var displayOrder = 0

    @SerializedName("scores_display_order")
    var scoreDisplayOrder = 0

    @SerializedName("status")
    var status = "live"

    @SerializedName("season_status")
    var seasonStatus = "live"

    val league: League
        get() = League.parseFromId(id)

    override val abbreviatedName get() = when {
        shortname.isEmpty() -> name
        else -> shortname
    }

    override fun getImageUrl() = LogoUtility.getColoredLeagueLogoPath(id)

    override fun isNCAAFBItem() = league == League.NCAA_FB

    override fun isNCAABBItem() = league == League.NCAA_BB

    override fun getSport() = league.sport

    val isStatusLive get() = if (BuildConfig.DEV_ENVIRONMENT) {
        status == "live" || status == "beta"
    } else {
        status == "live"
    }
}

// TT User Topics Author Item
@Entity(tableName = "user_topics_author")
class UserTopicsItemAuthor : UserTopicsBaseItem(), StoriesNotificationsTopic {
    @SerializedName("image_url")
    var imgUrl: String? = ""

    @SerializedName("notif_stories")
    override var notifyStories: Boolean = false

    @SerializedName("shortname")
    var shortname: String? = ""

    var subscribed: Boolean = false

    override val abbreviatedName get() = if (shortname.isNullOrEmpty()) name else shortname!!

    override fun getImageUrl() = imgUrl ?: ""
}

// TT User Topics Podcast Item
@Entity(tableName = "user_topics_podcast")
class UserTopicsItemPodcast {
    @PrimaryKey
    @SerializedName("id")
    var id: Long = 0L

    @SerializedName("title")
    var title: String = ""

    @SerializedName("notif_episodes")
    var notifEpisodes: Boolean = false
}

// TT User Topics Category Item
class UserTopicsItemCategory(override var id: Long, override var name: String) : UserTopicsBaseItem()

// TT User Topics InkStories Item
class UserTopicsItemInkStories(override var name: String) : UserTopicsBaseItem()