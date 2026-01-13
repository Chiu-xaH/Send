package com.xah.send.logic.jni

import com.xah.send.WINDOW_NAME
import com.xah.send.logic.jni.inf.IDesktopJni
import com.xah.send.logic.util.jvm.NativeLoader
import java.io.File

class WindowsJni() : IDesktopJni {
    companion object {
        init {
            NativeLoader.loadLibraryFiles("windows/windows-jni.dll")
        }
    }

    private external fun pickSingleFile() : String?

    private external fun warn(windowName : String) : Boolean

    external fun getDownloadFolder(): String?

    override fun pickFile(): File? {
        return pickSingleFile()?.let {
            File(it)
        }
    }

    override fun warn() {
        warn(WINDOW_NAME)
    }
}