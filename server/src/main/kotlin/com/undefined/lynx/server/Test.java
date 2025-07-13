package com.undefined.lynx.server;

import com.undefined.lynx.event.Events;
import com.undefined.lynx.sidebar.sidebar.lines.UpdatableLine;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class Test {

    public Test() {
        Events.event(PlayerJoinEvent.class, event -> {

        }, EventPriority.HIGH, true);


        new UpdatableLine(() -> {
            return "test";
        });

//        new ItemBuilder(Material.DIAMOND_CHESTPLATE)
//                .meta(SkullMeta.class, meta -> {
//                    meta.setTexture()
//                });

    }

}
