package com.theathletic.auth

import com.google.gson.Gson
import com.theathletic.auth.data.AuthenticationRepository
import com.theathletic.test.CoroutineTestRule
import com.theathletic.utility.logging.ICrashLogHandler
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class OAuthHelperTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var authService: AuthenticationRepository
    @Mock private lateinit var crashLogHandler: ICrashLogHandler
    @Mock private lateinit var authenticator: Authenticator

    private lateinit var oAuthHelper: OAuthHelper

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        runBlocking {
            whenever(
                authService.authWithOAuth2(
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull()
                )
            ).thenReturn(mock())
        }

        oAuthHelper = OAuthHelper(
            coroutineTestRule.dispatcherProvider,
            authService,
            crashLogHandler,
            authenticator,
            Gson()
        )
    }

    @Test
    fun `nonsensical json results in failure`() {
        runBlocking {
            assertEquals(
                OAuthResult.FAILURE,
                oAuthHelper.useOAuth(
                    "theathletic://oauth-callback?SSS%7B%22id_token%22%3A----A%22eyJraWQiOiJlWGF1bm1MIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLnRoZWF0aGxldGljLndlYi1zaWduLW9uLXRlc3RpbmciLCJleHAiOjE1ODUyNzM2NTAsImlhdCI6MTU4NTI3MzA1MCwic3ViIjoiMDAwNDc3LjM3OWIzMzc4Y2ZiMDRjMmI5YTBjMjljMmMxODBkOTNlLjIzMDQiLCJhdF9oYXNoIjoiUDAtb21SVEkxRXZIVmlPcFl5WWl4ZyIsImVtYWlsIjoiYXp6bXR1OWJwZEBwcml2YXRlcmVsYXkuYXBwbGVpZC5jb20iLCJlbWFpbF92ZXJpZmllZCI6InRydWUiLCJpc19wcml2YXRlX2VtYWlsIjoidHJ1ZSIsImF1dGhfdGltZSI6MTU4NTI3MzA0OSwibm9uY2Vfc3VwcG9ydGVkIjp0cnVlfQ.JlXAbQkToAYSseBktzaIdDBBJ5QncGppX9JgR-lwnjzvmdk0b4OW3oew0PkINrnsk_CclLEsvTM4AT9zCuN9mBbj9MJJdm3qeJbFYfcHF_qddceYZSEPruKdvusx9sIKQZHMd3FQzh_Sgcg8pviQhNThnDobpUJCekbIEZcNwkjbVa5V2S9pHvNLEhae7xkbfjXX8DnTf9G44za7thjGMH6rKdoMEM2UKjpoMxbh_avkAaPgTukFxEV2726wxND39a_6OPFK7dyQnRFiU3WuA8ilv1qc80Pcl_vvXmOZRt3FBw8tNVNTLKUERrP9WCb8HBNkJp7OAA_14SqsDYlXcw%22%2C%22sub%22%3A%22000477.379b3378cfb04c2b9a0c29c2c180d93e.2304%22%2C%22user%22%3Anull%7D",
                    OAuthFlow.APPLE
                )
            )
        }
        verify(crashLogHandler).logException(any())
    }

    @Test
    fun `valid json results in successful network call which results in success`() {
        runBlocking {
            assertEquals(
                OAuthResult.SUCCESS,
                oAuthHelper.useOAuth(
                    "theathletic://oauth-callback?%7B%22id_token%22%3A%22eyJraWQiOiI4NkQ4OEtmIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLnRoZWF0aGxldGljLndlYi1zaWduLW9uLXRlc3RpbmciLCJleHAiOjE1ODUyNzMyMTYsImlhdCI6MTU4NTI3MjYxNiwic3ViIjoiMDAwNDc3LjM3OWIzMzc4Y2ZiMDRjMmI5YTBjMjljMmMxODBkOTNlLjIzMDQiLCJhdF9oYXNoIjoiQXdmOGYyNjRfUWxiYjBpT3lneVE0USIsImVtYWlsIjoiYXp6bXR1OWJwZEBwcml2YXRlcmVsYXkuYXBwbGVpZC5jb20iLCJlbWFpbF92ZXJpZmllZCI6InRydWUiLCJpc19wcml2YXRlX2VtYWlsIjoidHJ1ZSIsImF1dGhfdGltZSI6MTU4NTI3MjYxNSwibm9uY2Vfc3VwcG9ydGVkIjp0cnVlfQ.GU_ul8fJ1zR5tQiw8BXBMbWxj05B-tHscE08eDPhCFpUxmPr3blr5XD1purrTwizpTFttjHiJeLd7yjkksr0GXmsk-OZluk1hcbNA0MtO_xoFhyVljUivI2SRD58sILbsiLqzLaH0WW_zTHwwOlkHK0kjsO5q8yqZb4gROHv_5DOP7jchqrtt7bjQsNfCCvXhk5PF-8WnwFLdOE9s4ABDdo2tefRuZsGTebV3gyf3Z18UZZvyBkXOqh498lJKccNdzTILo700YCVlgBu6gzk8o8AWtIntRHONkyqOLkUHVBLVFhNHsc_mGnEzPed7KPIJyme0ygp5a35i8TPpShbfQ%22%2C%22sub%22%3A%22000477.379b3378cfb04c2b9a0c29c2c180d93e.2304%22%2C%22user%22%3Anull%7D",
                    OAuthFlow.APPLE
                )
            )
        }
    }

    @Test
    fun `valid json results in failed network call which results in failure`() {
        runBlocking {
            whenever(
                authService.authWithOAuth2(
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull()
                )
            ).thenReturn(null)

            assertEquals(
                OAuthResult.FAILURE,
                oAuthHelper.useOAuth(
                    "theathletic://oauth-callback?%7B%22id_token%22%3A%22eyJraWQiOiI4NkQ4OEtmIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLnRoZWF0aGxldGljLndlYi1zaWduLW9uLXRlc3RpbmciLCJleHAiOjE1ODUyNzMyMTYsImlhdCI6MTU4NTI3MjYxNiwic3ViIjoiMDAwNDc3LjM3OWIzMzc4Y2ZiMDRjMmI5YTBjMjljMmMxODBkOTNlLjIzMDQiLCJhdF9oYXNoIjoiQXdmOGYyNjRfUWxiYjBpT3lneVE0USIsImVtYWlsIjoiYXp6bXR1OWJwZEBwcml2YXRlcmVsYXkuYXBwbGVpZC5jb20iLCJlbWFpbF92ZXJpZmllZCI6InRydWUiLCJpc19wcml2YXRlX2VtYWlsIjoidHJ1ZSIsImF1dGhfdGltZSI6MTU4NTI3MjYxNSwibm9uY2Vfc3VwcG9ydGVkIjp0cnVlfQ.GU_ul8fJ1zR5tQiw8BXBMbWxj05B-tHscE08eDPhCFpUxmPr3blr5XD1purrTwizpTFttjHiJeLd7yjkksr0GXmsk-OZluk1hcbNA0MtO_xoFhyVljUivI2SRD58sILbsiLqzLaH0WW_zTHwwOlkHK0kjsO5q8yqZb4gROHv_5DOP7jchqrtt7bjQsNfCCvXhk5PF-8WnwFLdOE9s4ABDdo2tefRuZsGTebV3gyf3Z18UZZvyBkXOqh498lJKccNdzTILo700YCVlgBu6gzk8o8AWtIntRHONkyqOLkUHVBLVFhNHsc_mGnEzPed7KPIJyme0ygp5a35i8TPpShbfQ%22%2C%22sub%22%3A%22000477.379b3378cfb04c2b9a0c29c2c180d93e.2304%22%2C%22user%22%3Anull%7D",
                    OAuthFlow.APPLE
                )
            )
        }
    }
}