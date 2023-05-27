package net.noodles.pl.nopluginscommand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class Teleport {
    static FileConfiguration config = net.noodles.pl.nopluginscommand.NoPluginsCommand.getProvidingPlugin(NoPluginsCommand.class).getConfig();
    public static void rtp(Player player) {

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
            if (blockType == Material.getMaterial(unsafeBlock)) {
            //if ( blockType.toString().equalsIgnoreCase("Material." + unsafeBlock)) {
                x = center.getBlockX() + random.nextInt(maxRange * 2) - maxRange;
                z = center.getBlockZ() + random.nextInt(maxRange * 2) - maxRange;
                y = player.getWorld().getHighestBlockYAt(x, z) + 1;
                location = new Location(player.getWorld(), x, y, z);
                blockType = location.getBlock().getType();
            }
        }
        player.teleport(location);
        player.sendMessage(Color.translate("&2您已成功传送到随机坐标: X: &c" + x + " &2Y: &c" + y + " &2Z: &c" + z));
        player.sendMessage(Color.translate("&2脚下方块: &c" + blockType.toString()));
    }
}
