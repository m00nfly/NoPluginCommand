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
    private static void tp(Player player,Location location,String msg) {
        player.teleport(location);
        player.sendMessage(Color.translate(msg));
        //player.playEffect(location, Effect.ENDER_SIGNAL, 1);
        player.playEffect(location, Effect.ENDER_SIGNAL, null);
    }
    public static void rtp(Player player) {
        if (!config.getBoolean("rtp.enable")) {
            return;
        }
        int maxRange = config.getInt("rtp.max-range");
        List<String> unsafeBlocks = config.getStringList("rtp.unsafe-blocks");
        Random random = new Random();
        Location center = player.getWorld().getSpawnLocation();
        int x = (center.getBlockX() + random.nextInt(maxRange * 2)) - maxRange;
        int z = (center.getBlockZ() + random.nextInt(maxRange * 2)) - maxRange;
        int y = player.getWorld().getHighestBlockYAt(x, z) + 1;
        Location location = new Location(player.getWorld(), x, y, z);
        Material blockType = location.getBlock().getType();
        //while (blockType == Material.WATER || blockType == Material.LAVA) {
        for (String unsafeBlock : unsafeBlocks) {
            if (!blockType.isSolid() && blockType.toString().equalsIgnoreCase(unsafeBlock)) {
            //if ( blockType.toString().equalsIgnoreCase("Material." + unsafeBlock)) {
                x = center.getBlockX() + random.nextInt(maxRange * 2) - maxRange;
                z = center.getBlockZ() + random.nextInt(maxRange * 2) - maxRange;
                y = player.getWorld().getHighestBlockYAt(x, z) + 1;
                location = new Location(player.getWorld(), x, y, z);
                blockType = location.getBlock().getType();
            }
        }
        tp(player,location, "&2您已成功传送到未知区域: X: &c" + x + " &2Y: &c" + y + " &2Z: &c" + z + "&c请小心行事！");
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


