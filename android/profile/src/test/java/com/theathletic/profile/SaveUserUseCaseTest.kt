package com.theathletic.profile

import com.theathletic.network.ResponseStatus
import com.theathletic.profile.ui.Customer
import com.theathletic.test.runTest
import com.theathletic.user.IUserManager
import com.theathletic.user.data.UserRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SaveUserUseCaseTest {
    private val userManager = mockk<IUserManager>(relaxUnitFun = true)
    private val userRepository = mockk<UserRepository>()

    private val testObject = SaveUserUseCase(userManager, userRepository)

    @Before
    fun setUp() {
        every { userManager.getCurrentUserId() } returns 1L
    }

    @Test
    fun `returns failure when editUser returns error`() = runTest {
        coEvery { userRepository.editUser(any(), any(), any(), any()) } returns ResponseStatus.Error(Throwable())
        val newCustomer = Customer(
            firstName = "NewFirstName",
            lastName = "NewLastName",
            email = "newEmail@test.com",
        )
        val result = testObject.invoke(newCustomer)
        assert(result.isFailure)
        verify(exactly = 0) { userManager.saveCurrentUser(any()) }
    }

    @Test
    fun `calls saveCurrentUser and returns success when editUser succeeds`() = runTest {
        val currentUser = userEntityFixture()
        coEvery { userRepository.editUser(any(), any(), any(), any()) } returns ResponseStatus.Success(Unit)
        every { userManager.getCurrentUser() } returns currentUser

        val newCustomer = Customer(
            firstName = "NewFirstName",
            lastName = "NewLastName",
            email = "newEmail@test.com",
        )
        val savedUser = currentUser.apply {
            firstName = newCustomer.firstName
            lastName = newCustomer.lastName
            email = newCustomer.email
        }

        val result = testObject.invoke(newCustomer)
        assert(result.isSuccess)
        verify { userManager.saveCurrentUser(savedUser) }
    }
}