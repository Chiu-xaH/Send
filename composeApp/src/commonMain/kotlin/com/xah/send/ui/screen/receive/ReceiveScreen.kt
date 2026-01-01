package com.xah.send.ui.screen.receive

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xah.send.ui.screen.send.GlobalStateHolder
import com.xah.send.ui.style.CenterScreen
import com.xah.send.ui.style.ColumnVertical
import com.xah.send.ui.style.CustomFloatingActionButtonShadow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import send.composeapp.generated.resources.Res
import send.composeapp.generated.resources.wifi_tethering
import send.composeapp.generated.resources.wifi_tethering_error
import send.composeapp.generated.resources.wifi_tethering_off
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.nio.ByteBuffer

@Composable
fun ReceiveScreen() {
    var linkStatus by remember { mutableStateOf(LinkStatus.ON) }

    LaunchedEffect(GlobalStateHolder.localIp) {
        linkStatus = if(GlobalStateHolder.localIp == null) {
            LinkStatus.OFF
        } else {
            LinkStatus.ON
        }
    }
    Scaffold(
        floatingActionButton = {
            when(linkStatus) {
                LinkStatus.OFF -> {

                }
                LinkStatus.ERROR -> {
                    // TODO 显示报错信息
                    ExtendedFloatingActionButton (
                        onClick = {

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

                Text(
                    GlobalStateHolder.localIp?.let { ip ->
                        "IP ${ip.address.hostAddress} 端口 #${ip.port}"
                    } ?: ""
                )
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


object ReceiveServerManager {
    private var serverSocket: ServerSocket? = null

    fun start(
        scope: CoroutineScope,
        onReceive: (String, InetSocketAddress) -> Unit
    ) {
        if (serverSocket != null) return  // 已启动

        serverSocket = startServer(
            scope = scope,
            onReceive = onReceive
        )
    }

    fun stop() {
        serverSocket?.close()
        serverSocket = null
    }

    fun startServer(
        scope: CoroutineScope,
        onReceive: (String, InetSocketAddress) -> Unit
    ): ServerSocket {
        val serverSocket = ServerSocket(GlobalStateHolder.localIp!!.port)

        scope.launch(Dispatchers.IO) {
            try {
                while (isActive) {
                    val socket = serverSocket.accept()
                    launch {
                        socket.use {
                            val input = it.getInputStream()

                            // 读 4 字节长度
                            val lenBuf = ByteArray(4)
                            input.read(lenBuf)
                            val length = ByteBuffer.wrap(lenBuf).int

                            // 读正文
                            val data = ByteArray(length)
                            input.readNBytes(data, 0, length)

                            val text = String(data, Charsets.UTF_8)
                            onReceive(text, it.remoteSocketAddress as InetSocketAddress)
                        }
                    }
                }
            } catch (e: Exception) {
                if (serverSocket.isClosed.not()) {
                    e.printStackTrace()
                }
            }
        }

        return serverSocket
    }

}


