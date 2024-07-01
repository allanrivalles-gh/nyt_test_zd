package com.theathletic.compass

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.theathletic.BuildConfig
import com.theathletic.compass.codegen.CompassTestExperiment
import com.theathletic.compass.codegen.TestProfile
import com.theathletic.compass.codegen.TestPushOptInPrePrompt
import com.theathletic.compass.codegen.TestZeroFreeArticlesExperiment
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.extension.safe
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.injection.baseModule
import com.theathletic.test.CoroutineTestRule
import com.theathletic.user.IUserManager
import com.theathletic.utility.IPreferences
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.device.DeviceInfo
import com.theathletic.utility.logging.ICrashLogHandler
import java.util.Calendar
import java.util.Calendar.HOUR
import java.util.concurrent.TimeUnit
import kotlin.test.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * This class uses CompassTestExperiment to ensure consistent experiments over time for testing
 */
class CompassClientTest : AutoCloseKoinTest() {
    @Mock
    private lateinit var crashLogHandler: ICrashLogHandler
    @Mock
    private lateinit var compassApi: CompassApi
    @Mock
    private lateinit var localeUtility: LocaleUtility
    @Mock
    private lateinit var userManager: IUserManager
    @Mock
    private lateinit var preferences: IPreferences

    private val testDeviceInfo = DeviceInfo(
        "deviceId",
        false,
        21,
        "tmobile",
        "motorola",
        "razor"
    )

