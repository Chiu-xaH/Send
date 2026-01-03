package com.xah.send.logic.model.state.transfer

import java.io.File

/**
 * 文件传输状态
 * @property Progress 进行中
 * @property Completed 完成
 * @property Error 出错
 */
sealed interface FileTransferState {
    /**
     * 进行中
     * @param currentBytes 已发送或接收的字节
     * @param totalBytes 整个文件的字节
     */
    data class Progress(
        val currentBytes: Long,
        val totalBytes: Long
    ) : FileTransferState

    /**
     * 完成
     * @param file 文件
     * @param expectedMd5 期望MD5 发送文件时不需要校验，传null
     */
    data class Completed(
        val file: File,
        val expectedMd5 : String?
    ) : FileTransferState

    /**
     * 完成
     * @param throwable 错误信息
     * @param msg 错误信息
     */
    data class Error(
        val throwable: Throwable,
        val msg : String? = throwable.message,
    ) : FileTransferState
}