package com.ponchannn.repairtoolswithcrafting;

import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Listeners implements Listener {

    HashMap<Player, ItemStack> playerItemMap = new HashMap<>();
    HashMap<Player, Integer> playerExpMap = new HashMap<>();   // can all repair -> true

    public void registerListener(RepairToolsWithCrafting plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ItemStack[] prepareCraftItems = event.getInventory().getMatrix();
        Player player = (Player) event.getView().getPlayer();

        if (player.getTotalExperience() <= 0) return;

        // クラフトするアイテムがあるかチェック
        for (ItemStack craftItem : prepareCraftItems) {

            // 結果が修繕付きツールかどうかをチェック
            if (craftItem != null && !craftItem.isEmpty() && craftItem.containsEnchantment(Enchantment.MENDING)) {
                // 結果が修繕付きツールかどうかをチェック
                Damageable craftItemMeta = (Damageable) craftItem.getItemMeta();
                if (craftItemMeta.getDamage() <= 0 && player.getLevel() <= 0) return;
                int currentDamage = craftItemMeta.getDamage();
                int playerLevels = player.getLevel();
                if (playerLevels > 0) {
                    int expToRepair = Math.round((float) Math.log10(currentDamage) * 5);
                    if (expToRepair > playerLevels) {
                        return;
                    }
                    playerExpMap.put(player, 0);

                    craftItemMeta.setDamage(0);

                    ItemStack resultItem = craftItem.clone();
                    resultItem.setItemMeta(craftItemMeta);

                    // assign to map
                    playerItemMap.put(player, resultItem);
                    event.getInventory().setResult(resultItem);
                }
            }
        }
    }

    @EventHandler
    public void onItemCraft(InventoryClickEvent event) {
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;
        Player player = (Player) event.getView().getPlayer();
        ItemStack preparedCraftItems = playerItemMap.get(player);
        if (preparedCraftItems == null) return;
        ItemStack craftItems = event.getCurrentItem();
        if (!craftItems.equals(preparedCraftItems)) return;

        player.setExperienceLevelAndProgress(playerExpMap.get(player));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        playerItemMap.remove(player);
        playerExpMap.remove(player);
    }
}
