package com.theathletic.profile.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class DeleteAccountParams(
    val type: String,
    val subjectType: String,
    val subject: DeleteAccountSubject,
    val region: DeleteAccountRegion,
)

@JsonClass(generateAdapter = true)
internal data class DeleteAccountSubject(
    val coreIdentifier: String,
    val email: String,
    val emailIsVerified: Boolean,
    val attestedExtraIdentifiers: DeleteAccountExtraIdentifier
)

@JsonClass(generateAdapter = true)
internal data class DeleteAccountRegion(
    val country: String?,
    val countrySubDivision: String?,
)

@JsonClass(generateAdapter = true)
internal class DeleteAccountExtraIdentifier(
    val custom: Array<DeleteAccountCustomIdentifier>,
)

@JsonClass(generateAdapter = true)
internal data class DeleteAccountCustomIdentifier(
    val value: String,
    val name: String,
)

data class ConsentDetails(
    val isConfirmed: Boolean
)