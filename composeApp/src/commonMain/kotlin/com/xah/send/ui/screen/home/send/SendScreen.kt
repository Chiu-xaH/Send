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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.xah.send.logic.function.find.DeviceBroadcastHelper
import com.xah.send.logic.function.find.DeviceFinder
import com.xah.send.logic.function.transfer.Sender
import com.xah.send.logic.model.Platform
import com.xah.send.logic.model.device.RemoteDevice
import com.xah.send.logic.model.state.transfer.FileTransferState
import com.xah.send.logic.model.state.transfer.TextTransferState
import com.xah.send.logic.util.ClipBoardHelper
import com.xah.send.logic.util.showToast
import com.xah.send.ui.componment.button.LargeButton
import com.xah.send.ui.componment.container.CardListItem
import com.xah.send.ui.componment.container.CustomCard
import com.xah.send.ui.componment.container.TransplantListItem
import com.xah.send.ui.componment.container.cardNormalColor
import com.xah.send.ui.componment.text.DividerTextExpandedWith
import com.xah.send.ui.style.CustomFloatingActionButtonShadow
import com.xah.send.ui.style.textFiledTransplant
import com.xah.send.ui.util.Constant.APP_HORIZONTAL_DP
import com.xah.send.ui.util.Constant.CARD_NORMAL_DP
import com.xah.send.ui.viewmodel.GlobalStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import send.composeapp.generated.resources.Res
import send.composeapp.generated.resources.close
import send.composeapp.generated.resources.content_paste
import send.composeapp.generated.resources.deployed_code
import send.composeapp.generated.resources.files
import send.composeapp.generated.resources.keyboard_alt
import send.composeapp.generated.resources.send
import java.io.File
import java.net.InetSocketAddress
import com.xah.send.logic.util.pickFile
import com.xah.send.logic.util.getPlatform
import com.xah.send.logic.util.AndroidFilePicker
import com.xah.send.logic.util.androidCleanCopiedCache
import com.xah.send.ui.screen.receive.TransferUI
import com.xah.send.ui.style.align.ColumnVertical
import send.composeapp.generated.resources.progress_activity
import send.composeapp.generated.resources.wifi_tethering

val buttonPadding = CARD_NORMAL_DP*4

