package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.entity.EntitySnapshot
import org.bukkit.entity.EntityType
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SpawnEggMeta

class SpawnEggMeta : ItemBuildMeta() {

    var entitySnapshot: EntitySnapshot? = null
    var entityType: EntityType? = null

    fun setEntitySnapshot(entitySnapshot: EntitySnapshot?) = apply {
        this.entitySnapshot = entitySnapshot
    }

    fun setEntityType(entityType: EntityType?) = apply {
        this.entityType = entityType
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        val spawnEggMeta = itemMeta as SpawnEggMeta
        this.entitySnapshot = spawnEggMeta.spawnedEntity
        this.entityType = spawnEggMeta.spawnedType
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val spawnEggMeta = itemMeta as? SpawnEggMeta ?: return itemMeta
        entitySnapshot?.let { spawnEggMeta.setSpawnedEntity(it) }
        spawnEggMeta.spawnedType = entityType
        return spawnEggMeta
    }
}