package com.xah.send.logic.model.device

import kotlinx.serialization.Serializable

@Serializable
data class LocalDevice(
    override val deviceId: String,
    override val deviceName: String,
    override val tcpPort: Int
) : Device()
