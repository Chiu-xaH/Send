package com.xah.send.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xah.send.logic.model.device.LocalDevice
import com.xah.send.logic.model.state.ReceiveTask
import com.xah.send.logic.util.getSimpleDeviceName
import kotlinx.coroutines.flow.MutableStateFlow
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.util.UUID

/**
 * 类ViewModel
 */
object GlobalStateHolder {
    /**
     * 本机信息
     */
    val localDevicePacket = createLocalDevicePacket()

    /**
     * 当前接收任务
     */
    val currentReceiveTask = MutableStateFlow<ReceiveTask?>(null)

    /**
     * 本机IP地址
     */
    var localIp by mutableStateOf<InetSocketAddress?>(null)

    /**
     * 新建本机信息，全局执行一次就行
     */
    private fun createLocalDevicePacket(): LocalDevice {
        val tcpPort = ServerSocket(0).use { it.localPort }
        return LocalDevice(
            deviceId = UUID.randomUUID().toString(),
            deviceName = getSimpleDeviceName(),
            tcpPort = tcpPort
        )
    }
}