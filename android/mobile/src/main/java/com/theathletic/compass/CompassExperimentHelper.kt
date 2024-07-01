package com.theathletic.compass

import com.theathletic.utility.logging.ICrashLogHandler

/**
 * Exception thrown when a type returned by the server does not match the type originally defined when codegen was run
 */
class CompassTypeMismatchException : Exception()

class VariantDoesNotExistException(override val message: String = "") : Exception(message)

class CompassNotInitializedException(
    override val message: String = "You cannot post an exposure until compass has initialized"
) : Exception(message)

inline fun <reified T> safeValue(
    value: String?,
    fallbackValue: T,
    crashLogHandler: ICrashLogHandler
): T {
    if (value == null) return fallbackValue
    return try {
        when (T::class) {
            Int::class -> value.toInt() as T
            Boolean::class -> value.toBoolean() as T
            Float::class -> value.toFloat() as T
            Double::class -> value.toDouble() as T
            else -> throw IllegalArgumentException("safeValue() cannot handle type ${T::class.simpleName}")
        }
    } catch (e: NumberFormatException) {
        crashLogHandler.logException(CompassTypeMismatchException())
        fallbackValue
    }
}