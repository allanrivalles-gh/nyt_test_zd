package com.theathletic.analytics

import android.util.Log
import com.datadog.android.log.Logger
import kotlin.test.assertFailsWith
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DatadogWrapperTest {

    @Mock
    lateinit var mockDatadogLogger: DatadogLogger
    @Mock
    lateinit var mockLogger: Logger

    private lateinit var datadogWrapper: DatadogWrapper

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        datadogWrapper = DatadogWrapper(mockDatadogLogger)
        whenever(mockDatadogLogger.crashLogger).thenReturn(mockLogger)
        whenever(mockDatadogLogger.analyticsLogger).thenReturn(mockLogger)
        whenever(mockDatadogLogger.isInitialized()).thenReturn(true)
    }

    @Test
    fun `verify analytics logger info log is called when send log occurs`() {
        datadogWrapper.sendLog(message = "test")
        verify(mockLogger).i("test")
    }

    @Test
    fun `verify analytics logger with proper log priority is called when send log with priority occurs`() {
        datadogWrapper.sendLog(priority = Log.DEBUG, message = "test")
        verify(mockLogger).d("test")
        datadogWrapper.sendLog(priority = Log.WARN, message = "test")
        verify(mockLogger).w("test")
        datadogWrapper.sendLog(priority = Log.ERROR, message = "test")
        verify(mockLogger).e("test")
        datadogWrapper.sendLog(priority = Log.VERBOSE, message = "test")
        verify(mockLogger).v("test")
    }

    @Test
    fun `verify analytics logger uses verbose priority if an unsupported priority is used with send log`() {
        datadogWrapper.sendLog(priority = Log.ASSERT, message = "test")
        verify(mockLogger).v("test")
    }

    @Test
    fun `verify crash logger is called when log exception occurs`() {
        datadogWrapper.logException(Throwable())
        verify(mockLogger).e(any(), any(), any())
    }

    @Test
    fun `verify crash logger is called when track exception occurs`() {
        val cause = "because"
        val message = "error"
        val log = "log"
        datadogWrapper.trackException(Throwable(), cause, message, log)
        verify(mockLogger).e(
            any(),
            any(),
            eq(
                mapOf(
                    "cause" to cause,
                    "message" to message,
                    "log" to log
                )
            )
        )
    }

    @Test
    fun `IllegalStateException when send log called without being properly initialized`() {
        val mockBadDatadogLogger = mock(DatadogLogger::class.java)
        whenever(mockBadDatadogLogger.isInitialized()).thenReturn(false)

        assertFailsWith<java.lang.IllegalStateException> {
            val wrapper = DatadogWrapper(mockBadDatadogLogger)
            wrapper.sendLog(message = "test")
        }
    }

    @Test
    fun `IllegalStateException when log error called without being properly initialized`() {
        val mockBadDatadogLogger = mock(DatadogLogger::class.java)
        whenever(mockBadDatadogLogger.isInitialized()).thenReturn(false)

        assertFailsWith<java.lang.IllegalStateException> {
            val wrapper = DatadogWrapper(mockBadDatadogLogger)
            wrapper.logException(Throwable())
        }
    }

    @Test
    fun `IllegalStateException when track error called without being properly initialized`() {
        val mockBadDatadogLogger = mock(DatadogLogger::class.java)
        whenever(mockBadDatadogLogger.isInitialized()).thenReturn(false)

        assertFailsWith<java.lang.IllegalStateException> {
            val wrapper = DatadogWrapper(mockBadDatadogLogger)
            wrapper.trackException(Throwable())
        }
    }
}