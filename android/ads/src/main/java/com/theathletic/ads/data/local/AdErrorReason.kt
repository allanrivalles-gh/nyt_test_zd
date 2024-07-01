package com.theathletic.ads.data.local

import com.google.android.gms.ads.AdRequest

private const val ERROR_CODE_AD_IS_BLANK = 12
private const val ERROR_CODE_AD_PRIVACY_DATA_MISSING = 13
private const val ERROR_CODE_AD_UNIT_PATH_IS_INVALID = 14
private const val ERROR_CODE_UNKNOWN = -1

enum class AdErrorReason(val code: Int, val reason: String) {
    INTERNAL_ERROR(AdRequest.ERROR_CODE_INTERNAL_ERROR, "Internal error"),
    INVALID_REQUEST(AdRequest.ERROR_CODE_INVALID_REQUEST, "Invalid request"),
    NETWORK_ERROR(AdRequest.ERROR_CODE_NETWORK_ERROR, "Network Error"),
    NO_FILL_ERROR(AdRequest.ERROR_CODE_NO_FILL, "No Fill"),
    APP_ID_MISSING(AdRequest.ERROR_CODE_APP_ID_MISSING, "App ID Missing"),
    REQEUST_ID_MISMATCH(AdRequest.ERROR_CODE_REQUEST_ID_MISMATCH, "Request ID Mismatch"),
    INVALID_AD_STRING(AdRequest.ERROR_CODE_INVALID_AD_STRING, "Invalid Ad String"),
    MEDIATION_NO_FILL(AdRequest.ERROR_CODE_MEDIATION_NO_FILL, "Mediation No Fill"),
    AD_IS_BLANK(ERROR_CODE_AD_IS_BLANK, "Ad is Blank"),
    AD_PRIVACY_DATA_MISSING(ERROR_CODE_AD_PRIVACY_DATA_MISSING, "Ad Privacy Data is Missing"),
    INVALID_AD_UNIT_PATH(ERROR_CODE_AD_UNIT_PATH_IS_INVALID, "Ad Unit Path is Invalid"),
    UNKNOWN_ERROR(ERROR_CODE_UNKNOWN, "Unknown Error.");

    companion object {
        fun findByCode(code: Int): AdErrorReason {
            return values().firstOrNull { it.code == code } ?: UNKNOWN_ERROR
        }
    }
}