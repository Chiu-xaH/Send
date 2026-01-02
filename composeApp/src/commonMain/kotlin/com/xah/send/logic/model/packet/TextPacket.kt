package com.xah.send.logic.model.packet

import com.xah.send.logic.model.device.LocalDevice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("text")
data class TextPacket(
    val text: String,
    override val from: LocalDevice
) : Packet()
