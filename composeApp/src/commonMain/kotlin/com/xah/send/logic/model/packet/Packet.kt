package com.xah.send.logic.model.packet

import com.xah.send.logic.model.device.LocalDevice
import kotlinx.serialization.Serializable

/**
 *传输数据用的基础数据格式
 * @param from 发送方的信息
 */
@Serializable
sealed class Packet {
    abstract val from: LocalDevice
}

