package io.github.sawors.hitman.game.sniper.items;

import io.github.sawors.hitman.Hitman;
import io.github.sawors.hitman.game.sniper.HitmanItem;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.List;

public class SniperRifle extends HitmanItem {
    
    public SniperRifle(){
        setMaterial(Material.CROSSBOW);
        setName(Component.text(ChatColor.GOLD+"Sniper Rifle"));
        List<Component> templore = List.of(
                Component.text(ChatColor.GRAY+"Use this weapon to kill your target"),
                Component.text(ChatColor.YELLOW+"Sneak"+ChatColor.GRAY+" to "+ChatColor.BOLD+"zoom"+ChatColor.GRAY+", "+ChatColor.YELLOW+"Right-Click"+ChatColor.GRAY+" to "+ChatColor.BOLD+"shoot")
        );
        setLore(templore);
    }
    
    public SniperRifle(String variant, String name){
        setMaterial(Material.CROSSBOW);
        setName(Component.text(ChatColor.GOLD+name));
        setVariant(variant);
        List<Component> templore = List.of(
                Component.text(ChatColor.GRAY+"Use this weapon to kill your target"),
                Component.text(ChatColor.YELLOW+"Sneak"+ChatColor.GRAY+" to "+ChatColor.BOLD+"zoom"+ChatColor.GRAY+", "+ChatColor.YELLOW+"Right-Click"+ChatColor.GRAY+" to "+ChatColor.BOLD+"shoot")
        );
        setLore(templore);
    }
    
    
    
    public static NamespacedKey getItemKey(){
        return new NamespacedKey(Hitman.getPlugin(), "type");
    }
    
    public static NamespacedKey getVariantKey(){
        return new NamespacedKey(Hitman.getPlugin(), "variant");
    }
}
