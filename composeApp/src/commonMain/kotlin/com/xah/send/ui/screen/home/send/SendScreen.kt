package com.xah.send.ui.screen.home.send

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.xah.send.logic.model.device.LocalDevice
import com.xah.send.logic.model.device.RemoteDevice
import com.xah.send.logic.function.transfer.Sender
import com.xah.send.logic.model.state.TextTransferState
import com.xah.send.logic.function.find.FindDevicesListener
import com.xah.send.logic.function.find.FinsDevicesHelper
import com.xah.send.logic.util.showToast
import com.xah.send.ui.componment.APP_HORIZONTAL_DP
import com.xah.send.ui.componment.CARD_NORMAL_DP
import com.xah.send.ui.componment.CardListItem
import com.xah.send.ui.componment.DividerTextExpandedWith
import com.xah.send.ui.componment.LargeButton
import com.xah.send.ui.componment.cardNormalColor
import com.xah.send.ui.style.CustomFloatingActionButtonShadow
import com.xah.send.ui.style.textFiledTransplant
import com.xah.send.ui.util.GlobalStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import send.composeapp.generated.resources.Res
import send.composeapp.generated.resources.content_paste
import send.composeapp.generated.resources.deployed_code
import send.composeapp.generated.resources.files
import send.composeapp.generated.resources.keyboard_alt
import send.composeapp.generated.resources.send
import java.net.InetSocketAddress


private val buttonPadding = CARD_NORMAL_DP*4

@Composable
fun SendScreen() {
    val scrollState = rememberScrollState()
    val devices = remember { mutableStateMapOf<String, RemoteDevice>() }
    val scope = rememberCoroutineScope()
    var inputAddress by rememberSaveable { mutableStateOf("") }
    val isInputValid = isValidAddress(inputAddress)
    var refresh by remember { mutableIntStateOf(0) }

    LaunchedEffect(refresh) {
        withContext(Dispatchers.IO) {
            FinsDevicesHelper.broadcastHello(GlobalStateHolder.localHelloPacket)
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
        FindDevicesListener { msg, address ->
            val packet = Json.decodeFromString<LocalDevice>(msg)
            scope.launch(Dispatchers.Main) {
                FinsDevicesHelper.handleHello(packet, address.hostAddress, devices)
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

    var sendTextContent by rememberSaveable { mutableStateOf("") }
    var showTextDialog by remember { mutableStateOf(false) }

    if(showTextDialog) {
        Dialog (
            onDismissRequest = { showTextDialog = false }
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP)
            ) {
                Column (modifier = Modifier.verticalScroll(rememberScrollState())) {
                    TextField(
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledTransplant(),
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = APP_HORIZONTAL_DP)
                            .padding(bottom = buttonPadding)
                            .padding(horizontal = APP_HORIZONTAL_DP),
                        value = sendTextContent,
                        onValueChange = {
                            sendTextContent = it
                        },
                        label = { Text("输入要发送的文本") }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .padding(bottom = APP_HORIZONTAL_DP)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                sendTextContent = ""
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth().weight(1 / 2f)
                        ) {
                            Text("清除")
                        }
                        Spacer(Modifier.width(buttonPadding))
                        FilledTonalButton(
                            onClick = {
                                showTextDialog = false
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth().weight(1 / 2f)
                        ) {
                            Text("保存")
                        }
                    }
                }
            }
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
                        // 录入文本
                        showTextDialog = true
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
                        showToast("正在开发")
                    }
                }
            }
            DividerTextExpandedWith("发送至") {
                devices.values.forEach { device ->
                    val isLocalDevice = GlobalStateHolder.localHelloPacket.deviceId == device.deviceId
                    val sendToAddress = InetSocketAddress(device.ip,device.tcpPort)
                    if(isLocalDevice) {
                        GlobalStateHolder.localIp = sendToAddress
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
                                        if(sendTextContent.isEmpty()) {
                                            showToast("要发送的文本为空")
                                            return@launch
                                        }
                                        Sender.sendText(sendToAddress,sendTextContent).collect { state ->
                                            when(state) {
                                                is TextTransferState.Error -> {
                                                    withContext(Dispatchers.IO) {
                                                        state.throwable.printStackTrace()
                                                    }
                                                    showToast("发送失败${state.throwable.message}")
                                                }
                                                is TextTransferState.Progress -> {}
                                                is TextTransferState.Completed -> {
                                                    showToast("发送完成")
                                                }
                                            }
                                        }
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
                                    if(sendTextContent.isEmpty()) {
                                        showToast("要发送的文本为空")
                                        return@launch
                                    }
                                    Sender.sendText(isInputValid!!,sendTextContent).collect { state ->
                                        when(state) {
                                            is TextTransferState.Error -> {
                                                withContext(Dispatchers.IO) {
                                                    state.throwable.printStackTrace()
                                                }
                                                showToast("发送失败${state.throwable.message}")
                                            }
                                            is TextTransferState.Progress -> {}
                                            is TextTransferState.Completed -> {
                                                showToast("发送完成")
                                            }
                                        }
                                    }
                                    showToast("发送完成")
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


// IP:Port
private fun isValidAddress(str: String): InetSocketAddress? {
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




