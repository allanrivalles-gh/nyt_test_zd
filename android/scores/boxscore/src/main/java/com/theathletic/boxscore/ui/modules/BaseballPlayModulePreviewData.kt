package com.theathletic.boxscore.ui.modules

object BaseballPlayModulePreviewData {
    val subPlays = listOf(
        BaseballPlayModule.PitchPlay(
            title = "Homerun",
            description = "86mph curveball",
            pitchNumber = 4,
            pitchOutcomeType = BaseballPitchOutcomeType.HIT,
            occupiedBases = emptyList(),
            hitZone = 21,
            pitchZone = 4
        ),
        BaseballPlayModule.PitchPlay(
            title = "Foul Ball",
            description = "82mph curveball",
            pitchNumber = 3,
            pitchOutcomeType = BaseballPitchOutcomeType.DEAD_BALL,
            occupiedBases = emptyList(),
            hitZone = 12,
            pitchZone = 131
        ),
        BaseballPlayModule.PitchPlay(
            title = "Ball",
            description = "93mph sinker",
            pitchNumber = 2,
            pitchOutcomeType = BaseballPitchOutcomeType.BALL,
            occupiedBases = emptyList(),
            hitZone = 12,
            pitchZone = 131
        ),
        BaseballPlayModule.StandardSubPlay(
            description = "Ramírez scored on passed ball by Roberto Perez."
        ),
        BaseballPlayModule.PitchPlay(
            title = "Strike",
            description = "92mph slider",
            pitchNumber = 1,
            pitchOutcomeType = BaseballPitchOutcomeType.STRIKE,
            occupiedBases = listOf(1, 3),
            hitZone = 9,
            pitchZone = 8
        ),
    )
}