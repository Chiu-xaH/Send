package com.xah.send.logic.model.state

sealed interface TextTransferState {
    object Progress : TextTransferState
    object Completed : TextTransferState
    data class Error(val throwable: Throwable) : TextTransferState
}