package me.example.playtime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PlayTimeManager {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final Map<UUID, PlayerData> players = new HashMap<>();

    private final File dataFile = new File("config/playtimeboard/playtime.json");

    // 判断玩家是否应被排除（名字包含"bot"，不区分大小写）
    private boolean shouldExclude(UUID uuid) {
        String name = getPlayerName(uuid);
        return name != null && name.toLowerCase().contains("bot");
    }

    public void load() {
        try {
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                save();
                return;
            }
            Type type = new TypeToken<HashMap<UUID, PlayerData>>() {}.getType();
            Map<UUID, PlayerData> loaded = gson.fromJson(new FileReader(dataFile), type);
            if (loaded != null) {
                players.putAll(loaded);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            dataFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(dataFile);
            gson.toJson(players, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playerJoin(ServerPlayer player) {
        UUID uuid = player.getUUID();
        PlayerData data = players.computeIfAbsent(uuid, k -> new PlayerData());
        data.name = player.getName().getString();
        data.online = true;
    }

    public void playerQuit(ServerPlayer player) {
        PlayerData data = players.get(player.getUUID());
        if (data != null) {
            data.online = false;
        }
    }

    public void tick() {
        for (PlayerData data : players.values()) {
            if (data.online) {
                data.playTicks++;
            }
        }
    }

    public long getPlayTicks(UUID uuid) {
        PlayerData data = players.get(uuid);
        return data == null ? 0 : data.playTicks;
    }

    public String getPlayerName(UUID uuid) {
        PlayerData data = players.get(uuid);
        return data == null ? "Unknown" : data.name;
    }

    public int getRank(UUID uuid) {
        int rank = 1;
        for (UUID current : getSortedPlayers().keySet()) {
            if (current.equals(uuid)) return rank;
            rank++;
        }
        return -1; // 未上榜或被排除
    }

    public Map<UUID, Long> getSortedPlayers() {
        LinkedHashMap<UUID, Long> result = new LinkedHashMap<>();
        players.entrySet()
                .stream()
                .filter(entry -> !shouldExclude(entry.getKey()))
                .sorted(Comparator.comparingLong(e -> -e.getValue().playTicks))
                .forEach(entry -> result.put(entry.getKey(), entry.getValue().playTicks));
        return result;
    }

    // ---------- 管理命令新增方法 ----------
    public boolean resetPlayer(UUID uuid) {
        PlayerData removed = players.remove(uuid);
        if (removed != null) {
            save();
            return true;
        }
        return false;
    }

    public void resetAll() {
        players.clear();
        save();
    }

    public void reload() {
        players.clear();
        load();
    }

    // ---------- 原有方法 ----------
    public String formatBoardTime(long ticks) {
        long minutes = ticks / 20 / 60;
        long days = minutes / 1440;
        long hours = (minutes % 1440) / 60;
        return days + "天 " + hours + "时";
    }

    public String formatFullTime(long ticks) {
        long minutes = ticks / 20 / 60;
        long days = minutes / 1440;
        long hours = (minutes % 1440) / 60;
        long mins = minutes % 60;
        return days + "天 " + hours + "时 " + mins + "分";
    }

    public PlayerData getOrCreate(UUID uuid) {
        return players.computeIfAbsent(uuid, k -> new PlayerData());
    }

    public static class PlayerData {
        public String name = "Unknown";
        public long playTicks = 0;
        public boolean online = false;
    }
}
