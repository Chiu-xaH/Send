package com.xah.send.ui.model.navigation.home

import com.xah.send.ui.model.navigation.BottomBarItem
import send.composeapp.generated.resources.Res
import send.composeapp.generated.resources.deployed_code
import send.composeapp.generated.resources.deployed_code_filled
import send.composeapp.generated.resources.join
import send.composeapp.generated.resources.join_filled
import send.composeapp.generated.resources.send
import send.composeapp.generated.resources.send_filled

sealed class HomeNavigationRoute(val bean : BottomBarItem) {
    object Receive : HomeNavigationRoute(
        BottomBarItem(
            "Receive",
            "接收",
            Res.drawable.join,
            Res.drawable.join_filled
        )
    )
    object Send : HomeNavigationRoute(
        BottomBarItem(
            "Send",
            "发送",
            Res.drawable.send,
            Res.drawable.send_filled
        )
    )
    object Settings : HomeNavigationRoute(
        BottomBarItem(
            "Settings",
            "设置",
            Res.drawable.deployed_code,
            Res.drawable.deployed_code_filled
        )
    )
}