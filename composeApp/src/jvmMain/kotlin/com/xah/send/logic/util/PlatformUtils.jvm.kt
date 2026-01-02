package com.xah.send.logic.util

import com.xah.send.logic.model.Platform
import java.awt.Desktop
import java.io.File
import java.net.URI
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

actual fun getPlatform(): Platform = Platform.DESKTOP

actual fun getPublicDownloadFolder(): File {
    val home = System.getProperty("user.home")
    val download = File(home, "Downloads")

    if (!download.exists()) {
        download.mkdirs()
    }

    return download
}

actual fun simpleLog(msg: String) {
    println(msg)
}

actual fun showToast(msg: String) {
    SwingUtilities.invokeLater {
        JOptionPane.showMessageDialog(null, msg)
    }
}

actual fun startUrl(url: String) {
    if (Desktop.isDesktopSupported()) {
        val desktop = Desktop.getDesktop()
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(URI(url))
        }
    }
}