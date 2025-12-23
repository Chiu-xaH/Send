package com.xah.send.ui.model

import send.composeapp.generated.resources.Res
import send.composeapp.generated.resources.deployed_code
import send.composeapp.generated.resources.deployed_code_filled
import send.composeapp.generated.resources.join
import send.composeapp.generated.resources.join_filled
import send.composeapp.generated.resources.send
import send.composeapp.generated.resources.send_filled

sealed class NavigationRoute(val bean : NavigationItem) {
    object Receive : NavigationRoute(
        NavigationItem(
            "Receive",
            "接收",
            Res.drawable.join,
            Res.drawable.join_filled
        )
    )
    object Send : NavigationRoute(
        NavigationItem(
            "Send",
            "发送",
            Res.drawable.send,
            Res.drawable.send_filled
        )
    )
    object Settings : NavigationRoute(
        NavigationItem(
            "Settings",
            "设置",
            Res.drawable.deployed_code,
            Res.drawable.deployed_code_filled
        )
    )
}
