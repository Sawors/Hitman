package io.github.sawors.hitman.game.sniper;

import io.github.sawors.hitman.Hitman;
import io.github.sawors.hitman.game.GameManager;
import io.github.sawors.hitman.game.PlayerRole;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class SniperListeners implements Listener {
    
    @EventHandler
    public static void onSniperScopeIn(PlayerToggleSneakEvent event){
        Player p = event.getPlayer();
        String gameid = Hitman.getPlayerGameId(p.getUniqueId());
        GameManager game = Hitman.getGameById(gameid);
        
        if(event.isSneaking() && game != null && Objects.equals(game.getPlayerRole(p.getUniqueId()), PlayerRole.SNIPER)){
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*5,12,false,false));
        }
    }
    
    @EventHandler
    public static void onSniperShoot(PlayerInteractEvent event){
        event.getPlayer().sendMessage(event.getAction().toString());
    }
}
