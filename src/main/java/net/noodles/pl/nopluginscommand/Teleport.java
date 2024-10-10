package net.noodles.pl.nopluginscommand;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class Teleport {
    static FileConfiguration config = net.noodles.pl.nopluginscommand.NoPluginsCommand.getProvidingPlugin(NoPluginsCommand.class).getConfig();
    static Server server = net.noodles.pl.nopluginscommand.NoPluginsCommand.getProvidingPlugin(NoPluginsCommand.class).getServer();

    // 私有方法，传送用户，并显示特效
    private static void tp(Player player,Location location,String msg) {
        player.teleport(location);
        player.sendMessage(Color.translate(msg));
        //player.playEffect(location, Effect.ENDER_SIGNAL, 1);
        player.playEffect(location, Effect.ENDER_SIGNAL, null);
    }

    // 检查玩家是否在禁止rtp的世界列表中
    private static boolean isInBlockWorlds(Player player) {
        List<String> blockWorlds = config.getStringList("rtp.block-worlds");
        for (String blockWorld : blockWorlds) {
            if (player.getWorld().getName().equalsIgnoreCase(blockWorld)){
                return true;
            }
        }
        return false;
    }

    // 判断制定block是否是一个不安全的方块
    private static boolean isUnsafeBlock(Material blockType, List<String> unsafeBlocks) {
        for (String unsafeBlock : unsafeBlocks) {
            if (blockType.name().equalsIgnoreCase(unsafeBlock)) {
                return true;
            }
        }
        return false;
    }

    public static void rtp(Player player) {
        if (!config.getBoolean("rtp.enable")) {     // 是否启用 rtp 功能
            player.sendMessage(Color.translate("&c随机传送不可用，请老老实实徒步旅行吧！"));
            return;
        }else if (isInBlockWorlds(player)) {       // 玩家处在禁止使用 rtp 的世界
            player.sendMessage(Color.translate("&c当前世界禁止使用随机传送！"));
            return;
        }

        int maxRange = config.getInt("rtp.max-range");
        List<String> unsafeBlocks = config.getStringList("rtp.unsafe-blocks");
        Random random = new Random();
        Location center = player.getWorld().getSpawnLocation();
        Location location;
        Material blockType;
        do {
            int x = center.getBlockX() + random.nextInt(maxRange * 2) - maxRange;
            int z = center.getBlockZ() + random.nextInt(maxRange * 2) - maxRange;
            int y = player.getWorld().getHighestBlockYAt(x, z) + 1;
            location = new Location(player.getWorld(), x, y, z);
            blockType = location.getBlock().getType();
        } while (isUnsafeBlock(blockType,unsafeBlocks));

        tp(player,location, "&2您已成功传送到未知区域: X: &c" + location.getBlockX() + " &2Y: &c" + location.getBlockY() + " &2Z: &c" + location.getBlockZ() + "&c请小心行事！");
    }

    //spawn回城指令，传送玩家到世界出生点
    public static void spawn(Player player) {
        if (config.getBoolean("spawn.enable")) {
            Location spawnPoint = server.getWorlds().get(0).getSpawnLocation();
            tp(player, spawnPoint, "&d一道神秘的力量将您送回到了服务器出生点");
        }
    }

    //home指令回玩家出生点，床的位置
    public static void home(Player player) {
        if (config.getBoolean("home.enable")) {
            Location homePoint;
            String msg;
            if ( player.getBedSpawnLocation() != null) {
                homePoint = player.getBedSpawnLocation();
                msg = "&d在圣光的护佑下让你安全返回了温暖的家";
            } else {
                homePoint = server.getWorlds().get(0).getSpawnLocation();
                msg = "&d由于在这个世界中没有找到你的家/床，只能将你送回到服务器出生点";
            }
            tp(player, homePoint, msg);
        }
    }
}


