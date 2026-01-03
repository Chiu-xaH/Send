package com.xah.send.logic.model.state

import com.xah.send.logic.model.packet.FileMetaPacket
import com.xah.send.logic.model.packet.TextPacket
import com.xah.send.logic.model.state.transfer.FileTransferState
import kotlinx.coroutines.flow.StateFlow
import java.net.InetSocketAddress

/**
 * 接收任务的类型
 * @property Text 文本
 * @property File 文件
 */
sealed interface ReceiveTask {
    /**
     * 文本
     * @param from 来源IP与端口
     * @param packet 文本内容
     */
    data class Text(
        val from: InetSocketAddress,
        val packet: TextPacket
    ) : ReceiveTask

    /**
     * 文件
     * @param from 来源IP与端口
     * @param meta 元数据（文件基本信息）
     * @param state 传输进度
     */
    data class File(
        val from: InetSocketAddress,
        val meta: FileMetaPacket,
        val state: StateFlow<FileTransferState>
    ) : ReceiveTask
}