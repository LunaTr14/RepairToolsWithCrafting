package com.ponchannn.repairtoolswithcrafting;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class RepairToolsWithCrafting extends JavaPlugin {
    private static Listeners eventHandlers = null;

    @Override
    public void onEnable() {
        eventHandlers = new Listeners();
        eventHandlers.registerListener(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
