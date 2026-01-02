package com.xah.send.logic.model.packet

import com.xah.send.logic.model.device.LocalDevice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("file_meta")
data class FileMetaPacket(
    val fileName: String,
    val fileSize: Long,
    val mime: String?,
    override val from: LocalDevice
) : Packet()
