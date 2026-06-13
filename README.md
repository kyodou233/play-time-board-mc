# PlayTimeBoard

By Kyodou233, Deepseek, Chatgpt

[中文](#中文) | [English](#english)

---

## 中文

### 📖 简介

PlayTimeBoard 是一个 **Fabric 服务端模组**，用于统计玩家的在线时长，并在侧边栏（Scoreboard）和玩家列表（Tab List）中实时显示排行榜。  
支持自动排除机器人账户（名字中包含 "Bot"），并提供 OP 命令进行数据管理。

**当前版本：`5.20.0`**  
**支持 Minecraft 版本：`26.1.2`**

---

### ✨ 功能特性

- ✅ 自动记录每个玩家的**在线时长**（单位：游戏刻，20 刻 = 1 秒）
- 🏆 侧边栏实时显示**在线时长排行榜**（前 10 名，可自定义数量）
- 📋 Tab 列表显示玩家**排名、名称和时长**（按 Tab 键可见）
- 🤖 **排除机器人**：名字中包含 "Bot"（不区分大小写）的玩家不参与排名
- ⚙️ **管理命令**（需权限）：
  - `/playtime del <玩家名>` – 删除指定玩家的数据
  - `/playtime reload` – 重新加载配置文件
- 🎨 **美观样式**：支持 Minecraft 颜色代码（`§`），排行榜第 1/2/3 名使用不同颜色
- 💾 **自动保存**：数据每 20 秒自动保存至磁盘，服务器关闭时也会保存
- 🔌 **权限支持**：集成 Fabric Permissions API，可配合 LuckPerms 等模组进行精细权限控制
- 🌐 **纯服务端**：无需客户端安装，完全服务端运行

---

### 📋 命令与权限

| 命令 | 描述 | 权限节点 | 默认 OP 等级 |
|------|------|----------|--------------|
| `/playtime` | 查看排行榜 | 无（任何人可用） | - |
| `/playtime help` | 显示帮助信息 | 无 | - |
| `/playtime del <玩家名>` | 删除指定玩家的数据 | `playtime.delete` | 2 |
| `/playtime reload` | 重新加载数据文件 | `playtime.reload` | 2 |

> **说明**：如果未安装权限管理模组（如 LuckPerms），将自动回退到原版 OP 等级检查（`level 2` 即普通 OP）。  
> 控制台执行命令始终拥有最高权限。

---

### ⚙️ 配置文件

- **位置**：`config/playtimeboard/playtime.json`
- **格式**：JSON，以玩家 UUID 为键，存储玩家名称、在线时长（刻）和在线状态。

#### 示例
```json
{
  "00000000-0000-0000-0000-000000000001": {
    "name": "Steve",
    "playTicks": 72000,
    "online": false
  }
}
```
> `playTicks` 除以 20 得到秒数，除以 1200 得到分钟，除以 72000 得到小时。

---

### 📦 依赖

- **Minecraft**：`26.1.2`
- **Fabric Loader**：`>=0.19.0`
- **Fabric API**：`0.150.0+26.1.2`
- **SimpleFabricScoreboard**：`1.1.1`（已内嵌）
- **Fabric Permissions API**（可选）：`0.3.3` – 用于精细权限控制

> 构建时，`SimpleFabricScoreboard` 和 `fabric-permissions-api` 会自动打包进模组 JAR，无需额外下载。

---

### 🚀 安装指南

1. 确保你的服务器已安装 **Fabric Loader** 和 **Fabric API**（版本与上述一致）。
2. 将本模组 JAR 文件（`PlayTimeBoard-5.20.0.jar`）放入服务器的 `mods` 文件夹。
3. 启动服务器，模组会自动创建 `config/playtimeboard/playtime.json`。
4. （可选）安装 LuckPerms 等权限管理模组，以使用权限节点精细分配命令权限。
5. 进入游戏，OP 玩家可执行 `/playtime` 查看排行榜。

---

### 🛠️ 从源码构建

```bash
git clone <repository-url>
cd PlayTimeBoard
./gradlew clean build
```

构建成功后，生成的 JAR 文件位于 `build/libs/PlayTimeBoard-5.20.0.jar`。

---

## English

### 📖 Description

**PlayTimeBoard** is a **Fabric server-side mod** that tracks players' online time and displays a real-time leaderboard in the sidebar (Scoreboard) and the tab list.  
It can automatically exclude bots (players with "Bot" in their name) and provides admin commands for data management.

**Current Version: `5.20.0`**  
**Supported Minecraft Version: `26.1.2`**

---

### ✨ Features

- ✅ Automatically records each player's **online time** (in ticks – 20 ticks = 1 second)
- 🏆 Real-time **leaderboard in the sidebar** (top 10, configurable)
- 📋 **Tab list** shows player rank, name, and formatted time (visible when pressing Tab)
- 🤖 **Bot exclusion**: players with "Bot" in their name (case‑insensitive) are excluded from the leaderboard
- ⚙️ **Admin commands** (permission-based):
  - `/playtime del <player>` – delete a specific player's data
  - `/playtime reload` – reload the data file
- 🎨 **Coloured formatting** using Minecraft `§` codes – top 3 ranks have special colours
- 💾 **Auto‑save**: data is saved every 20 seconds and on server shutdown
- 🔌 **Permissions API support**: integrates with Fabric Permissions API for fine‑grained control (e.g., LuckPerms)
- 🌐 **Server‑side only**: no client installation required

---

### 📋 Commands & Permissions

| Command | Description | Permission Node | Default OP Level |
|---------|-------------|----------------|------------------|
| `/playtime` | View leaderboard | none (everyone) | - |
| `/playtime help` | Show help | none | - |
| `/playtime del <player>` | Delete a player's data | `playtime.delete` | 2 |
| `/playtime reload` | Reload data file | `playtime.reload` | 2 |

> **Note**: If no permission management mod (e.g., LuckPerms) is installed, the mod falls back to vanilla OP level checks (`level 2` = regular OP).  
> The console always has full permission.

---

### ⚙️ Configuration File

- **Path**: `config/playtimeboard/playtime.json`
- **Format**: JSON, keyed by player UUID, storing player name, play ticks, and online status.

#### Example
```json
{
  "00000000-0000-0000-0000-000000000001": {
    "name": "Steve",
    "playTicks": 72000,
    "online": false
  }
}
```
> `playTicks` divided by 20 gives seconds, by 1200 gives minutes, and by 72000 gives hours.

---

### 📦 Dependencies

- **Minecraft**: `26.1.2`
- **Fabric Loader**: `>=0.19.0`
- **Fabric API**: `0.150.0+26.1.2`
- **SimpleFabricScoreboard**: `1.1.1` (shaded)
- **Fabric Permissions API** (optional): `0.3.3` – for fine‑grained permission control

> During build, both `SimpleFabricScoreboard` and `fabric-permissions-api` are automatically bundled into the mod JAR.

---

### 🚀 Installation Guide

1. Make sure your server has **Fabric Loader** and **Fabric API** installed (matching the versions above).
2. Place the mod JAR (`PlayTimeBoard-5.20.0.jar`) into your server's `mods` folder.
3. Start the server – the config file will be created automatically at `config/playtimeboard/playtime.json`.
4. (Optional) Install a permission management mod like LuckPerms to use the permission nodes.
5. Join the game and run `/playtime` as an OP to see the leaderboard.

---

### 🛠️ Build from source

```bash
git clone <repository-url>
cd PlayTimeBoard
./gradlew clean build
```

The resulting JAR will be located at `build/libs/PlayTimeBoard-5.20.0.jar`.
