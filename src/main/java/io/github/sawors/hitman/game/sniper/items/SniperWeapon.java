package io.github.sawors.hitman.game.sniper.items;

import io.github.sawors.hitman.Hitman;
import io.github.sawors.hitman.game.sniper.HitmanItem;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;

import java.util.List;

public class SniperWeapon extends HitmanItem {
    
    public SniperWeapon(){
        setName(Component.text(ChatColor.GOLD+"Sniper Rifle"));
        List<Component> templore = List.of(
                Component.text(ChatColor.GRAY+"Use this weapon to kill your target"),
                Component.text(ChatColor.GRAY+"Sneak to zoom, Right-Click to shoot")
        );
        setLore(templore);
    }
    
    public SniperWeapon(String variant, String name){
        setName(Component.text(ChatColor.GOLD+name));
        setVariant(variant);
        List<Component> templore = List.of(
                Component.text(ChatColor.GRAY+"Use this weapon to kill your target"),
                Component.text(ChatColor.GRAY+"Sneak to zoom, Right-Click to shoot")
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
