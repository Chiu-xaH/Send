package com.xah.send.logic.util

import androidx.compose.runtime.Composable
import com.xah.send.logic.jni.WindowsJni
import com.xah.send.logic.model.JvmPlatform
import com.xah.send.logic.util.jvm.getJvmPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.io.File
import java.io.IOException
import javax.swing.JFileChooser
import javax.swing.UIManager

actual suspend fun pickFile(): File? {
    // 判断平台
    return when(getJvmPlatform()) {
        JvmPlatform.WINDOWS -> {
            WindowsJni().pickFile()
        }
        else -> {
            pickFileByJava2()
        }
    }
}

suspend fun pickFileByJava2(): File? = withContext(Dispatchers.IO) {
    val dialog = java.awt.FileDialog(null as java.awt.Frame?, "选择文件", java.awt.FileDialog.LOAD)
    dialog.isVisible = true

    val dir = dialog.directory
    val file = dialog.file

    if (dir != null && file != null) {
        File(dir, file)
    } else {
        null
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

@Composable
actual fun AndroidFilePicker(open: Boolean, onResult: (File?) -> Unit) {}

actual suspend fun androidCleanCopiedCache() {}

actual fun jumpToOpenFile(file: File, openParent : Boolean) {
    try {
        if (!file.exists()) {
            showToast("文件不存在")
            return
        }

        if (!Desktop.isDesktopSupported()) {
            showToast("系统不支持，请自行按目录寻找")
            return
        }

        val desktop = Desktop.getDesktop()
        val parent = file.parentFile

        if(openParent) {
            if (parent != null && parent.exists()) {
                desktop.open(parent)
            } else {
                showToast("无法打开文件及所在目录")
            }
        } else {
            try {
                // 优先尝试直接打开
                desktop.open(file)
            } catch (e: IOException) {
                // 无关联程序 / 无法打开文件
                val parent = file.parentFile
                if (parent != null && parent.exists()) {
                    desktop.open(parent)
                } else {
                    showToast("无法打开文件及所在目录")
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        e.message?.let { showToast(it) }
    }
}
