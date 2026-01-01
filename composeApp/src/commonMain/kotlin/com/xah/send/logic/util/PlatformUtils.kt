package com.xah.send.logic.util

import com.xah.send.logic.model.Platform

expect fun getPlatform() : Platform

fun getDeviceName() : String {
    val platformName = when(getPlatform()) {
        Platform.ANDROID -> "Android设备"
        Platform.DESKTOP -> "计算机"
    }
    return platformName
}