package com.xah.send.logic.function.transfer

import com.xah.send.logic.model.packet.FileMetaPacket
import com.xah.send.logic.model.packet.Packet
import com.xah.send.logic.model.packet.TextPacket
import com.xah.send.logic.model.state.FileTransferState
import com.xah.send.logic.model.state.ReceiveTask
import com.xah.send.logic.util.getPublicDownloadFolder
import com.xah.send.ui.viewmodel.GlobalStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.net.InetSocketAddress
import java.net.ServerSocket

object Receiver {
    private var serverSocket: ServerSocket? = null
    val saveDir = getPublicDownloadFolder()

    fun start(
        scope: CoroutineScope,
    ) {
        if (serverSocket != null) return  // 已启动

        serverSocket = startServer(
            scope = scope,
        )
    }

    fun stop() {
        serverSocket?.close()
        serverSocket = null
    }

    fun startServer(
        scope: CoroutineScope,
    ): ServerSocket {
        val serverSocket = ServerSocket(GlobalStateHolder.localIp!!.port)

        scope.launch(Dispatchers.IO) {
            try {
                while (isActive) {
                    val socket = serverSocket.accept()
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
                                    val flow = receiveFileAsFlow(packet, input)
                                    GlobalStateHolder.currentReceiveTask.value = ReceiveTask.File(it.remoteSocketAddress as InetSocketAddress, packet, flow)
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

    private fun resolveFileConflict(
        dir: File,
        fileName: String
    ): File {
        val dotIndex = fileName.lastIndexOf('.')

        val baseName: String
        val extension: String?

        if (dotIndex > 0) {
            baseName = fileName.substring(0, dotIndex)
            extension = fileName.substring(dotIndex) // 包含 .
        } else {
            baseName = fileName
            extension = null
        }

        var index = 0
        var candidate: File

        do {
            val name = when {
                index == 0 -> fileName
                extension != null -> "$baseName ($index)$extension"
                else -> "$baseName ($index)"
            }
            candidate = File(dir, name)
            index++
        } while (candidate.exists())

        return candidate
    }


    fun receiveFileAsFlow(
        meta: FileMetaPacket,
        input: DataInputStream
    ): Flow<FileTransferState> = flow {
        val outFile = resolveFileConflict(saveDir, meta.fileName)

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

            emit(FileTransferState.Completed(outFile))
        } catch (e: Exception) {
            outFile.delete()
            emit(FileTransferState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

}