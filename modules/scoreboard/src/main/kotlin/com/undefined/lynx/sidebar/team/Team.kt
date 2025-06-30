package com.undefined.lynx.sidebar.team

import com.undefined.lynx.NMSManager
import com.undefined.lynx.sidebar.ScoreboardManager
import com.undefined.lynx.sidebar.checkAsyncAndApply
import com.undefined.lynx.sidebar.order
import com.undefined.lynx.team.CollisionRule
import com.undefined.lynx.team.NameTagVisibility
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import java.util.UUID

@Suppress("unused")
class Team(
    internal val autoLoad: Boolean = true,
    scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    private val async: Boolean = false,
    order: Int = 1,
    kotlinDSL: Team.() -> Unit = {}
) {

    internal val playersList: MutableList<Player> = mutableListOf()
    private val team = NMSManager.nms.scoreboard.createTeam(scoreboard, "${order(order)}${UUID.randomUUID()}")

    var prefix: String = ""
        set(value) {
            NMSManager.nms.scoreboard.setTeamPrefix(team, value)
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players())
            field = value
        }

    var suffix: String = ""
        set(value) {
            NMSManager.nms.scoreboard.setTeamSuffix(team, value)
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players())
            field = value
        }

    var color: ChatColor = ChatColor.WHITE
        set(value) {
            NMSManager.nms.scoreboard.setTeamColor(team, value)
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players())
            field = value
        }

    var seeFriendlyInvisible: Boolean = true
        set(value) {
            NMSManager.nms.scoreboard.setTeamSeeFriendlyInvisibles(team, value)
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players())
            field = value
        }

    var nameTagVisibility: NameTagVisibility = NameTagVisibility.ALWAYS
        set(value) {
            NMSManager.nms.scoreboard.setTeamNameTagVisibility(team, value)
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players())
            field = value
        }

    var collisionRule: CollisionRule = CollisionRule.ALWAYS
        set(value) {
            NMSManager.nms.scoreboard.setTeamCollisionRule(team, value)
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players())
            field = value
        }

    init {
        ScoreboardManager.activeTeams.add(this)
        kotlinDSL()
    }

    fun addEntry(player: Player) = addEntries(listOf(player.name))

    fun addEntries(players: Set<Player>) = addEntries(players.map { it.name })

    fun addEntry(name: String) = addEntries(listOf(name))

    fun addEntries(names: List<String>) = checkAsyncAndApply(async) {
        names.forEach { NMSManager.nms.scoreboard.addTeamEntry(team, it) }
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players())
    }
    
    fun setPrefix(prefix: String) = checkAsyncAndApply(async) {
        this.prefix = prefix
    }

    fun setSuffix(suffix: String) = checkAsyncAndApply(async) {
        this.suffix = suffix
    }

    fun setColor(color: ChatColor) = checkAsyncAndApply(async) {
        this.color = color
    }

    fun setSeeFriendlyInvisible(canSee: Boolean) = checkAsyncAndApply(async) {
        this.seeFriendlyInvisible = canSee
    }

    fun setNameTagVisibility(visible: NameTagVisibility) = checkAsyncAndApply(async) {
        this.nameTagVisibility = visible
    }

    fun setCollisionRule(rule: CollisionRule) = checkAsyncAndApply(async) {
        this.collisionRule = rule
    }
    
    fun addViewer(player: Player) = addViewers(listOf(player))
    
    fun addViewers(players: List<Player>) = checkAsyncAndApply(async) {
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players)
        this.playersList.addAll(players)
    }

    fun removeViewer(player: Player) = removeViewers(listOf(player))
    
    fun removeViewers(players: List<Player>) = checkAsyncAndApply(async) {
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketRemove(team, players)
        playersList.removeAll(players)
    }

    fun remove() = checkAsyncAndApply(async) {
        removeViewers(players())
        ScoreboardManager.activeTeams.remove(this)
    }

    private fun players(): List<Player> = if (autoLoad) Bukkit.getOnlinePlayers().toList() else playersList

}

fun team(
    autoLoad: Boolean = true,
    scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    async: Boolean = false,
    order: Int = 1,
    kotlinDSL: Team.() -> Unit
) = Team(autoLoad, scoreboard, async, order, kotlinDSL)