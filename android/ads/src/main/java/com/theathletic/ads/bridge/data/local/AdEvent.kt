package com.theathletic.ads.bridge.data.local

sealed class AdEvent(id: Int) {
    object NotInitialized : AdEvent(-1)
    object Attach : AdEvent(-1)
    data class AdRequest(
        val id: Int,
        val pos: String?,
        val extras: Map<String, Any>? = null
    ) : AdEvent(id = id)
    data class AdResponseSuccess(
        val id: Int,
        val pos: String?,
        val extras: Map<String, Any>? = null
    ) : AdEvent(id = id)
    data class AdResponseFail(
        val id: Int,
        val pos: String?,
        val error: String,
        val extras: Map<String, Any>? = null
    ) : AdEvent(id = id)
    data class AdNoFill(
        val id: Int,
        val pos: String?,
        val extras: Map<String, Any>? = null
    ) : AdEvent(id = id)
    data class AdImpression(
        val id: Int,
        val pos: String?,
        val extras: Map<String, Any>? = null
    ) : AdEvent(id = id)
}