package com.xah.send.logic.function.find

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xah.send.logic.function.LocalDeviceManager
import com.xah.send.logic.util.Constant.FIND_DEVICES_PORT
import com.xah.send.ui.model.LinkStatus
import com.xah.send.ui.viewmodel.GlobalStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * 向外发送广播，让其他人发现自己
 */
object DeviceBroadcastHelper {
    /**
     * 广播地址
     */
    private const val ADDRESS = "255.255.255.255"

    /**
     * 状态
     */
    var status by mutableStateOf(LinkStatus.OFF)
        private set

    /**
     * 轮询间隔
     */
    const val TIME = 1500L

    /**
     * 向外发送一次广播，让其他人发现自己
     */
    suspend fun broadcastSelf() = withContext(Dispatchers.IO) {
        try {
            val socket = DatagramSocket().apply {
                broadcast = true
            }

            val data = Json.encodeToString(
                LocalDeviceManager.localDevicePacket
            ).toByteArray()

            socket.use {
                it.send(
                    DatagramPacket(
                        data,
                        data.size,
                        InetAddress.getByName(ADDRESS),
                        FIND_DEVICES_PORT
                    )
                )
            }

            status = LinkStatus.ON
        } catch (e: Exception) {
            status = LinkStatus.ERROR
            e.printStackTrace()
        }
    }

}