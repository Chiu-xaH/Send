package com.xah.send.logic.model.bean

import kotlinx.serialization.Serializable

@Serializable
data class HelloPacket(
    val type: String = "HELLO",
    val deviceId: String,
    val deviceName: String,
    val tcpPort: Int
)
