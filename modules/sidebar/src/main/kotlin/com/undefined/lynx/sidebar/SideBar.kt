package com.undefined.lynx.sidebar

import com.undefined.lynx.NMSManager
import org.bukkit.entity.Player

object SideBar {

    fun setBar(player: Player) {

        NMSManager.nms.sideBar.sendSideBar(player)

    }

}