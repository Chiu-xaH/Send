package com.xah.send.ui.screen.home

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xah.send.logic.function.find.DeviceBroadcastHelper
import com.xah.send.ui.model.navigation.home.HomeNavigationRoute
import com.xah.send.ui.screen.home.receive.ReceiveScreen
import com.xah.send.ui.screen.home.send.SendScreen
import com.xah.send.ui.screen.home.settings.SettingsScreen
import com.xah.send.ui.util.navigation.currentRouteWithoutArgs
import com.xah.send.ui.util.navigation.navigateForBottomBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

private val navigationItems = listOf(
    HomeNavigationRoute.Send.bean,
    HomeNavigationRoute.Receive.bean,
    HomeNavigationRoute.Settings.bean,
)

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    val defaultPage = remember { navigationItems[0] }
    val currentPage = navigationItems.find {
        navController.currentRouteWithoutArgs() == it.route
    } ?: defaultPage

    // 在主页时一直轮询发送自己的信号，让其他设备探测到
    LaunchedEffect(Unit) {
        while(true) {
            // 轮询
            DeviceBroadcastHelper.broadcastSelf()
            delay(DeviceBroadcastHelper.TIME)
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            navigationItems.forEach { item ->
                val selected = item == currentPage
                item(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigateForBottomBar(item.route)
                        }
                    },
                    label = { Text(text = item.label) },
                    icon = {
                        Icon(
                            painterResource(
                                if(selected) item.filledIcon
                                else item.icon
                            ),
                            contentDescription = item.label
                        )
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = defaultPage.route
        ) {
            composable(HomeNavigationRoute.Receive.bean.route) {
                ReceiveScreen()
            }
            composable(HomeNavigationRoute.Send.bean.route) {
                SendScreen()
            }
            composable(HomeNavigationRoute.Settings.bean.route) {
                SettingsScreen()
            }
        }
    }
}