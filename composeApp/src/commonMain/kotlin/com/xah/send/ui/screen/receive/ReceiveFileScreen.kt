package com.xah.send.ui.screen.receive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.xah.send.logic.function.transfer.Receiver
import com.xah.send.logic.model.state.ReceiveTask
import com.xah.send.logic.model.state.transfer.FileTransferState
import com.xah.send.logic.util.TransferSpeedCalculator
import com.xah.send.logic.util.jumpToOpenFile
import com.xah.send.logic.util.md5
import com.xah.send.ui.componment.button.NavigationBackButton
import com.xah.send.ui.componment.progress.CustomLineProgressIndicator
import com.xah.send.ui.screen.home.send.buttonPadding
import com.xah.send.ui.style.CustomFloatingActionButtonShadow
import com.xah.send.ui.style.align.CenterScreen
import com.xah.send.ui.style.align.ColumnVertical
import com.xah.send.ui.style.topBarTransplantColor
import com.xah.send.ui.util.Constant.APP_HORIZONTAL_DP
import com.xah.send.ui.viewmodel.GlobalStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.xah.send.ui.componment.dialog.CustomDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiveFileScreen() {
    val transfer = GlobalStateHolder.currentReceiveTask.collectAsState().value as? ReceiveTask.File ?: return
    val sendData = transfer.meta
    val from = transfer.from
    val progressFlow by transfer.state.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }

    if(showDialog) {
        CustomDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                Receiver.stopReceive()
                showDialog = false
            },
            dialogTitle = "是否中止接收?",
            dialogText = "如果不中止，直接退出页面，也将在后台继续接收",
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("来自 ${sendData.from.deviceName}(${from.address.hostAddress})")
                },
                colors = topBarTransplantColor(),
                navigationIcon = {
                    NavigationBackButton()
                }
            )
        },
        floatingActionButton = {
            when(progressFlow) {
                is FileTransferState.Progress -> {
                    ExtendedFloatingActionButton (
                        onClick = {
                            showDialog = true
                        },
                        contentColor = MaterialTheme.colorScheme.onError,
                        containerColor = MaterialTheme.colorScheme.error,
                        elevation = CustomFloatingActionButtonShadow()
                    ) {
                        Text("中止接收")
                    }
                }
                is FileTransferState.Completed ->  {
                    val file = (progressFlow as FileTransferState.Completed)
                    if(file.expectedMd5 != null && file.expectedMd5 != file.file.md5()) { } else {
                        Row {
                            ExtendedFloatingActionButton (
                                onClick = {
                                    jumpToOpenFile(file.file,false)
                                },
                                elevation = CustomFloatingActionButtonShadow()
                            ) {
                                Text("打开文件")
                            }
                            Spacer(Modifier.width(APP_HORIZONTAL_DP))
                            ExtendedFloatingActionButton (
                                onClick = {
                                    jumpToOpenFile(file.file,true)
                                },
                                elevation = CustomFloatingActionButtonShadow()
                            ) {
                                Text("打开所在文件夹")
                            }
                        }
                    }
                }
                is FileTransferState.Error -> {}
                null -> {}
            }
        }
    ) { innerPadding ->
        Box (
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CenterScreen {
                when(progressFlow) {
                    null -> {
                        // 准备传送
                    }
                    is FileTransferState.Progress -> {
                        TransferUI(progressFlow as FileTransferState.Progress)
                    }
                    is FileTransferState.Error -> {
                        val p = (progressFlow as FileTransferState.Error)
                        p.throwable.message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    }
                    is FileTransferState.Completed -> {
                        // 检验MD5
                        val p = (progressFlow as FileTransferState.Completed)
                        val exceptedMd5 = p.expectedMd5
                        if(exceptedMd5 != null && exceptedMd5 != p.file.md5()) {
                            LaunchedEffect(Unit) {
                                scope.launch(Dispatchers.IO) {
                                    p.file.delete()
                                }
                            }
                            Text("MD5校验失败",color = MaterialTheme.colorScheme.error)
                        } else {
                            ColumnVertical {
                                Text("接收完成")
                                Text("${p.file.absolutePath}",color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 传输进度条
 */
@Composable
fun TransferUI(
    progressFlow : FileTransferState.Progress
) {
    val progressValue = if (progressFlow.totalBytes != 0L) {
        progressFlow.currentBytes.toFloat() / progressFlow.totalBytes.toFloat()
    } else {
        0f
    }

    val speedCalculator = remember { TransferSpeedCalculator() }
    var speedText by remember { mutableStateOf("-- MB/s") }
    var etaText by remember { mutableStateOf("--:--") }

    LaunchedEffect(progressFlow.currentBytes) {
        speedCalculator.update(progressFlow.currentBytes, progressFlow.totalBytes)?.let { state ->
            val speedMb = state.currentSpeedBytesPerSec / 1024 / 1024
            speedText = String.format("%.2f MB/s", speedMb)

            val min = state.remainingSeconds / 60
            val sec = state.remainingSeconds % 60
            etaText = String.format("%02d:%02d", min, sec)
        }
    }

    ColumnVertical {
        CustomLineProgressIndicator(
            text = null,
            value = progressValue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = APP_HORIZONTAL_DP)
                .padding(bottom = buttonPadding)
        )
        Text(
            "${progressFlow.currentBytes} / ${progressFlow.totalBytes} - ${(progressValue*100).roundToInt()}%"
        )
        Text("当前 $speedText 预估剩余 $etaText")
    }
}
