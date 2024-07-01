package com.theathletic.rooms.create.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class LiveRoomCreationInputValidator @AutoKoin(Scope.SINGLE) constructor() {

    companion object {
        const val TITLE_MAX_CHARACTERS = 75
        const val DESCRIPTION_MAX_CHARACTERS = 500
    }

    fun isValid(input: LiveRoomCreationInput): Boolean {
        val title = input.title.trim()
        val validTitle = title.isNotEmpty() && title.length <= TITLE_MAX_CHARACTERS

        val description = input.description.trim()
        val validDescription = description.isNotEmpty() && description.length <= DESCRIPTION_MAX_CHARACTERS

        val validHosts = input.hosts.isNotEmpty() || input.currentUserIsHost

        val validTags = input.tags.isNotEmpty()

        return validTitle &&
            validDescription &&
            validHosts &&
            validTags
    }
}