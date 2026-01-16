package com.xah.send.logic.model.packet

import com.xah.send.logic.model.device.LocalDevice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *传输文件用的数据格式
 * @param from 发送方的信息
 * @param fileName 文件名
 * @param fileSize 文件大小
 */
@Serializable
@SerialName("file_meta")
data class FileMetaPacket(
    val fileName: String,
    val fileSize: Long,
    val md5: String,
    override val from: LocalDevice
) : Packet()
