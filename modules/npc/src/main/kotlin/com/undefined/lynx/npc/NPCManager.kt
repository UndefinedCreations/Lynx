package com.undefined.lynx.npc

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.event.Listener
import java.util.*

object NPCManager : Listener {

    internal val autoLoadNPCS: MutableList<NPC> = mutableListOf()

    internal val spawnedNPC: MutableList<NPC> = mutableListOf()

    init {

        Bukkit.getPluginManager().registerEvents(NPCListener(), LynxConfig.javaPlugin)

        NMSManager.nms.npc.onClick {
            val id = this.entityID
            spawnedNPC.filter { it.getEntityID() == id }.forEach { it.clickActions.forEach { it(this) } }
        }

    }

    fun getAllNPCs(): MutableList<NPC> = spawnedNPC

}

fun World.spawnNPC(
    location: Location,
    name: String = "STEVE",
    texture: String = "ewogICJ0aW1lc3RhbXAiIDogMTcyNDQ1OTY2NDU1MywKICAicHJvZmlsZUlkIiA6ICJjMDZmODkwNjRjOGE0OTExOWMyOWVhMWRiZDFhYWI4MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU3RldmUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNGVlNWNlMjBhZWQ5ZTMzZTg2NmM2NmNhYTM3MTc4NjA2MjM0YjM3MjEwODRiZjAxZDEzMzIwZmIyZWIzZiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
    signature: String = "l85wVdzBMGxA2i5ua7HOXSONtytElEg7CsDFfYy/mTZWDEbBioELI8ZHGTzhnX0vVxtt7Gujoaev/XEcUHyKEaLHmxPr2prb6nV94SljibM4FG29/USkPNKRJUFsruSZZp6P6XUjcSfaBwGtVVmxzO3QP8My7wHesmiuhJhS2s9eXXIIOJBoPpfPrqQAOv0hD7Q/2oVpDd71ryEvyjjbkjAwSwSaOTJ4JNAgTjNnPKNdDrx+vVJgTeKMx7wmqMDPNmL8zhsdCbxvVlr3GYucPynu/abkYWLdNpjyQ2JcjTCmgzVtQ2MVOSCn3ZUw/+STZ0GnO31PwS8G7tNTZlLDSlrM8xqu55PjuhVSdsi8VpnCSb4ycNGq2aWcBsvYIIs50idPcyvgikf+BQf/XIDdNasxpTUkgNCrRldHodmx+mIwbN448NBzr4nCdl0IOQexaodTbLcEnvys+79Gmy4D2rLkZDiu4hOfHUEouxCEV/41k18lWU5Rhl5vBTyu8XkqFYI11QCKEr+uayn53QItqVgIVHrPypGoFHkHrccEQVE7ZwVRctQavkI9eOJoyPsd8J0V2W+J21XGmt6iY944jxBn32Fx0Mt4CPAkEDoYq+F1l2xH8CVENyei9+nG9c4HsCoZyUShMkablKcrMMuYbtvydENSzoMukb21brlgRY0=",
    visibleTo: List<UUID>? = null,
    autoLoad: Boolean = true
): NPC {
    val serverPlayer = NMSManager.nms.npc.createServerPlayer(name, texture, signature)
    NMSManager.nms.npc.sendSpawnPacket(serverPlayer, location)
    val npc = NPC(serverPlayer, visibleTo, location)
    if (autoLoad) NPCManager.autoLoadNPCS.add(npc)
    NPCManager.spawnedNPC.add(npc)
    return npc
}

fun World.getNPC(uuid: UUID): NPC? = NPCManager.spawnedNPC.filter { it.getUUID() == uuid }.getOrNull(0)