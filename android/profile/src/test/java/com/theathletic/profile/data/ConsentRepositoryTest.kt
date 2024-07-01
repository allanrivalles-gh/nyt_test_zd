package com.theathletic.profile.data

import com.theathletic.profile.data.remote.ConsentDetails
import com.theathletic.profile.data.remote.TranscendConsentWrapper
import com.theathletic.test.runTest
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsentRepositoryTest {
    private val transcendConsentWrapper = mockk<TranscendConsentWrapper>()
    private val testObject = ConsentRepository(transcendConsentWrapper)

    @Test
    fun `getIsConsentConfirmed returns false when getConsent throws exception`() = runTest {
        coEvery { transcendConsentWrapper.getConsent() } throws Exception()
        assertFalse(testObject.getIsConsentConfirmed())
    }

    @Test
    fun `getIsConsentConfirmed returns false when getConsent isConfirmed is false`() = runTest {
        coEvery { transcendConsentWrapper.getConsent() } returns ConsentDetails(false)
        assertFalse(testObject.getIsConsentConfirmed())
    }

    @Test
    fun `getIsConsentConfirmed returns true when getConsent isConfirmed is true`() = runTest {
        coEvery { transcendConsentWrapper.getConsent() } returns ConsentDetails(true)
        assertTrue(testObject.getIsConsentConfirmed())
    }

    @Test
    fun `getIsUserInGDPR returns false when getRegimes throws exception`() = runTest {
        coEvery { transcendConsentWrapper.getRegimes() } throws Exception()
        assertFalse(testObject.getIsUserInGDPR())
    }

    @Test
    fun `getIsUserInGDPR returns false when getRegimes does not contain gdpr`() = runTest {
        coEvery { transcendConsentWrapper.getRegimes() } returns setOf("us")
        assertFalse(testObject.getIsUserInGDPR())
    }

    @Test
    fun `getIsUserInGDPR returns true when getRegimes contain gdpr`() = runTest {
        coEvery { transcendConsentWrapper.getRegimes() } returns setOf("gdpr")
        assertTrue(testObject.getIsUserInGDPR())
    }
}