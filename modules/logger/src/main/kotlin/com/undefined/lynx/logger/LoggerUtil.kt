package com.undefined.lynx.logger

import com.undefined.lynx.LynxConfig

fun sendError(message: Any) = LoggerUtil.sendError(message)
fun sendWarn(message: Any) = LoggerUtil.sendWarn(message)
fun sendInfo(message: Any) = LoggerUtil.sendInfo(message)

fun String.sendError() = LoggerUtil.sendError(this)
fun String.sendWarn() = LoggerUtil.sendWarn(this)
fun String.sendInfo() = LoggerUtil.sendInfo(this)

object LoggerUtil {
    @JvmStatic
    fun sendError(message: Any) = LynxConfig.javaPlugin.logger.severe(message.toString())
    @JvmStatic
    fun sendWarn(message: Any) = LynxConfig.javaPlugin.logger.warning(message.toString())
    @JvmStatic
    fun sendInfo(message: Any) = LynxConfig.javaPlugin.logger.info(message.toString())
}