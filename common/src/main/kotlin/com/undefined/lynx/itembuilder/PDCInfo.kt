package com.undefined.lynx.itembuilder

import org.bukkit.persistence.PersistentDataType

data class PDCInfo<P, C : Any>(val type: PersistentDataType<P, C>, val value: C)