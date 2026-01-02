package com.xah.send.logic.function.transfer

import com.xah.send.logic.model.packet.FileMetaPacket
import com.xah.send.logic.model.packet.Packet
import com.xah.send.logic.model.packet.TextPacket
import com.xah.send.logic.model.state.FileTransferState
import com.xah.send.logic.model.state.TextTransferState
import com.xah.send.ui.viewmodel.GlobalStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.net.InetSocketAddress
import java.net.Socket

object Sender {
    fun sendText(
        address: InetSocketAddress,
        text: String
    ): Flow<TextTransferState> = flow {
        emit(TextTransferState.Progress)

        try {
            withContext(Dispatchers.IO) {
                Socket().use { socket ->
                    socket.connect(address, 5_000)

                    val packet = TextPacket(
                        text = text,
                        from = GlobalStateHolder.localHelloPacket
                    )

                    val bytes = Json.Default
                        .encodeToString<Packet>(packet)
                        .toByteArray(Charsets.UTF_8)

                    val out = DataOutputStream(socket.getOutputStream())
                    out.writeInt(bytes.size)
                    out.write(bytes)
                    out.flush()
                }
            }

            emit(TextTransferState.Completed)
        } catch (e: Throwable) {
            emit(TextTransferState.Error(e))
        }
    }.flowOn(Dispatchers.IO)


    fun sendFileWithProgress(
        address: InetSocketAddress,
        file: File
    ): Flow<FileTransferState> = flow {
        try {
            withContext(Dispatchers.IO) {
                Socket().use { socket ->
                    socket.connect(address, 5_000)

                    val out = DataOutputStream(
                        BufferedOutputStream(socket.getOutputStream())
                    )

                    // 1. 发送文件元信息
                    val meta = FileMetaPacket(
                        fileName = file.name,
                        fileSize = file.length(),
                        mime = null,
                        from = GlobalStateHolder.localHelloPacket
                    )

                    val metaBytes = Json.Default
                        .encodeToString<Packet>(meta)
                        .toByteArray(Charsets.UTF_8)

                    out.writeInt(metaBytes.size)
                    out.write(metaBytes)
                    out.flush()

                    // 2. 发送文件内容（带进度）
                    val buffer = ByteArray(8 * 1024)
                    var sentBytes = 0L
                    val totalBytes = file.length()

                    file.inputStream().use { fis ->
                        while (true) {
                            val read = fis.read(buffer)
                            if (read <= 0) break

                            out.write(buffer, 0, read)
                            sentBytes += read

                            emit(
                                FileTransferState.Progress(
                                    currentBytes = sentBytes,
                                    totalBytes = totalBytes
                                )
                            )
                        }
                    }

                    out.flush()

                    emit(FileTransferState.Completed(file))
                }
            }
        } catch (e: Throwable) {
            emit(FileTransferState.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}