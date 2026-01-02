package com.xah.send.logic.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.xah.send.application.MyApplication

actual object ClipBoardHelper {
     actual fun copy(str : String, tips : String?) {
        try {
            val clipboard = MyApplication.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(null, str)
            clipboard.setPrimaryClip(clipData)
            tips?.let { showToast(it) }
        } catch (e : Exception) {
            e.printStackTrace()
            showToast("复制到剪切板失败")
        }
    }

    actual fun paste(): String? {
        return try {
            val clipboard = MyApplication.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip() && clipboard.primaryClip!!.itemCount > 0) {
                clipboard.primaryClip!!.getItemAt(0).text?.toString()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("获取剪切板内容失败")
            null
        }
    }
}