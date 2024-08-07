package com.ponchannn.repairtoolswithcrafting;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class RepairToolsWithCrafting extends JavaPlugin {
    Map<Player, ItemStack> playerItemMap = new HashMap<>();
    Map<Player, Integer> playerExpMap = new HashMap<>(); // last exp value

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new Listeners(playerItemMap, playerExpMap), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
