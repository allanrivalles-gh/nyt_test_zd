package com.theathletic.utility

import com.theathletic.entity.main.FEED_MY_FEED_ID

object LogoUtility {

    @JvmStatic
    fun getTeamLogoPath(id: Number?) = id?.let {
        when (id) {
            0 -> "file:///android_asset/logos/team-logo-empty.png"
            0L -> "file:///android_asset/logos/team-logo-empty.png"
            -1 -> "file:///android_asset/logos/team-logo-empty.png"
            -1L -> "file:///android_asset/logos/team-logo-empty.png"
            FEED_MY_FEED_ID -> "file:///android_asset/logos/team-logo-all-300x300.png"
            else -> "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-$id-300x300.png"
        }
    } ?: ""

    @JvmStatic
    fun getTeamLogoPath(id: String) = getTeamLogoPath(id.toIntOrNull())

    @JvmStatic
    fun getTeamLogoPath(idsList: ArrayList<Long>): String {
        if (!idsList.isEmpty()) {
            val id = idsList.first()
            return when (id) {
                0L -> "file:///android_asset/logos/team-logo-empty.png"
                -1L -> "file:///android_asset/logos/team-logo-empty.png"
                FEED_MY_FEED_ID -> "file:///android_asset/logos/team-logo-all-300x300.png"
                else -> "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-$id-300x300.png"
            }
        }
        return "file:///android_asset/logos/team-logo-empty.png"
    }

    @JvmStatic
    fun getTeamSmallLogoPath(id: Number?) = id?.let {
        when (id) {
            0 -> "file:///android_asset/logos/team-logo-empty.png"
            0L -> "file:///android_asset/logos/team-logo-empty.png"
            -1 -> "file:///android_asset/logos/team-logo-empty.png"
            -1L -> "file:///android_asset/logos/team-logo-empty.png"
            FEED_MY_FEED_ID -> "file:///android_asset/logos/team-logo-all-50x50.png"
            else -> "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-$id-50x50.png"
        }
    } ?: ""

    @JvmStatic
    fun getTeamSmallLogoPath(idsList: ArrayList<Long>, position: Int = 0): String {
        if (idsList.size > position) {
            val id = idsList[position]
            return when (id) {
                0L -> "file:///android_asset/logos/team-logo-empty.png"
                -1L -> "file:///android_asset/logos/team-logo-empty.png"
                FEED_MY_FEED_ID -> "file:///android_asset/logos/team-logo-all-50x50.png"
                else -> "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-$id-50x50.png"
            }
        }
        return "file:///android_asset/logos/team-logo-empty.png"
    }

    @JvmStatic
    @Deprecated("Use getColoredLeagueLogoPath(), as the URL generated from this method is not returning Logos for all the leagues")
    fun getColoredLeagueSmallLogoPath(id: Number?) = id?.let {
        "https://s3-us-west-2.amazonaws.com/theathletic-league-logos/league-$id-color-small@2x.png" // 100x100
    } ?: ""

    @JvmStatic
    fun getColoredLeagueLogoPath(id: Number?) = id?.let {
        "https://s3-us-west-2.amazonaws.com/theathletic-league-logos/league-$id-color-small@3x.png" // 100x100
    } ?: ""

    @JvmStatic
    fun getColoredLeagueLogoPath(id: String) = "https://s3-us-west-2.amazonaws.com/theathletic-league-logos/league-$id-color-small@3x.png" // 100x100
}