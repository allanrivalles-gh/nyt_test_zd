package com.theathletic.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isOpen
import com.squareup.kotlinpoet.metadata.readKotlinClassMetadata
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmProperty
import kotlinx.metadata.jvm.KotlinClassMetadata
import java.io.File
import javax.lang.model.element.Element

private val analyticsPackage = "com.theathletic.analytics.newarch"
private val analyticsClassName = ClassName("com.theathletic.analytics", "IAnalytics")
val mutableMapClassName = ClassName("kotlin.collections", "Map")
val mapOfStringToString = mutableMapClassName.parameterizedBy(String::class.asTypeName(), String::class.asTypeName())

@KotlinPoetMetadataPreview
fun createAnalyticsExtensions(
    parentEventClass: Element,
    kaptPath: String?,
    dynamicPropertyEvents: Set<String>,
    noisyEvents: Set<String>
) {
    val fileBuilder = FileSpec.builder(analyticsPackage, "AnalyticsExtensions")

    val fileSpec = fileBuilder.apply {
        // for every Event class
        parentEventClass.enclosedElements.forEach { groupClass ->
            groupClass.enclosedElements.forEach eventForEach@{ eventClass ->
                val metadata = eventClass.getAnnotation(Metadata::class.java)?.readKotlinClassMetadata()
                    ?: return@eventForEach
                val kotlinClassMetadata = metadata as? KotlinClassMetadata.Class
                    ?: throw IllegalStateException("2nd child of root class should be a class")
                val eventKmClass = kotlinClassMetadata.toKmClass()
                val hasDynamicProperties = dynamicPropertyEvents.contains(eventKmClass.name)
                val isNoisyEvent = noisyEvents.contains(eventKmClass.name)
                addExtensionFunction(eventKmClass, hasDynamicProperties, isNoisyEvent)
            }
        }
    }.build()
    val oldPath = "mobile/build/generated/analytics"
    fileSpec.writeTo(File(kaptPath ?: oldPath))
}

@KotlinPoetMetadataPreview
private fun FileSpec.Builder.addExtensionFunction(
    eventKmClass: KmClass,
    hasDynamicProperties: Boolean,
    isNoisyEvent: Boolean
) {
    val constructor = eventKmClass.constructors.first()
    val numParams = constructor.valueParameters.size

    check(numParams == eventKmClass.properties.size) {
        "All constructor parameters must be properties for class ${eventKmClass.name}"
    }

    addTrackExtensionFunction(
        // Only use properties that aren't marked as `override`. For some reason they're getting
        // marked just as `open` but it still works for us.
        eventKmClass.properties.filterNot { it.flags.isOpen },
        eventKmClass,
        hasDynamicProperties,
        isNoisyEvent
    )
}

@OptIn(KotlinPoetMetadataPreview::class)
private fun FileSpec.Builder.addTrackExtensionFunction(
    properties: List<KmProperty>,
    innerClass: KmClass,
    hasDynamicProperties: Boolean,
    isNoisyEvent: Boolean
) {
    val isNoisyParam = if (isNoisyEvent) {
        "isNoisy = true, "
    } else {
        ""
    }
    val codeBlock = if (properties.isEmpty()) {
        CodeBlock.of("""trackEvent(event, ${isNoisyParam}propertiesMap = hashMapOf())""")
    } else {
        CodeBlock.Builder()
            .apply {
                addStatement("""trackEvent(event = event, ${isNoisyParam}propertiesMap = mutableMapOf(""")
                indent()
                apply {
                    properties.forEachIndexed { index, property ->
                        val comma = if (index == properties.size - 1) "" else ","
                        addStatement("""%S to event.${property.name}$comma""", property.name)
                    }
                }
                if (hasDynamicProperties) {
                    addStatement(").also { it.putAll(dynamicProperties) }")
                } else {
                    addStatement(")")
                }
                unindent()
                addStatement(")")
            }
            .build()
    }
    addFunction(
        FunSpec.builder("track").apply {
            addParameter("event", innerClass.toClassName())
            if (hasDynamicProperties) {
                addParameter(
                    ParameterSpec.builder("dynamicProperties", mapOfStringToString)
                        .defaultValue("emptyMap()")
                        .build()
                )
            }
            addCode(codeBlock)
            receiver(analyticsClassName)
        }.build()
    )
}

@OptIn(KotlinPoetMetadataPreview::class)
fun KmClass.toClassName(): ClassName {
    val splitBySlash = this.name.split("/")
    val pkg = splitBySlash.subList(0, splitBySlash.lastIndex).joinToString(".")
    val className = splitBySlash.last()
    return ClassName(pkg, className)
}