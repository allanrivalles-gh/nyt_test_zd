package com.theathletic.entity.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * Filters the types of updates from the entity table so a Flow collector can get notified when a
 * type it cares about gets updated. This prevents unnecessary computations from occuring.
 *
 * For instance, if you have a screen that only cares about articles and podcasts, then you can
 * use this to specify those entity types so that it's [Flow.collect] function does not get called
 * when a headline entity updates.
 */
fun Flow<Set<AthleticEntity.Type>>.filterTypes(vararg types: AthleticEntity.Type) = flow {
    val typeSet = types.toSet()

    collect { entityTypes ->
        val intersection = typeSet.intersect(entityTypes)
        if (intersection.isNotEmpty()) {
            emit(intersection)
        }
    }
}