package com.undefined.lynx.sidebar.team

import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.sidebar.ScoreboardManager
import com.undefined.lynx.sidebar.checkAsyncAndApply
import com.undefined.lynx.team.CollisionRule
import com.undefined.lynx.team.NameTagVisibility
import com.undefined.lynx.util.RunBlock
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard

@Suppress("UNUSED")
class Team @JvmOverloads constructor(
    override val autoLoad: Boolean = true,
    private val async: Boolean = false,
    order: Int = 1,
    scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    block: RunBlock<Team> = RunBlock {}
): AbstractTeam(autoLoad, scoreboard, async, order) {


    init {
        ScoreboardManager.activeTeams.add(this)
        block.run(this)
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

    fun addEntry(player: Player) = addEntries(listOf(player.name))

    fun addEntries(players: Set<Player>) = addEntries(players.map { it.name })

    fun addEntry(name: String) = addEntries(listOf(name))

    fun addEntries(names: List<String>) = checkAsyncAndApply(async) {
        names.forEach { NMSManager.nms.scoreboard.addTeamEntry(team, it) }
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players())
    }


    fun setPrefix(prefix: String) = checkAsyncAndApply(async) {
        setPrefixJson(prefix.toJson())
    }

    fun setSuffix(suffix: String) = checkAsyncAndApply(async) {
        setSuffixJson(suffix.toJson())
    }

    fun setPrefixComponent(prefix: Component) = checkAsyncAndApply(async) {
        setPrefixJson(prefix.toJson())
    }

    fun setSuffixComponent(suffix: Component) = checkAsyncAndApply(async) {
        setSuffixJson(suffix.toJson())
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

}

fun team(
    autoLoad: Boolean = true,
    async: Boolean = false,
    order: Int = 1,
    scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    kotlinDSL: Team.() -> Unit
) = Team(autoLoad, async, order, scoreboard, kotlinDSL)