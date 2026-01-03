package com.xah.send.logic.util

import com.xah.send.logic.model.JvmPlatform

fun getJvmPlatform(): JvmPlatform {
    val osName = System.getProperty("os.name")?.lowercase() ?: return JvmPlatform.UNKNOWN

    return when {
        osName.contains("win") -> JvmPlatform.WINDOWS
        osName.contains("mac") -> JvmPlatform.MAC
        osName.contains("linux") -> JvmPlatform.LINUX
        else -> JvmPlatform.UNKNOWN
    }
}

