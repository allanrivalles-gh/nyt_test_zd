package com.theathletic.brackets.ui

import com.theathletic.brackets.data.local.TournamentRoundGame
import com.theathletic.brackets.ui.components.HeaderRowUi
import com.theathletic.data.SizedImage

private const val EAST = "East"
private const val WEST = "West"
private const val NORTH = "North"
private const val SOUTH = "South"

object BracketsPreviewData {
    private val labels = listOf(EAST, WEST, NORTH, SOUTH)
    val logos = listOf(
        SizedImage(
            width = 20,
            height = 20,
            uri = "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-91-72x72.png",
        )
    )
    val tabs = List(7) { HeaderRowUi.BracketTab(label = "Title of Round", isCurrentRound = true) }
    val currentRoundIndex = 0
    val preGameMatch = BracketsUi.Match(
        id = "123",
        dateAndTimeText = "Sun, Mar 17, Wells Fargo Arena",
        firstTeam = BracketsUi.Team.PreGameTeam(
            name = "UT",
            seed = "16",
            logos = logos,
            record = "14-1",
        ),
        secondTeam = BracketsUi.Team.PreGameTeam(
            name = "UT",
            seed = "16",
            logos = logos,
            record = "14-1",
        ),
        hasBoxScore = true,
        phase = TournamentRoundGame.Phase.PreGame,
    )
    val postGameMatch = BracketsUi.Match(
        id = "123",
        dateAndTimeText = "Sun, Mar 17, Wells Fargo Arena",
        firstTeam = BracketsUi.Team.PostGameTeam(
            name = "DAV",
            logos = logos,
            score = "100",
            isWinner = true
        ),
        secondTeam = BracketsUi.Team.PostGameTeam(
            name = "DAV",
            logos = logos,
            score = "90",
            isWinner = false
        ),
        hasBoxScore = true,
        phase = TournamentRoundGame.Phase.PreGame
    )
    val placeholderMatch = BracketsUi.Match(
        id = "123",
        dateAndTimeText = "Sun, Mar 17, Wells Fargo Arena",
        firstTeam = BracketsUi.Team.PlaceholderTeam(
            name = ""
        ),
        secondTeam = BracketsUi.Team.PlaceholderTeam(
            name = "DAV"
        ),
        hasBoxScore = true,
        phase = TournamentRoundGame.Phase.PreGame
    )
    val rounds = listOf(
        BracketsUi.Round.Pre(
            groups = listOfGroups(numberOfGroups = 4, numberOfMatches = 1)
        ),
        BracketsUi.Round.Initial(
            groups = listOfGroups(numberOfGroups = 4, numberOfMatches = 8)
        ),
        BracketsUi.Round.Standard(
            groups = listOfGroups(numberOfGroups = 4, numberOfMatches = 4)
        ),
        BracketsUi.Round.Standard(
            groups = listOfGroups(numberOfGroups = 4, numberOfMatches = 2)
        ),
        BracketsUi.Round.Standard(
            groups = listOfGroups(numberOfGroups = 4, numberOfMatches = 1)
        ),
        BracketsUi.Round.SemiFinal(
            groups = listOfGroups(numberOfGroups = 1, numberOfMatches = 1) +
                listOf(
                    BracketsUi.Group(
                        label = labels.random(),
                        matches = listOfMatches(1),
                    )
                )
        ),
        BracketsUi.Round.Final(
            groups = listOfGroups(numberOfGroups = 1, numberOfMatches = 1)
        )
    )

    private fun listOfGroups(numberOfGroups: Int, numberOfMatches: Int) = List(numberOfGroups) {
        BracketsUi.Group(
            label = labels.random(),
            matches = listOfMatches(numberOfMatches)
        )
    }

    private fun listOfMatches(count: Int) = List(count) {
        BracketsUi.Match(
            id = "123",
            dateAndTimeText = "Sun, Mar 17, Wells Fargo Arena",
            firstTeam = BracketsUi.Team.PostGameTeam(
                name = "DAV",
                logos = logos,
                score = "100",
                isWinner = true
            ),
            secondTeam = BracketsUi.Team.PostGameTeam(
                name = "DAV",
                logos = logos,
                score = "90",
                isWinner = false
            ),
            hasBoxScore = true,
            phase = TournamentRoundGame.Phase.PreGame
        )
    }
}