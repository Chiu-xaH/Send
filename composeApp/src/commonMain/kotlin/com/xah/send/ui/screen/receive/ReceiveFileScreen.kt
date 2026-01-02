package com.xah.send.ui.screen.receive

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.xah.send.logic.model.state.FileTransferState
import com.xah.send.logic.model.state.ReceiveTask
import com.xah.send.logic.util.showToast
import com.xah.send.ui.style.CustomFloatingActionButtonShadow
import com.xah.send.ui.viewmodel.GlobalStateHolder
import com.xah.send.ui.util.navigation.LocalAppNavController
import org.jetbrains.compose.resources.painterResource
import send.composeapp.generated.resources.Res
import send.composeapp.generated.resources.deployed_code

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiveFileScreen() {
    val navController = LocalAppNavController.current
    val transfer = GlobalStateHolder.currentReceiveTask.collectAsState().value as? ReceiveTask.File ?: return
    val sendData = transfer.meta
    val from = transfer.from
    val progressFlow by transfer.progress.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("来自 ${sendData.from.deviceName}(${from.address.hostAddress})")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            painterResource(Res.drawable.deployed_code),
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton (
                onClick = {
                    // TODO
                    showToast("正在开发")
                },
                elevation = CustomFloatingActionButtonShadow()
            ) {
                Text("复制")
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
                        progress = { progress },
                    )
                }
                is FileTransferState.Error -> {
                    val p = (progressFlow as FileTransferState.Error)
                    p.throwable.message?.let { Text(it) }
                }
                is FileTransferState.Completed -> {
                    Text("完成,位于${(progressFlow as FileTransferState.Completed).file.absolutePath}")
                }
            }
        }
    }
}