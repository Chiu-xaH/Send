package com.send.core.bean

import kotlinx.serialization.Serializable

@Serializable
data class HelloPacket(
    val type: String = "HELLO",
    val deviceId: String,
    val deviceName: String,
    val tcpPort: Int
)
