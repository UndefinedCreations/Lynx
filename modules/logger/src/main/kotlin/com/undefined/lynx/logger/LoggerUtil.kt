package com.undefined.lynx.logger

import com.undefined.lynx.LynxConfig

fun sendError(message: String) = LoggerUtil.sendError(message)
fun sendWarn(message: String) = LoggerUtil.sendWarn(message)
fun sendInfo(message: String) = LoggerUtil.sendInfo(message)
fun sendDebug(message: String) = LoggerUtil.sendDebug(message)
fun sendTrace(message: String) = LoggerUtil.sendTrace(message)

object LoggerUtil {
    fun sendError(message: String) = LynxConfig.javaPlugin.logger.severe(message)
    fun sendWarn(message: String) = LynxConfig.javaPlugin.logger.warning(message)
    fun sendInfo(message: String) = LynxConfig.javaPlugin.logger.info(message)
    fun sendDebug(message: String) = LynxConfig.javaPlugin.logger.config(message)
    fun sendTrace(message: String) = LynxConfig.javaPlugin.logger.fine(message)
}