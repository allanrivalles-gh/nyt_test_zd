package com.theathletic.ads.bridge.data.remote

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BridgeCommandTest {

    @Test
    fun `verify BridgeCommand parses event name correct`() {
        val json = "{\"eventName\":\"AdRendered\",\"eventData\":{}}"
        val command = BridgeCommand.fromJson(json)
        assertEquals(AdSlotEvent.AdRendered, command?.eventName)
    }

    @Test
    fun `verify BridgeCommand parses UnsupportedEvents for incorrect event names`() {
        var json = "{\"eventName\":\"NotValidEvent\",\"eventData\":{}}"
        var command = BridgeCommand.fromJson(json)
        assertEquals(AdSlotEvent.UnsupportedEvent, command?.eventName)

        json = "{}"
        command = BridgeCommand.fromJson(json)
        assertEquals(AdSlotEvent.UnsupportedEvent, command?.eventName)
    }

    @Test
    fun `verify BridgeCommand handles events without extra event data`() {
        var json = "{\"eventName\":\"NotValidEvent\",\"eventData\":{}}"
        var command = BridgeCommand.fromJson(json)
        assertEquals(emptyMap(), command?.eventData)

        json = "{\"eventName\":\"NotValidEvent\"}"
        command = BridgeCommand.fromJson(json)
        assertNull(command?.eventData)
    }

    @Test
    fun `verify BridgeCommand handles extra data within events`() {
        val json = "{\"eventName\":\"AdRendered\",\"eventData\":{\"position\":\"mid1\", \"isEmpty\":true}}"
        val command = BridgeCommand.fromJson(json)
        assertEquals("mid1", command?.eventData?.get("position"))
        assertEquals(true, command?.eventData?.get("isEmpty"))
    }
}