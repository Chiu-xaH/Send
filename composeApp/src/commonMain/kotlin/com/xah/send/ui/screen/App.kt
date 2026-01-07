package com.xah.send.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xah.send.logic.model.state.ReceiveTask
import com.xah.send.logic.function.transfer.Receiver
import com.xah.send.logic.util.simpleLog
import com.xah.send.ui.model.navigation.AppNavRoute
import com.xah.send.ui.screen.home.HomeScreen
import com.xah.send.ui.screen.receive.ReceiveFileScreen
import com.xah.send.ui.screen.receive.ReceiveTextScreen
import com.xah.send.ui.viewmodel.GlobalStateHolder
import com.xah.send.ui.util.navigation.LocalAppNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun App() {
    val navTopController = rememberNavController()

    CompositionLocalProvider(
        LocalAppNavController provides navTopController
    ) {
        MaterialTheme {
//            val localAddress = GlobalStateHolder.localIp
            val transfer by GlobalStateHolder.currentReceiveTask.collectAsState()

            // 跟随整个应用的生命周期，一直监听是否有人向自己发送内容
            LaunchedEffect(transfer) {
                when (transfer) {
                    is ReceiveTask.Text -> {
                        simpleLog("接收文本")
                        navTopController.navigate(AppNavRoute.ReceiveText.route)
                    }
                    is ReceiveTask.File -> {
                        simpleLog("接收文件")
                        navTopController.navigate(AppNavRoute.ReceiveFile.route)
                    }
                    null -> {}
                }
            }

            // 首次初始化自己的信息
            LaunchedEffect(Unit) {
//                if(localAddress == null) {
//                    return@LaunchedEffect
//                }
                // 监听接收事件
                withContext(Dispatchers.IO) {
                    Receiver.start(this@LaunchedEffect)
                }
            }

            NavHost(
                navController = navTopController,
                startDestination = AppNavRoute.Home.route
            ) {
                composable(AppNavRoute.Home.route) {
                    HomeScreen()
                }
                composable(AppNavRoute.ReceiveFile.route) {
                    // 相比于文本传输，多了传输进度展示
                    ReceiveFileScreen()
                }
                composable(AppNavRoute.ReceiveText.route) {
                    ReceiveTextScreen()
                }
            }
        }
    }
}


