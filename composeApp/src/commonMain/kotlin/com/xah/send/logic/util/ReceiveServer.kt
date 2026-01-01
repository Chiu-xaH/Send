package com.xah.send.logic.util

import com.xah.send.ui.util.GlobalStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.nio.ByteBuffer

object ReceiveServer {
    private var serverSocket: ServerSocket? = null

    fun start(
        scope: CoroutineScope,
        onReceive: (String, InetSocketAddress) -> Unit
    ) {
        if (serverSocket != null) return  // 已启动

        serverSocket = startServer(
            scope = scope,
            onReceive = onReceive
        )
    }

    fun stop() {
        serverSocket?.close()
        serverSocket = null
    }

    fun startServer(
        scope: CoroutineScope,
        onReceive: (String, InetSocketAddress) -> Unit
    ): ServerSocket {
        val serverSocket = ServerSocket(GlobalStateHolder.localIp!!.port)

        scope.launch(Dispatchers.IO) {
            try {
                while (isActive) {
                    val socket = serverSocket.accept()
                    launch {
                        socket.use {
                            val input = it.getInputStream()

                            // 读 4 字节长度
                            val lenBuf = ByteArray(4)
                            input.read(lenBuf)
                            val length = ByteBuffer.wrap(lenBuf).int

                            // 读正文
                            val data = ByteArray(length)
                            input.readNBytes(data, 0, length)

                            val text = String(data, Charsets.UTF_8)
                            onReceive(text, it.remoteSocketAddress as InetSocketAddress)
                        }
                    }
                }
            } catch (e: Exception) {
                if (serverSocket.isClosed.not()) {
                    e.printStackTrace()
                }
            }
        }

        return serverSocket
    }

}
