package com.xah.send.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xah.send.logic.model.device.LocalDevice
import com.xah.send.logic.model.state.ReceiveTask
import com.xah.send.logic.util.getSimpleDeviceName
import kotlinx.coroutines.flow.MutableStateFlow
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.SocketException
import java.util.UUID

/**
 * 类ViewModel
 */
object GlobalStateHolder {
    /**
     * 当前接收任务
     */
    val currentReceiveTask = MutableStateFlow<ReceiveTask?>(null)
}