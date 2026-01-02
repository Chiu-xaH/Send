package com.xah.send.logic.util

expect object ClipBoardHelper {
    fun copy(str : String, tips : String?)
    fun paste(): String?
}