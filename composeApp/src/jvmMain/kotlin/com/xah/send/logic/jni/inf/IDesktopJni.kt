package com.xah.send.logic.jni.inf

import java.io.File

/**
 * 桌面端原生调用的接口，Windows、Linux、MacOS均需要各自继承实现
 */
interface IDesktopJni {
    /**
     * 选取文件
     */
    fun pickFile() : File?

    /**
     * 任务栏高亮或弹跳提醒
     */
    fun warn()
}