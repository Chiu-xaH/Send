package com.xah.send.ui.screen.send

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavUri
import com.send.core.bean.HelloPacket
import com.send.core.bean.RemoteDevice
import com.xah.send.logic.model.Platform
import com.xah.send.logic.util.getDeviceName
import com.xah.send.logic.util.simpleLog
import com.xah.send.ui.componment.APP_HORIZONTAL_DP
import com.xah.send.ui.componment.CARD_NORMAL_DP
import com.xah.send.ui.componment.CardListItem
import com.xah.send.ui.componment.DividerTextExpandedWith
import com.xah.send.ui.componment.LargeButton
import com.xah.send.ui.componment.cardNormalColor
import com.xah.send.ui.style.CustomFloatingActionButtonShadow
import com.xah.send.ui.style.textFiledTransplant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import send.composeapp.generated.resources.Res
import send.composeapp.generated.resources.content_paste
import send.composeapp.generated.resources.deployed_code
import send.composeapp.generated.resources.files
import send.composeapp.generated.resources.keyboard_alt
import send.composeapp.generated.resources.send
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.UUID

private val buttonPadding = CARD_NORMAL_DP*4

const val DISCOVERY_PORT = 9527

object GlobalStateHolder {
    val localHelloPacket = createLocalHelloPacket()
    var localIp by mutableStateOf<InetSocketAddress?>(null)
    // 全局一次就行 除非要刷新
    private fun createLocalHelloPacket(): HelloPacket {
        val tcpPort = ServerSocket(0).use { it.localPort }
        return HelloPacket(
            deviceId = UUID.randomUUID().toString(),
            deviceName = getDeviceName(),
            tcpPort = tcpPort
        )
    }
}

fun handleHello(
    packet: HelloPacket,
    fromIp: String,
    devices: MutableMap<String, RemoteDevice>
) {
    val now = System.currentTimeMillis()

    val device = devices[packet.deviceId]
    if (device == null) {
        devices[packet.deviceId] = RemoteDevice(
            deviceId = packet.deviceId,
            deviceName = packet.deviceName,
            ip = fromIp,
            tcpPort = packet.tcpPort,
            lastSeen = now
        )
    } else {
        device.lastSeen = now
    }
}

// 开始监听
fun broadcastHello(helloPacket: HelloPacket) {
    val socket = DatagramSocket().apply {
        broadcast = true
    }

    val data = Json.encodeToString(helloPacket).toByteArray()

    socket.send(
        DatagramPacket(data, data.size, InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT)
    )

    socket.close()
}

// 发广播
class UdpHelloListener(
    private val onReceive: (String, InetAddress) -> Unit
) {
    private var socket: DatagramSocket? = null
    private var job: Job? = null

    fun start(scope: CoroutineScope) {
        if (job != null) return   // 防止重复启动

        job = scope.launch(Dispatchers.IO) {
            try {
                socket = DatagramSocket(DISCOVERY_PORT)
                val buffer = ByteArray(1024)

                while (isActive) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket?.receive(packet)  // 阻塞点

                    val msg = String(packet.data, 0, packet.length)
                    onReceive(msg, packet.address)
                }
            } catch (e: SocketException) {
                // 正常关闭 socket 会走到这里，不要当错误
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                socket?.close()
                socket = null
            }
        }
    }

    fun stop() {
        job?.cancel()     // 取消协程
        job = null
        socket?.close()   // 强制打断 receive()
        socket = null
    }
}

