package com.theathletic.adapter

interface TheSame {
    fun isItemTheSame(other: Any?): Boolean
    fun isContentTheSame(other: Any?): Boolean
    fun getChangePayload(newItem: Any?): Any? = null
}