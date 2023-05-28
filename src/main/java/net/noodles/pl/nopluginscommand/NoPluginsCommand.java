package net.noodles.pl.nopluginscommand;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public final class NoPluginsCommand extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        updateCheck(Bukkit.getConsoleSender());
    }

    public void updateCheck(CommandSender sender) {
        try {
            String urlString = "https://updatecheck.bghddevelopment.com";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuffer response = new StringBuffer();
            while ((input = reader.readLine()) != null) {
                response.append(input);
            }
            reader.close();
            JsonObject object = new JsonParser().parse(response.toString()).getAsJsonObject();

            if (object.has("plugins")) {
                JsonObject plugins = object.get("plugins").getAsJsonObject();
                JsonObject info = plugins.get("NoPluginCommand").getAsJsonObject();
                String version = info.get("version").getAsString();
                if (version.equals(getDescription().getVersion())) {
                    sender.sendMessage(Color.translate("&aNoPluginCommand is on the latest version."));
                } else {
                    sender.sendMessage(Color.translate(""));
                    sender.sendMessage(Color.translate(""));
                    sender.sendMessage(Color.translate("&cYour NoPluginCommand version is out of date!"));
                    sender.sendMessage(Color.translate("&cWe recommend updating ASAP!"));
                    sender.sendMessage(Color.translate(""));
                    sender.sendMessage(Color.translate("&cYour Version: &e" + getDescription().getVersion()));
                    sender.sendMessage(Color.translate("&aNewest Version: &e" + version));
                    sender.sendMessage(Color.translate(""));
                    sender.sendMessage(Color.translate(""));
                }
            } else {
                sender.sendMessage(Color.translate("&cWrong response from update API, contact plugin developer!"));
            }
        } catch (
                Exception ex) {
            sender.sendMessage(Color.translate("&cFailed to get updater check. (" + ex.getMessage() + ")"));
        }
    }

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        List<String> commands = getConfig().getStringList("blockCommand.commands");
        String[] arrCommand = event.getMessage().toLowerCase().split(" ", 2);
        //List<String> commands = Arrays.asList("?", "pl", "about", "version", "ver", "plugins", "bukkit:?", "bukkit:pl", "bukkit:about", "bukkit:version", "bukkit:ver", "bukkit:plugins", "minecraft:pl", "minecraft:plugins", "minecraft:about", "minecraft:version", "minecraft:ver");
        commands.forEach(all -> {
         //屏蔽指令逻辑
         if (arrCommand[0].equalsIgnoreCase("/" + all.toLowerCase())) {
             //处理配置文件中的 bypassOS 开关
             event.setCancelled(!getConfig().getBoolean("blockCommand.bypassOP") || !event.getPlayer().isOp());
             String msg = getConfig().getString("blockCommand.blkMsg");
             if (!msg.isEmpty()){
                 event.getPlayer().sendMessage(Color.translate(msg));
             }
         }
        });

        //rtp -> enable，处理rtp 指令
        if (arrCommand[0].equalsIgnoreCase("/rtp")) {
            Teleport.rtp(event.getPlayer());
        }
        //spawn 处理回城指令
        if (arrCommand[0].equalsIgnoreCase("/spawn")) {
            Teleport.spawn(event.getPlayer());
        }
        //home 处理返回重生点指令
        if (arrCommand[0].equalsIgnoreCase("/home")) {
            Teleport.home(event.getPlayer());
        }
    }

}