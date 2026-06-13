package me.example.playtime;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class PlayTimeBoardFabric implements ModInitializer {

    public static final String MOD_ID = "playtimeboard";

    private static PlayTimeBoardFabric instance;

    private PlayTimeManager playTimeManager;

    private MinecraftServer server;

    private int saveTimer = 0;

    public static PlayTimeBoardFabric getInstance() {
        return instance;
    }

    public PlayTimeManager getPlayTimeManager() {
        return playTimeManager;
    }

    public MinecraftServer getServer() {
        return server;
    }

    @Override
    public void onInitialize() {
        instance = this;
        playTimeManager = new PlayTimeManager();

        // 命令注册必须在 onInitialize 中直接进行，不能放在 SERVER_STARTED 里
        PlayTimeCommand.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            playTimeManager.load();
            PlayerEvents.register();
            System.out.println("[PlayTimeBoard] Enabled");
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            playTimeManager.save();
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            playTimeManager.tick();
            saveTimer++;
            if (saveTimer >= 1200) {
                saveTimer = 0;
                playTimeManager.save();
            }
            if (server.getTickCount() % 20 == 0) {
                ScoreboardUpdater.updateAll();
                TabListUpdater.updateAll();
            }
        });
    }
}
