package com.xah.send.logic.model.state

import java.io.File

sealed interface FileTransferState {
    data class Progress(
        val currentBytes: Long,
        val totalBytes: Long
    ) : FileTransferState

    data class Completed(
        val file: File
    ) : FileTransferState

    data class Error(
        val throwable: Throwable
    ) : FileTransferState
}