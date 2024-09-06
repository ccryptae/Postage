package com.cryptae.postage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DeliveryCommand implements CommandExecutor {
    private final DeliveryStorage deliveryStorage;

    public DeliveryCommand(JavaPlugin plugin) {
        this.deliveryStorage = new DeliveryStorage(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            Inventory deliveryGui = GuiManager.createDeliveryInventory(player, deliveryStorage);
            player.openInventory(deliveryGui);
            return true;
        }
        return false;
    }
}