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
            String urlString = "https://mc.moonfly.net/plugins/checkversion.php";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "MC-Plugins/5.0");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuilder response = new StringBuilder();
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
                    sender.sendMessage(Color.translate("&c============== NoPluginCommand ==============="));
                    sender.sendMessage(Color.translate("&c您的 NoPluginCommand 插件版本可能已过期！"));
                    sender.sendMessage(Color.translate("&c建议及时更新最新版本！"));
                    sender.sendMessage(Color.translate(""));
                    sender.sendMessage(Color.translate("&c当前版本: &e" + getDescription().getVersion()));
                    sender.sendMessage(Color.translate("&a在线版本: &e" + version));
                    sender.sendMessage(Color.translate("&c==============================================="));
                }
            } else {
                sender.sendMessage(Color.translate("&c版本更新接口响应数据异常！请联系开发者或手动检查版本更新！"));
            }
        } catch (
                Exception ex) {
            sender.sendMessage(Color.translate("&c版本更新 API 检查失败: (" + ex.getMessage() + ")"));
        }
    }

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        List<String> commands = getConfig().getStringList("blockCommand.commands");
        String[] arrCommand = event.getMessage().toLowerCase().split(" ", 2);
        commands.forEach(all -> {
            //屏蔽指令逻辑
            if (arrCommand[0].equalsIgnoreCase("/" + all.toLowerCase())) {
                //处理配置文件中的 bypassOP 开关
                if (!getConfig().getBoolean("blockCommand.bypassOP") || !event.getPlayer().isOp()) {
                    event.setCancelled(true);
                    String msg = getConfig().getString("blockCommand.blkMsg");
                    if (!msg.isEmpty()) {
                        event.getPlayer().sendMessage(Color.translate(msg));
                    }
                }
            }
        });

        switch (arrCommand[0]) {
            case "/rtp":          //rtp -> enable，处理rtp 指令
                Teleport.rtp(event.getPlayer());
                event.setCancelled(true);
                break;
            case "/spawn":        //spawn 处理回城指令
                Teleport.spawn(event.getPlayer());
                event.setCancelled(true);
                break;
            case "/home":        //home 处理返回重生点指令
                Teleport.home(event.getPlayer());
                event.setCancelled(true);
                break;
            default:
                break;
        }
    }
}