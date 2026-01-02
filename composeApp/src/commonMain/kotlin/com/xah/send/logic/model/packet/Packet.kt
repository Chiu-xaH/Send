package com.xah.send.logic.model.packet

import com.xah.send.logic.model.device.LocalDevice
import kotlinx.serialization.Serializable


@Serializable
sealed class Packet {
    abstract val from: LocalDevice
}

