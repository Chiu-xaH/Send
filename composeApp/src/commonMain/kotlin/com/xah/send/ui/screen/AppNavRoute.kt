package com.xah.send.ui.screen

sealed class AppNavRoute(val route : String,val label : String) {
    data object ReceiveFile : AppNavRoute(
        "ReceiveFile",
        "接收",
    )
    data object ReceiveText : AppNavRoute(
        "ReceiveText",
        "接收",
    )
    data object Home : AppNavRoute(
        "Home",
        "设置",
    )
}