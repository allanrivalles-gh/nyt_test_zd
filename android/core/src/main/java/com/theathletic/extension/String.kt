package com.theathletic.extension

import java.math.BigInteger
import java.security.MessageDigest

fun String.extGetMd5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

infix fun String?.otherwise(otherString: String): String {
    return if (this != null && this.isNotEmpty()) this else otherString
}

/**
 * Returns `null` if the String is not null but is empty. Helpful for simplifying data binding logic
 */
fun String?.nullIfEmpty(): String? {
    return if (this?.isEmpty() == true) {
        null
    } else {
        this
    }
}

/**
 * Returns a text with the initials of each string word inside a string
 * e.g.: The Athletic -> TA
 */
fun String.splitInitials() = split(" ").mapNotNull {
    if (it.isNotBlank()) it.first() else null
}.joinToString(separator = "")