@Composable
fun SendScreen() {
    val scrollState = rememberScrollState()
    val devices = remember { mutableStateMapOf<String, RemoteDevice>() }
    val scope = rememberCoroutineScope()
    var inputAddress by remember { mutableStateOf("") }
    val isInputValid = isValidAddress(inputAddress)
    var refresh by remember { mutableIntStateOf(0) }

    LaunchedEffect(refresh) {
        withContext(Dispatchers.IO) {
            broadcastHello(GlobalStateHolder.localHelloPacket)
        }
    }

    LaunchedEffect(Unit) {
        while(true) {
            // 轮询
            delay(1000L)
            refresh++
        }
    }

    val listener = remember {
        UdpHelloListener { msg, address ->
            val packet = Json.decodeFromString<HelloPacket>(msg)
            scope.launch(Dispatchers.Main) {
                handleHello(packet, address.hostAddress, devices)
            }
        }
    }

    // 释放监听
    DisposableEffect(Unit) {
        listener.start(scope)

        onDispose {
            listener.stop()
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton (
                onClick = {
                    // 手动刷新
                    refresh++
                },
                elevation = CustomFloatingActionButtonShadow()
            ) {
                Text("扫描设备")
            }
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            DividerTextExpandedWith("发送内容") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP)
                ) {
                    // TODO 文件
                    LargeButton(
                        icon = Res.drawable.files,
                        text = "文件",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f)
                            .padding(end = buttonPadding)
                    ) {

                    }
                    // TODO 文本
                    LargeButton(
                        icon = Res.drawable.keyboard_alt,
                        text = "文本",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f)
                            .padding(end = buttonPadding)
                    ) {

                    }
                    // TODO 剪切板
                    LargeButton(
                        icon = Res.drawable.content_paste,
                        text = "剪切板",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f)
                    ) {

                    }
                }
            }
            DividerTextExpandedWith("发送至") {
                devices.values.forEach { device ->
                    val isLocalDevice = GlobalStateHolder.localHelloPacket.deviceId == device.deviceId
                    if(isLocalDevice) {
                        GlobalStateHolder.localIp = InetSocketAddress(device.ip, device.tcpPort)
                    }
                    CardListItem(
                        headlineContent = {
                            Text(device.deviceName)
                        },
                        color = if(isLocalDevice) MaterialTheme.colorScheme.primaryContainer else cardNormalColor(),
                        trailingContent = {
                            if(isLocalDevice) Text("本机")
                        },
                        supportingContent = {
                            Text("IP ${device.ip} 端口 ${device.tcpPort}")
                        },
                        leadingContent = {
                            Icon(
                                painterResource(
                                    if(isLocalDevice) {
                                        Res.drawable.deployed_code
                                    } else {
                                        Res.drawable.deployed_code
                                    }
                                ),
                                null
                            )
                        },
                        modifier = Modifier.let {
                            if(isLocalDevice) {
                                it
                            } else {
                                it.clickable {
                                    scope.launch {
                                        sendTo(InetSocketAddress(device.ip,device.tcpPort),"")
                                    }
                                }
                            }
                        }
                    )
                }
                TextField(
                    shape = MaterialTheme.shapes.medium,
                    colors = textFiledTransplant(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP),
                    value = inputAddress,
                    onValueChange = {
                        inputAddress = it
                    },
                    trailingIcon = {
                        IconButton(
                            enabled = isInputValid != null,
                            onClick = {
                                scope.launch {
                                    sendTo(isInputValid!!,"")
                                }
                            }
                        ) {
                            Icon(painterResource(Res.drawable.send),null)
                        }
                    },
                    label = { Text("输入 IP:端口") },
                )
            }
        }
    }
}

suspend fun sendTo(
    address : InetSocketAddress,
    data : Any
) {
    sendTextTo(address,"From ${GlobalStateHolder.localHelloPacket.deviceName}")
}

suspend fun sendTextTo(
    address: InetSocketAddress,
    text: String
) = withContext(Dispatchers.IO) {
    try {
        Socket().use { socket ->
            socket.connect(address, 5_000)

            val bytes = text.toByteArray(Charsets.UTF_8)
            val out = socket.getOutputStream()

            // 先发长度（4 字节）
            val lenBytes = ByteBuffer.allocate(4)
                .putInt(bytes.size)
                .array()

            out.write(lenBytes)
            out.write(bytes)
            out.flush()

            simpleLog("发送文本 $text")
        }
    } catch (e : Exception) {
        e.printStackTrace()
        e.message?.let { simpleLog(it) }
    }
}


// IP:Port
fun isValidAddress(str: String): InetSocketAddress? {
    val parts = str.split(":")
    if (parts.size != 2) {
        return null
    }

    val port = parts[1].toIntOrNull() ?: return null
    if (port !in 1..65535) {
        return null
    }

    return try {
        val target = InetSocketAddress(parts[0], port)
        if(target == GlobalStateHolder.localIp) {
            // 自己
            null
        } else {
            target
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

