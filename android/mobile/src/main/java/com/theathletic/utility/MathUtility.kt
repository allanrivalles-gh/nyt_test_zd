package com.theathletic.utility

object MathUtility {
    fun incrementIfLessThanOrEqual(baseValue: Int, incrementableValue: Int): Int {
        return if (incrementableValue <= baseValue) {
            baseValue + 1
        } else {
            incrementableValue
        }
    }
}