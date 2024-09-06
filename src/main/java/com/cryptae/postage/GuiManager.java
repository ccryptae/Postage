package com.cryptae.postage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;

import java.util.Map;

public class GuiManager {
    public static Inventory createSendingInventory(String recipientName) {
        Inventory sendingGui = Bukkit.createInventory(null, 54, "Send Items to " + recipientName);

        ItemStack sendButton = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta sendMeta = sendButton.getItemMeta();
        if (sendMeta != null) {
            sendMeta.setDisplayName("Send Items");
            sendButton.setItemMeta(sendMeta);
        }
        sendingGui.setItem(49, sendButton);

        ItemStack cancelButton = new ItemStack(Material.RED_CONCRETE);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        if (cancelMeta != null) {
            cancelMeta.setDisplayName("Cancel");
            cancelButton.setItemMeta(cancelMeta);
        }
        sendingGui.setItem(53, cancelButton);

        return sendingGui;
    }

    public static Inventory createDeliveryInventory(Player player, DeliveryStorage deliveryStorage) {
        Inventory deliveryGui = Bukkit.createInventory(null, 54, "Your Deliveries");

        Map<Integer, ItemStack> itemsToDeliver = deliveryStorage.loadDeliveries(player.getUniqueId());
        for (Map.Entry<Integer, ItemStack> entry : itemsToDeliver.entrySet()) {
            deliveryGui.setItem(entry.getKey(), entry.getValue());
        }

        ItemStack closeButton = new ItemStack(Material.RED_CONCRETE);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName("Close");
            closeButton.setItemMeta(closeMeta);
        }
        deliveryGui.setItem(53, closeButton);

        return deliveryGui;
    }
}