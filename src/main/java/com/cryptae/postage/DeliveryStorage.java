package com.cryptae.postage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeliveryStorage {
    private final File deliveryFile;
    private final FileConfiguration deliveryConfig;

    public DeliveryStorage(JavaPlugin plugin) {
        deliveryFile = new File(plugin.getDataFolder(), "deliveries.yml");
        if (!deliveryFile.exists()) {
            try {
                deliveryFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        deliveryConfig = YamlConfiguration.loadConfiguration(deliveryFile);
    }

    public void saveDelivery(UUID recipientUUID, Map<Integer, ItemStack> items) {
        String path = "deliveries." + recipientUUID.toString();
        deliveryConfig.set(path, null); // Clear old data

        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            deliveryConfig.set(path + "." + entry.getKey(), entry.getValue());
        }

        saveConfig();
    }

    public Map<Integer, ItemStack> loadDelivery(UUID recipientUUID) {
        String path = "deliveries." + recipientUUID.toString();
        Map<Integer, ItemStack> items = new HashMap<>();

        if (deliveryConfig.contains(path)) {
            for (String key : deliveryConfig.getConfigurationSection(path).getKeys(false)) {
                ItemStack item = deliveryConfig.getItemStack(path + "." + key);
                items.put(Integer.parseInt(key), item);
            }
        }

        return items;
    }

    public void removeDelivery(UUID recipientUUID) {
        deliveryConfig.set("deliveries." + recipientUUID.toString(), null);
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