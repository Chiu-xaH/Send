package com.xah.send.logic.function.transfer

import com.xah.send.logic.model.packet.FileMetaPacket
import com.xah.send.logic.model.packet.Packet
import com.xah.send.logic.model.packet.TextPacket
import com.xah.send.logic.model.state.transfer.FileTransferState
import com.xah.send.logic.model.state.transfer.TextTransferState
import com.xah.send.logic.util.md5
import com.xah.send.ui.viewmodel.GlobalStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
import com.xah.send.logic.util.simpleLog
/**
 * 发送器
 */
object Sender {
    /**
     * 当前传输，用于双方切断传输
     */
    private var currentSocket: Socket? = null
    /**
     * 硬切断传输
     */
    fun stopSend() {
        currentSocket?.close()
        currentSocket = null
        simpleLog("终止传输")
    }
    /**
     * 向某地址发送文本
     * @param address 目标设备的InetSocketAddress(IP地址,端口号)
     * @param text 发送的文本
     */
    fun sendText(
        address: InetSocketAddress,
        text: String
    ): Flow<TextTransferState> = flow {
        emit(TextTransferState.Progress)

        try {
            withContext(Dispatchers.IO) {
                Socket().use { socket ->
                    currentSocket = socket
                    socket.connect(address, 5_000)

                    val packet = TextPacket(
                        text = text,
                        from = GlobalStateHolder.localDevicePacket
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
            e.printStackTrace()
            emit(TextTransferState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * 向某地址发送文件
     * @param address 目标设备的InetSocketAddress(IP地址,端口号)
     * @param file 发送的文件
     */
    fun sendFile(
        address: InetSocketAddress,
        file: File
    ): Flow<FileTransferState> = flow {

        Socket().use { socket ->
            currentSocket = socket
            socket.connect(address, 5_000)

            val out = DataOutputStream(
                BufferedOutputStream(socket.getOutputStream())
            )

            // 发送元信息
            val meta = FileMetaPacket(
                fileName = file.name,
                fileSize = file.length(),
                md5 = file.md5(),
                from = GlobalStateHolder.localDevicePacket
            )

            val metaBytes = Json.encodeToString<Packet>(meta)
                .toByteArray(Charsets.UTF_8)

            out.writeInt(metaBytes.size)
            out.write(metaBytes)
            out.flush()

            // 发送文件 + 进度
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

            emit(FileTransferState.Completed(file, expectedMd5 = null))
        }

    }.catch { e ->
        e.printStackTrace()
        emit(FileTransferState.Error(e))
    }.flowOn(Dispatchers.IO)

}