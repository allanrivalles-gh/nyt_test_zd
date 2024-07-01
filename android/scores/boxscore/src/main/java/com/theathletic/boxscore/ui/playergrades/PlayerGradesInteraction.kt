package com.theathletic.boxscore.ui.playergrades

interface PlayerGradesInteraction

interface PlayerGradesInteractor {
    fun send(interaction: PlayerGradesInteraction)
}

val EmptyPlayerGradesInteractor = object : PlayerGradesInteractor {
    override fun send(interaction: PlayerGradesInteraction) {
        // Do nothing
    }
}