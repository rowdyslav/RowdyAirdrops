package main;

import main.commands.airdrop;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class RowdyAirdrops extends JavaPlugin {
    public void onEnable() {
        // создаем папку с конфигами, если она не существует
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        // создаем airdrops.yml, если не существует
        File airdropsFile = new File(getDataFolder(), "airdrops.yml");
        if (!airdropsFile.exists()) {
            saveResource("airdrops.yml", false);
        }
        // создаем config.yml (главный конфиг), если не существует
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        // регистрируем команды
        getCommand("airdrop").setExecutor(new airdrop(this));
    }
}