    @get:Rule var coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            baseModule,
            module {
                factory { crashLogHandler }
                factory { compassApi }
                factory { declareMock<DebugPreferences>() }
                factory { localeUtility }
                factory { userManager }
                factory { preferences }
            }
        )
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        compassApi.mockGetConfigResponse(
            CompassApi.CompassConfigResponse(
                timestamp = "",
                experiments = emptyList()
            )
        )
        whenever(userManager.getCurrentUserId()).thenReturn(123L)
        whenever(preferences.pushTokenKey).thenReturn("123L")
        whenever(localeUtility.acceptLanguage).thenReturn("es-ES")
    }

    @After
    fun tearDown() {
        CompassTestExperiment.resetMap()
    }

    @Test
    fun `basic usage`() {
        val compassClient = createCompassClient()
        runBlocking {
            compassClient.loadConfig(isUserLoggedIn = false)
            val experiment = CompassTestExperiment.ZERO_FREE_ARTICLES_EXPERIMENT
            val variant = experiment.variant
            when (variant) {
                is TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.CTRL -> {
                    println(variant.message)
                    println(variant.product)
                }
                is TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.A -> {
                    println(variant.message)
                    println(variant.product)
                }
                is TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.B -> {
                    println(variant.message)
                    println(variant.product)
                }
            }.safe
            experiment.postExposure(userId = -1L)

            // reserved words work as fields
            val variant2 = CompassTestExperiment.PROFILE.variant
            when (variant2) {
                is TestProfile.ProfileVariant.CTRL -> {
                    println(variant2.message)
                }
                is TestProfile.ProfileVariant.A -> {
                    println(variant2.`package`)
                }
                is TestProfile.ProfileVariant.B -> {
                    println(variant2.message)
                }
            }.safe
        }
    }

    @Test
    fun `concurrent loadConfig calls relays to crashLogHandler in Release builds`() {
        if (BuildConfig.DEBUG) return
        val client = createCompassClient()
        runBlocking {
            client.configState.set(CompassClient.ConfigState.IS_POPULATING)
            client.loadConfig(isUserLoggedIn = false)
        }
        verify(crashLogHandler).logException(any())
    }

    @Test
    fun `concurrent loadConfig calls crash application in Debug builds`() {
        if (!BuildConfig.DEBUG) return
        val client = createCompassClient()
        try {
            runBlocking {
                client.configState.set(CompassClient.ConfigState.IS_POPULATING)
                launch { client.loadConfig(isUserLoggedIn = false) }
            }
            fail("DEBUG configuration should throw an exception")
        } catch (e: Exception) {
            // expected
        }
    }

    @Test(expected = CompassNotInitializedException::class)
    fun `verify cannot post exposure until client has initialized`() {
        val client = createCompassClient()
        val someExperiment = TestZeroFreeArticlesExperiment(
            exists = false,
            crashLogHandler = crashLogHandler,
            debugPreferences = mock()
        )
        runBlocking { client.postExposure(someExperiment, -1L) }
    }

    @Test
    fun `verify referencing variant before compass client is initialized logs error in Release builds`() {
        if (BuildConfig.DEBUG) return
        val someExperiment = TestZeroFreeArticlesExperiment(
            exists = false,
            crashLogHandler = crashLogHandler,
            debugPreferences = mock()
        )
        someExperiment.variant
        verify(crashLogHandler).logException(any())
    }

    @Test
    fun `verify referencing variant before compass client is initialized throws exception in Debug builds`() {
        if (!BuildConfig.DEBUG) return
        try {
            val someExperiment = TestZeroFreeArticlesExperiment(
                exists = false,
                crashLogHandler = crashLogHandler,
                debugPreferences = mock()
            )
            someExperiment.variant
            fail("DEBUG configuration should throw an exception")
        } catch (e: IllegalAccessException) {
            // expected exception
        } catch (e: Exception) {
            fail("Expected IllegalAccessException but received: $e")
        }
    }

    @Test
    fun `verify existent experiment does have an active variant`() {
        compassApi.mockGetConfigResponse(
            CompassApi.CompassConfigResponse(
                timestamp = "",
                experiments = listOf(
                    CompassApi.ExperimentResponse(
                        variant = "A",
                        id = "Ads v1",
                        data = null
                    )
                )
            )
        )
        runBlocking {
            val client = createCompassClient()
            client.loadConfig(false)
            val experiment = CompassTestExperiment.ADS_V1_EXPERIMENT
            assertThat(experiment.activeVariant?._name).isEqualTo("A")
            assertThat(experiment.exists).isEqualTo(true)
        }
    }

    @Test
    fun `verify non existent experiment doesnt have an active variant`() {
        compassApi.mockGetConfigResponse(
            CompassApi.CompassConfigResponse(
                timestamp = "",
                experiments = emptyList()
            )
        )
        runBlocking {
            val client = createCompassClient()
            client.loadConfig(false)
            val experiment = CompassTestExperiment.ADS_V1_EXPERIMENT
            assertThat(experiment.activeVariant).isEqualTo(null)
            assertThat(experiment.exists).isEqualTo(false)
        }
    }

    @Test
    fun `verify non existent experiment cannot post exposure`() {
        compassApi.mockGetConfigResponse(
            CompassApi.CompassConfigResponse(
                timestamp = "",
                experiments = emptyList()
            )
        )
        runBlocking {
            val client = createCompassClient()
            client.loadConfig(isUserLoggedIn = false)
            val someExperiment = TestZeroFreeArticlesExperiment(
                exists = false,
                crashLogHandler = crashLogHandler,
                debugPreferences = mock()
            )
            client.postExposure(someExperiment, -1L)
            verify(compassApi, never()).postExposure(any())
        }
    }

    @Test
    fun `verify an experiment where exists=true should post an exposure`() {
        val client = createCompassClient()
        compassApi.mockGetConfigResponse(
            CompassApi.CompassConfigResponse(
                timestamp = "",
                experiments = listOf(
                    CompassApi.ExperimentResponse(
                        variant = "A",
                        id = "profile",
                        data = emptyList()
                    )
                )
            )
        )
        runBlocking { client.loadConfig(isUserLoggedIn = false) }
        val someExperiment = TestZeroFreeArticlesExperiment(
            exists = true,
            crashLogHandler = crashLogHandler,
            debugPreferences = mock()
        )
        runBlocking { client.postExposure(someExperiment, -1L) }
        runBlocking { verify(compassApi, times(1)).postExposure(any()) }
    }

    @Test
    fun `throw CompassTypeMismatchException when server response type differs from client type`() {
        val client = createCompassClient()
        compassApi.mockGetConfigResponse(
            CompassApi.CompassConfigResponse(
                timestamp = "",
                experiments = listOf(
                    CompassApi.ExperimentResponse(
                        variant = "A",
                        id = "profile",
                        data = listOf(FieldResponse("string", "floatPrice", "hello world")),
                    )
                )
            )
        )
        runBlocking { client.loadConfig(isUserLoggedIn = false) }
        verify(crashLogHandler, times(1)).logException(any<CompassTypeMismatchException>())
    }

    @Test
    fun `should fail silently when field exists on server but not client`() {
        val client = createCompassClient()
        compassApi.mockGetConfigResponse(
            CompassApi.CompassConfigResponse(
                timestamp = "",
                experiments = listOf(
                    CompassApi.ExperimentResponse(
                        variant = "A",
                        id = "profile",
                        data = emptyList()
                    )
                )
            )
        )
        runBlocking { client.loadConfig(isUserLoggedIn = false) }
        verify(crashLogHandler, never()).logException(any<CompassTypeMismatchException>())
    }

    @Test
    fun `should fail silently when field exists on client but not server`() {
        val client = createCompassClient()
        compassApi.mockGetConfigResponse(
            CompassApi.CompassConfigResponse(
                timestamp = "",
                experiments = listOf(
                    CompassApi.ExperimentResponse(
                        variant = "A",
                        id = "profile",
                        data = emptyList()
                    )
                )
            )
        )
        runBlocking { client.loadConfig(isUserLoggedIn = false) }
        verify(crashLogHandler, never()).logException(any<CompassTypeMismatchException>())
    }

    @Test
    fun `verify configuration is updated by successful initialization`() {
        val client = createCompassClient()
        compassApi.mockGetConfigResponse(
            CompassApi.CompassConfigResponse(
                timestamp = "",
                experiments = listOf(
                    CompassApi.ExperimentResponse(
                        variant = "A",
                        id = "profile",
                        data = listOf(FieldResponse("string", "message", "hello test"))
                    )
                )
            )
        )
        runBlocking { client.loadConfig(isUserLoggedIn = false) }
        assert(CompassTestExperiment.PROFILE.variant is TestProfile.ProfileVariant.A)
        val variantA = CompassTestExperiment.PROFILE.variant as TestProfile.ProfileVariant.A
        assert(variantA.message == "hello test")
    }

    @Test
    fun `should default to CTRL variant`() {
        val compassClient = createCompassClient()
        runBlocking { compassClient.loadConfig(isUserLoggedIn = false) }
        assert(CompassTestExperiment.PUSH_OPT_IN_PRE_PROMPT.variant is TestPushOptInPrePrompt.PushOptInPrePromptVariant.CTRL)
    }

    @Test
    fun `should default to A variant when selected inside debug preferences`() {
        val compassClient = createCompassClient()
        runBlocking { compassClient.loadConfig(isUserLoggedIn = false) }
        whenever(CompassTestExperiment.PROFILE.debugPreferences.compassSelectedVariantMap)
            .thenReturn(mapOf(Pair("Push opt-in pre prompt", "A")))
        assert(CompassTestExperiment.PUSH_OPT_IN_PRE_PROMPT.variant is TestPushOptInPrePrompt.PushOptInPrePromptVariant.A)
    }

    @Test
    fun `should default to CTRL variant when nothing specified in debug preferences`() {
        val compassClient = createCompassClient()
        runBlocking { compassClient.loadConfig(isUserLoggedIn = false) }
        whenever(CompassTestExperiment.PROFILE.debugPreferences.compassSelectedVariantMap)
            .thenReturn(mapOf(Pair("Some experiment name", "B")))
        assert(CompassTestExperiment.PUSH_OPT_IN_PRE_PROMPT.variant is TestPushOptInPrePrompt.PushOptInPrePromptVariant.CTRL)
    }

    @Test
    fun `decommissioned experiments are removed from persisted set of exposures`() {
        val compassPreferences = object : ICompassPreferences {
            override var lastConfig: String = ""
            override var exposureSet: Set<String> =
                setOf("profile", "i-do-not-exist", "empty field experiment")
            override var lastUpdatedInMillis: Long = 0
        }
        runBlocking {
            createCompassClient(compassPreferences = compassPreferences)
                .loadConfig(isUserLoggedIn = false)
        }
        assert(
            compassPreferences.exposureSet.containsAll(
                setOf("profile", "empty field experiment")
            )
        )
        assert(!compassPreferences.exposureSet.contains("i-do-not-exist"))
    }

    @Test
    fun `cannot post exposure twice for the same experiment`() {
        val compassPreferences = object : ICompassPreferences {
            override var lastConfig: String = ""
            override var exposureSet: Set<String> = emptySet()
            override var lastUpdatedInMillis: Long = 0
        }
        CompassTestExperiment.PROFILE.exists = true
        val client = createCompassClient(compassPreferences = compassPreferences)
        runBlocking { client.loadConfig(isUserLoggedIn = false) }
        runBlocking { client.postExposure(CompassTestExperiment.PROFILE, -1L) }
        runBlocking { client.postExposure(CompassTestExperiment.PROFILE, -1L) }
        runBlocking { verify(compassApi, times(1)).postExposure(any()) }
    }

    @Test
    fun `do not make network call when last successful fetch was less than 4 hours ago`() {
        val now = Calendar.getInstance()
        now.add(HOUR, -3)
        val compassPreferences = object : ICompassPreferences {
            override var lastConfig: String = JSONObject().apply {
                put("timestamp", "")
                put("experiments", JSONArray())
            }.toString()
            override var exposureSet: Set<String> = emptySet()
            override var lastUpdatedInMillis: Long = now.timeInMillis
        }
        val client = createCompassClient(compassPreferences = compassPreferences)
        runBlocking { client.loadConfig(true) }
        runBlocking {
            verify(compassApi, never()).getConfig(
                lastChangeDate = any(),
                deviceId = any(),
                osVersion = any(),
                deviceModel = any(),
                deviceBrand = any(),
                deviceCarrier = any(),
                localeString = any(),
                explorerType = any(),
                requestorType = any(),
                appVersion = any(),
                bundleIdentifier = any(),
                userId = anyOrNull(),
                devicePushToken = any()
            )
        }
    }

    @Test
    fun `make network call when last successful fetch was greater than 4 hours ago`() {
        val compassPreferences = object : ICompassPreferences {
            override var lastConfig: String = JSONObject().apply {
                put("timestamp", "")
                put("experiments", JSONArray())
            }.toString()
            override var exposureSet: Set<String> = emptySet()
            override var lastUpdatedInMillis: Long = -TimeUnit.HOURS.toMillis(5)
        }
        val client = createCompassClient(compassPreferences = compassPreferences)
        runBlocking { client.loadConfig(true) }
        runBlocking {
            verify(compassApi, times(1)).getConfig(
                lastChangeDate = any(),
                deviceId = any(),
                osVersion = any(),
                deviceModel = any(),
                deviceBrand = any(),
                deviceCarrier = any(),
                localeString = any(),
                explorerType = any(),
                requestorType = any(),
                appVersion = any(),
                bundleIdentifier = any(),
                userId = anyOrNull(),
                devicePushToken = any()
            )
        }
    }

    @Test
    fun `getConfig sends device info`() {
        val client = createCompassClient(
            compassPreferences = createCompassPreferences(-TimeUnit.HOURS.toMillis(5))
        )

        runBlocking { client.loadConfig(true) }
        runBlocking {
            verify(compassApi, times(1)).getConfig(
                lastChangeDate = any(),
                deviceId = eq(testDeviceInfo.deviceId),
                osVersion = eq(testDeviceInfo.osSdkVersion.toString()),
                deviceModel = eq(testDeviceInfo.model),
                deviceBrand = eq(testDeviceInfo.brand),
                deviceCarrier = eq(testDeviceInfo.carrier),
                localeString = eq("es-ES"),
                explorerType = any(),
                requestorType = any(),
                appVersion = any(),
                bundleIdentifier = any(),
                userId = eq(123L),
                devicePushToken = any()
            )
        }
    }

    private fun createCompassPreferences(timeInMillis: Long) =
        object : ICompassPreferences {
            override var lastConfig: String = JSONObject().apply {
                put("timestamp", "")
                put("experiments", JSONArray())
            }.toString()
            override var exposureSet: Set<String> = emptySet()
            override var lastUpdatedInMillis: Long = timeInMillis
        }

    @Test
    fun `network call omits invalid userid from device info`() {
        val client = createCompassClient(
            compassPreferences = createCompassPreferences(-TimeUnit.HOURS.toMillis(5))
        )

        whenever(userManager.getCurrentUserId()).thenReturn(-1L)

        runBlocking { client.loadConfig(true) }
        runBlocking {
            verify(compassApi, times(1)).getConfig(
                lastChangeDate = any(),
                deviceId = eq(testDeviceInfo.deviceId),
                osVersion = eq(testDeviceInfo.osSdkVersion.toString()),
                deviceModel = eq(testDeviceInfo.model),
                deviceBrand = eq(testDeviceInfo.brand),
                deviceCarrier = eq(testDeviceInfo.carrier),
                localeString = eq("es-ES"),
                explorerType = any(),
                requestorType = any(),
                appVersion = any(),
                bundleIdentifier = any(),
                userId = eq(null),
                devicePushToken = any()
            )
        }
    }

    private fun createCompassClient(
        compassPreferences: ICompassPreferences = mock()
    ): CompassClient {
        val featureSwitches = mock<FeatureSwitches>()
        whenever(featureSwitches.isFeatureEnabled(any())).thenAnswer { true }
        return CompassClient(
            compassExperiment = CompassTestExperiment,
            compassPreferences = compassPreferences,
            deviceInfo = testDeviceInfo,
            compassApi = compassApi,
            crashLogHandler = crashLogHandler,
            gson = Gson(),
            dispatcherProvider = coroutineTestRule.dispatcherProvider,
            userManager = userManager,
            localeUtility = localeUtility,
            timeProvider = mock { on { currentTimeMs } doReturn 0 },
            preferences = preferences
        )
    }

    private fun CompassApi.mockGetConfigResponse(response: CompassApi.CompassConfigResponse) {
        runBlocking {
            whenever(
                this@mockGetConfigResponse.getConfig(
                    lastChangeDate = any(),
                    deviceId = any(),
                    osVersion = any(),
                    deviceModel = any(),
                    deviceBrand = any(),
                    deviceCarrier = any(),
                    localeString = any(),
                    explorerType = any(),
                    requestorType = any(),
                    appVersion = any(),
                    bundleIdentifier = any(),
                    userId = any(),
                    devicePushToken = any()
                )
            ).thenAnswer { response }
        }
    }
}