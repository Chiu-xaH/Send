package com.xah.send.logic.util

import com.xah.send.logic.model.Platform
import java.io.File

/**
 * 获取运行的平台：Android或Jvm
 */
expect fun getPlatform() : Platform

/**
 * 获取简略的设备名
 */
fun getSimpleDeviceName() : String {
    val platformName = when(getPlatform()) {
        Platform.ANDROID -> "Android设备"
        Platform.DESKTOP -> "计算机"
    }
    return platformName
}

/**
 * 获取公共下载文件夹路径
 */
expect fun getPublicDownloadFolder() : File

/**
 * 打印日志
 * @param msg 日志内容
 */
expect fun simpleLog(msg : String)

/**
 * 显示提示
 * @param msg 文字提示
 */
expect fun showToast(msg : String)

/**
 * 打开网页
 * @param url 链接
 */
expect fun startWebUrl(url : String)

