package com.xah.send.ui.screen.home.receive

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
import com.xah.send.logic.function.find.DeviceBroadcastHelper
import com.xah.send.ui.model.LinkStatus
import com.xah.send.ui.style.align.CenterScreen
import com.xah.send.ui.style.align.ColumnVertical
import com.xah.send.ui.style.CustomFloatingActionButtonShadow
import com.xah.send.ui.viewmodel.GlobalStateHolder
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import send.composeapp.generated.resources.Res
import send.composeapp.generated.resources.wifi_tethering
import send.composeapp.generated.resources.wifi_tethering_error
import send.composeapp.generated.resources.wifi_tethering_off

@Composable
fun ReceiveScreen() {
    var linkStatus by remember { mutableStateOf(LinkStatus.ON) }

    LaunchedEffect(GlobalStateHolder.localIp, DeviceBroadcastHelper.status) {
        linkStatus = if(GlobalStateHolder.localIp == null) {
            LinkStatus.OFF
        } else {
            DeviceBroadcastHelper.status
        }
    }

    Scaffold { innerPadding ->
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
                        "IPv4 ${GlobalStateHolder.getLocalIpv4Address()} 端口 #${ip.port}"
                    } ?: ""
                )
            }
        }
    }
}

@Composable
private fun linkStatusIcon(status : LinkStatus) : Pair<Color, DrawableResource> = when(status) {
    LinkStatus.ERROR -> Pair(MaterialTheme.colorScheme.error,Res.drawable.wifi_tethering_error)
    LinkStatus.ON -> Pair(MaterialTheme.colorScheme.primary,Res.drawable.wifi_tethering)
    LinkStatus.OFF -> Pair(MaterialTheme.colorScheme.outline,Res.drawable.wifi_tethering_off)
}




