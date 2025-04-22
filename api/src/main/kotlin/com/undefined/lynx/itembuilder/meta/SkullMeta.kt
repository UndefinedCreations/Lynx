package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.NMSManager
import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.profile.PlayerProfile

class SkullMeta : ItemBuildMeta() {

    private var noteBlockSound: NamespacedKey? = null

    private var owner: String? = null
    private var playerProfile: PlayerProfile? = null
    private var offlinePlayer: OfflinePlayer? = null
    private var texture: String? = null

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
        texture?.let { NMSManager.nms.setSkullTexture(skullMeta, it) }
        return skullMeta
    }
}