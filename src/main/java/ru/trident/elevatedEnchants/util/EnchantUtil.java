package ru.trident.elevatedEnchants.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class EnchantUtil
{
    public Map<Enchantment, Integer> getAllEnchants(ItemStack item)
    {
        // метод получения всех чар на предмете в Map.

        final Map<Enchantment, Integer> enchants = new HashMap<>();
        if (item == null) return enchants;

        if (item.getItemMeta() instanceof EnchantmentStorageMeta meta) {
            enchants.putAll(meta.getStoredEnchants());
        } else {
            final ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                enchants.putAll(meta.getEnchants());
            }
        }
        return enchants;
    }

    public void applyEnchant(ItemStack item, Enchantment ench, int level) {

        // Применение чар

        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            if (storageMeta.hasStoredEnchant(ench)) {
                storageMeta.removeStoredEnchant(ench);
            }
            storageMeta.addStoredEnchant(ench, level, true);
        } else {
            if (meta.hasEnchant(ench)) {
                meta.removeEnchant(ench);
            }
            meta.addEnchant(ench, level, true);
        }
        item.setItemMeta(meta);
    }
}
