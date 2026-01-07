package com.xah.send.logic.function

import com.xah.send.logic.model.device.LocalDevice
import com.xah.send.logic.util.getSimpleDeviceName
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.SocketException
import java.util.UUID

/**
 * 本机信息初始化
 */
object LocalDeviceManager {

    /**
     * 本机端口
     */
    val tcpPort = ServerSocket(0).use { it.localPort }

    /**
     * 本机IPv4
     */
    val ipv4Address = getLocalIpv4Address()

    /**
     * 本机信息
     */
    val localDevicePacket = createLocalDevicePacket()

    /**
     * 新建本机信息，全局执行一次就行
     */
    private fun createLocalDevicePacket(): LocalDevice {
        return LocalDevice(
            deviceId = UUID.randomUUID().toString(),
            deviceName = getSimpleDeviceName(),
            tcpPort = tcpPort
        )
    }

    private fun getLocalIpv4Address(): String? {
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