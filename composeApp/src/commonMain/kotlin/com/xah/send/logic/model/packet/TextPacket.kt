package com.xah.send.logic.model.packet

import com.xah.send.logic.model.device.LocalDevice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *传输文本用的数据格式
 * @param from 发送方的信息
 * @param text 文本内容
 */
@Serializable
@SerialName("text")
data class TextPacket(
    val text: String,
    override val from: LocalDevice
) : Packet()
