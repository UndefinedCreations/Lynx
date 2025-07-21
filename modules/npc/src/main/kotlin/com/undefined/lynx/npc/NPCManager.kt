package com.undefined.lynx.npc

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.Skin
import com.undefined.lynx.npc.NPCManager.DEFAULT_NAME
import com.undefined.lynx.npc.NPCManager.DEFAULT_SKIN
import com.undefined.lynx.util.RunBlock
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

object NPCManager : Listener {

    internal val autoLoadNPCS: MutableList<NPC> = mutableListOf()
    internal val spawnedNPC: MutableList<NPC> = mutableListOf()

    @JvmStatic
    var DEFAULT_SKIN = Skin(
    "ewogICJ0aW1lc3RhbXAiIDogMTcyNDQ1OTY2NDU1MywKICAicHJvZmlsZUlkIiA6ICJjMDZmODkwNjRjOGE0OTExOWMyOWVhMWRiZDFhYWI4MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU3RldmUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNGVlNWNlMjBhZWQ5ZTMzZTg2NmM2NmNhYTM3MTc4NjA2MjM0YjM3MjEwODRiZjAxZDEzMzIwZmIyZWIzZiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
    "l85wVdzBMGxA2i5ua7HOXSONtytElEg7CsDFfYy/mTZWDEbBioELI8ZHGTzhnX0vVxtt7Gujoaev/XEcUHyKEaLHmxPr2prb6nV94SljibM4FG29/USkPNKRJUFsruSZZp6P6XUjcSfaBwGtVVmxzO3QP8My7wHesmiuhJhS2s9eXXIIOJBoPpfPrqQAOv0hD7Q/2oVpDd71ryEvyjjbkjAwSwSaOTJ4JNAgTjNnPKNdDrx+vVJgTeKMx7wmqMDPNmL8zhsdCbxvVlr3GYucPynu/abkYWLdNpjyQ2JcjTCmgzVtQ2MVOSCn3ZUw/+STZ0GnO31PwS8G7tNTZlLDSlrM8xqu55PjuhVSdsi8VpnCSb4ycNGq2aWcBsvYIIs50idPcyvgikf+BQf/XIDdNasxpTUkgNCrRldHodmx+mIwbN448NBzr4nCdl0IOQexaodTbLcEnvys+79Gmy4D2rLkZDiu4hOfHUEouxCEV/41k18lWU5Rhl5vBTyu8XkqFYI11QCKEr+uayn53QItqVgIVHrPypGoFHkHrccEQVE7ZwVRctQavkI9eOJoyPsd8J0V2W+J21XGmt6iY944jxBn32Fx0Mt4CPAkEDoYq+F1l2xH8CVENyei9+nG9c4HsCoZyUShMkablKcrMMuYbtvydENSzoMukb21brlgRY0="
    )
    @JvmStatic
    var DEFAULT_NAME = "STEVE"

    init {

        Bukkit.getPluginManager().registerEvents(NPCListener(), LynxConfig.javaPlugin)
        NMSManager.nms.npc.onClick {
            spawnedNPC.firstOrNull() { it.getEntityID() == this.entityID }?.let {
                Bukkit.getScheduler().runTask(LynxConfig.javaPlugin, Runnable {
                    for (run in it.clickActions) run(this)
                })
            }
        }

    }

    @JvmStatic
    fun getAllNPCs(): MutableList<NPC> = spawnedNPC

    @JvmOverloads
    @JvmStatic
    fun spawnNPC(
        location: Location,
        name: String = DEFAULT_NAME,
        texture: String = DEFAULT_SKIN.texture,
        signature: String = DEFAULT_SKIN.signature,
        visibleTo: List<Player>? = null,
        autoLoad: Boolean = true,
        dsl: RunBlock<NPC> = RunBlock {}
    ): NPC {
        val serverPlayer = NMSManager.nms.npc.createServerPlayer(name, texture, signature)
        val team = NMSManager.nms.scoreboard.createTeam(Bukkit.getScoreboardManager()!!.mainScoreboard, UUID.randomUUID().toString())
        NMSManager.nms.scoreboard.addTeamEntry(team, name)
        val players = visibleTo ?: Bukkit.getOnlinePlayers().toList()
        val npc = NPC(serverPlayer, team, visibleTo?.toMutableList(), location)
        npc.addViewers(players)
        if (autoLoad) autoLoadNPCS.add(npc)
        spawnedNPC.add(npc)
        dsl.run(npc)
        return npc
    }

    @JvmOverloads
    @JvmStatic
    fun spawnNPC(
        location: Location,
        name: String = DEFAULT_NAME,
        skin: Skin,
        visibleTo: List<Player>? = null,
        autoLoad: Boolean = true,
        dsl: RunBlock<NPC> = RunBlock {}
    ) = spawnNPC(location, name, skin.texture, skin.signature, visibleTo, autoLoad, dsl)

}


fun Location.spawnNPC(
    name: String = DEFAULT_NAME,
    texture: String = DEFAULT_SKIN.texture,
    signature: String = DEFAULT_SKIN.signature,
    visibleTo: List<Player>? = null,
    autoLoad: Boolean = true,
    dsl: RunBlock<NPC> = RunBlock {}
) = NPCManager.spawnNPC(this, name, texture, signature, visibleTo, autoLoad, dsl)

fun Location.spawnNPC(
    name: String = DEFAULT_NAME,
    skin: Skin,
    visibleTo: List<Player>? = null,
    autoLoad: Boolean = true,
    dsl: RunBlock<NPC> = RunBlock {}
) = NPCManager.spawnNPC(this, name, skin, visibleTo, autoLoad, dsl)

fun World.getNPC(uuid: UUID): NPC? = NPCManager.spawnedNPC.filter { it.getUUID() == uuid }.getOrNull(0)