package me.example.playtime;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

public class PlayerEvents {

    public static void register() {

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
	    
	    System.out.println("[PlayTimeBoard] Player joined: " + player.getName().getString());

            PlayTimeBoardFabric.getInstance()
                    .getPlayTimeManager()
                    .playerJoin(player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();

            PlayTimeBoardFabric.getInstance()
                    .getPlayTimeManager()
                    .playerQuit(player);
        });
    }
}
