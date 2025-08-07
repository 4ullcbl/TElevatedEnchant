package ru.trident.elevatedEnchants.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.trident.elevatedEnchants.TElevatedEnchants;
import ru.trident.elevatedEnchants.util.EnchantUtil;

import java.util.Map;

public class PrepareAnvil implements Listener
{

    private final TElevatedEnchants plugin;
    private final EnchantUtil enchantUtil;

    public PrepareAnvil(TElevatedEnchants plugin, EnchantUtil enchantUtil)
    {
        this.plugin = plugin;
        this.enchantUtil = enchantUtil;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepare(PrepareAnvilEvent event)
    {
        final ItemStack leftItem = event.getInventory().getItem(0);
        final ItemStack rightItem = event.getInventory().getItem(1);

        if (leftItem == null || rightItem == null)
            return;

        if (!rightItem.getType().equals(Material.ENCHANTED_BOOK) && !leftItem.getType().equals(rightItem.getType()))
            return;

        final ItemStack result = leftItem.clone();
        final ItemMeta meta = result.getItemMeta();

        if (meta == null)
            return;

        for (Enchantment ench : result.getEnchantments().keySet()) {
            meta.removeEnchant(ench);
        }

        result.setItemMeta(meta);

        final Map<Enchantment, Integer> leftEnchants = this.enchantUtil.getAllEnchants(leftItem);
        final Map<Enchantment, Integer> rightEnchants = this.enchantUtil.getAllEnchants(rightItem);

        int extraCost = 0;
        boolean modified = false;

        for (Map.Entry<Enchantment, Integer> entry : leftEnchants.entrySet()) {
            final Enchantment ench = entry.getKey();
            final int leftLevel = entry.getValue();
            final int rightLevel = rightEnchants.getOrDefault(ench, 0);

            final int maxAllowed = this.plugin.getMaxLevels().getOrDefault(ench, ench.getMaxLevel());
            int newLevel = leftLevel;

            if (rightLevel > 0) {
                if (leftLevel == rightLevel) {
                    newLevel = Math.min(leftLevel + 1, maxAllowed);
                } else {
                    newLevel = Math.min(Math.max(leftLevel, rightLevel), maxAllowed);
                }
            }

            this.enchantUtil.applyEnchant(result, ench, newLevel);
            if (newLevel > leftLevel) {
                extraCost += (newLevel * 2);
                modified = true;
            }
        }

        for (Map.Entry<Enchantment, Integer> entry : rightEnchants.entrySet()) {
            Enchantment ench = entry.getKey();
            if (leftEnchants.containsKey(ench)) continue;

            int rightLevel = entry.getValue();
            int maxAllowed = this.plugin.getMaxLevels().getOrDefault(ench, ench.getMaxLevel());
            int newLevel = Math.min(rightLevel, maxAllowed);

            this.enchantUtil.applyEnchant(result, ench, newLevel);
            extraCost += (newLevel * 2);
            modified = true;
        }

        if (modified) {
            event.setResult(result);

            if (this.plugin.isEnableScaling()) {
                int newRepairCost = Math.min(event.getInventory().getRepairCost() + extraCost, 39);
                event.getInventory().setRepairCost(newRepairCost);
            }

            if (!event.getViewers().isEmpty() && event.getViewers().get(0) instanceof Player) {
                final Player player = (Player) event.getViewers().get(0);
                Bukkit.getScheduler().runTaskLater(this.plugin, player::updateInventory, 1L);
            }
        }
    }
}
