package com.theathletic.extension

import androidx.databinding.ObservableField

typealias ObservableString = ObservableField<String?>

class ObservableStringNonNull(value: String) : ObservableFieldNonNull<String>(value) {
    constructor() : this("")
}

open class ObservableFieldNonNull<T>(value: T) : ObservableField<T>(value) {
    /**
     * This works, because we provide empty string in constructor
     */
    override fun get(): T = super.get()!!

    /**
     * Can't set null
     */
    @Suppress("RedundantOverride")
    override fun set(value: T) = super.set(value)
}