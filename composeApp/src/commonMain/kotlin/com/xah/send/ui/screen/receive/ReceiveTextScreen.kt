package com.xah.send.ui.screen.receive

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
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
fun ReceiveTextScreen() {
    val navController = LocalAppNavController.current
    val transfer = GlobalStateHolder.currentReceiveTask.collectAsState().value as? ReceiveTask.Text ?: return
    val sendData = transfer.packet
    val from = transfer.from

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
        Text(sendData.text, modifier = Modifier.padding(innerPadding))
    }
}