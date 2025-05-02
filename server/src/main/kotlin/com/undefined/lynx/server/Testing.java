package com.undefined.lynx.server;

import com.undefined.stellar.StellarCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Testing {

    private void testing() {

        List<Arena> list = new ArrayList<>();

        new StellarCommand("arena")
                .addListArgument("mapid", context -> {
                    return list;
                }, (sender, string) -> {
                    for (Arena arena : list) {
                        if (arena.getId().toString().equals(string)) {
                            return arena;
                        }
                    }
                    throw new IllegalArgumentException("Arena with ID " + string + " not found.");
                })
                .addExecution(Player.class, context -> {
                    Arena arena = context.getArgument("mapid");
                    // Use arena as needed
                });

    }

}

class Arena {

    private UUID id;

    public Arena(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

}
