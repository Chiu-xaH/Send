package com.xah.send.ui.screen.receive

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.xah.send.logic.model.state.transfer.FileTransferState
import com.xah.send.logic.model.state.ReceiveTask
import com.xah.send.logic.util.ClipBoardHelper
import com.xah.send.logic.util.md5
import com.xah.send.logic.util.showToast
import com.xah.send.ui.componment.button.NavigationBackButton
import com.xah.send.ui.style.CustomFloatingActionButtonShadow
import com.xah.send.ui.viewmodel.GlobalStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiveFileScreen() {
    val transfer = GlobalStateHolder.currentReceiveTask.collectAsState().value as? ReceiveTask.File ?: return
    val sendData = transfer.meta
    val from = transfer.from
    val progressFlow by transfer.state.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("来自 ${sendData.from.deviceName}(${from.address.hostAddress})")
                },
                navigationIcon = {
                    NavigationBackButton()
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton (
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        showToast("正在开发")
                    }
                },
                elevation = CustomFloatingActionButtonShadow()
            ) {
                Text("打开文件夹")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            when(progressFlow) {
                null -> {
                    // 准备传送
                }
                is FileTransferState.Progress -> {
                    // 进度条
                    val p = (progressFlow as FileTransferState.Progress)
                    val progress = if(p.totalBytes != 0L) {
                        (p.currentBytes / p.totalBytes).toFloat()
                    } else {
                        0f
                    }
                    Text("${p.currentBytes}/${p.totalBytes} - ${progress * 100}%")
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f,1f) },
                    )
                }
                is FileTransferState.Error -> {
                    val p = (progressFlow as FileTransferState.Error)
                    p.throwable.message?.let { Text(it) }
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
                        Text("MD5校验失败,已自动删除文件")
                    } else {
                        Text("完成,位于${p.file.absolutePath}")
                    }
                }
            }
        }
    }
}