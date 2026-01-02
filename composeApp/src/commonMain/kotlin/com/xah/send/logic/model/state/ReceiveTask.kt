package com.xah.send.logic.model.state

import com.xah.send.logic.model.packet.FileMetaPacket
import com.xah.send.logic.model.packet.TextPacket
import kotlinx.coroutines.flow.Flow
import java.net.InetSocketAddress

sealed interface ReceiveTask {
    data class Text(
        val from: InetSocketAddress,
        val packet: TextPacket
    ) : ReceiveTask

    data class File(
        val from: InetSocketAddress,
        val meta: FileMetaPacket,
        val progress: Flow<FileTransferState>
    ) : ReceiveTask
}