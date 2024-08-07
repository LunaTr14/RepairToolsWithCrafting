package com.ponchannn.repairtoolswithcrafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Listeners implements Listener {

    Map<Player, ItemStack> playerItemMap;
    Map<Player, Integer> playerExpMap;   // can all repair -> true

    public void registerListener(RepairToolsWithCrafting plugin){
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ItemStack[] prepareCraftItems = event.getInventory().getMatrix();
        Player player = (Player) event.getView().getPlayer();

        if (!isN(prepareCraftItems, 0)) playerItemMap.remove(player);

        // 一つだけ
        if (!isN(prepareCraftItems, 1)) return;

        // クラフトするアイテムがあるかチェック

        for (ItemStack craftItem : prepareCraftItems) {

            // 結果が修繕付きツールかどうかをチェック
            if (craftItem != null && !craftItem.isEmpty() && craftItem.containsEnchantment(Enchantment.MENDING)) {
                Damageable craftItemMeta =(Damageable) craftItem.getItemMeta();
                int currentDamage = craftItemMeta.getDamage();
                Material itemType = craftItem.getType();
                int maxDurability = itemType.getMaxDurability();

                if (currentDamage == 0) return;

                int expLevels = player.calculateTotalExperiencePoints();
                if (expLevels > 0) {
                    // 修繕付きアイテムの耐久値を修復
                    int canRepairAmount = expLevels * 2;

                    if (currentDamage <= canRepairAmount) {
                        playerExpMap.put(player, expLevels - currentDamage / 2);
                        craftItemMeta.setDamage(0);
                    } else {
                        playerExpMap.put(player, 0);
                        craftItemMeta.setDamage(currentDamage - canRepairAmount);
                    }

                    ItemStack resultItem = craftItem.clone();
                    resultItem.setItemMeta(craftItemMeta);

                    // assign to map
                    playerItemMap.put(player, resultItem);

                    event.getInventory().setResult(resultItem);
                    break;
                }
            }
        }
    }

    private boolean isN (ItemStack[] itemStacks, int n) {
        int iSLen = itemStacks.length;
        int nullLen = 0;

        for (ItemStack itemStack : itemStacks) {
            if (itemStack == null) nullLen++;
            continue;
        }
        if (nullLen == iSLen - n) return true;
        else return false;
    }

    @EventHandler
    public void onItemCraft (InventoryClickEvent event) {
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
