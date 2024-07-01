package com.theathletic.scores.data.local

import com.google.common.truth.Truth.assertThat
import org.junit.Test

private const val TEST_GROUP_INDEX = 5

class UpdateScoresFeedDayUseCaseTest {

    private val useCase = UpdateScoresFeedDayUseCase()

    @Test
    fun `when a scores feed day group needs to be inserted into score feed it is done correctly`() {
        val baseScoresFeed = ScoresFeedFixtures.scoresFeedFixture()
        assertThat(baseScoresFeed.days[TEST_GROUP_INDEX].day).isEqualTo("2023-06-06")
        assertThat(baseScoresFeed.days[TEST_GROUP_INDEX].groups).isEmpty()

        val updatedScoresFeed = useCase(
            day = "2023-06-06",
            dayGroups = listOf(
                ScoresFeedFixtures.scoresFeedGroupFixture("Group10"),
                ScoresFeedFixtures.scoresFeedGroupFixture("Group11"),
                ScoresFeedFixtures.scoresFeedGroupFixture("Group12"),
                ScoresFeedFixtures.scoresFeedGroupFixture("Group13"),
                ScoresFeedFixtures.scoresFeedGroupFixture("Group14"),
                ScoresFeedFixtures.scoresFeedGroupFixture("Group15"),
            ),
            feed = baseScoresFeed
        )

        assertThat(updatedScoresFeed.days[TEST_GROUP_INDEX].day).isEqualTo("2023-06-06")
        assertThat(updatedScoresFeed.days[TEST_GROUP_INDEX].groups.size).isEqualTo(6)
        assertThat(updatedScoresFeed.days[TEST_GROUP_INDEX].groups.map { it.title }).isEqualTo(
            listOf("Group10", "Group11", "Group12", "Group13", "Group14", "Group15")
        )
    }

    @Test
    fun `when a scores feed day group needs to be updated in the  it is done correctly`() {
        val baseScoresFeed = ScoresFeedFixtures.scoresFeedFixture(populateDay = true)
        assertThat(baseScoresFeed.days[TEST_GROUP_INDEX].day).isEqualTo("2023-06-06")
        assertThat(baseScoresFeed.days[TEST_GROUP_INDEX].groups.size).isEqualTo(6)
        assertThat(baseScoresFeed.days[TEST_GROUP_INDEX].groups.map { it.title }).isEqualTo(
            listOf("Group100", "Group101", "Group102", "Group103", "Group104", "Group105")
        )

        val updatedScoresFeed = useCase(
            day = "2023-06-06",
            dayGroups = listOf(
                ScoresFeedFixtures.scoresFeedGroupFixture("Group20"),
                ScoresFeedFixtures.scoresFeedGroupFixture("Group21"),
                ScoresFeedFixtures.scoresFeedGroupFixture("Group22"),
                ScoresFeedFixtures.scoresFeedGroupFixture("Group23"),
                ScoresFeedFixtures.scoresFeedGroupFixture("Group24"),
                ScoresFeedFixtures.scoresFeedGroupFixture("Group25"),
                ScoresFeedFixtures.scoresFeedGroupFixture("Group26"),
            ),
            feed = baseScoresFeed
        )

        assertThat(updatedScoresFeed.days[TEST_GROUP_INDEX].day).isEqualTo("2023-06-06")
        assertThat(updatedScoresFeed.days[TEST_GROUP_INDEX].groups.size).isEqualTo(7)
        assertThat(updatedScoresFeed.days[TEST_GROUP_INDEX].groups.map { it.title }).isEqualTo(
            listOf("Group20", "Group21", "Group22", "Group23", "Group24", "Group25", "Group26")
        )
    }
}

object ScoresFeedFixtures {

    fun scoresFeedFixture(populateDay: Boolean = false) = ScoresFeedLocalModel(
        id = "scoresfeed-base",
        days = listOf(
            scoresFeedDayFixture(
                day = "2023-06-01",
                isTopGames = false,
                groups = emptyList()
            ),
            scoresFeedDayFixture(
                day = "2023-06-02",
                isTopGames = false,
                groups = emptyList()
            ),
            scoresFeedDayFixture(
                day = "2023-06-03",
                isTopGames = false,
                groups = emptyList()
            ),
            scoresFeedDayFixture(
                day = "2023-06-04",
                isTopGames = true,
                groups = listOf(
                    scoresFeedGroupFixture("Group1"),
                    scoresFeedGroupFixture("Group2"),
                    scoresFeedGroupFixture("Group3"),
                    scoresFeedGroupFixture("Group4"),
                    scoresFeedGroupFixture("Group5"),
                )
            ),
            scoresFeedDayFixture(
                day = "2023-06-05",
                isTopGames = false,
                groups = emptyList()
            ),
            scoresFeedDayFixture(
                day = "2023-06-06",
                isTopGames = false,
                groups = if (populateDay) {
                    listOf(
                        scoresFeedGroupFixture("Group100"),
                        scoresFeedGroupFixture("Group101"),
                        scoresFeedGroupFixture("Group102"),
                        scoresFeedGroupFixture("Group103"),
                        scoresFeedGroupFixture("Group104"),
                        scoresFeedGroupFixture("Group105"),
                    )
                } else {
                    emptyList()
                }
            ),
            scoresFeedDayFixture(
                day = "2023-06-07",
                isTopGames = false,
                groups = emptyList()
            )
        ),
        navigationBar = emptyList()
    )

    private fun scoresFeedDayFixture(
        day: String,
        isTopGames: Boolean,
        groups: List<ScoresFeedGroup>
    ) = ScoresFeedDay(
        id = "scoresfeed-day-$day",
        day = day,
        isTopGames = isTopGames,
        groups = groups
    )

    fun scoresFeedGroupFixture(title: String) = ScoresFeedBaseGroup(
        id = "scoresfeed-group-$title",
        title = title,
        subTitle = null,
        blocks = emptyList(),
        widget = null
    )
}