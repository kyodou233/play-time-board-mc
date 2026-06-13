package me.example.playtime;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TabListUpdater {

    public static void updateAll() {
        MinecraftServer server = PlayTimeBoardFabric.getInstance().getServer();
        if (server == null) return;

        PlayTimeManager manager = PlayTimeBoardFabric.getInstance().getPlayTimeManager();

        List<UUID> ranking = new ArrayList<>(manager.getSortedPlayers().keySet());

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            int rank = ranking.indexOf(player.getUUID()) + 1;

            String displayName = "§e#" + rank
                    + " §f" + player.getName().getString()
                    + " §7" + manager.formatBoardTime(manager.getPlayTicks(player.getUUID()));

            // 设置头顶名称（按 Tab 键时也会显示在列表中）
            player.setCustomName(Component.literal(displayName));
            player.setCustomNameVisible(true);
        }
    }
}
