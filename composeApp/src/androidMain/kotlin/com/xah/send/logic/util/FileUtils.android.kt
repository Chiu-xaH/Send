package com.xah.send.logic.util

import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import com.xah.send.application.MyApplication
import com.xah.send.ui.componment.LaunchFilePicker
import com.xah.send.ui.componment.cleanCopiedCache
import java.io.File

actual suspend fun pickFile() : File? {
    return null
}

@Composable
actual fun AndroidFilePicker(open: Boolean, onResult: (File?) -> Unit) {
    LaunchFilePicker(open,onResult)
}

actual suspend fun androidCleanCopiedCache() {
    cleanCopiedCache(MyApplication.context)
}

actual fun jumpToOpenFile(file: File, openParent: Boolean) {
    val context = MyApplication.context

    if (!file.exists()) {
        showToast("文件不存在")
        return
    }

    try {
        val targetFile = if (openParent) {
            file.parentFile ?: run {
                showToast("无法获取父目录")
                return
            }
        } else {
            file
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", targetFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            if (openParent) {
                // 打开文件夹
                setDataAndType(uri,"*/*")
            } else {
                // 打开文件
                setDataAndType(uri, getMimeType(file))
            }

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(
            Intent
                .createChooser(intent, if (openParent) "打开所在文件夹" else "选择打开方式")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (e: Exception) {
        e.printStackTrace()
        showToast("系统无法打开")
    }
}

private fun getMimeType(file: File): String {
    val extension = MimeTypeMap.getFileExtensionFromUrl(file.name)
    return MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(extension.lowercase())
        ?: "*/*"
}
