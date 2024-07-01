package com.theathletic.utility

inline fun <E : Any, R : Any?, T : Collection<E>> T.transformIfNotEmptyElseNull(defaultValue: (T) -> R?): R? {
    return if (this.isEmpty()) null else defaultValue(this)
}