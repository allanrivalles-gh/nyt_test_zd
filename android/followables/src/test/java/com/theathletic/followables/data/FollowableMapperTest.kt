package com.theathletic.followables.data

import com.google.common.truth.Truth.assertThat
import com.theathletic.followables.test.fixtures.authorFixture
import com.theathletic.followables.test.fixtures.leagueFixture
import com.theathletic.followables.test.fixtures.localAuthorFixture
import com.theathletic.followables.test.fixtures.localLeagueFixture
import com.theathletic.followables.test.fixtures.localTeamFixture
import com.theathletic.followables.test.fixtures.teamFixture
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FollowableMapperTest {

    @Test
    fun `map team local model to domain`() {
        val team = localTeamFixture(name = "Golden State", shortName = "GS")

        val followable = team.toDomain()

        assertThat(followable)
            .isEqualTo(teamFixture(name = "Golden State", shortName = "GS"))
    }

    @Test
    fun `map league to followable`() {
        val league = localLeagueFixture(shortName = "NHL")

        val followable = league.toDomain()

        assertThat(followable).isEqualTo(leagueFixture(shortName = "NHL"))
    }

    @Test
    fun `map author to followable`() {
        val league = localAuthorFixture(shortName = "R.Edg")

        val followable = league.toDomain()

        assertThat(followable).isEqualTo(authorFixture(shortName = "R.Edg"))
    }
}