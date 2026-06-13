package me.example.playtime;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;

public class PlayTimeCommand {

    // 定义权限节点
    private static final String PERM_DELETE = "playtime.delete";
    private static final String PERM_RELOAD = "playtime.reload";

    public static void register() {
        System.out.println("[PlayTimeBoard] Registering command callback");

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> {
                    System.out.println("[PlayTimeBoard] Registering /playtime");
                    register(dispatcher);
                }
        );
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("playtime")
                        // 默认显示排行榜
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            ServerPlayer player = source.getPlayer();
                            if (player == null) {
                                source.sendFailure(Component.literal("该命令只能由玩家执行"));
                                return 1;
                            }
                            showLeaderboard(player);
                            return 1;
                        })
                        // 帮助子命令
                        .then(Commands.literal("help")
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ServerPlayer player = source.getPlayer();
                                    if (player == null) {
                                        source.sendFailure(Component.literal("该命令只能由玩家执行"));
                                        return 1;
                                    }
                                    showHelp(player);
                                    return 1;
                                })
                        )
                        // 管理子命令：删除玩家数据（使用权限节点 playtime.delete，默认 OP 等级 2）
                        .then(Commands.literal("del")
                                .requires(Permissions.require(PERM_DELETE, 2))
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(context -> {
                                            String playerName = StringArgumentType.getString(context, "player");
                                            CommandSourceStack source = context.getSource();
                                            PlayTimeManager manager = PlayTimeBoardFabric.getInstance().getPlayTimeManager();

                                            UUID targetUuid = null;
                                            for (UUID uuid : manager.getSortedPlayers().keySet()) {
                                                if (manager.getPlayerName(uuid).equalsIgnoreCase(playerName)) {
                                                    targetUuid = uuid;
                                                    break;
                                                }
                                            }
                                            if (targetUuid == null) {
                                                source.sendFailure(Component.literal("未找到玩家: " + playerName));
                                                return 0;
                                            }
                                            boolean success = manager.resetPlayer(targetUuid);
                                            if (success) {
                                                source.sendSuccess(() -> Component.literal("已删除玩家 " + playerName + " 的时长数据"), true);
                                                ScoreboardUpdater.updateAll();
                                                TabListUpdater.updateAll();
                                            } else {
                                                source.sendFailure(Component.literal("删除失败，玩家可能不存在"));
                                            }
                                            return 1;
                                        })
                                )
                        )
                        // 管理子命令：重新加载数据文件（使用权限节点 playtime.reload，默认 OP 等级 2）
                        .then(Commands.literal("reload")
                                .requires(Permissions.require(PERM_RELOAD, 2))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    PlayTimeManager manager = PlayTimeBoardFabric.getInstance().getPlayTimeManager();
                                    manager.reload();
                                    source.sendSuccess(() -> Component.literal("已重新加载数据文件"), true);
                                    ScoreboardUpdater.updateAll();
                                    TabListUpdater.updateAll();
                                    return 1;
                                })
                        )
                        // 处理未知子命令
                        .then(Commands.argument("unknown", StringArgumentType.word())
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ServerPlayer player = source.getPlayer();
                                    if (player == null) {
                                        source.sendFailure(Component.literal("该命令只能由玩家执行"));
                                        return 1;
                                    }
                                    player.sendSystemMessage(Component.literal("§c未知子命令，请输入 /playtime help 查看帮助"));
                                    return 1;
                                })
                        )
        );
    }

    private static void showLeaderboard(ServerPlayer player) {
        PlayTimeManager manager = PlayTimeBoardFabric.getInstance().getPlayTimeManager();

        player.sendSystemMessage(Component.literal("§6===== 在线时长排行榜 ====="));

        int rank = 1;
        for (Map.Entry<UUID, Long> entry : manager.getSortedPlayers().entrySet()) {
            String name = manager.getPlayerName(entry.getKey());
            String time = manager.formatBoardTime(entry.getValue());
            player.sendSystemMessage(Component.literal(
                    "§e#" + rank + " §f" + name + " §7" + time
            ));
            rank++;
        }

        if (rank == 1) {
            player.sendSystemMessage(Component.literal("§7暂无符合条件的玩家"));
        }

        player.sendSystemMessage(Component.literal(" "));

        int playerRank = manager.getRank(player.getUUID());
        if (playerRank == -1) {
            player.sendSystemMessage(Component.literal("§c你的账号带有 Bot 标识，不参与排名"));
        } else {
            player.sendSystemMessage(Component.literal("§a你的排名：#" + playerRank));
        }

        player.sendSystemMessage(Component.literal(
                "§b当前时长：" + manager.formatFullTime(manager.getPlayTicks(player.getUUID()))
        ));

        player.sendSystemMessage(Component.literal("§7输入 /playtime help 查看帮助"));
    }

    private static void showHelp(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§6===== PlayTimeBoard v5.20.0 Help ====="));
        player.sendSystemMessage(Component.literal("§e/playtime §7- 查看在线时长排行榜"));
        player.sendSystemMessage(Component.literal("§e/playtime help §7- 显示此帮助信息"));
        player.sendSystemMessage(Component.literal("§e/playtime del <玩家名> §7- 删除指定玩家的时长数据 §8(需权限: " + PERM_DELETE + ")"));
        player.sendSystemMessage(Component.literal("§e/playtime reload §7- 重新加载数据文件 §8(需权限: " + PERM_RELOAD + ")"));
        player.sendSystemMessage(Component.literal("§7说明：名字包含 §cBot §7的玩家不参与排名"));
        player.sendSystemMessage(Component.literal("§7数据每20秒自动保存，服务器关闭时也会保存"));
	player.sendSystemMessage(Component.literal(" "));
	player.sendSystemMessage(Component.literal("§7by kyodou233 2026.6.10"));
player.sendSystemMessage(Component.literal("§7mc.ba4ssp.cn"));
    }
}
