package com.undefined.lynx.commonProtocol

data class ClientInfo(
    var protocolVersion: Int,
    var client: String,
    var mods: List<String>
)