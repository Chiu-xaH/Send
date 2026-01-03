package com.xah.send.ui.screen.receive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.xah.send.logic.model.state.ReceiveTask
import com.xah.send.logic.util.ClipBoardHelper
import com.xah.send.ui.componment.button.NavigationBackButton
import com.xah.send.ui.componment.container.CustomCard
import com.xah.send.ui.style.topBarTransplantColor
import com.xah.send.ui.util.Constant.APP_HORIZONTAL_DP
import com.xah.send.ui.viewmodel.GlobalStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiveTextScreen() {
    val transfer = GlobalStateHolder.currentReceiveTask.collectAsState().value as? ReceiveTask.Text ?: return
    val sendData = transfer.packet
    val from = transfer.from
    val scope = rememberCoroutineScope()

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
    ) { innerPadding ->
        CustomCard(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(bottom = APP_HORIZONTAL_DP)
                .clip(MaterialTheme.shapes.large)
                .clickable {
                    scope.launch(Dispatchers.IO) {
                        ClipBoardHelper.copy(sendData.text)
                    }
                }
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = maxHeight)
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sendData.text,
                        modifier = Modifier
                            .padding(horizontal = APP_HORIZONTAL_DP)
                    )
                }
            }
        }
    }
}