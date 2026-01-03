package com.xah.send.logic.model.device

import kotlinx.serialization.Serializable

/**
 * 设备发现用的基础数据格式
 * @param deviceId 随机生成，唯一标识
 * @param deviceName 设备名，供用户辨别
 * @param tcpPort 端口号
 */
@Serializable
sealed class Device {
    abstract val deviceId: String
    abstract val deviceName: String
    abstract val tcpPort: Int
}

