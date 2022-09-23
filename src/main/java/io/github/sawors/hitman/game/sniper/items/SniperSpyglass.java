package io.github.sawors.hitman.game.sniper.items;

import io.github.sawors.hitman.game.sniper.HitmanItem;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;

import java.util.List;

public class SniperSpyglass extends HitmanItem {
    public SniperSpyglass(){
        setName(Component.text(ChatColor.GOLD+"Sniper Spyglass"));
        List<Component> templore = List.of(
                Component.text(ChatColor.GRAY+"Use this spyglass to observe the scene")
        );
        setLore(templore);
    }
}
