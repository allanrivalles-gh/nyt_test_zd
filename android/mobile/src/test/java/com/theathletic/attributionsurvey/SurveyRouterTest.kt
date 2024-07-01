package com.theathletic.attributionsurvey

import com.google.common.truth.Truth.assertThat
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.test.runTest
import com.theathletic.utility.AttributionPreferences
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SurveyRouterTest {
    private var preferences = mockk<AttributionPreferences>(relaxed = true)
    private var surveyRouter = mockk<SurveyRouter>(relaxed = true)
    private var repository = mockk<OnboardingRepository>(relaxed = true)
    private var featureSwitches = mockk<FeatureSwitches>(relaxed = true)

    @Before
    fun setup() {
        every { preferences.hasMadePurchaseForSurvey } returns true
        every { preferences.hasBeenEligibleForSurvey } returns true
        every { preferences.hasSeenAttributionSurvey } returns false
        every { featureSwitches.isFeatureEnabled(FeatureSwitch.ATTRIBUTION_SURVEY) } returns true
        surveyRouter = SurveyRouter(preferences, repository, featureSwitches)
    }

    @Test
    fun `shouldPresentSurvey returns false if user is not eligible`() {
        every { preferences.hasBeenEligibleForSurvey } returns false
        assertThat(surveyRouter.shouldPresentSurvey()).isFalse()
    }

    @Test
    fun `shouldPresentSurvey returns false if already seen`() {
        every { preferences.hasSeenAttributionSurvey } returns true
        assertThat(surveyRouter.shouldPresentSurvey()).isFalse()
    }

    @Test
    fun `shouldPresentSurvey returns false if featureSwitch is off`() {
        every { featureSwitches.isFeatureEnabled(FeatureSwitch.ATTRIBUTION_SURVEY) } returns false
        assertThat(surveyRouter.shouldPresentSurvey()).isFalse()
    }

    @Test
    fun `shouldPresentSurvey returns false if no purchase made`() {
        every { preferences.hasMadePurchaseForSurvey } returns false
        assertThat(surveyRouter.shouldPresentSurvey()).isFalse()
    }

    @Test
    fun `shouldPresentSurvey returns true if all conditions met`() {
        assertThat(surveyRouter.shouldPresentSurvey()).isTrue()
    }

    @Test
    fun `fetchSurveyIfQualified does not fetch survey if ineligible`() = runTest {
        every { preferences.hasBeenEligibleForSurvey } returns false
        surveyRouter.fetchSurveyIfQualified()
        coVerify(exactly = 0) { repository.fetchSurvey() }
    }

    @Test
    fun `fetchSurveyIfQualified fetches survey even without purchase`() = runTest {
        every { preferences.hasMadePurchaseForSurvey } returns false
        surveyRouter.fetchSurveyIfQualified()
        coVerify(exactly = 1) { repository.fetchSurvey() }
    }

    @Test
    fun `hasMadePurchase updates preferences`() {
        surveyRouter.hasMadeSuccessfulPurchase()
        verify { preferences.hasMadePurchaseForSurvey = true }
    }
}