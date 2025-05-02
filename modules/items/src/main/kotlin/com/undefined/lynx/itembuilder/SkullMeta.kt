package com.undefined.lynx.itembuilder

import com.undefined.lynx.NMSManager
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.profile.PlayerProfile

class SkullMeta : ItemBuildMeta() {

    var noteBlockSound: NamespacedKey? = null

    var owner: String? = null
    var playerProfile: PlayerProfile? = null
    var offlinePlayer: OfflinePlayer? = null
    var texture: String? = null

    fun setNoteBlockSound(namespacedKey: NamespacedKey?) = apply {
        this.noteBlockSound = namespacedKey
    }

    fun setPlayerProfile(playerProfile: PlayerProfile?) = apply {
        this.playerProfile = playerProfile
    }

    fun setOfflinePlayer(offlinePlayer: OfflinePlayer?) = apply {
        this.offlinePlayer = offlinePlayer
    }

    fun setTexture(texture: String?) = apply {
        this.texture = texture
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        val skullMeta = itemMeta as SkullMeta
        this.owner = skullMeta.owner
        this.playerProfile = skullMeta.ownerProfile
        this.offlinePlayer = skullMeta.owningPlayer
        this.noteBlockSound = skullMeta.noteBlockSound
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val skullMeta = itemMeta as? SkullMeta ?: return itemMeta
        skullMeta.noteBlockSound = noteBlockSound
        skullMeta.owningPlayer = offlinePlayer
        skullMeta.ownerProfile = playerProfile
        skullMeta.owner = owner
        texture?.let { NMSManager.nms.itemBuilder.setSkullTexture(skullMeta, it) }
        return skullMeta
    }
}