@Composable
fun SendScreen() {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val devices = remember { mutableStateMapOf<String, RemoteDevice>() }
    val listener = remember { DeviceFinder(devices) }

    var inputAddress by rememberSaveable { mutableStateOf("") }
    var showTextDialog by remember { mutableStateOf(false) }
    val isInputValid = isValidAddress(inputAddress)

    var sendFile by remember { mutableStateOf<File?>(null) }
    var sendTextContent by rememberSaveable { mutableStateOf("") }
    val hasContent = !(sendTextContent.isEmpty() && sendFile == null)

    var showProgressDialog by remember { mutableStateOf(false) }
    var progressFlow by remember { mutableStateOf<FileTransferState.Progress?>(null) }

    // 释放监听
    DisposableEffect(Unit) {
        listener.start(scope)

        onDispose {
            listener.stop()
        }
    }

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
                        Button(
                            onClick = {
                                sendTextContent = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
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

    if(showProgressDialog && progressFlow != null) {
        Dialog (
            onDismissRequest = { showProgressDialog = false }
        ) {
            LaunchedEffect(progressFlow) {
                if(progressFlow == null) {
                    showProgressDialog = false
                }
            }
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP)
            ) {
                ColumnVertical (modifier = Modifier.padding(vertical = APP_HORIZONTAL_DP)) {
                    TransferUI(progressFlow!!)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .padding(top = buttonPadding)
                    ) {
                        Button(
                            onClick = {
                                Sender.stopSend()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("中止发送")
                        }
                    }
                }
            }
        }
    }

    var openFilePicker by remember { mutableStateOf(false) }
    AndroidFilePicker(openFilePicker) {
        sendFile = it
        openFilePicker = false
    }
    // AndroidFilePicker缓存释放
    LaunchedEffect(sendFile) {
        if(sendFile == null) {
            androidCleanCopiedCache()
        }
    }

    val sendAction : suspend (InetSocketAddress) -> Unit =  m@ { sendToAddress ->
        if(!hasContent) {
            showToast("要发送的内容为空")
            return@m
        }
        // TODO 这里代码可以优化，合二为一
        if(sendTextContent.isNotEmpty() && sendFile == null) {
            Sender.sendText(sendToAddress,sendTextContent).collect { state ->
                when(state) {
                    is TextTransferState.Error -> {
                        showToast("发送失败(${state.throwable.message})")
                    }
                    is TextTransferState.Progress -> {}
                    is TextTransferState.Completed -> {
                        showToast("发送完成")
                        sendTextContent = ""
                    }
                }
            }
        } else if(sendTextContent.isEmpty() && sendFile != null) {
            Sender.sendFile(sendToAddress, sendFile!!).collect { state ->
                when(state) {
                    is FileTransferState.Error -> {
                        progressFlow = null
                        showToast("发送失败(${state.throwable.message})")
                    }
                    is FileTransferState.Progress -> {
                        if(showProgressDialog == false) {
                            showProgressDialog = true
                        }
                        progressFlow = state
                    }
                    is FileTransferState.Completed -> {
                        progressFlow = null
                        showToast("发送完成")
                        sendFile = null
                    }
                }
            }
        } else {
            progressFlow = null
            showToast("异常")
        }
    }

    Scaffold(
        floatingActionButton = {
            var loading by remember { mutableStateOf(false) }
            FloatingActionButton (
                onClick = {
                    // 手动刷新
                    scope.launch {
                        loading = true
                        DeviceBroadcastHelper.broadcastSelf()
                        loading = false
                    }
                },
                elevation = CustomFloatingActionButtonShadow()
            ) {
                Icon(
                    painterResource(
                        if(loading) {
                            Res.drawable.progress_activity
                        } else {
                            Res.drawable.wifi_tethering
                        }
                    ),
                    null
                )
            }
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            DividerTextExpandedWith("发送类型") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP)
                ) {
                    // TODO 拖拽文件
                    LargeButton(
                        icon = Res.drawable.files,
                        text = "文件",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f)
                            .padding(end = buttonPadding)
                    ) {
                        scope.launch {
                            sendTextContent = ""
                            when(getPlatform()) {
                                Platform.DESKTOP ->  {
                                    with(Dispatchers.IO) {
                                        sendFile = pickFile()
                                    }
                                }
                                Platform.ANDROID -> {
                                    openFilePicker = true
                                }
                            }
                        }
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
                        sendFile = null
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
                        sendFile = null
                        ClipBoardHelper.paste()?.let { text ->
                            sendTextContent = text
                            showToast("获取剪切板完成，可发送")
                        }
                    }
                }
                if(hasContent) {
                    CustomCard (
                        color = cardNormalColor(),
                        modifier = Modifier.padding(top = buttonPadding - CARD_NORMAL_DP)
                    ) {
                        TransplantListItem(
                            headlineContent = { Text(
                                // TODO 互斥
                                if(sendTextContent.isNotEmpty() && sendFile == null) {
                                    sendTextContent
                                } else if(sendTextContent.isEmpty() && sendFile != null) {
                                    sendFile!!.absolutePath
                                } else {
                                    "异常"
                                }
                            ) },
                            leadingContent = {
                                // TODO 展示类型
                                Icon(
                                    painterResource(
                                        if(sendTextContent.isNotEmpty() && sendFile == null) {
                                            Res.drawable.keyboard_alt
                                        } else if(sendTextContent.isEmpty() && sendFile != null) {
                                            Res.drawable.files
                                        } else {
                                            Res.drawable.deployed_code
                                        }
                                    ),
                                    null
                                )
                            },
                            trailingContent = {
                                IconButton(onClick = {
                                    sendFile = null
                                    sendTextContent = ""
                                }) {
                                    Icon(
                                        painterResource(Res.drawable.close),
                                        null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            overlineContent = {
                                Text("发送区")
                            }
                        )
                    }
                }
            }
            DividerTextExpandedWith("发送至") {
                devices.values.forEach { device ->
                    val isLocalDevice = GlobalStateHolder.localDevicePacket.deviceId == device.deviceId
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
                                it.clickable {
                                    sendTextContent = "${sendToAddress.address.hostAddress}:${sendToAddress.port}"
                                }
                            } else {
                                it.clickable {
                                    scope.launch {
                                        sendAction(sendToAddress)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP),
                    value = inputAddress,
                    onValueChange = {
                        inputAddress = it
                    },
                    trailingIcon = {
                        IconButton(
                            enabled = isInputValid != null,
                            onClick = {
                                scope.launch {
                                    scope.launch {
                                        sendAction(isInputValid!!)
                                    }
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

