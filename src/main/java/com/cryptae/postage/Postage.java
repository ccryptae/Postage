package com.cryptae.postage;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Postage extends JavaPlugin {
    @Override
    public void onEnable() {
        DeliveryStorage deliveryStorage = new DeliveryStorage(this);
        PostageCommand postageCommand = new PostageCommand(this);
        DeliveryCommand deliveryCommand = new DeliveryCommand(this);

        Objects.requireNonNull(getCommand("postage")).setExecutor(postageCommand);
        Objects.requireNonNull(getCommand("delivery")).setExecutor(deliveryCommand);

        // Register the listener
        getServer().getPluginManager().registerEvents(new PostageListener(this), this);
    }

    @Override
    public void onDisable() {
        // Handle any cleanup if needed
    }
}