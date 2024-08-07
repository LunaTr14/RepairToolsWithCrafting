package com.ponchannn.repairtoolswithcrafting;
import org.bukkit.plugin.java.JavaPlugin;

public final class RepairToolsWithCrafting extends JavaPlugin {
    private Listeners eventHandlers = null;

    @Override
    public void onEnable() {
        eventHandlers = new Listeners();
        eventHandlers.registerListener(this);
    }

    @Override
    public void onDisable() {
        this.eventHandlers = null;
        // Plugin shutdown logic
    }
}