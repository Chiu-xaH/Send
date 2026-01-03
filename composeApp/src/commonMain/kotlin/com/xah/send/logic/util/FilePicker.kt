package com.xah.send.logic.util

import java.io.File
import java.security.MessageDigest

expect fun pickFile() : File?

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
