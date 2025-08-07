package ru.trident.elevatedEnchants.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.trident.elevatedEnchants.TElevatedEnchants;

public class ElevateEnchantmentCommand implements CommandExecutor
{

    private final TElevatedEnchants plugin;

    public ElevateEnchantmentCommand(TElevatedEnchants plugin)
    {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        this.plugin.reloadConfig();
        this.plugin.loadConfig();

        sender.sendMessage("config successfully reload!");

        return true;
    }
}
