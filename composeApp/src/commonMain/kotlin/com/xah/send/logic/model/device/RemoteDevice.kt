package com.xah.send.logic.model.device

data class RemoteDevice(
    override val deviceId: String,
    override val deviceName: String,
    val ip: String,
    override val tcpPort: Int,
    var lastSeen: Long
) : Device()
