package com.xah.send.logic.model.state.transfer

/**
 * 文本传输状态
 * @property Progress 进行中
 * @property Completed 完成
 * @property Error 出错
 */
sealed interface TextTransferState {
    /**
     * 进行中
     */
    object Progress : TextTransferState

    /**
     * 完成
     */
    object Completed : TextTransferState

    /**
     * 错误
     * @param throwable 错误信息
     */
    data class Error(val throwable: Throwable) : TextTransferState
}