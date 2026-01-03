package com.xah.send.logic.util

import java.io.File

/**
 * 从resources目录加载native库
 */
object NativeLoader {
    /**
     * 根目录配置
     */
    private const val ROOT_PATH = "/lib/"

    /**
     * 从resources目录加载native库
     */
    fun loadLibraryFiles(resourcePath: String) {
        val path = "$ROOT_PATH$resourcePath"
        val inputStream = NativeLoader::class.java
            .getResourceAsStream(path)
            ?: error("Native library not found: $path")

        val tempFile = File.createTempFile("native_", ".dll")
        tempFile.deleteOnExit()

        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        System.load(tempFile.absolutePath)
    }
}