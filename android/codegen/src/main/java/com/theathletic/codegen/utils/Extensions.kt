package com.theathletic.codegen.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.readKotlinClassMetadata
import kotlinx.metadata.KmClass
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.lang.model.element.TypeElement

@KotlinPoetMetadataPreview
fun TypeElement.toKmClass(): KmClass {
    return this.kotlinClassMetadata<KotlinClassMetadata.Class>().toKmClass()
}

@KotlinPoetMetadataPreview
inline fun <reified T : KotlinClassMetadata> TypeElement.kotlinClassMetadata(): T {
    return this.getAnnotation(Metadata::class.java)?.readKotlinClassMetadata()!! as T
}

@KotlinPoetMetadataPreview
val KmClass.fullyQualifiedName: ClassName get() {
    return ClassName.bestGuess(this.name.replace("/", "."))
}