package com.undefined.lynx.exception

/**
 * This exception is thrown whenever this API does not support a feature or entirely on a specific Minecraft version.
 */
class UnsupportedVersionException(supportedVersions: Collection<String>) : Exception("This Minecraft version is unsupported! Supported versions: $supportedVersions")