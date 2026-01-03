package com.xah.send.logic.model.device

/**
 * 存储已发现的设备的数据格式
 * @param deviceId 随机生成，唯一标识
 * @param deviceName 设备名，供用户辨别
 * @param tcpPort 端口号
 * @param ip IP地址
 * @param lastSeen 最近一次发现的时间
 */
data class RemoteDevice(
    override val deviceId: String,
    override val deviceName: String,
    val ip: String,
    override val tcpPort: Int,
    var lastSeen: Long
) : Device()
