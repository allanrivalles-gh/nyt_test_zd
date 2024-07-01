package com.theathletic.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.SerialExecutor
import androidx.work.impl.utils.taskexecutor.TaskExecutor
import androidx.work.workDataOf
import com.google.common.truth.Truth.assertThat
import com.theathletic.analytics.IAnalytics
import com.theathletic.auth.data.AuthenticationRepository
import com.theathletic.billing.BillingRepository
import com.theathletic.billing.RegisterGooglePurchaseWorker
import com.theathletic.billing.RegisterGooglePurchaseWorker.Companion.TOKEN_KEY
import com.theathletic.di.autoKoinModules
import com.theathletic.injection.apiModule
import com.theathletic.injection.baseModule
import com.theathletic.test.CoroutineTestRule
import com.theathletic.user.IUserManager
import com.theathletic.utility.IPreferences
import com.theathletic.utility.logging.ICrashLogHandler
import java.io.IOException
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class RegisterGooglePurchaseWorkerTest : AutoCloseKoinTest() {
    private val userManager by inject<IUserManager>()
    private val authenticationRepository = mock<AuthenticationRepository>()
    lateinit var context: Context
    lateinit var worker: RegisterGooglePurchaseWorker
    private val testUserId = 13L
    private val testDeviceId = "some_device"

    private val workData = workDataOf(TOKEN_KEY to "some_token")

    @Mock
    private lateinit var billingRepository: BillingRepository
    private var autoCloseable: AutoCloseable? = null

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Before
    fun setupMocks() {
        autoCloseable = MockitoAnnotations.openMocks(this)

        runBlocking {
            whenever(billingRepository.getPurchaseDataByToken(any())).thenReturn(mock())
        }

        startKoin {
            modules(
                autoKoinModules + listOf(
                    baseModule, apiModule,
                    module {
                        single { authenticationRepository }
                        single { billingRepository }
                    }
                )
            )
            declareMock<ICrashLogHandler>()
            declareMock<IPreferences>()
            declareMock<IUserManager>()
            declareMock<IAnalytics>()
        }
        context = mock()
        worker = RegisterGooglePurchaseWorker(context, buildTestParams(Data.EMPTY))
        whenever(userManager.getCurrentUserId()).thenReturn(testUserId)
        whenever(userManager.getDeviceId()).thenReturn(testDeviceId)
    }

    @After
    fun tearDown() {
        autoCloseable?.close()
        stopKoin()
    }

    private fun buildTestParams(inputData: Data): WorkerParameters {
        val backgroundExecutor = mock<SerialExecutor>()
        val executor = mock<TaskExecutor>()
        val params = mock<WorkerParameters>()
        whenever(executor.backgroundExecutor).thenReturn(backgroundExecutor)
        whenever(params.taskExecutor).thenReturn(executor)
        whenever(params.inputData).thenReturn(inputData)
        return params
    }

    @Test
    fun `doWork returns success with good inputs and working api`() {
        worker = RegisterGooglePurchaseWorker(context, buildTestParams(workData))
        val result = runBlocking {
            setupApiSuccess()
            worker.doWork()
        }
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
    }

    @Test
    fun `doWork returns failure with missing token`() {
        val workData = workDataOf()
        worker = RegisterGooglePurchaseWorker(context, buildTestParams(workData))
        val result = runBlocking {
            setupApiSuccess()
            worker.doWork()
        }
        assertThat(result).isEqualTo(ListenableWorker.Result.failure())
    }

    @Test
    fun `doWork returns retry with good inputs and 500 api error`() {
        worker = RegisterGooglePurchaseWorker(context, buildTestParams(workData))
        val result = runBlocking {
            setupApiErrorCode(500)
            worker.doWork()
        }
        assertThat(result).isEqualTo(ListenableWorker.Result.retry())
    }

    @Test
    fun `doWork returns failure with good inputs and non-500 api error`() {
        worker = RegisterGooglePurchaseWorker(context, buildTestParams(workData))
        val result = runBlocking {
            setupApiErrorCode(401)
            worker.doWork()
        }
        assertThat(result).isEqualTo(ListenableWorker.Result.failure())
    }

    @Test
    fun `doWork returns retry with good inputs and first api exception`() {
        worker = RegisterGooglePurchaseWorker(context, buildTestParams(workData))
        val result = runBlocking {
            setupApiException()
            worker.doWork()
        }
        assertThat(result).isEqualTo(ListenableWorker.Result.retry())
    }

    @Test
    fun `doWork returns failure with good inputs and 7 api exceptions`() {
        worker = RegisterGooglePurchaseWorker(context, buildTestParams(workData))
        whenever(worker.runAttemptCount).thenReturn(7)
        val result = runBlocking {
            setupApiException()
            worker.doWork()
        }
        assertThat(result).isEqualTo(ListenableWorker.Result.failure())
    }

    private suspend fun setupApiSuccess() {
        whenever(
            billingRepository.registerGooglePurchase(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(Response.success(true))
    }

    private suspend fun setupApiErrorCode(code: Int) {
        val errorResponse = mock<ResponseBody>()
        val error = Response.error<Boolean>(code, errorResponse)
        whenever(
            billingRepository.registerGooglePurchase(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(error)
    }

    private suspend fun setupApiException() {
        whenever(
            billingRepository.registerGooglePurchase(
                anyOrNull(),
                anyOrNull()
            )
        ).doAnswer { throw IOException("Error") }
    }
}