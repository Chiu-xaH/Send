package com.xah.send.logic.util

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

actual object ClipBoardHelper {
    actual fun copy(str: String, tips: String?) {
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val selection = StringSelection(str)
            clipboard.setContents(selection, null)
            tips?.let { showToast(it) }
        } catch (e: Exception) {
            showToast("复制到剪切板失败")
            e.printStackTrace()
        }
    }

    actual fun paste(): String? {
        return try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                clipboard.getData(DataFlavor.stringFlavor) as? String
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