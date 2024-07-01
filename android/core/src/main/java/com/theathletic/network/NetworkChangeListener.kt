package com.theathletic.network

interface NetworkChangeListener {
    /**
     * Called whenever the device has no network connection then gains a network connection. This is NOT called when
     * the user changes between networks such as turning on WiFi when they have a cell connection or disconnects from
     * WiFi but still has cell connection.
     *
     * @param isMobile - Whether or not the newly connected network is a mobile data connection
     */
    fun onConnected(isMobile: Boolean)

    /**
     * This is called only when the user changes between two connected networks. So from WiFi to cell connection or
     * vice versa. This is NOT called when going from no connection to having connection, use [onConnected] for that.
     * This is NOT called when losing all connection, use [onDisconnected] for that.
     *
     * This is useful when making decision of prefetching data, such as podcasts. We should be free to pre-load more
     * data while on WiFi connections but when we switch, we should be more careful to conserve user data plans.
     *
     * @param isMobile - Whether or not the newly connected network is a mobile data connection
     */
    fun onNetworkChanged(isMobile: Boolean)

    /**
     * Called whenever the phone loses all network connections.
     */
    fun onDisconnected()
}