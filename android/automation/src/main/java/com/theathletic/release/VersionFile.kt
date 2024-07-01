package com.theathletic.release

import java.io.File
import java.util.Properties

class VersionFile(rootDir: File) {
    companion object {
        const val MAJOR = "version.major"
        const val MINOR = "version.minor"
        const val PATCH = "version.patch"

        const val FILE_NAME = "version.properties"
    }

    private val file = File(
        "${rootDir.path}${File.separator}mobile",
        FILE_NAME
    )
    private val properties = Properties().apply { load(file.inputStream()) }

    val majorVersion get() = properties.getProperty(MAJOR).toInt()
    val minorVersion get() = properties.getProperty(MINOR).toInt()
    val patchVersion get() = properties.getProperty(PATCH).toInt()

    val versionString get() = "$majorVersion.$minorVersion.$patchVersion"

    fun update(major: Int, minor: Int, patch: Int) {
        file.writeText(
            """
                $MAJOR=$major
                $MINOR=$minor
                $PATCH=$patch
            """.trimIndent()
        )
        properties.load(file.inputStream())
    }
}