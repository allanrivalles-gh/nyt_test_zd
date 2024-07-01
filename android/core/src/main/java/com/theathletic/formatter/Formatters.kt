package com.theathletic.formatter

interface Formatter<T> {
    fun format(formattable: T): Any
}

interface FormatterWithParams<T, Params> {
    fun format(formattable: T, params: Params): Any
}