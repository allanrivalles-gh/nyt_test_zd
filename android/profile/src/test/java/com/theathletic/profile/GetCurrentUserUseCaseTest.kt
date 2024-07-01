package com.theathletic.profile

import com.theathletic.profile.data.toDomain
import com.theathletic.user.IUserManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class GetCurrentUserUseCaseTest {
    private val userManager = mockk<IUserManager>()

    private val testObject = GetCurrentUserUseCase(userManager)
    @Test
    fun `returns failure when current user returns null`() {
        every { userManager.getCurrentUser() } returns null
        assert(testObject.invoke().isFailure)
    }

    @Test
    fun `returns success when current user returns userEntity`() {
        val currentUser = userEntityFixture()
        every { userManager.getCurrentUser() } returns currentUser
        every { userManager.isUserSubscribed() } returns true

        val expectedResult = Result.success(currentUser.toDomain(true))
        assertEquals(expectedResult, testObject.invoke())
    }
}