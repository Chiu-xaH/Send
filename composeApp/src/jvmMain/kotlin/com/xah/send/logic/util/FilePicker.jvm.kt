package com.xah.send.logic.util

import com.xah.send.logic.jni.WindowsJni
import com.xah.send.logic.model.JvmPlatform
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager

actual fun pickFile(): File? {
    // 判断平台
    return when(getJvmPlatform()) {
        JvmPlatform.WINDOWS -> {
            WindowsJni().pickFile()
        }
        else -> {
            pickFileByJava()
        }
    }
}


private fun pickFileByJava(): File? {
    // 使用系统风格
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (_: Exception) {}

    val chooser = JFileChooser().apply {
        dialogTitle = "选择文件"
        fileSelectionMode = JFileChooser.FILES_ONLY
        isMultiSelectionEnabled = false
    }

    val result = chooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFile
    } else {
        null
    }
}
