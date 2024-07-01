package com.theathletic.brackets.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.brackets.data.BracketsGraphqlApi

/**
 * ReplayGameUseCase is used for replaying games that are less then 2 weeks old on the dev build.
 * This is for testing purposes only.
 */
class ReplayGameUseCase @AutoKoin constructor(
    private val bracketsGraphqlApi: BracketsGraphqlApi
) {
    suspend operator fun invoke(gameId: String) {
        bracketsGraphqlApi.gameReplay(gameId)
    }
}