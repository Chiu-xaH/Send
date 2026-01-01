package com.xah.send.logic.util

import com.xah.send.logic.model.bean.HelloPacket
import com.xah.send.logic.model.bean.RemoteDevice
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.collections.set

const val DISCOVERY_PORT = 9527

fun handleHello(
    packet: HelloPacket,
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

// 开始监听
fun broadcastHello(helloPacket: HelloPacket) {
    val socket = DatagramSocket().apply {
        broadcast = true
    }

    val data = Json.encodeToString(helloPacket).toByteArray()

    socket.send(
        DatagramPacket(data, data.size, InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT)
    )

    socket.close()
}

