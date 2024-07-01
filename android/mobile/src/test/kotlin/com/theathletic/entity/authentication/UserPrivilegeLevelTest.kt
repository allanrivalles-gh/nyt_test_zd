package com.theathletic.entity.authentication

import org.junit.Assert
import org.junit.Test

class UserPrivilegeLevelTest {

    @Test
    fun userPrivilegeLevel_ParseUserLevelCorrectlyFromNumber() {
        Assert.assertTrue(UserPrivilegeLevel.from(-259) == UserPrivilegeLevel.REGULAR_USER)
        Assert.assertTrue(UserPrivilegeLevel.from(-1) == UserPrivilegeLevel.REGULAR_USER)
        Assert.assertTrue(UserPrivilegeLevel.from(0) == UserPrivilegeLevel.REGULAR_USER)
        Assert.assertTrue(UserPrivilegeLevel.from(1) == UserPrivilegeLevel.CONTRIBUTOR)
        Assert.assertTrue(UserPrivilegeLevel.from(2) == UserPrivilegeLevel.AUTHOR)
        Assert.assertTrue(UserPrivilegeLevel.from(3) == UserPrivilegeLevel.AUTHOR)
        Assert.assertTrue(UserPrivilegeLevel.from(5) == UserPrivilegeLevel.AUTHOR)
        Assert.assertTrue(UserPrivilegeLevel.from(7) == UserPrivilegeLevel.EDITOR)
        Assert.assertTrue(UserPrivilegeLevel.from(8) == UserPrivilegeLevel.EDITOR)
        Assert.assertTrue(UserPrivilegeLevel.from(9) == UserPrivilegeLevel.EDITOR)
        Assert.assertTrue(UserPrivilegeLevel.from(10) == UserPrivilegeLevel.ADMINISTRATOR)
        Assert.assertTrue(UserPrivilegeLevel.from(25) == UserPrivilegeLevel.ADMINISTRATOR)
        Assert.assertTrue(UserPrivilegeLevel.from(9498) == UserPrivilegeLevel.ADMINISTRATOR)
    }

