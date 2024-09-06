package com.cryptae.postage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DeliveryStorage {
    private final File deliveryFile;
    private final FileConfiguration deliveryConfig;

    public DeliveryStorage(JavaPlugin plugin) {
        this.deliveryFile = new File(plugin.getDataFolder(), "deliveries.yml");

        if (!deliveryFile.exists()) {
            plugin.saveResource("deliveries.yml", false); // Save default file if it doesn't exist
        }

        this.deliveryConfig = YamlConfiguration.loadConfiguration(deliveryFile);
    }

    public void saveDelivery(UUID recipientUUID, Map<Integer, ItemStack> items) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(deliveryFile);
        String path = "deliveries." + recipientUUID.toString();
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            config.set(path + "." + entry.getKey(), entry.getValue());
        }
        saveConfig();
    }

    public Map<Integer, ItemStack> loadDeliveries(UUID recipientUUID) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(deliveryFile);
        Map<Integer, ItemStack> items = new HashMap<>();
        String path = "deliveries." + recipientUUID.toString();
        if (config.contains(path)) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection(path)).getKeys(false)) {
                ItemStack item = config.getItemStack(path + "." + key);
                items.put(Integer.parseInt(key), item);
            }
        }
        return items;
    }

    public void removeDelivery(UUID recipientUUID) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(deliveryFile);
        String path = "deliveries." + recipientUUID.toString();
        config.set(path, null);
        saveConfig();
    }

    private void saveConfig() {
        try {
            deliveryConfig.save(deliveryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}