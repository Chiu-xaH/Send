package com.xah.send.ui.screen

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xah.send.logic.util.simpleLog
import com.xah.send.ui.model.NavigationRoute
import com.xah.send.ui.screen.receive.ReceiveScreen
import com.xah.send.ui.screen.receive.ReceiveServerManager
import com.xah.send.ui.screen.send.GlobalStateHolder
import com.xah.send.ui.screen.send.SendScreen
import com.xah.send.ui.screen.settings.SettingsScreen
import com.xah.send.ui.util.currentRouteWithoutArgs
import com.xah.send.ui.util.navigateForBottomBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val navigationItems = listOf(
    NavigationRoute.Send.bean,
    NavigationRoute.Receive.bean,
    NavigationRoute.Settings.bean,
)

@Composable
@Preview
fun App() {
    val localAddress = GlobalStateHolder.localIp
    LaunchedEffect(localAddress) {
        if(localAddress == null) {
            return@LaunchedEffect
        }
        simpleLog("本地初始化完成")
        // 监听接收事件
        withContext(Dispatchers.IO) {
            ReceiveServerManager.start(this@LaunchedEffect) { data,address ->
                simpleLog("接收数据 $data")
            }
        }
    }

    MaterialTheme {
        val navController = rememberNavController()
        val defaultPage = remember { navigationItems[0] }
        val currentPage = navigationItems.find {
            navController.currentRouteWithoutArgs() == it.route
        } ?: defaultPage
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
                composable(NavigationRoute.Receive.bean.route) {
                    ReceiveScreen()
                }
                composable(NavigationRoute.Send.bean.route) {
                    SendScreen()
                }
                composable(NavigationRoute.Settings.bean.route) {
                    SettingsScreen()
                }
            }
        }
    }
}


