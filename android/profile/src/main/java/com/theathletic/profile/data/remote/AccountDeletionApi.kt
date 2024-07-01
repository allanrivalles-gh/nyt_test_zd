package com.theathletic.profile.data.remote

import com.squareup.moshi.Moshi
import com.theathletic.annotation.autokoin.AutoKoin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.IOException

class AccountDeletionApi @AutoKoin constructor(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }

    private val client by lazy {
        OkHttpClient()
    }
    suspend fun deleteAccount(
        userId: String,
        email: String,
        country: String?,
        countrySubDivision: String?,
    ): Result<Unit> = withContext(coroutineDispatcher) {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(DeleteAccountParams::class.java)
        val params = DeleteAccountParams(
            type = "ERASURE",
            subjectType = "customer",
            subject = DeleteAccountSubject(
                coreIdentifier = email,
                email = email,
                emailIsVerified = true,
                attestedExtraIdentifiers = DeleteAccountExtraIdentifier(
                    arrayOf(
                        DeleteAccountCustomIdentifier(
                            value = userId,
                            name = "user_id"
                        )
                    )
                )
            ),
            region = DeleteAccountRegion(
                country = country,
                countrySubDivision = "$country-$countrySubDivision"
            ),
        )

        try {
            val requestBody = jsonAdapter.toJson(params).toRequestBody(MEDIA_TYPE_JSON)

            val request = Request.Builder()
                .url("https://multi-tenant.sombra.transcend.io/v1/data-subject-request")
                .addHeader("Authorization", "Bearer e529e723940a93fed003bf19bd98d096212bd1f61353c56d8505b276d6c7aa04")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    val exception = IOException("Unexpected code $response")
                    Timber.e(exception)
                    Result.failure(exception)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure(e)
        }
    }
}