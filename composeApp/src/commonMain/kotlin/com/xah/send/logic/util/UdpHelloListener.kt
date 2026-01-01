package com.xah.send.logic.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException

// 发广播
class UdpHelloListener(
    private val onReceive: (String, InetAddress) -> Unit
) {
    private var socket: DatagramSocket? = null
    private var job: Job? = null

    fun start(scope: CoroutineScope) {
        if (job != null) return   // 防止重复启动

        job = scope.launch(Dispatchers.IO) {
            try {
                socket = DatagramSocket(DISCOVERY_PORT)
                val buffer = ByteArray(1024)

                while (isActive) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket?.receive(packet)  // 阻塞点

                    val msg = String(packet.data, 0, packet.length)
                    onReceive(msg, packet.address)
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

    fun stop() {
        job?.cancel()     // 取消协程
        job = null
        socket?.close()   // 强制打断 receive()
        socket = null
    }
}