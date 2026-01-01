package com.send.core.bean

data class RemoteDevice(
    val deviceId: String,
    val deviceName: String,
    val ip: String,
    val tcpPort: Int,
    var lastSeen: Long
)
