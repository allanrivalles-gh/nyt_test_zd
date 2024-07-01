package com.theathletic.feed.compose

import android.util.Size
import com.google.common.truth.Truth.assertThat
import com.theathletic.ads.AdConfig
import com.theathletic.ads.AdConfigClient
import com.theathletic.ads.data.local.ContentType
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.ui.ads.FeedAdsPage
import com.theathletic.location.data.LocationRepository
import com.theathletic.remoteconfig.RemoteConfigRepository
import com.theathletic.test.runTest
import com.theathletic.user.IUserManager
import com.theathletic.utility.AdPreferences
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test

class PrepareAdConfigCreatorUseCaseTest {
    private val page = FeedAdsPage(
        pageViewId = "1fdac676-aa9b-4f5c-9490-3e4ed82d8532",
        feedType = FeedType.FOLLOWING,
    )
    private val environment = PrepareAdConfigCreatorUseCase.Environment(
        screenSize = Size(1080, 1920),
        appVersionName = "1.0.0",
        experiments = listOf(),
    )
    private val coroutineContext = UnconfinedTestDispatcher()
    private val adUnitPath = "/29390238/theathletic/discover/feed"
    private val adId = "0-1"
    private lateinit var adConfigBuilder: AdConfig.Builder
    private lateinit var locationRepository: LocationRepository
    private lateinit var remoteConfigRepository: RemoteConfigRepository
    private lateinit var prepareAdConfigCreator: PrepareAdConfigCreatorUseCase

    private fun setUp(
        gdprSupportedCountries: Flow<List<String>> = flowOf(listOf()),
        ccpaSupportedStates: Flow<List<String>> = flowOf(listOf()),
    ) {
        val userManager = mockk<IUserManager> {
            every { isUserSubscribed() }.returns(true)
        }
        val adPreferences = object : AdPreferences {
            override var adKeyword: String? = null
            override var privacyCountryCode: String? = null
            override var privacyStateAbbr: String? = null
            override var privacyEnabled: Boolean = false
        }
        val adConfigClient = object : AdConfigClient {
            override val platform: String = "android"
        }
        adConfigBuilder = spyk(AdConfig.Builder(adPreferences, adConfigClient))
        locationRepository = mockk(relaxed = true)
        remoteConfigRepository = mockk {
            every { this@mockk.gdprSupportedCountries }.returns(gdprSupportedCountries)
            every { this@mockk.ccpaSupportedStates }.returns(ccpaSupportedStates)
        }
        prepareAdConfigCreator = PrepareAdConfigCreatorUseCase(
            adConfigBuilder,
            locationRepository,
            remoteConfigRepository,
            userManager,
        )
    }

    @Test
    fun `creates config with HOME_PAGE content type for following feed`() = runTest(coroutineContext) {
        setUp()
        val config = prepareAdConfigCreator(
            page.copy(feedType = FeedType.FOLLOWING),
            environment,
        ).createConfig(adUnitPath, adId)
        assertThat(config.contentType).isEqualTo(ContentType.HOME_PAGE)
    }

    @Test
    fun `creates config with COLLECTION content type for non following feeds`() = runTest(coroutineContext) {
        for (feedType in FeedType.values().filter { it !== FeedType.FOLLOWING }) {
            setUp()
            val config = prepareAdConfigCreator(
                page.copy(feedType = feedType),
                environment,
            ).createConfig(adUnitPath, adId)
            assertThat(config.contentType).isEqualTo(ContentType.COLLECTION)
        }
    }

    @Test
    fun `creates config with latest value for GDPR countries`() = runTest(coroutineContext) {
        val testCountries = listOf("DE", "FR")
        setUp(gdprSupportedCountries = MutableStateFlow(testCountries))
        val config = prepareAdConfigCreator(page, environment).createConfig(adUnitPath, adId)
        assertThat(config.adPrivacy.gdprCountries).isEqualTo(testCountries)
    }

    @Test
    fun `creates config with latest value for CCPA states`() = runTest(coroutineContext) {
        val testStates = listOf("CA")
        setUp(ccpaSupportedStates = MutableStateFlow(testStates))
        val config = prepareAdConfigCreator(page, environment).createConfig(adUnitPath, adId)
        assertThat(config.adPrivacy.ccpaStates).isEqualTo(testStates)
    }

    @Test
    fun `preconfigures builder with last updated location`() = runTest(coroutineContext) {
        setUp()
        val countryCode = "US"
        val state = "CA"
        coEvery { locationRepository.getCountryCode() }.returns(countryCode)
        coEvery { locationRepository.getState() }.returns(state)
        every { adConfigBuilder.setGeo(any(), any()) }.returns(adConfigBuilder)

        prepareAdConfigCreator(page, environment)
        verify { adConfigBuilder.setGeo(countryCode, state) }
    }
}

private val AdConfig.contentType: ContentType
    get() = ContentType.findByType(adRequirements["typ"]!!)