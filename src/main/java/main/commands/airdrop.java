package main.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class airdrop implements CommandExecutor {

    private final Plugin plugin;
    public airdrop(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Использование: /airdrop <subcommand> [args...]");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Использование: /airdrop create <имя> [шанс]");
                    return false;
                }
                String name = args[1];
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    Block block = player.getTargetBlockExact(5);
                    if (block != null && block.getType() == Material.CHEST) {
                        // получаем конфигурацию
                        FileConfiguration config = plugin.getConfig();

                        // добавляем новую секцию с именем
                        ConfigurationSection section = config.createSection(name);

                        // добавляем предметы из сундука в секцию
                        Chest chest = (Chest) block.getState();
                        Inventory inventory = chest.getInventory();
                        int chance = 50; // значение по умолчанию
                        if (args.length == 3) {
                            chance = Integer.parseInt(args[2]);
                        }
                        List<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));
                        List<Map<String, Object>> itemList = new ArrayList<>();
                        for (ItemStack item : items) {
                            if (item != null) {
                                Map<String, Object> itemMap = item.serialize();
                                itemMap.put("chance", chance);
                                itemList.add(itemMap);
                            }
                        }
                        section.set("items", itemList);

                        // сохраняем конфигурацию
                        try {
                            config.save(new File(plugin.getDataFolder(), "airdrops.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        sender.sendMessage(ChatColor.GREEN + "Успешно добавлен аиродроп с именем " + name);
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "Ты должен смотреть на сундук, чтобы добавить аиродроп");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "Эта команда может быть выполнена только игроком");
                return true;

            case "reload":
                // Перезагрузка конфигураций
                plugin.reloadConfig();
                File airdropFile = new File(plugin.getDataFolder(), "airdrops.yml");
                if (!airdropFile.exists()) {
                    plugin.saveResource("airdrops.yml", false);
                }
                File configFile = new File(plugin.getDataFolder(), "config.yml");
                if (!configFile.exists()) {
                    plugin.saveDefaultConfig();
                }
                sender.sendMessage(ChatColor.GREEN + "Файлы конфигурации успешно перезагружены");
                return true;

            default:
                // недопустимый аргумент команды
                sender.sendMessage(ChatColor.RED + "Неизвестная подкоманда");
                return false;
        }
    }
}

