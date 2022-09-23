package io.github.sawors.hitman.commands;

import io.github.sawors.hitman.game.sniper.items.SniperRifle;
import io.github.sawors.hitman.game.sniper.items.SniperSpyglass;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class GiveItem implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player p && args.length >= 1){
            String itemname = args[0];
            new ItemStack(Material.STICK);
            ItemStack item = switch (itemname.toLowerCase(Locale.ROOT)) {
                case "sniper" -> new SniperRifle().get();
                case "spyglass" -> new SniperSpyglass().get();
                default -> new ItemStack(Material.STICK);
            };
    
            p.getInventory().addItem(item);
            return true;
        }
        return false;
    }
}
