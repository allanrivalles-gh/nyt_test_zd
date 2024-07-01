package com.theathletic.release.data

import com.squareup.moshi.Moshi
import com.theathletic.release.data.remote.PullRequestParams
import com.theathletic.release.data.remote.ReleaseParams
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class GithubRepository(private val gitToken: String) {
    companion object {
        val MEDIA_TYPE_JSON = "text/json; charset=utf-8".toMediaType()
    }

    private val client by lazy {
        OkHttpClient()
    }

    internal fun createGHPrerelease(
        tagName: String,
        branchName: String,
        title: String
    ) {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(ReleaseParams::class.java)

        val params = ReleaseParams(
            name = title,
            tagName = tagName,
            targetCommitish = branchName,
            prerelease = true,
            generateReleaseNotes = true
        )
        val postContents = jsonAdapter.toJson(params)

        val request = Request.Builder()
            .url("https://api.github.com/repos/TheAthletic/android/releases")
            .addHeader("Authorization", "token $gitToken")
            .addHeader("Accept", "application/vnd.github.v3+json")
            .post(postContents.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            println("Prerelease created for: $tagName")
        }
    }

    internal fun createPullRequest(
        branchName: String,
        baseBranchName: String,
        title: String,
        body: String = "Automated PR created from release script: $title"
    ) {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(PullRequestParams::class.java)

        val params = PullRequestParams(
            title = title,
            head = branchName,
            body = body,
            base = baseBranchName
        )
        val postContents = jsonAdapter.toJson(params)

        val request = Request.Builder()
            .url("https://api.github.com/repos/TheAthletic/android/pulls")
            .addHeader("Authorization", "token $gitToken")
            .addHeader("Accept", "application/vnd.github.v3+json")
            .post(postContents.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            println("PR created for: $branchName")
        }
    }

    internal fun validateAuth() {
        val request = Request.Builder()
            .url("https://api.github.com/repos/TheAthletic/android/pulls")
            .addHeader("Authorization", "token $gitToken")
            .addHeader("Accept", "application/vnd.github.v3+json")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            println("Successfully connected to github.com with given token")
        }
    }
}