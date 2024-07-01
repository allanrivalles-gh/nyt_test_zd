package com.theathletic.extension

import java.io.File

private const val BYTE_SIZE = 1024.0

fun File.getDirectorySizeMb(): Float {
    if (!isDirectory) return 0f

    val totalSize = listFiles()?.sumByDouble {
        it.length() / BYTE_SIZE / BYTE_SIZE
    } ?: 0.0

    return totalSize.toFloat()
}