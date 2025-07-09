package com.undefined.lynx.server;

import com.undefined.lynx.event.Events;
import com.undefined.lynx.scheduler.Scheduler;
import com.undefined.lynx.tab.TabLatency;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class Test {

    public Test() {
        Events.event(PlayerJoinEvent.class, event -> {

        }, EventPriority.HIGH, true);



//        new ItemBuilder(Material.DIAMOND_CHESTPLATE)
//                .meta(SkullMeta.class, meta -> {
//                    meta.setTexture()
//                });

    }

}
