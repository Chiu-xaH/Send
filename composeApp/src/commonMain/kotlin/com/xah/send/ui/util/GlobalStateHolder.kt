package com.xah.send.ui.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xah.send.logic.model.bean.HelloPacket
import com.xah.send.logic.util.getDeviceName
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.util.UUID

object GlobalStateHolder {
    val localHelloPacket = createLocalHelloPacket()
    var localIp by mutableStateOf<InetSocketAddress?>(null)
    // 全局一次就行 除非要刷新
    private fun createLocalHelloPacket(): HelloPacket {
        val tcpPort = ServerSocket(0).use { it.localPort }
        return HelloPacket(
            deviceId = UUID.randomUUID().toString(),
            deviceName = getDeviceName(),
            tcpPort = tcpPort
        )
    }
}
