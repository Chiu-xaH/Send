package com.xah.send.logic.util

import java.awt.Desktop
import java.net.URI
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

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