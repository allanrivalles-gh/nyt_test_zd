package com.theathletic.repository.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.theathletic.entity.settings.UserTopicsItemAuthor
import com.theathletic.entity.settings.UserTopicsItemLeague
import com.theathletic.entity.settings.UserTopicsItemTeam
import com.theathletic.followable.Followable
import com.theathletic.profile.manage.UserTopicId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val ABBREVIATION_NUM_CHARS = 6

data class FollowableItems(
    val teams: List<TeamLocal>,
    val leagues: List<LeagueLocal>,
    val authors: List<AuthorLocal>
)

@Entity(tableName = "team")
@TypeConverters(TeamDbConverters::class)
data class TeamLocal(
    @PrimaryKey
    override val id: Followable.Id,
    override val name: String,
    override val shortName: String,
    override val searchText: String,
    val url: String = "",
    val colorScheme: ColorScheme,
    val displayName: String,
    val leagueId: Followable.Id,
    val graphqlId: String? = null
) : Followable {

    @JsonClass(generateAdapter = true)
    data class ColorScheme(
        val primaryColor: String? = null,
        val iconContrastColor: String? = null,
    )
}

@Entity(tableName = "league")
@TypeConverters(LeagueDbConverters::class)
data class LeagueLocal(
    @PrimaryKey
    override val id: Followable.Id,
    override val name: String,
    override val shortName: String,
    override val searchText: String,
    val league: com.theathletic.entity.main.League,
    val url: String = "",
    val sportType: String? = null,
    val hasActiveBracket: Boolean = false,
    val hasScores: Boolean = true,
    val displayName: String,
) : Followable

@Entity(tableName = "author")
data class AuthorLocal(
    @PrimaryKey
    override val id: Followable.Id,
    override val name: String,
    override val shortName: String,
    override val searchText: String,
    val imageUrl: String,
    val url: String = ""
) : Followable

@Entity(tableName = "user_following")
data class UserFollowingItem(
    @PrimaryKey
    val id: Followable.Id
)

fun TeamLocal.shortName(ncaaLeagues: List<LeagueLocal>) =
    ncaaLeagues.firstOrNull { it.id == leagueId }?.shortName ?: shortName

fun TeamLocal.name(ncaaLeagues: List<LeagueLocal>) =
    ncaaLeagues.firstOrNull { it.id == leagueId }?.name ?: name

fun AuthorLocal.nameAbbreviation() = shortName.take(ABBREVIATION_NUM_CHARS)

// Temporary mappers for the old data types so we can put the new followable work
// behind a feature switch, will remove these after we verify the new implementation is working correctly
fun UserTopicsItemTeam.toFollowableModel() = TeamLocal(
    id = Followable.Id(
        id = id.toString(),
        type = Followable.Type.TEAM
    ),
    name = name,
    shortName = shortname.orEmpty(),
    searchText = searchText ?: name,
    colorScheme = TeamLocal.ColorScheme(
        primaryColor = color,
        iconContrastColor = color,
    ),
    leagueId = Followable.Id(
        id = leagueId.toString(),
        type = Followable.Type.LEAGUE
    ),
    displayName = name
)

fun UserTopicsItemLeague.toFollowableModel() = LeagueLocal(
    id = Followable.Id(
        id = id.toString(),
        type = Followable.Type.LEAGUE
    ),
    name = name,
    shortName = shortname,
    searchText = searchText ?: name,
    league = league,
    displayName = name,
)

fun UserTopicsItemAuthor.toFollowableModel() = AuthorLocal(
    id = Followable.Id(
        id = id.toString(),
        type = Followable.Type.AUTHOR
    ),
    name = name,
    shortName = shortname.orEmpty(),
    imageUrl = imgUrl.orEmpty(),
    searchText = searchText ?: name
)

fun Followable.Id.toUserTopicId() = when (type) {
    Followable.Type.TEAM -> UserTopicId.Team(id = id.toLongOrNull() ?: -1)
    Followable.Type.LEAGUE -> UserTopicId.League(id = id.toLongOrNull() ?: -1)
    Followable.Type.AUTHOR -> UserTopicId.Author(id = id.toLongOrNull() ?: -1)
}

fun UserTopicId.toFollowableId() = when (this) {
    is UserTopicId.League -> Followable.Id(id.toString(), Followable.Type.LEAGUE)
    is UserTopicId.Team -> Followable.Id(id.toString(), Followable.Type.TEAM)
    is UserTopicId.Author -> Followable.Id(id.toString(), Followable.Type.AUTHOR)
}

internal class TeamDbConverters : KoinComponent {
    private val moshi by inject<Moshi>()

    @TypeConverter
    fun colorSchemeToString(
        colorScheme: TeamLocal.ColorScheme
    ): String = moshi.adapter(TeamLocal.ColorScheme::class.java).toJson(colorScheme)

    @TypeConverter
    fun stringToColorScheme(
        value: String
    ): TeamLocal.ColorScheme = try {
        moshi.adapter(TeamLocal.ColorScheme::class.java).fromJson(value)
    } catch (exception: Exception) {
        null
    } ?: TeamLocal.ColorScheme()
}

internal class LeagueDbConverters {
    @TypeConverter
    fun leagueCodeToString(leagueCode: com.theathletic.entity.main.League): String = leagueCode.name

    @TypeConverter
    fun stringToLeagueCode(value: String) = try {
        com.theathletic.entity.main.League.valueOf(value)
    } catch (ex: Exception) {
        com.theathletic.entity.main.League.UNKNOWN
    }
}