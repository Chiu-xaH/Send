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

    fun getLocalIpv4Address(): String? {
        try {
            // 获取设备的所有网络接口
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                // 遍历网络接口的所有地址
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    // 过滤掉回环地址和IPv6地址
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }

}