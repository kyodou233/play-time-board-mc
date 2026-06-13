package me.example.playtime;

import de.hotkeyyy.simplefabricscoreboard.scoreboard.ScoreboardManager;
import de.hotkeyyy.simplefabricscoreboard.scoreboard.Simplescoreboard;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;

public class ScoreboardUpdater {

    public static void updateAll() {
        MinecraftServer server = PlayTimeBoardFabric.getInstance().getServer();
        if (server == null) return;

        PlayTimeManager manager = PlayTimeBoardFabric.getInstance().getPlayTimeManager();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            updatePlayer(player, manager);
        }
    }

    private static void updatePlayer(ServerPlayer player, PlayTimeManager manager) {
        // 1. 移除旧的记分板（通过 INSTANCE 访问 Kotlin object）
        ScoreboardManager.INSTANCE.removePlayerScoreboard(player);

        // 2. 获取服务器实例
        MinecraftServer server = PlayTimeBoardFabric.getInstance().getServer();

        // 3. 创建新的记分板
        Simplescoreboard board = new Simplescoreboard(
                "playtime_" + player.getUUID(),
                Component.literal("在线时长排行榜"),
                server
        );

        // 4. 添加排行榜前10名
        int rank = 1;
        for (Map.Entry<UUID, Long> entry : manager.getSortedPlayers().entrySet()) {
            if (rank > 10) break;

            String name = manager.getPlayerName(entry.getKey());
            String time = manager.formatBoardTime(entry.getValue());

            String line = name + " §7" + time;
            if (line.length() > 40) line = line.substring(0, 40);

            board.line(Component.literal(line));
            rank++;
        }

        // 5. 添加空行
        board.line(board.emptyLine());

        // 6. 添加个人排名
        board.line(Component.literal("§a排名 §e#" + manager.getRank(player.getUUID())));
        // 8. 添加当前时长标签和具体时长
	board.line(Component.literal("§a当前时长：§b" + manager.formatFullTime(manager.getPlayTicks(player.getUUID()))));
        // 9. 绑定到玩家
        ScoreboardManager.INSTANCE.setPlayerScoreboard(player, board);
    }
}
