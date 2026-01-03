package com.xah.send.logic.util

import androidx.compose.runtime.Composable
import java.io.File
import java.security.MessageDigest

/**
 * 选择文件
 */
expect suspend fun pickFile() : File?

/**
 * 计算MD5
 */
fun File.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    inputStream().use { fis ->
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (fis.read(buffer).also { bytesRead = it } != -1) {
            md.update(buffer, 0, bytesRead)
        }
    }
    return md.digest().joinToString("") { "%02x".format(it) }
}

/**
 * Android文件选择器
 * @param open 启用
 * @param onResult 回调
 */
@Composable
expect fun AndroidFilePicker(open : Boolean,onResult: (File?) -> Unit)

/**
 * 解决同名文件冲突
 * @param dir 要存放的路径
 * @param fileName 文件名
 */
fun resolveFileConflict(
    dir: File,
    fileName: String
): File {
    val dotIndex = fileName.lastIndexOf('.')

    val baseName: String
    val extension: String?

    if (dotIndex > 0) {
        baseName = fileName.substring(0, dotIndex)
        extension = fileName.substring(dotIndex) // 含 .
    } else {
        baseName = fileName
        extension = null
    }

    var index = 0
    var candidate: File

    do {
        val name = when {
            index == 0 -> fileName
            extension != null -> "$baseName ($index)$extension"
            else -> "$baseName ($index)"
        }
        candidate = File(dir, name)
        index++
    } while (candidate.exists())

    return candidate
}

/**
 * 清除AndroidFilePicker所带来的缓存
 */
expect suspend fun androidCleanCopiedCache()

/**
 * 打开File（文件夹或文件）
 * @param file File
 * @param openParent 打开其所在文件夹
 */
expect fun jumpToOpenFile(file : File, openParent : Boolean)