    @Test
    fun isAtLeastAtLevel_UserLevelsSame() {
        Assert.assertTrue(UserPrivilegeLevel.REGULAR_USER.isAtLeastAtLevel(UserPrivilegeLevel.REGULAR_USER))
        Assert.assertTrue(UserPrivilegeLevel.CONTRIBUTOR.isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR))
        Assert.assertTrue(UserPrivilegeLevel.AUTHOR.isAtLeastAtLevel(UserPrivilegeLevel.AUTHOR))
        Assert.assertTrue(UserPrivilegeLevel.EDITOR.isAtLeastAtLevel(UserPrivilegeLevel.EDITOR))
        Assert.assertTrue(UserPrivilegeLevel.ADMINISTRATOR.isAtLeastAtLevel(UserPrivilegeLevel.ADMINISTRATOR))
    }

    @Test
    fun isAtLeastAtLevel_MinimumPrivilegeReached() {
        Assert.assertTrue(UserPrivilegeLevel.REGULAR_USER.isAtLeastAtLevel(UserPrivilegeLevel.REGULAR_USER))
        Assert.assertTrue(UserPrivilegeLevel.CONTRIBUTOR.isAtLeastAtLevel(UserPrivilegeLevel.REGULAR_USER))
        Assert.assertTrue(UserPrivilegeLevel.AUTHOR.isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR))
        Assert.assertTrue(UserPrivilegeLevel.ADMINISTRATOR.isAtLeastAtLevel(UserPrivilegeLevel.REGULAR_USER))
        Assert.assertTrue(UserPrivilegeLevel.EDITOR.isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR))
        Assert.assertTrue(UserPrivilegeLevel.EDITOR.isAtLeastAtLevel(UserPrivilegeLevel.AUTHOR))
        Assert.assertTrue(UserPrivilegeLevel.ADMINISTRATOR.isAtLeastAtLevel(UserPrivilegeLevel.EDITOR))
    }

    @Test
    fun isAtLeastAtLevel_MinimumPrivilegeNotReached() {
        Assert.assertFalse(UserPrivilegeLevel.REGULAR_USER.isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR))
        Assert.assertFalse(UserPrivilegeLevel.REGULAR_USER.isAtLeastAtLevel(UserPrivilegeLevel.ADMINISTRATOR))
        Assert.assertFalse(UserPrivilegeLevel.CONTRIBUTOR.isAtLeastAtLevel(UserPrivilegeLevel.AUTHOR))
        Assert.assertFalse(UserPrivilegeLevel.EDITOR.isAtLeastAtLevel(UserPrivilegeLevel.ADMINISTRATOR))
        Assert.assertFalse(UserPrivilegeLevel.CONTRIBUTOR.isAtLeastAtLevel(UserPrivilegeLevel.ADMINISTRATOR))
    }

    @Test
    fun isAtMostAtLevel_UserLevelsSame() {
        Assert.assertTrue(UserPrivilegeLevel.REGULAR_USER.isAtMostAtLevel(UserPrivilegeLevel.REGULAR_USER))
        Assert.assertTrue(UserPrivilegeLevel.CONTRIBUTOR.isAtMostAtLevel(UserPrivilegeLevel.CONTRIBUTOR))
        Assert.assertTrue(UserPrivilegeLevel.AUTHOR.isAtMostAtLevel(UserPrivilegeLevel.AUTHOR))
        Assert.assertTrue(UserPrivilegeLevel.EDITOR.isAtMostAtLevel(UserPrivilegeLevel.EDITOR))
        Assert.assertTrue(UserPrivilegeLevel.ADMINISTRATOR.isAtMostAtLevel(UserPrivilegeLevel.ADMINISTRATOR))
    }

    @Test
    fun isAtMostAtLevel_MaximumPrivilegeNotOverreached() {
        Assert.assertTrue(UserPrivilegeLevel.REGULAR_USER.isAtMostAtLevel(UserPrivilegeLevel.CONTRIBUTOR))
        Assert.assertTrue(UserPrivilegeLevel.CONTRIBUTOR.isAtMostAtLevel(UserPrivilegeLevel.AUTHOR))
        Assert.assertTrue(UserPrivilegeLevel.AUTHOR.isAtMostAtLevel(UserPrivilegeLevel.EDITOR))
        Assert.assertTrue(UserPrivilegeLevel.ADMINISTRATOR.isAtMostAtLevel(UserPrivilegeLevel.ADMINISTRATOR))
        Assert.assertTrue(UserPrivilegeLevel.EDITOR.isAtMostAtLevel(UserPrivilegeLevel.ADMINISTRATOR))
        Assert.assertTrue(UserPrivilegeLevel.CONTRIBUTOR.isAtMostAtLevel(UserPrivilegeLevel.ADMINISTRATOR))
        Assert.assertTrue(UserPrivilegeLevel.REGULAR_USER.isAtMostAtLevel(UserPrivilegeLevel.EDITOR))
        Assert.assertTrue(UserPrivilegeLevel.REGULAR_USER.isAtMostAtLevel(UserPrivilegeLevel.ADMINISTRATOR))
    }

    @Test
    fun isAtMostAtLevel_MaximumPrivilegeOverreached() {
        Assert.assertFalse(UserPrivilegeLevel.CONTRIBUTOR.isAtMostAtLevel(UserPrivilegeLevel.REGULAR_USER))
        Assert.assertFalse(UserPrivilegeLevel.ADMINISTRATOR.isAtMostAtLevel(UserPrivilegeLevel.REGULAR_USER))
        Assert.assertFalse(UserPrivilegeLevel.ADMINISTRATOR.isAtMostAtLevel(UserPrivilegeLevel.AUTHOR))
        Assert.assertFalse(UserPrivilegeLevel.EDITOR.isAtMostAtLevel(UserPrivilegeLevel.CONTRIBUTOR))
        Assert.assertFalse(UserPrivilegeLevel.EDITOR.isAtMostAtLevel(UserPrivilegeLevel.REGULAR_USER))
    }
}