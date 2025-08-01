package com.undefined.lynx.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
        display.onClick( event -> {
           event.getPlayer().sendMessage(event.getClickType().name());
        });


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

    public static Skin getSkinTexture(String name) throws Exception {
        URL url = new URI("https://api.mojang.com/users/profiles/minecraft/" + name).toURL();
        try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
            JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
            String uuid = response.get("id").getAsString();

            URL url1 = new URI("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false").toURL();
            try (InputStreamReader reader1 = new InputStreamReader(url1.openStream())) {
                JsonObject response1 = JsonParser.parseReader(reader1).getAsJsonObject();
                JsonArray properties = response1.getAsJsonArray("properties");
                JsonObject textureProperty = properties.get(0).getAsJsonObject();

                String value = textureProperty.get("value").getAsString();
                String signature = textureProperty.get("signature").getAsString();

                return new Skin(value, signature);
            }
        }
    }

}

