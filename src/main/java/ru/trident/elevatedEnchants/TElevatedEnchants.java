package ru.trident.elevatedEnchants;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import ru.trident.elevatedEnchants.command.ElevateEnchantmentCommand;
import ru.trident.elevatedEnchants.listener.PrepareAnvil;
import ru.trident.elevatedEnchants.util.EnchantUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class TElevatedEnchants extends JavaPlugin
{
    private final Map<Enchantment, Integer> maxLevels = new HashMap<>();
    private boolean enableScaling = false;
    private int maxScal = 39;
    private EnchantUtil enchantUtil;
    private long startTime;

    @Override
    public void onLoad()
    {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();
        this.loadConfig();

        enchantUtil = new EnchantUtil();

        this.getServer().getPluginManager().registerEvents(new PrepareAnvil(this, this.enchantUtil), this);
        this.getCommand("elevate-enchant").setExecutor(new ElevateEnchantmentCommand(this));

        this.getLogger().info("started on: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Override
    public void onDisable()
    {
        enchantUtil = null;
    }

    public void loadConfig() {
        maxLevels.clear();

        final FileConfiguration config = this.getConfig();
        enableScaling = config.getBoolean("enable_scaling", false);
        maxScal = config.getInt("max_scaling");

        if (!config.contains("enchantments")) return;

        final Set<String> enchantKeys = config.getConfigurationSection("enchantments")
                .getKeys(false);

        for (String enchantKey: enchantKeys) {
            final int maxLvl = config.getInt("enchantments." + enchantKey);

            final Enchantment ench = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(enchantKey.toLowerCase()));

            if (ench == null) continue;

            maxLevels.put(ench, maxLvl);
            this.getLogger().info("register " + ench.getKey());
        }
    }

    public Map<Enchantment, Integer> getMaxLevels()
    {
        return maxLevels;
    }

    public boolean isEnableScaling()
    {
        return enableScaling;
    }

    public int getMaxScal()
    {
        return maxScal;
    }
}
