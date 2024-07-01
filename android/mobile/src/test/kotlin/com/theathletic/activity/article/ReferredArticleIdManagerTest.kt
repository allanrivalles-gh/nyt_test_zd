package com.theathletic.activity.article

import android.content.Context
import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
import com.theathletic.analytics.data.ClickSource
import com.theathletic.auth.data.AuthenticationRepository
import com.theathletic.datetime.TimeProvider
import com.theathletic.utility.IActivityUtility
import com.theathletic.utility.IPreferences
import java.util.Calendar
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever

class ReferredArticleIdManagerTest {
    private lateinit var activityUtility: IActivityUtility
    private lateinit var manager: ReferredArticleIdManager
    private lateinit var timeProvider: TimeProvider
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var context: Context
    private lateinit var preferences: IPreferences

    class TestPreferences : IPreferences {
        override var kochavaArticleId: String? = null
        override var kochavaArticleDate: Date? = null
        override var articlesRatings: HashMap<String, Long> = hashMapOf()
        override var giftsPendingPaymentDataJson: String? = null
        override var accessToken: String? = null
        override var logGoogleSubLastToken: String? = null
        override var lastGoogleSubArticleId: Long? = null
        override var lastGoogleSubPodcastId: Long? = null
        override var lastDeclinedUpdateVersionCode: Int = 0
        override var hasSeenWebViewVersionNotice: Boolean? = false
        override var followablesOrder: Map<String, Int> = emptyMap()
        override val followablesOrderStateFlow: StateFlow<Map<String, Int>> = MutableStateFlow(emptyMap())
        override var hasCustomFollowableOrder: Boolean = false
        override var pushTokenKey: String? = null
    }

    @Before
    fun setUp() {
        activityUtility = mock()
        context = mock()
        timeProvider = mock()
        preferences = TestPreferences()
        authenticationRepository = mock()
        manager = ReferredArticleIdManager(activityUtility, preferences, timeProvider, authenticationRepository)
        val sharedPreferences: SharedPreferences = mock()
        whenever(context.getSharedPreferences(any(), any())).thenReturn(sharedPreferences)
        whenever(timeProvider.currentDate).thenReturn(Date())
    }

    @Test
    fun checkAndRouteToArticle_Routes() {
        manager.setArticleId(12L)
        manager.checkAndRouteToArticle(context)
        verify(activityUtility).startArticleActivity(eq(context), eq(12L), anyOrNull<ClickSource>())
    }

    @Test
    fun checkAndRouteToArticle_DoesNothingIfNullArticleId() {
        manager.checkAndRouteToArticle(context)
        verifyZeroInteractions(activityUtility)
    }

    @Test
    fun getArticleId_ReturnsArticle() {
        manager.setArticleId(12L)
        assertThat(manager.getArticleId()).isEqualTo(12L)
    }

    @Test
    fun clearArticleId_Clears() {
        manager.setArticleId(12L)
        assertThat(manager.getArticleId()).isEqualTo(12L)
        manager.clearArticleId()
        assertThat(manager.getArticleId()).isNull()
    }

    @Test
    fun getArticleId_ReturnsNullIfStale() {
        val cal = Calendar.getInstance()
        whenever(timeProvider.currentDate).thenReturn(cal.time)
        manager.setArticleId(12L)
        cal.add(Calendar.DAY_OF_MONTH, 3)
        whenever(timeProvider.currentDate).thenReturn(cal.time)
        assertThat(manager.getArticleId()).isNull()
    }
}