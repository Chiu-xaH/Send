package com.xah.send.logic.model

/**
 * 表示一个文件传输的进度
 * @param currentSpeedBytesPerSec 当前速度
 * @param averageSpeedBytesPerSec 平均速度
 * @param remainingSeconds 剩余的秒数
 */
data class TransferSpeedState(
    val currentSpeedBytesPerSec: Double,
    val averageSpeedBytesPerSec: Double,
    val remainingSeconds: Long
)
