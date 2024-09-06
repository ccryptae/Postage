package com.cryptae.postage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PostageCommand implements CommandExecutor {
    private final DeliveryStorage deliveryStorage;
    private final Map<Player, Map<Integer, ItemStack>> playerOriginalInventory = new HashMap<>();

    public PostageCommand(JavaPlugin plugin) {
        this.deliveryStorage = new DeliveryStorage(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.sendMessage("Usage: /postage <recipient>");
                return false;
            }

            String recipientName = args[0];
            OfflinePlayer recipient = Bukkit.getOfflinePlayer(recipientName);

            if (!recipient.hasPlayedBefore()) {
                player.sendMessage("Recipient not found.");
                return false;
            }

            // Track the original inventory
            Map<Integer, ItemStack> originalInventory = new HashMap<>();
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    originalInventory.put(i, item.clone());
                }
            }
            playerOriginalInventory.put(player, originalInventory);

            Inventory sendingGui = GuiManager.createSendingInventory(recipientName);
            player.openInventory(sendingGui);
            return true;
        }
        return false;
    }

    public void restorePlayerItems(Player player) {
        // Only restore items if the player has original inventory saved
        if (playerOriginalInventory.containsKey(player)) {
            Map<Integer, ItemStack> originalInventory = playerOriginalInventory.remove(player);
            // Clear the player's current inventory first
            player.getInventory().clear();
            // Restore original inventory items
            for (Map.Entry<Integer, ItemStack> entry : originalInventory.entrySet()) {
                player.getInventory().setItem(entry.getKey(), entry.getValue());
            }
        }
    }
}