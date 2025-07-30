package com.undefined.lynx.server;

import com.undefined.lynx.Skin;
import com.undefined.lynx.display.DisplayManager;
import com.undefined.lynx.display.implementions.BlockDisplay;
import com.undefined.lynx.display.implementions.TextDisplay;
import com.undefined.lynx.event.Events;
import com.undefined.lynx.itembuilder.ItemBuilder;
import com.undefined.lynx.itembuilder.SkullMeta;
import com.undefined.lynx.nick.PlayerMetaUtil;
import com.undefined.lynx.nick.events.PlayerGameProfileChangeEvent;
import com.undefined.lynx.sidebar.sidebar.lines.UpdatableLine;
import com.undefined.stellar.BaseStellarCommand;
import com.undefined.stellar.StellarCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.ArrayList;
import java.util.List;

public class Test {

    private final String text = "Testing";

    public Test() {

        new ItemBuilder(Material.PLAYER_HEAD)
                .meta(SkullMeta.class, meta -> {
                    meta.setTexture("texture");
                }).build();

        BlockDisplay display = new BlockDisplay();
        display.setT


        StellarCommand mainLevel = new StellarCommand("disguise");

        mainLevel.addArgument("name")
                .addStringArgument("newName")
                .addExecution(Player.class, context -> {
                    String newName = context.getArgument("name");
                    if (newName.length() > 16) {
                        context.getSender().sendMessage(ChatColor.RED + "The passed username isn't a valid name");
                        return;
                    }
                    PlayerMetaUtil.setName(context.getSender(), newName);
                });

        mainLevel.addArgument("skin")
                .addStringArgument("texture")
                .addStringArgument("signature")
                .addExecution(Player.class, context -> {
                    String texture = context.getArgument("texture");
                    String signature = context.getArgument("signature");
                    PlayerMetaUtil.setSkin(context.getSender(), texture, signature);
                });

        mainLevel.addArgument("reset")
                .addExecution(Player.class, context -> {
                    PlayerMetaUtil.resetName(context.getSender());
                    PlayerMetaUtil.resetSkin(context.getSender());
                });

        mainLevel.register();




        new UpdatableLine(() -> {
            return "test";
        });

//        new ItemBuilder(Material.DIAMOND_CHESTPLATE)
//                .meta(SkullMeta.class, meta -> {
//                    meta.setTexture()
//                });

    }

}

