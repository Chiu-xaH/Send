package com.xah.send.logic.model.device

import kotlinx.serialization.Serializable

@Serializable
sealed class Device {
    abstract val deviceId: String
    abstract val deviceName: String
    abstract val tcpPort: Int
}

