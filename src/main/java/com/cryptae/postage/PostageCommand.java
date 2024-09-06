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
    private final Map<Player, Map<Integer, ItemStack>> playerOriginalItems = new HashMap<>();

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

            // Save player's current inventory
            Map<Integer, ItemStack> originalItems = new HashMap<>();
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    originalItems.put(i, item.clone());
                }
            }
            playerOriginalItems.put(player, originalItems);

            Inventory sendingGui = GuiManager.createSendingInventory(recipientName);
            player.openInventory(sendingGui);
            return true;
        }
        return false;
    }

    public void restorePlayerItems(Player player) {
        Map<Integer, ItemStack> originalItems = playerOriginalItems.remove(player);
        if (originalItems != null) {
            for (Map.Entry<Integer, ItemStack> entry : originalItems.entrySet()) {
                player.getInventory().setItem(entry.getKey(), entry.getValue());
            }
        }
    }
}