package com.xah.send.logic.function.transfer

import com.xah.send.logic.model.packet.FileMetaPacket
import com.xah.send.logic.model.packet.Packet
import com.xah.send.logic.model.packet.TextPacket
import com.xah.send.logic.model.state.transfer.FileTransferState
import com.xah.send.logic.model.state.ReceiveTask
import com.xah.send.logic.util.getPublicDownloadFolder
import com.xah.send.logic.util.resolveFileConflict
import com.xah.send.logic.util.simpleLog
import com.xah.send.ui.viewmodel.GlobalStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

/**
 * 接收器
 */
object Receiver {
    /**
     * 当前传输，用于双方切断传输
     */
    private var currentSocket: Socket? = null
    /**
     * 服务实例
     */
    private var serverSocket: ServerSocket? = null

    /**
     * 默认接收文件的存放路径
     */
    val saveDir = getPublicDownloadFolder()

    /**
     * 启动接收器
     * @param scope 生命周期作用域
     */
    fun start(
        scope: CoroutineScope
    ) {
        if (serverSocket != null) {
            return  // 已启动
        }
        serverSocket = startServer(scope)
    }

    /**
     * 停止接收器
     */
    fun stop() {
        serverSocket?.close()
        serverSocket = null
    }

    /**
     * 启动接收器
     * @param scope 生命周期作用域
     */
    fun startServer(
        scope: CoroutineScope,
    ): ServerSocket {
        val serverSocket = ServerSocket(GlobalStateHolder.localIp!!.port)

        scope.launch(Dispatchers.IO) {
            try {
                while (isActive) {
                    val socket = serverSocket.accept()
                    currentSocket = socket
                    launch {
                        socket.use {
                            val input = DataInputStream(BufferedInputStream(it.getInputStream()))

                            val length = input.readInt()
                            require(length in 1..(10 * 1024 * 1024))

                            val jsonBytes = ByteArray(length)
                            input.readFully(jsonBytes)

                            val packet = Json.Default.decodeFromString<Packet>(
                                String(jsonBytes, Charsets.UTF_8)
                            )

                            when (packet) {
                                is TextPacket -> {
                                    GlobalStateHolder.currentReceiveTask.value = ReceiveTask.Text(it.remoteSocketAddress as InetSocketAddress, packet)
                                }

                                is FileMetaPacket -> {
                                    val state = MutableStateFlow<FileTransferState>(FileTransferState.Progress(0, packet.fileSize))
                                    GlobalStateHolder.currentReceiveTask.value = ReceiveTask.File(it.remoteSocketAddress as InetSocketAddress, packet, state)
                                    // 在 socket.use 生命周期内 collect
                                    receiveFile(packet, input).collect { state.value = it }
                                }
                            }
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

    /**
     * 硬切断传输
     */
    fun stopReceive() {
        currentSocket?.close()
        currentSocket = null
        simpleLog("终止传输")
    }

    /**
     * 接收文件并保存
     * @param meta FileMetaPacket
     * @param input DataInputStream
     */
    fun receiveFile(
        meta: FileMetaPacket,
        input: DataInputStream
    ): Flow<FileTransferState> = flow {
        val outFile = resolveFileConflict(saveDir,meta.fileName)

        var received = 0L
        val buffer = ByteArray(8 * 1024)

        try {
            outFile.outputStream().use { fos ->
                while (received < meta.fileSize) {
                    val toRead = minOf(
                        buffer.size.toLong(),
                        meta.fileSize - received
                    ).toInt()

                    val read = input.read(buffer, 0, toRead)
                    if (read < 0) break

                    fos.write(buffer, 0, read)
                    received += read

                    emit(
                        FileTransferState.Progress(
                            currentBytes = received,
                            totalBytes = meta.fileSize
                        )
                    )
                }
            }

            emit(FileTransferState.Completed(outFile, meta.md5))
        } catch (e: Exception) {
            outFile.delete()
            emit(FileTransferState.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}