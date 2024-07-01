package com.theathletic.utility

import com.google.common.truth.Truth.assertThat
import com.theathletic.datetime.TimeProvider
import com.theathletic.entity.article.ArticleRating
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.user.IUserManager
import java.util.Date
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class AppRatingEngineTest {
    lateinit var ratingEngine: AppRatingEngine
    @Mock lateinit var preferences: IPreferences
    @Mock lateinit var userManager: IUserManager
    @Mock lateinit var timeProvider: TimeProvider
    @Mock lateinit var featureSwitches: FeatureSwitches
    @Mock lateinit var userDataRepository: IUserDataRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ratingEngine = AppRatingEngine(
            preferences,
            userManager,
            featureSwitches,
            userDataRepository
        )
        whenever(userManager.isUserSubscribed()).thenReturn(true)
        whenever(timeProvider.currentDate).thenReturn(Date())
        whenever(featureSwitches.isFeatureEnabled(FeatureSwitch.APP_RATING_ENABLED)).thenReturn(true)
    }

    @Test
    fun `should not try rating dialog with no ratings or reads`() {
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isFalse()
    }

    @Test
    fun `should not try rating dialog with bad ratings and no reads`() {
        whenever(preferences.articlesRatings).thenReturn(
            hashMapOf(
                "1" to ArticleRating.MEH.value,
                "2" to ArticleRating.SOLID.value
            )
        )
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isFalse()
    }

    @Test
    fun `should not try rating dialog with no ratings and less than 10 reads`() {
        whenever(userDataRepository.totalReadArticleCount).thenReturn(3)
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isFalse()
    }

    @Test
    fun `should not try rating dialog with no ratings and 11 reads`() {
        whenever(userDataRepository.totalReadArticleCount).thenReturn(11)
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isFalse()
    }

    @Test
    fun `should not try rating dialog if not a subscriber`() {
        whenever(preferences.articlesRatings).thenReturn(generateAwesomeMap(1))
        whenever(userManager.isUserSubscribed()).thenReturn(false)
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isFalse()
    }

    @Test
    fun `should not try rating dialog if already disabled`() {
        ratingEngine.disable()
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isFalse()
    }

    @Test
    fun `should not try rating dialog if disabled after trigger`() {
        whenever(preferences.articlesRatings).thenReturn(generateAwesomeMap(1))
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isTrue()
        ratingEngine.disable()
        assertThat(ratingEngine.shouldTryRatingDialogue).isFalse()
    }

    @Test
    fun `should not try rating dialog if feature off`() {
        whenever(preferences.articlesRatings).thenReturn(generateAwesomeMap(1))
        whenever(featureSwitches.isFeatureEnabled(any())).thenReturn(false)
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isFalse()
    }

    @Test
    fun `should not try rating dialog if one awesome rating but no trigger`() {
        whenever(preferences.articlesRatings).thenReturn(generateAwesomeMap(1))
        assertThat(ratingEngine.shouldTryRatingDialogue).isFalse()
    }

    @Test
    fun `should try rating dialog if one awesome rating`() {
        whenever(preferences.articlesRatings).thenReturn(generateAwesomeMap(1))
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isTrue()
    }

    @Test
    fun `should try rating dialog on subsequent 3rd awesome ratings`() {
        whenever(preferences.articlesRatings).thenReturn(generateAwesomeMap(4))
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isTrue()
    }

    @Test
    fun `should try rating dialog after 10 article reads`() {
        whenever(userDataRepository.totalReadArticleCount).thenReturn(10)
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isTrue()
    }

    @Test
    fun `should try rating dialog if triggered true and then triggered false`() {
        whenever(userDataRepository.totalReadArticleCount).thenReturn(10)
        ratingEngine.onRatingTrigger()
        whenever(userDataRepository.totalReadArticleCount).thenReturn(11)
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isTrue()
    }

    @Test
    fun `should try rating dialog after subsequent 10 article reads`() {
        whenever(userDataRepository.totalReadArticleCount).thenReturn(30)
        ratingEngine.onRatingTrigger()
        assertThat(ratingEngine.shouldTryRatingDialogue).isTrue()
    }

    private fun generateAwesomeMap(awesomeCount: Int): HashMap<String, Long> {
        val result = hashMapOf<String, Long>()
        for (x in 1..awesomeCount) {
            result[x.toString()] = 3L
        }
        return result
    }
}