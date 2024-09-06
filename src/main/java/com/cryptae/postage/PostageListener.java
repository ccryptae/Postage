package com.cryptae.postage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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

        boolean isPlaceholder = isPlaceholderItem(clickedItem);

        if (title.startsWith("Send Items to ") || title.equals("Your Deliveries")) {
            if (isPlaceholder) {
                if (clickedItem.getType() == Material.GREEN_CONCRETE && "Send Items".equals(clickedItem.getItemMeta().getDisplayName())) {
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
                } else if (clickedItem.getType() == Material.RED_CONCRETE && "Cancel".equals(clickedItem.getItemMeta().getDisplayName())) {
                    event.setCancelled(true); // Prevent further interaction
                    postageCommand.restorePlayerItems(player);
                    player.closeInventory();
                } else if (clickedItem.getType() == Material.RED_CONCRETE && "Close".equals(clickedItem.getItemMeta().getDisplayName())) {
                    event.setCancelled(true); // Prevent further interaction
                    player.closeInventory();
                }
                if (isPlaceholder) {
                    event.setCancelled(true); // Prevent interaction with placeholders
                }
            }
            return;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() == null || !(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();
        if (title.startsWith("Send Items to ")) {
            // Restore player items if GUI was closed without pressing a button
            postageCommand.restorePlayerItems(player);
        }
    }

    private boolean isPlaceholderItem(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return item.getType() == Material.GRAY_STAINED_GLASS_PANE ||
                (item.getType() == Material.GREEN_CONCRETE && "Send Items".equals(meta.getDisplayName())) ||
                (item.getType() == Material.RED_CONCRETE && ("Cancel".equals(meta.getDisplayName()) || "Close".equals(meta.getDisplayName())));
    }
}