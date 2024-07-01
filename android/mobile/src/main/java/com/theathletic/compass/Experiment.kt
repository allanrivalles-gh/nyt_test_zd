package com.theathletic.compass

import com.theathletic.compass.codegen.VariantKey
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.utility.logging.ICrashLogHandler

abstract class Experiment {
    abstract var name: String
    abstract var exists: Boolean
    abstract var activeVariant: Variant?
    abstract val crashLogHandler: ICrashLogHandler
    abstract val debugPreferences: DebugPreferences
    var client: CompassClient? = null

    abstract fun copy(activeVariant: Variant, exists: Boolean): Experiment
}

interface Variant {
    val _name: String
    fun populateFromFieldMap(
        fieldMap: Map<String, FieldResponse>,
        crashLogHandler: ICrashLogHandler
    ): Variant
}

data class FieldResponse(
    val type: String,
    val key: String,
    val value: String
)

abstract class ICompassExperiment {
    @Volatile open var experimentMap: HashMap<String, Experiment> = hashMapOf()

    abstract val variantMap: HashMap<VariantKey, Variant>
}