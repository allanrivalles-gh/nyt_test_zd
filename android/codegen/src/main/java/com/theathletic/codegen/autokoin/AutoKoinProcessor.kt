package com.theathletic.codegen.autokoin

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.annotation.autokoin.Named
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.codegen.utils.fullyQualifiedName
import com.theathletic.codegen.utils.toKmClass
import kotlinx.metadata.KmClass
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import kotlin.reflect.KClass

@KotlinPoetMetadataPreview
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(
    AutoKoinProcessor.OPTION_KAPT_KOTLIN_GENERATED,
    AutoKoinProcessor.OPTION_MODULE_PACKAGE_NAME
)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class AutoKoinProcessor : AbstractProcessor() {

    companion object {
        const val OPTION_KAPT_KOTLIN_GENERATED = "kapt.kotlin.generated"
        const val OPTION_MODULE_PACKAGE_NAME = "theathletic.packagename"
    }

    private val nodes = mutableMapOf<String, Node>()

    private lateinit var filer: Filer
    private lateinit var typeUtils: Types
    private lateinit var elementUtils: Elements
    private lateinit var messager: Messager
    private lateinit var packageName: String

    override fun init(env: ProcessingEnvironment) {
        super.init(env)
        filer = env.filer
        typeUtils = env.typeUtils
        elementUtils = env.elementUtils
        messager = env.messager
        packageName = env.options[OPTION_MODULE_PACKAGE_NAME] ?: "com.theathletic"
    }

    override fun process(set: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment): Boolean {
        if (!roundEnvironment.processingOver()) {
            val classes = roundEnvironment.getElementsAnnotatedWith(AutoKoin::class.java) ?: return false

            if (classes.isEmpty()) return false

            classes.forEach { constructor ->
                val parent = constructor.enclosingElement as TypeElement
                val element = constructor as ExecutableElement
                val exposedType = parent.getAnnotationClassValue<Exposes> { clazz }

                // Validate that if we are exposing as a parent type that the class actually extends
                // or implements that type.
                exposedType?.let {
                    if (!typeUtils.isAssignable(parent.asType(), it)) {
                        throw java.lang.IllegalStateException("${parent.asType()} does not subclass $it")
                    }
                }

                val key = exposedType?.toString() ?: parent.asType().toString()

                nodes[key] = Node(
                    inputs = element.parameters.map { param ->
                        Dependency(
                            variableName = param.simpleName,
                            fullyQualifiedName = param.asType().toString(),
                            nameParameter = param.getAnnotation(Named::class.java)?.name,
                            isAssisted = param.getAnnotation(Assisted::class.java) != null
                        )
                    },
                    clazz = parent.toKmClass(),
                    exposedClazz = exposedType?.let {
                        typeUtils.asElement(it) as TypeElement
                    }?.toKmClass(),
                    scope = element.getAnnotation(AutoKoin::class.java).scope,
                    isViewModel = elementUtils.getTypeElement("androidx.lifecycle.ViewModel")?.let {
                        typeUtils.isAssignable(parent.asType(), it.asType())
                    } ?: false
                )
            }

            val graphValidation = GraphValidator.validate(
                nodes.keys.associateWith { nodes[it]!!.inputs.map { input -> input.fullyQualifiedName } }
            )
            if (graphValidation is GraphValidator.Result.Fail) {
                messager.printMessage(Diagnostic.Kind.ERROR, graphValidation.reason)
                throw IllegalStateException(graphValidation.reason)
            }

            val moduleBlock = CodeBlock.builder().beginControlFlow("module {")

            nodes.values.sortedBy { it.clazz.name }.forEach { node ->
                // Creates assisted params, e.g. (bundle: android.os.Bundle) ->
                val args = node.inputs.filter { it.isAssisted }
                    .joinToString(", ") { "${it.variableName}:·${it.fullyQualifiedName}" }
                val paramsString = if (args.isEmpty()) "" else "($args)·-> "

                // Creates construtor arguments, e.g. get(), get(named("string"))
                val gets = node.inputs.map {
                    when {
                        it.isAssisted -> it.variableName
                        it.nameParameter != null -> "get(named(\"${it.nameParameter}\"))"
                        else -> "get()"
                    }
                }

                // Creates injection type, e.g. viewModel { } or single { }
                val injectionType = when {
                    node.isViewModel -> "viewModel"
                    node.scope == Scope.SINGLE -> "single"
                    else -> "factory"
                }

                moduleBlock.apply {
                    if (node.exposedClazz != null) {
                        beginControlFlow(
                            "$injectionType<%T> {",
                            node.exposedClazz.fullyQualifiedName
                        )
                    } else {
                        beginControlFlow("$injectionType {")
                    }
                    addStatement(
                        "$paramsString%T(${gets.joinToString(", ")})",
                        node.clazz.fullyQualifiedName
                    )
                    endControlFlow()
                }
            }

            moduleBlock.endControlFlow()

            val propertySpec = PropertySpec.builder(
                "autoKoinModule",
                ClassName("org.koin.core.module", "Module")
            )
                .initializer(moduleBlock.build())
                .build()

            FileSpec.builder("$packageName.di", "AutoKoinModule")
                .addImport("org.koin.dsl", "module")
                .addImport("org.koin.androidx.viewmodel.dsl", "viewModel")
                .addImport("org.koin.core.qualifier", "named")
                .addProperty(propertySpec)
                .build()
                .writeTo(filer)
        }
        return true
    }

    override fun getSupportedAnnotationTypes() = setOf(AutoKoin::class.java.name)

    override fun getSupportedSourceVersion() = SourceVersion.latest()!!
}

@KotlinPoetMetadataPreview
data class Node(
    val inputs: List<Dependency>,
    val clazz: KmClass,
    val exposedClazz: KmClass?,
    val scope: Scope,
    val isViewModel: Boolean
)

data class Dependency(
    val variableName: CharSequence,
    val fullyQualifiedName: String,
    val nameParameter: String?,
    val isAssisted: Boolean
)

inline fun <reified T : Annotation> Element.getAnnotationClassValue(
    field: T.() -> KClass<*>
): TypeMirror? {
    val annotation = getAnnotation(T::class.java) ?: return null
    return try {
        annotation.field()
        throw Exception("Expected to get a MirroredTypeException")
    } catch (e: MirroredTypeException) {
        e.typeMirror
    }
}