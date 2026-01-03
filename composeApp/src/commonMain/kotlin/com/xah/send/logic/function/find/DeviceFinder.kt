package com.xah.send.logic.function.find

import com.xah.send.logic.model.device.LocalDevice
import com.xah.send.logic.model.device.RemoteDevice
import com.xah.send.logic.util.Constant.FIND_DEVICES_PORT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException

/**
 * 寻找其他设备
 * @param devices 传入待更新的设备列表
 */
class DeviceFinder(
    private var devices: MutableMap<String, RemoteDevice>
) {
    private var socket: DatagramSocket? = null
    private var job: Job? = null

    /**
     * 启动服务
     */
    fun start(scope: CoroutineScope) {
        if (job != null) return   // 防止重复启动

        job = scope.launch(Dispatchers.IO) {
            try {
                socket = DatagramSocket(FIND_DEVICES_PORT)
                val buffer = ByteArray(1024)

                while (isActive) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket?.receive(packet)  // 阻塞点

                    val json = String(packet.data, 0, packet.length)
                    val bean = Json.decodeFromString<LocalDevice>(json)
                    parseRemoteDevice(bean, packet.address.hostAddress, devices)
                }
            } catch (e: SocketException) {
                // 正常关闭 socket 会走到这里，不要当错误
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                socket?.close()
                socket = null
            }
        }
    }

    /**
     * 停止服务
     */
    fun stop() {
        job?.cancel()     // 取消协程
        job = null
        socket?.close()   // 强制打断 receive()
        socket = null
    }

    /**
     * 解析外来的广播信息，并添加到发现的设备列表中
     * @param packet 来源设备的信息
     * @param fromIp 来源IP
     * @param devices 存放发现的设备
     */
    private fun parseRemoteDevice(
        packet: LocalDevice,
        fromIp: String,
        devices: MutableMap<String, RemoteDevice>
    ) {
        val now = System.currentTimeMillis()

        val device = devices[packet.deviceId]
        if (device == null) {
            devices[packet.deviceId] = RemoteDevice(
                deviceId = packet.deviceId,
                deviceName = packet.deviceName,
                ip = fromIp,
                tcpPort = packet.tcpPort,
                lastSeen = now
            )
        } else {
            device.lastSeen = now
        }
    }
}