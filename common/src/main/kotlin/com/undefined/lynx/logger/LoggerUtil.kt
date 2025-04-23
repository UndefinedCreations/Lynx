package com.undefined.lynx.logger

import com.undefined.lynx.LynxConfig

fun sendError(message: String) = LynxConfig.javaPlugin.logger.severe(message)
fun sendWarn(message: String) = LynxConfig.javaPlugin.logger.warning(message)
fun sendInfo(message: String) = LynxConfig.javaPlugin.logger.info(message)
fun sendDebug(message: String) = LynxConfig.javaPlugin.logger.config(message)
fun sendTrace(message: String) = LynxConfig.javaPlugin.logger.fine(message)