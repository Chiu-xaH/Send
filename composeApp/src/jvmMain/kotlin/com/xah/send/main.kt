package com.xah.send

import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.xah.send.ui.screen.App

const val WINDOW_NAME = "Send"

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = WINDOW_NAME,
        state = WindowState(position = WindowPosition.Aligned(Alignment.Center)),
//        alwaysOnTop = true
    ) {
        App()
    }
}