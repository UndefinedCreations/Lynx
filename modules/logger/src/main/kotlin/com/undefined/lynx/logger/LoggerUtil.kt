package com.undefined.lynx.logger

import com.undefined.lynx.LynxConfig

fun sendError(message: Any) = LoggerUtil.sendError(message)
fun sendWarn(message: Any) = LoggerUtil.sendWarn(message)
fun sendInfo(message: Any) = LoggerUtil.sendInfo(message)
fun sendDebug(message: Any) = LoggerUtil.sendDebug(message)
fun sendTrace(message: Any) = LoggerUtil.sendTrace(message)

fun String.sendError() = LoggerUtil.sendError(this)
fun String.sendWarn() = LoggerUtil.sendWarn(this)
fun String.sendInfo() = LoggerUtil.sendInfo(this)
fun String.sendDebug() = LoggerUtil.sendDebug(this)
fun String.sendTrace() = LoggerUtil.sendTrace(this)

object LoggerUtil {
    fun sendError(message: Any) = LynxConfig.javaPlugin.logger.severe(message.toString())
    fun sendWarn(message: Any) = LynxConfig.javaPlugin.logger.warning(message.toString())
    fun sendInfo(message: Any) = LynxConfig.javaPlugin.logger.info(message.toString())
    fun sendDebug(message: Any) = LynxConfig.javaPlugin.logger.config(message.toString())
    fun sendTrace(message: Any) = LynxConfig.javaPlugin.logger.fine(message.toString())
}