package com.xah.send.logic.util

import android.content.Intent
import android.os.Environment
import androidx.core.net.toUri
import com.xah.send.application.MyApplication
import com.xah.send.logic.model.Platform
import java.io.File

actual fun getPlatform(): Platform = Platform.ANDROID

actual fun getPublicDownloadFolder(): File {
    return Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS
    )
}

actual fun simpleLog(msg: String) {
    LogUtil.debug(msg)
}

actual fun showToast(msg: String) {
    ToastUtil.showToast(MyApplication.context,msg)
}

actual fun startWebUrl(url: String) {
    try {
        val it = Intent(Intent.ACTION_VIEW, url.toUri())
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        MyApplication.context.startActivity(it)
    } catch (e : Exception) {
        showToast("启动浏览器失败")
    }
}