package com.theathletic.user.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeCalculator
import com.theathletic.datetime.TimeDiff
import com.theathletic.datetime.TimeProvider
import com.theathletic.entity.user.UserEntity
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.user.IUserManager
import com.theathletic.user.data.remote.PrivacyAcknowledgmentScheduler
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.PrivacyPreferences
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

internal class PrivacyPolicyViewModelDelegateTest {

    private lateinit var privacyPolicyViewModel: PrivacyPolicyViewModelDelegate

    @Mock private lateinit var featureSwitches: FeatureSwitches
    @Mock private lateinit var localeUtility: LocaleUtility
    @Mock private lateinit var preferences: PrivacyPreferences
    @Mock private lateinit var timeCalculator: TimeCalculator
    @Mock private lateinit var userManager: IUserManager
    @Mock private lateinit var timeProvider: TimeProvider
    @Mock private lateinit var privacyAcknowledgmentScheduler: PrivacyAcknowledgmentScheduler
    @Mock private lateinit var analytics: Analytics

    private val testCurrentTimeMillis = 1000L

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        privacyPolicyViewModel = PrivacyPolicyViewModelDelegate(
            featureSwitches,
            localeUtility,
            preferences,
            timeCalculator,
            userManager,
            timeProvider,
            privacyAcknowledgmentScheduler,
            analytics
        )

        whenever(timeProvider.currentTimeMs).thenReturn(testCurrentTimeMillis)
        // defaulting to all the conditions necessary to show a privacy policy, will override in tests
        whenever(
            featureSwitches.isFeatureEnabled(FeatureSwitch.PRIVACY_REFRESH_DIALOG_ENABLED)
        ).thenReturn(true)
        whenever(preferences.privacyPolicyUpdateLastRequestedDate).thenReturn(Datetime(0))
        val timeDiff = mock<TimeDiff> { on { inDays } doReturn 2 }
        whenever(timeCalculator.timeDiffFromNow(any())).thenReturn(timeDiff)
        val user = mock<UserEntity> {
            on { privacyPolicy } doReturn false
            on { termsAndConditions } doReturn false
        }
        whenever(userManager.getCurrentUser()).thenReturn(user)
    }

    @Test
    fun `onPrivacyAccepted schedules acknowledgement worker`() {
        privacyPolicyViewModel.onPrivacyAccepted()
        verify(privacyAcknowledgmentScheduler).schedule()
    }

    @Test
    fun `onPrivacyAccepted sends analytics`() {
        privacyPolicyViewModel.onPrivacyAccepted()
        verify(analytics).track(Event.User.PrivacyAcknowledgment("click"))
    }

    @Test
    fun `didDisplayPrivacyDialog records time in Preferences`() {
        privacyPolicyViewModel.didDisplayPrivacyDialog()
        verify(preferences).privacyPolicyUpdateLastRequestedDate = Datetime(testCurrentTimeMillis)
    }

    @Test
    fun `didDisplayPrivacyDialog sends analytics`() {
        privacyPolicyViewModel.didDisplayPrivacyDialog()
        verify(analytics).track(Event.User.PrivacyAcknowledgment("view"))
    }

    @Test
    fun `shouldPresentPrivacyRefresh false if feature is off`() {
        whenever(
            featureSwitches.isFeatureEnabled(FeatureSwitch.PRIVACY_REFRESH_DIALOG_ENABLED)
        ).thenReturn(false)
        assertThat(privacyPolicyViewModel.shouldPresentPrivacyRefresh()).isFalse()
    }

    @Test
    fun `shouldPresentPrivacyRefresh false if has seen refresh in past day`() {
        val timeDiff = mock<TimeDiff> { on { inDays } doReturn 0 }
        whenever(timeCalculator.timeDiffFromNow(any())).thenReturn(timeDiff)
        assertThat(privacyPolicyViewModel.shouldPresentPrivacyRefresh()).isFalse()
    }

    @Test
    fun `shouldPresentPrivacyRefresh false if already accepted`() {
        val user = mock<UserEntity> {
            on { privacyPolicy } doReturn true
            on { termsAndConditions } doReturn true
        }
        whenever(userManager.getCurrentUser()).thenReturn(user)
        assertThat(privacyPolicyViewModel.shouldPresentPrivacyRefresh()).isFalse()
    }

    @Test
    fun `shouldPresentPrivacyRefresh true if no user`() {
        whenever(userManager.getCurrentUser()).thenReturn(null)
        assertThat(privacyPolicyViewModel.shouldPresentPrivacyRefresh()).isTrue()
    }

    @Test
    fun `shouldPresentPrivacyRefresh true`() {
        assertThat(privacyPolicyViewModel.shouldPresentPrivacyRefresh()).isTrue()
    }
}