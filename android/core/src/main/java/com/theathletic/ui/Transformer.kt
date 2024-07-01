package com.theathletic.ui

interface Transformer<in From, out To> {
    fun transform(data: From): To

    operator fun invoke(from: From): To {
        return transform(from)
    }
}