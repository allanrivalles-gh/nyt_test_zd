package com.theathletic.annotation.autokoin

import kotlin.reflect.KClass

enum class Scope {
    SINGLE,
    FACTORY
}

@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoKoin(
    val scope: Scope = Scope.FACTORY
)

@Retention(AnnotationRetention.SOURCE)
annotation class Named(val name: String)

@Retention(AnnotationRetention.SOURCE)
annotation class Assisted

@Retention(AnnotationRetention.SOURCE)
annotation class Exposes(
    val clazz: KClass<*>
)