package com.cryptae.postage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PostageListener implements Listener {
    private final DeliveryStorage deliveryStorage;
    private final PostageCommand postageCommand;

    public PostageListener(JavaPlugin plugin) {
        this.deliveryStorage = new DeliveryStorage(plugin);
        this.postageCommand = (PostageCommand) plugin.getCommand("postage").getExecutor();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        String title = event.getView().getTitle();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        // Check if the clicked item is a placeholder or button
        if (isPlaceholderItem(clickedItem)) {
            event.setCancelled(true); // Prevent interaction with placeholders and buttons
            return;
        }

        if (title.startsWith("Send Items to ")) {
            // Allow moving items to the inventory if it's not a placeholder
            if (event.getRawSlot() >= 0 && event.getRawSlot() < 45) {
                // Allow moving items in the GUI slots
                return;
            }

            // Handle the button actions
            if (clickedItem.getType() == Material.GREEN_CONCRETE && clickedItem.getItemMeta().getDisplayName().equals("Send Items")) {
                event.setCancelled(true); // Prevent further interaction
                // Collect items to send
                Map<Integer, ItemStack> items = new HashMap<>();
                for (int i = 0; i < clickedInventory.getSize(); i++) {
                    ItemStack item = clickedInventory.getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        items.put(i, item);
                    }
                }

                String recipientName = title.substring("Send Items to ".length());
                OfflinePlayer recipient = Bukkit.getOfflinePlayer(recipientName);

                deliveryStorage.saveDelivery(recipient.getUniqueId(), items);
                player.sendMessage("Items sent successfully!");
                player.closeInventory();
            } else if (clickedItem.getType() == Material.RED_CONCRETE && clickedItem.getItemMeta().getDisplayName().equals("Cancel")) {
                event.setCancelled(true); // Prevent further interaction
                // Restore player items and close inventory
                postageCommand.restorePlayerItems(player);
                player.closeInventory();
            }
            event.setCancelled(true); // Prevent further interaction
        } else if (title.equals("Your Deliveries")) {
            if (clickedItem.getType() == Material.RED_CONCRETE && clickedItem.getItemMeta().getDisplayName().equals("Close")) {
                event.setCancelled(true); // Prevent further interaction
                player.closeInventory();
            }
            event.setCancelled(true); // Prevent further interaction
        }
    }

    private boolean isPlaceholderItem(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        // Check for placeholder types
        return item.getType() == Material.GRAY_STAINED_GLASS_PANE ||
                item.getType() == Material.GREEN_CONCRETE ||
                item.getType() == Material.RED_CONCRETE;
    }
}
