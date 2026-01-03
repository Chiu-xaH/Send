package com.xah.send.logic.util

/**
 * 剪切板工具
 */
expect object ClipBoardHelper {
    /**
     * 写入到剪切板，即复制
     * @param str 要写入的内容
     * @param tips 完成后的提示文字
     */
    fun copy(str : String, tips : String? = "已复制到剪切板")

    /**
     * 读取剪切板第一条，即粘贴
     */
    fun paste(): String?
}