package io.github.sawors.hitman.game.sniper;

import io.github.sawors.hitman.Hitman;
import io.github.sawors.hitman.game.sniper.items.SniperRifle;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class SniperListeners implements Listener {
    
    @EventHandler
    public static void onSniperScopeIn(PlayerToggleSneakEvent event){
        Player p = event.getPlayer();
        
        if(event.isSneaking() && HitmanItem.getItemId(p.getInventory().getItemInMainHand()).equals(new SniperRifle().getId())){
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(p.isOnline() && p.getInventory().getItemInMainHand().hasItemMeta() && HitmanItem.getItemId(p.getInventory().getItemInMainHand()).equals(new SniperRifle().getId()) && p.isSneaking()){
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10,4,false,false));
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(Hitman.getPlugin(),0,10);
        }
    }
    
    @EventHandler
    public static void onSniperShoot(EntityShootBowEvent event){
        if(event.getBow() != null && HitmanItem.getItemId(event.getBow()).equals(new SniperRifle().getId()) && event.getEntity() instanceof Player player){
            event.getProjectile().remove();
            event.getEntity().getLocation().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_GENERIC_EXPLODE,12,1);
            event.getEntity().getLocation().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,12,1);
            
            int maxdistance = (Hitman.getPlugin().getServer().getSimulationDistance()-1)*8;
    
            RayTraceResult rt = player.rayTraceBlocks(maxdistance);
            Entity e = player.getTargetEntity(maxdistance);
            Vector halfdirection = player.getLocation().getDirection().normalize().multiply(.5);
            Location spawnloc = player.getLocation().clone().add(0,1.25,0);
            World w = spawnloc.getWorld();
            int distance = (Hitman.getPlugin().getServer().getSimulationDistance()-1)*8;
            
            if(e instanceof LivingEntity living){
                living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,60,0,false,false));
                distance = (int) player.getEyeLocation().distance(living.getEyeLocation());
            } else if(rt != null && rt.getHitBlock() != null){
                Location targetloc = rt.getHitPosition().toLocation(player.getWorld());
                w.spawnParticle(Particle.BLOCK_DUST,targetloc,32,.1,.1,.1,0, rt.getHitBlock().getBlockData());
                w.playSound(targetloc,rt.getHitBlock().getBlockSoundGroup().getBreakSound(),1,1);
                distance = (int) player.getEyeLocation().distance(targetloc);
            }
    
            for(int i = 0; i<distance*2; i++){
                spawnloc.add(halfdirection);
    
                w.spawnParticle(Particle.REDSTONE, spawnloc,1,0,0,0,.01, new Particle.DustOptions(Color.WHITE,.5f));
            }
            
        }
    }
    
    @EventHandler
    public static void preventCrossbowReload(EntityLoadCrossbowEvent event){
        if(event.getCrossbow() != null && HitmanItem.getItemId(event.getCrossbow()).equals(new SniperRifle().getId())){
            event.setConsumeItem(false);
        }
    }
}
