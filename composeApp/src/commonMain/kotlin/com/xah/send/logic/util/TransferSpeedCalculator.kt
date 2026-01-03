package com.xah.send.logic.util

import com.xah.send.logic.model.TransferSpeedState

/**
 * 计算文件传输速度
 */
class TransferSpeedCalculator {

    private var startTime: Long = 0L

    private var lastBytes: Long = 0L
    private var lastTime: Long = 0L

    fun update(
        currentBytes: Long,
        totalBytes: Long
    ): TransferSpeedState? {
        val now = System.currentTimeMillis()

        if (startTime == 0L) {
            startTime = now
            lastTime = now
            lastBytes = currentBytes
            return null
        }

        val deltaBytes = currentBytes - lastBytes
        val deltaTimeMs = now - lastTime
        val elapsedTimeMs = now - startTime

        if (deltaTimeMs <= 0 || deltaBytes <= 0 || elapsedTimeMs <= 0) {
            return null
        }

        // ① 当前速度（瞬时）
        val currentSpeed =
            deltaBytes.toDouble() / deltaTimeMs * 1000.0

        // ② 平均速度
        val averageSpeed =
            currentBytes.toDouble() / elapsedTimeMs * 1000.0

        // ③ 剩余时间 —— 用平均速度
        val remainingBytes = totalBytes - currentBytes
        val remainingSeconds =
            if (averageSpeed > 0)
                (remainingBytes / averageSpeed).toLong()
            else 0L

        lastTime = now
        lastBytes = currentBytes

        return TransferSpeedState(
            currentSpeedBytesPerSec = currentSpeed,
            averageSpeedBytesPerSec = averageSpeed,
            remainingSeconds = remainingSeconds
        )
    }
}
