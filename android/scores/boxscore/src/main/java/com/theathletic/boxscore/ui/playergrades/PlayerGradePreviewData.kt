package com.theathletic.boxscore.ui.playergrades

import com.theathletic.boxscore.ui.modules.PlayerGradeCardModule
import com.theathletic.boxscore.ui.modules.PlayerGradeMiniCardModule
import com.theathletic.boxscore.ui.modules.PlayerGradeModule
import com.theathletic.ui.ResourceString.StringWrapper

object PlayerGradePreviewData {

    val playerGradeCardModule = PlayerGradeCardModule(
        id = "001",
        playerGradeCard = PlayerGradeModel.PlayerGradeCard(
            isGraded = true,
            awardedGrade = 3,
            player = PlayerGradeModel.Player(
                id = "002",
                name = "N. Chubb",
                position = "QB",
                totalGrades = 1,
                averageGrade = "3.5",
                teamLogos = emptyList(),
                headshots = emptyList(),
                stats = MutableList(4) {
                    PlayerGradeModel.Stat(
                        title = StringWrapper("STAT"),
                        value = "9999"
                    )
                },
                teamColor = "#ABCDEF"
            )
        )
    )

    val playerGradeCardNotGradedModule = PlayerGradeCardModule(
        id = "001",
        playerGradeCard = PlayerGradeModel.PlayerGradeCard(
            isGraded = false,
            awardedGrade = 0,
            player = PlayerGradeModel.Player(
                id = "003",
                name = "N. Chubb",
                position = "QB",
                totalGrades = 999,
                averageGrade = "3.5",
                teamLogos = emptyList(),
                headshots = emptyList(),
                stats = MutableList(4) {
                    PlayerGradeModel.Stat(
                        title = StringWrapper("STAT"),
                        value = "9999"
                    )
                },
                teamColor = "#ABCDEF"
            )
        )
    )

    val playerGradeModule = PlayerGradeModule(
        id = "001",
        playerGrades = BoxScorePlayerGrades.PlayerGrades(
            teams = BoxScorePlayerGrades.Teams(
                firstTeamName = "Steelers",
                secondTeamName = "Browns"
            ),
            isLocked = false,
            firstTeamPlayerGrades = listOf(playerGradeCardModule, playerGradeCardModule, playerGradeCardModule),
            secondTeamPlayerGrades = listOf(playerGradeCardModule, playerGradeCardModule, playerGradeCardModule)
        ),
        showFirstTeam = true
    )

    val miniCard = PlayerGradeMiniCardModel(
        id = "001",
        playerName = "Archie Marvik John",
        playerStats = "QB, 99/88, 123 YDS",
        playerHeadshot = emptyList(),
        averageGrade = "3.7",
        totalGrades = 543,
        isLocked = true,
        isGraded = false,
        awardedGrade = 3,
        teamColor = "#ABCDEF",
        teamLogos = emptyList()
    )

    val lockedUngradedMiniCardModule =
        PlayerGradeMiniCardModule(
            id = "001",
            playerGradeMiniCard = miniCard.copy(
                isLocked = true,
                isGraded = false,
                totalGrades = 0
            )
        )

    val gradedMiniCardModule = PlayerGradeMiniCardModule(
        id = "001",
        playerGradeMiniCard = miniCard.copy(
            awardedGrade = 3,
            averageGrade = "4.6",
            playerStats = "30 Minutes, 17 Touches, 62.5% Pass Completion",
            isGraded = true,
            isLocked = false
        )
    )

    val ungradedMiniCardModule =
        PlayerGradeMiniCardModule(id = "001", playerGradeMiniCard = miniCard.copy(awardedGrade = 0, isGraded = false, totalGrades = 1))

    val unlockedUngradedMiniCardModule =
        PlayerGradeMiniCardModule(
            id = "001",
            playerGradeMiniCard = miniCard.copy(awardedGrade = 0, isGraded = false, isLocked = false)
        )

    val playerGradeLockedGradedModule = PlayerGradeModule(
        id = "001",
        playerGrades = BoxScorePlayerGrades.PlayerGrades(
            teams = BoxScorePlayerGrades.Teams(
                firstTeamName = "Steelers",
                secondTeamName = "Browns"
            ),
            isLocked = true,
            firstTeamPlayerGrades = listOf(gradedMiniCardModule, gradedMiniCardModule, gradedMiniCardModule),
            secondTeamPlayerGrades = listOf(gradedMiniCardModule, gradedMiniCardModule, gradedMiniCardModule)
        ),
        showFirstTeam = true
    )

    val playerGradeLockedUngradedModule = PlayerGradeModule(
        id = "001",
        playerGrades = BoxScorePlayerGrades.PlayerGrades(
            teams = BoxScorePlayerGrades.Teams(
                firstTeamName = "Steelers",
                secondTeamName = "Browns"
            ),
            isLocked = true,
            firstTeamPlayerGrades = listOf(
                lockedUngradedMiniCardModule,
                lockedUngradedMiniCardModule,
                lockedUngradedMiniCardModule
            ),
            secondTeamPlayerGrades = listOf(
                lockedUngradedMiniCardModule,
                lockedUngradedMiniCardModule,
                lockedUngradedMiniCardModule
            )
        ),
        showFirstTeam = true
    )
}