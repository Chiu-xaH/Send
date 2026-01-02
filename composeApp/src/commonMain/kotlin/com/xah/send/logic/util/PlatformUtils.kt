package com.xah.send.logic.util

import com.xah.send.logic.model.Platform
import java.io.File

expect fun getPlatform() : Platform

fun getDeviceName() : String {
    val platformName = when(getPlatform()) {
        Platform.ANDROID -> "Android设备"
        Platform.DESKTOP -> "计算机"
    }
    return platformName
}

expect fun getPublicDownloadFolder() : File

expect fun simpleLog(msg : String)

expect fun showToast(msg : String)

expect fun startUrl(url : String)

