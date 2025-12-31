package com.xah.send.ui.screen.receive

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xah.send.ui.style.CenterScreen
import com.xah.send.ui.style.ColumnVertical
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import send.composeapp.generated.resources.Res
import send.composeapp.generated.resources.wifi_tethering
import send.composeapp.generated.resources.wifi_tethering_error
import send.composeapp.generated.resources.wifi_tethering_off

@Composable
fun ReceiveScreen() {
    var linkStatus by remember { mutableStateOf(LinkStatus.OFF) }

    Scaffold(
        floatingActionButton = {
            when(linkStatus) {
                LinkStatus.OFF -> {
                    // TODO 重启服务按钮
                    ExtendedFloatingActionButton (
                        onClick = {
                            linkStatus = LinkStatus.ON
                        },
                        elevation = CustomFloatingActionButtonShadow()
                    ) {
                        Text("启动服务")
                    }
                }
                LinkStatus.ERROR -> {
                    // TODO 显示报错信息
                    ExtendedFloatingActionButton (
                        onClick = {
                            linkStatus = LinkStatus.ON
                        },
                        elevation = CustomFloatingActionButtonShadow()
                    ) {
                        Text("连接失败")
                    }
                }
                LinkStatus.ON -> {
                    // TODO 发送按钮
                    ExtendedFloatingActionButton (
                        onClick = {
                            linkStatus = LinkStatus.OFF
                        },
                        elevation = CustomFloatingActionButtonShadow()
                    ) {
                        Text("等待接收文件")
                    }
                }
            }
        }
    ) { innerPadding ->
        CenterScreen {
            ColumnVertical () {
                with(linkStatusIcon(linkStatus)) {
                    Icon(
                        painterResource(second),
                        null,
                        tint = first,
                        modifier = Modifier.size(200.dp)
                    )
                }
                Text("端口 #XX IP 192.168.0.0")
            }
        }
    }
}

// 入网状态
enum class LinkStatus {
    ERROR, OFF, ON
}

@Composable
fun linkStatusIcon(status : LinkStatus) : Pair<Color, DrawableResource> = when(status) {
    LinkStatus.ERROR -> Pair(MaterialTheme.colorScheme.error,Res.drawable.wifi_tethering_error)
    LinkStatus.ON -> Pair(MaterialTheme.colorScheme.primary,Res.drawable.wifi_tethering)
    LinkStatus.OFF -> Pair(MaterialTheme.colorScheme.outline,Res.drawable.wifi_tethering_off)
}


@Composable
fun CustomFloatingActionButtonShadow() = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)