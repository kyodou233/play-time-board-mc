package me.example.playtime;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions; // 导入 Fabric Permissions API
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class PlayTimeAdminCommand {

    private static final String RESET_NODE = "playtimeadmin.reset"; // 定义权限节点
    private static final String RESETALL_NODE = "playtimeadmin.resetall"; // 定义权限节点
    private static final String RELOAD_NODE = "playtimeadmin.reload"; // 定义权限节点

    private static final int DEFAULT_OP_LEVEL = 2; // 默认的 OP 等级（2 是普通 OP）

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                Commands.literal("playtimeadmin")
                    // 使用 Permissions.require() 进行权限检查
                    .then(Commands.literal("reset")
                        .requires(Permissions.require(RESET_NODE, DEFAULT_OP_LEVEL))
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
                                    source.sendSuccess(() -> Component.literal("已重置玩家 " + playerName + " 的时长数据"), true);
                                    // 刷新所有在线玩家的侧边栏
                                    ScoreboardUpdater.updateAll();
                                    TabListUpdater.updateAll();
                                } else {
                                    source.sendFailure(Component.literal("重置失败，玩家可能不存在"));
                                }
                                return 1;
                            })))
                    .then(Commands.literal("resetall")
                        .requires(Permissions.require(RESETALL_NODE, DEFAULT_OP_LEVEL))
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            PlayTimeManager manager = PlayTimeBoardFabric.getInstance().getPlayTimeManager();
                            manager.resetAll();
                            source.sendSuccess(() -> Component.literal("已清空所有玩家的时长数据"), true);
                            ScoreboardUpdater.updateAll();
                            TabListUpdater.updateAll();
                            return 1;
                        }))
                    .then(Commands.literal("reload")
                        .requires(Permissions.require(RELOAD_NODE, DEFAULT_OP_LEVEL))
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            PlayTimeManager manager = PlayTimeBoardFabric.getInstance().getPlayTimeManager();
                            manager.reload();
                            source.sendSuccess(() -> Component.literal("已重新加载数据文件"), true);
                            ScoreboardUpdater.updateAll();
                            TabListUpdater.updateAll();
                            return 1;
                        }))
            );
        });
    }
}
