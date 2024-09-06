package com.cryptae.postage;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final DeliveryStorage deliveryStorage;

    public PlayerJoinListener(JavaPlugin plugin) {
        this.deliveryStorage = new DeliveryStorage(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Load items sent to the player
        Map<Integer, ItemStack> itemsToDeliver = deliveryStorage.loadDeliveries(playerUUID);

        if (!itemsToDeliver.isEmpty()) {
            // Add items to the player's inventory
            for (ItemStack item : itemsToDeliver.values()) {
                player.getInventory().addItem(item);
            }

            // Notify the player
            player.sendMessage("You have received items from your mailbox!");

            // Remove items from the delivery storage after delivery
            deliveryStorage.removeDelivery(playerUUID);
        }
    }
}