package com.theathletic.codegen

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.readKotlinClassMetadata
import com.theathletic.annotation.AnalyticsEvents
import com.theathletic.annotation.HasDynamicProperties
import com.theathletic.annotation.NoisyEvent
import kotlinx.metadata.jvm.KotlinClassMetadata
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@KotlinPoetMetadataPreview
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(FileGenerator.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class FileGenerator : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes() = setOf(AnalyticsEvents::class.java.name)

    override fun getSupportedSourceVersion() = SourceVersion.latest()

    override fun process(set: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        val allEvents = roundEnvironment?.getElementsAnnotatedWith(AnalyticsEvents::class.java) ?: return false
        val noisyEventClassNames = roundEnvironment.getElementsAnnotatedWith(NoisyEvent::class.java)
            .map {
                val metadata = it.getAnnotation(Metadata::class.java)?.readKotlinClassMetadata()
                    ?: throw IllegalStateException("Only kotlin is supported for @NoisyEvent annotation")
                val kotlinClassMetadata = metadata as? KotlinClassMetadata.Class
                    ?: throw IllegalStateException("The kotlin metadata annotation was hijacked")
                kotlinClassMetadata.toKmClass().name
            }.toSet()

        val dynamicPropertyEventClassNames = roundEnvironment.getElementsAnnotatedWith(HasDynamicProperties::class.java)
            .map {
                val metadata = it.getAnnotation(Metadata::class.java)?.readKotlinClassMetadata()
                    ?: throw IllegalStateException("Only kotlin is supported for @HasDynamicProperty annotation")
                val kotlinClassMetadata = metadata as? KotlinClassMetadata.Class
                    ?: throw IllegalStateException("The kotlin metadata annotation was hijacked")
                kotlinClassMetadata.toKmClass().name
            }.toSet()

        check(allEvents.size <= 1) { "Only one class may be annotated with AnalyticsEvents annotation" }
        val kaptPath = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        allEvents.forEach { element ->
            createAnalyticsExtensions(
                element,
                kaptPath,
                dynamicPropertyEventClassNames,
                noisyEventClassNames
            )
        }
        return true
    }
}