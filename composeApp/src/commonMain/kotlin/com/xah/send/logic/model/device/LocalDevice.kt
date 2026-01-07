package com.xah.send.logic.model.device

import kotlinx.serialization.Serializable

/**
 * 设备之间互相发现用的数据格式
 * @param deviceId 随机生成，唯一标识
 * @param deviceName 设备名，供用户辨别
 * @param tcpPort 端口号
 */
@Serializable
data class LocalDevice(
    override val deviceId: String,
    override val deviceName: String,
    override val tcpPort: Int,
) : Device()
