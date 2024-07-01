package com.theathletic.scores

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.theathletic.ui.asString
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class GameUtilTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `should use home v away for soccer game`() {
        val resourceString = GameUtil.buildGameTitle(
            "WHU",
            "ARS",
            firstTeamTbd = false,
            secondTeamTbd = false,
            isSoccer = true
        )
        assertEquals("WHU v ARS", resourceString.asString(context, ""))
    }

    @Test
    fun `should use home v TBC for soccer game where away team is TBD`() {
        val resourceString = GameUtil.buildGameTitle(
            "WHU",
            "",
            firstTeamTbd = false,
            secondTeamTbd = true,
            isSoccer = true
        )
        assertEquals("WHU v TBC", resourceString.asString(context, ""))
    }

    @Test
    fun `should use TBC v away for soccer game where home team is TBD`() {
        val resourceString = GameUtil.buildGameTitle(
            "",
            "ARS",
            firstTeamTbd = true,
            secondTeamTbd = false,
            isSoccer = true
        )
        assertEquals("TBC v ARS", resourceString.asString(context, ""))
    }

    @Test
    fun `should use TBC v TBC for soccer game where both teams are TBD`() {
        val resourceString = GameUtil.buildGameTitle(
            "",
            "",
            firstTeamTbd = true,
            secondTeamTbd = true,
            isSoccer = true
        )
        assertEquals("TBC v TBC", resourceString.asString(context, ""))
    }

    @Test
    fun `should use away @ home for non-soccer game`() {
        val resourceString = GameUtil.buildGameTitle(
            "CHC",
            "MIL",
            firstTeamTbd = false,
            secondTeamTbd = false,
            isSoccer = false
        )
        assertEquals("CHC @ MIL", resourceString.asString(context, ""))
    }

    @Test
    fun `should use TBC @ TBC for non-soccer game where both teams are TBD`() {
        val resourceString = GameUtil.buildGameTitle(
            "",
            "",
            firstTeamTbd = true,
            secondTeamTbd = true,
            isSoccer = false
        )
        assertEquals("TBC @ TBC", resourceString.asString(context, ""))
    }

    @Test
    fun `should use TBC @ home for non-soccer game where away team is TBD`() {
        val resourceString = GameUtil.buildGameTitle(
            "",
            "MIL",
            firstTeamTbd = true,
            secondTeamTbd = false,
            isSoccer = false
        )
        assertEquals("TBC @ MIL", resourceString.asString(context, ""))
    }

    @Test
    fun `should use away @ TBC for non-soccer game where home team is TBD`() {
        val resourceString = GameUtil.buildGameTitle(
            "CHC",
            "",
            firstTeamTbd = false,
            secondTeamTbd = true,
            isSoccer = false
        )
        assertEquals("CHC @ TBC", resourceString.asString(context, ""))
    }
}